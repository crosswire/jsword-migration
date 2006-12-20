/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.DocumentWriter;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.basic.Verifier;
import org.crosswire.jsword.passage.Key;

/**
 * This displays the results of a comparision that occurs in a separate
 * thread.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CompareResultsPane extends JPanel implements Runnable
{
	/**
     * Basic Constructor
     */
    public CompareResultsPane(Verifier ver)
    {
        this.ver = ver;
        init();
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        setTitles();
        boxBibles = Box.createVerticalBox();
        boxBibles.add(lblBible1, null);
        boxBibles.add(lblBible2, null);

        barProgress.setString(""); //$NON-NLS-1$
        barProgress.setStringPainted(true);
        txtResults.setRows(5);
        txtResults.setColumns(40);
        scrResults.getViewport().add(txtResults, null);
        pnlResults.setLayout(new BorderLayout(5, 5));
        pnlResults.setBorder(new TitledBorder(LimboMsg.RESULTS_TITLE.toString()));
        pnlResults.add(scrResults, BorderLayout.CENTER);
        pnlResults.add(barProgress, BorderLayout.NORTH);

        // I18N: migrate this to an ActionFactory
        btnStop.setText(LimboMsg.RESULTS_START.toString());
        btnStop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                startStop();
            }
        });
        pnlButtons.add(btnStop, null);

        this.setLayout(new BorderLayout());
        this.add(boxBibles, BorderLayout.NORTH);
        this.add(pnlResults, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
    }

    /**
     * This allows up to easily display this component in a window and
     * have the 2 work together on close actions and so on.
     */
    public void showInFrame(Frame parent)
    {
        final JDialog frame = new JDialog(parent, LimboMsg.RESULTS_DIALOG.toString());

        // I18N: migrate this to an ActionFactory
        btnClose = new JButton(LimboMsg.RESULTS_CLOSE.toString());
        btnClose.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                if (work != null)
                    startStop();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnlButtons.add(btnClose, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.addWindowListener(new WindowAdapter()
        {
            /* @Override */
            public void windowClosed(WindowEvent ev)
            {
                if (work != null)
                    startStop();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        frame.pack();
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Start running the tests
     */
    public void startStop()
    {
        if (work == null)
        {
            // New thread to do the real work
            work = new Thread(this);
            work.start();
            work.setPriority(Thread.MIN_PRIORITY);
        }
        else
        {
            work.interrupt();
            work = null;
        }
    }

    /**
     * The text that we will check, null for no check, we apply startsWith
     * to the given word before we run the check.
     */
    public void setCheckText(String checkText)
    {
        this.checkText = checkText;
        setTitles();
    }

    /**
     * The Passage that we will check, null for no check.
     */
    public void setCheckPassages(Key checkRef)
    {
        this.checkRef = checkRef;
        setTitles();
    }

    /**
     * Set the title of the pane to what we are doing
     */
    private void setTitles()
    {
        lblBible1.setText("<html><b>" + LimboMsg.RESULTS_BOOKS + "</b> " //$NON-NLS-1$ //$NON-NLS-2$
                           + ver.getBible1().getName() + " / " //$NON-NLS-1$
                           + ver.getBible2().getName());

        String compare = "<html><b>" + LimboMsg.RESULTS_COMPARING + "</b> "; //$NON-NLS-1$ //$NON-NLS-2$
        if (checkRef != null)
        {
            compare += LimboMsg.RESULTS_PASSAGE + "=" + checkRef + ' '; //$NON-NLS-1$
        }

        if (checkText != null)
        {
            compare += LimboMsg.RESULTS_WORDS + "=" + (checkText.equals("") ? "*" : checkText); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        lblBible2.setText(compare);
    }

    /**
     * A class to be run in a Thread to do the real work of comparing the
     * selected Books
     */
    public void run()
    {
        // While we are working stop anyone editing the values
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // I18N: migrate this to an ActionFactory
                btnStop.setText(LimboMsg.RESULTS_STOP.toString());
            }
        });

        Document doc = txtResults.getDocument();
        dout.setDocument(doc);
        PrintWriter out = new PrintWriter(dout);
        alive = true;

        try
        {
            JobManager.addWorkListener(cpl);

            if (checkRef != null && checkRef.isEmpty() && alive)
            {
                ver.checkText(checkRef, out);
            }
        }
        catch (final Exception ex)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    ExceptionPane.showExceptionDialog(CompareResultsPane.this, ex);
                }
            });
        }
        finally
        {
            JobManager.removeWorkListener(cpl);
        }

        // Re-enable the values
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                // I18N: migrate this to an ActionFactory
                btnStop.setText(LimboMsg.RESULTS_START.toString());
            }
        });
    }

    /**
     * Are we being told to die
     */
    private boolean alive = true;

    /**
     * The text to check
     */
    private String checkText = null;

    /**
     * The passage to check
     */
    private Key checkRef = null;

    /**
     * The Bible verifier
     */
    private Verifier ver;

    /**
     * The DocumentWriter that the comparison can write to
     */
    private DocumentWriter dout = new DocumentWriter();

    /**
     * Work in progress
     */
    protected Thread work;

    /**
     * The progress listener
     */
    private CustomProgressListener cpl = new CustomProgressListener();

    /* GUI components */
    private JPanel pnlResults = new JPanel();
    private JScrollPane scrResults = new JScrollPane();
    private JTextArea txtResults = new JTextArea();
    protected JProgressBar barProgress = new JProgressBar();
    private Box boxBibles;
    private JLabel lblBible1 = new JLabel();
    private JPanel pnlButtons = new JPanel();
    protected JButton btnStop = new JButton();
    private JButton btnClose = null;
    private JLabel lblBible2 = new JLabel();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257845467781085240L;

    /**
     * Report progress changes to the screen
     */
    class CustomProgressListener implements WorkListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.progress.WorkListener#progressMade(org.crosswire.common.progress.WorkEvent)
         */
        public void workProgressed(final WorkEvent ev)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    Progress job = ev.getJob();
                    int percent = job.getWork();
                    barProgress.setString(job.getSectionName() + ": (" + percent + "%)"); //$NON-NLS-1$ //$NON-NLS-2$
                    barProgress.setValue(percent);
                }
            });
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.progress.WorkListener#workStateChanged(org.crosswire.common.progress.WorkEvent)
         */
        public void workStateChanged(WorkEvent ev)
        {
        }
    }
}
