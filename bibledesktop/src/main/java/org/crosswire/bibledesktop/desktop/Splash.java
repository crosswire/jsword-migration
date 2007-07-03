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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;

import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.progress.swing.JobsProgressBar;
import org.crosswire.common.swing.GuiUtil;

/**
 * A Simple splash screen.
 * <p>so start one of these call:
 * <pre>
 * Splash s = new Splash();
 * ... // init code
 * s.close();
 * </pre>
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Splash extends JWindow
{
    /**
     * Create a splash window
     */
    public Splash()
    {
        super(GuiUtil.getFrame(null));

        init();
    }

    /**
     * Init the graphics
     */
    private void init()
    {
        Icon icon = GuiUtil.getIcon(Msg.SPLASH_IMAGE.toString());

        JLabel lblPicture = new JLabel();
        lblPicture.setBackground(Color.WHITE);
        lblPicture.setOpaque(true);
        lblPicture.setIcon(icon);
        //lblPicture.setBorder(null);
        lblPicture.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        /*
        JLabel lblText = new JLabel();
        lblText.setFont(new Font(SPLASH_FONT, Font.BOLD, 48));
        lblText.setForeground(new Color(0x99, 0x66, 0xAA));
        lblText.setOpaque(false);
        lblText.setVerticalAlignment(SwingConstants.BOTTOM);
        lblText.setHorizontalAlignment(SwingConstants.RIGHT);
        lblText.setText(Msg.SPLASH_TITLE.toString());
        */

        JPanel pnlDisplay = new JPanel();
        pnlDisplay.setLayout(new GridBagLayout());
        //pnlDisplay.add(lblText, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 10), 0, 0));
        pnlDisplay.add(lblPicture, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JLabel lblInfo = new JLabel();
        lblInfo.setBorder(null);
        lblInfo.setFont(new Font(SPLASH_FONT, Font.PLAIN, 9));
        lblInfo.setForeground(Color.WHITE);
        lblInfo.setBackground(Color.BLACK);
        //lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        lblInfo.setHorizontalAlignment(SwingConstants.RIGHT);
        lblInfo.setText(Msg.getVersionInfo() + ' ');
        lblInfo.setOpaque(true);

        JobsProgressBar pnlJobs = new JobsProgressBar(false);
        pnlJobs.setBackground(Color.WHITE);
        pnlJobs.setForeground(Color.BLACK);
        pnlJobs.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BorderLayout(5, 0));
        pnlInfo.setOpaque(true);
        //pnlInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        pnlInfo.add(lblInfo, BorderLayout.CENTER);
        pnlInfo.add(pnlJobs, BorderLayout.SOUTH);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(pnlInfo, BorderLayout.SOUTH);
        this.getContentPane().add(pnlDisplay, BorderLayout.CENTER);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension window = lblPicture.getPreferredSize();
        this.setLocation(screen.width / 2 - (window.width / 2), screen.height / 2 - (window.height / 2));

        JobManager.addWorkListener(listener);

        this.addMouseListener(new MouseAdapter()
        {
            /* (non-Javadoc)
             * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
             */
            /* @Override */
            public void mousePressed(MouseEvent ev)
            {
                close();
            }
        });

        GuiUtil.applyDefaultOrientation(this);
        this.pack();
        this.setVisible(true);
    }

    /**
     * Shut up shop
     */
    public final void close()
    {
        JobManager.removeWorkListener(listener);

        setVisible(false);
        dispose();
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        listener = new CustomWorkListener();
        is.defaultReadObject();
    }

    private transient CustomWorkListener listener = new CustomWorkListener();

    private static final String SPLASH_FONT = "SanSerif"; //$NON-NLS-1$

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258133565731256119L;

    /**
     * Pack the frame if we get new jobs that could shunt things around
     */
    final class CustomWorkListener implements WorkListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.progress.WorkListener#workProgressed(org.crosswire.common.progress.WorkEvent)
         */
        public void workProgressed(WorkEvent ev)
        {
            Progress job = ev.getJob();
            if (job.getWork() == 0 || job.isFinished())
            {
                Splash.this.pack();
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.progress.WorkListener#workStateChanged(org.crosswire.common.progress.WorkEvent)
         */
        public void workStateChanged(WorkEvent ev)
        {
            // we don't care about these events.
        }
    }
}
