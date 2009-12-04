/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.common.progress.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Logger;

/**
 * JobsViewPane is a small JProgressBar based viewer for current jobs.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class JobsProgressBar extends JPanel implements WorkListener {
    /**
     * Simple ctor
     */
    public JobsProgressBar(boolean small) {
        jobs = new HashMap();
        positions = new ArrayList();
        shaper = new NumberShaper();
        actions = new ActionFactory(JobsProgressBar.class, this);

        if (small) {
            // They start off at 15pt (on Windows at least)
            font = new Font("SansSerif", Font.PLAIN, 10); //$NON-NLS-1$
        }

        JobManager.addWorkListener(this);

        Set current = JobManager.getJobs();
        Iterator it = current.iterator();
        while (it.hasNext()) {
            Progress job = (Job) it.next();
            addJob(job);
        }

        this.setLayout(new GridLayout(1, 0, 2, 0));

        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Create a cancel button that only shows the cancel icon. When the button
     * is pressed the job is interrupted.
     * 
     * @return a custom cancel button
     */
    public synchronized JButton createCancelButton(Progress job) {
        JButton cancelButton = actions.createActionIcon(STOP, new JobCancelListener(job));
        return cancelButton;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire
     * .common.progress.WorkEvent)
     */
    public synchronized void workProgressed(final WorkEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Progress job = ev.getJob();

                if (!jobs.containsKey(job)) {
                    addJob(job);
                }

                updateJob(job);

                if (job.isFinished()) {
                    removeJob(job);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.progress.WorkListener#workStateChanged(org.crosswire
     * .common.progress.WorkEvent)
     */
    public void workStateChanged(WorkEvent ev) {
        Progress job = (Job) ev.getSource();
        JobData jobdata = (JobData) jobs.get(job);
        jobdata.workStateChanged(ev);
    }

    /**
     * Create a new set of components for the new Job
     */
    /* private */final synchronized void addJob(Progress job) {
        ((Job) job).addWorkListener(this);

        int i = findEmptyPosition();
        log.debug("adding job to panel at " + i + ": " + job.getJobName()); //$NON-NLS-1$ //$NON-NLS-2$

        JProgressBar progress = new JProgressBar();
        progress.setStringPainted(true);
        progress.setToolTipText(job.getJobName());
        progress.setBorder(null);
        progress.setBackground(getBackground());
        progress.setForeground(getForeground());
        if (font != null) {
            progress.setFont(font);
        }
        GuiUtil.applyDefaultOrientation(progress);

        // Dimension preferred = progress.getPreferredSize();
        // preferred.width = 50;
        // progress.setPreferredSize(preferred);

        JobData jobdata = new JobData(this, job, i, progress);
        jobs.put(job, jobdata);
        if (i >= positions.size()) {
            positions.add(jobdata);
        } else {
            positions.set(i, jobdata);
        }

        this.add(jobdata.getComponent(), i);
        GuiUtil.refresh(this);
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Update the job details because it has just progressed
     */
    protected synchronized void updateJob(Progress job) {
        JobData jobdata = (JobData) jobs.get(job);

        int percent = job.getWork();
        StringBuffer buf = new StringBuffer(job.getSectionName());
        buf.append(": "); //$NON-NLS-1$
        buf.append(shaper.shape(Integer.toString(percent)));
        buf.append('%');
        jobdata.getProgress().setString(buf.toString());
        jobdata.getProgress().setValue(percent);
    }

    /**
     * Remove the set of components from the panel
     */
    protected synchronized void removeJob(Progress job) {
        ((Job) job).removeWorkListener(this);

        JobData jobdata = (JobData) jobs.get(job);

        positions.set(jobdata.getIndex(), null);
        jobs.remove(job);
        log.debug("removing job from panel: " + jobdata.getJob().getJobName()); //$NON-NLS-1$

        this.remove(jobdata.getComponent());
        GuiUtil.refresh(this);
        jobdata.invalidate();
    }

    /**
     * Where is the next hole in the positions array
     */
    private int findEmptyPosition() {
        int i = 0;
        while (true) {
            if (i >= positions.size()) {
                break;
            }

            if (positions.get(i) == null) {
                break;
            }

            i++;
        }

        return i;
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        actions = new ActionFactory(JobsProgressBar.class, this);
        is.defaultReadObject();
    }

    /**
     * Where we store the currently displayed jobs
     */
    protected Map jobs;

    /**
     * Array telling us what y position the jobs have in the window
     */
    private List positions;

    /**
     * The font for the progress-bars
     */
    private Font font;

    /**
     * Shape numbers into locale representation.
     */
    private NumberShaper shaper = new NumberShaper();

    /**
     * The home of the stop action.
     */
    private transient ActionFactory actions;

    /**
     * The key for the Stop action.
     */
    private static final String STOP = "Stop"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(JobsProgressBar.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257563988660663606L;

    /**
     * A simple class to group information about a Job
     */
    private static class JobData implements WorkListener {
        /**
         * Simple ctor
         */
        JobData(JobsProgressBar bar, Progress job, int index, JProgressBar progress) {
            this.bar = bar;
            this.job = job;
            this.index = index;
            this.progress = progress;
            this.comp = decorateProgressBar();
        }

        /**
         * ensure we can't be used again
         */
        void invalidate() {
            job = null;
            progress = null;
            index = -1;
        }

        /**
         * Accessor for the Job
         */
        Progress getJob() {
            return job;
        }

        /**
         * Accessor for the Progress Bar
         */
        JProgressBar getProgress() {
            return progress;
        }

        /**
         *
         */
        public Component getComponent() {
            return comp;
        }

        /**
         * @return Returns the cancelButton.
         */
        public JButton getCancelButton() {
            if (cancelButton == null) {
                cancelButton = bar.createCancelButton(job);
            }
            return cancelButton;
        }

        /**
         * Accessor for the index
         */
        int getIndex() {
            return index;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.common.progress.WorkListener#workStateChanged(org.crosswire
         * .common.progress.WorkEvent)
         */
        public void workStateChanged(WorkEvent evt) {
            if (cancelButton != null) {
                cancelButton.setEnabled(job.isCancelable());
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire
         * .common.progress.WorkEvent)
         */
        public void workProgressed(WorkEvent ev) {
            // Don't care about progress
        }

        /**
         * Decorate the progress bar if the job can be interrupted. We put the
         * cancel button in a 1 row, 2 column grid where the button is in a
         * minimally sized fixed cell and the progress meter follows in a
         * horizontally stretchy cell
         */
        private Component decorateProgressBar() {
            if (!job.isCancelable()) {
                return progress;
            }

            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.NONE;
            panel.add(bar.createCancelButton(job), gbc);
            gbc.weightx = 1.0;
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(progress, gbc);
            GuiUtil.applyDefaultOrientation(panel);
            return panel;
        }

        private JobsProgressBar bar;
        private Progress job;
        private int index;
        private JProgressBar progress;
        private Component comp;
        private JButton cancelButton;
    }
}
