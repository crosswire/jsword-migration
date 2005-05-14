/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.MapTableModel;

/**
 * AboutPane is a window that contains various advanced user tools in
 * one place.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class AboutPane
{
    /**
     * Basic constructor
     */
    public AboutPane()
    {
        init();
    }

    /**
     * Build the GUI components
     */
    private void init()
    {
        Icon icon = GuiUtil.getIcon(Msg.SPLASH_IMAGE.toString());

        JLabel lblPicture = new JLabel();
        lblPicture.setIcon(icon);
        lblPicture.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblPicture.setHorizontalAlignment(SwingConstants.CENTER);
        lblPicture.setVerticalAlignment(SwingConstants.CENTER);

        JLabel lblInfo = new JLabel();
        lblInfo.setFont(new Font(SPLASH_FONT, 1, 14));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        lblInfo.setOpaque(true);
        lblInfo.setHorizontalAlignment(SwingConstants.RIGHT);
        lblInfo.setText(Msg.getVersionInfo());

        ActionFactory actions = new ActionFactory(AboutPane.class, this);
        JButton btnOk = new JButton(actions.getAction(ABOUT_OK));

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnOk);

        pnlMain = new JPanel();
        pnlMain.setLayout(new BorderLayout(5, 5));
        pnlMain.add(pnlButtons, BorderLayout.SOUTH);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // Create and add the splash tab
        JPanel pnlSplash = new JPanel();
        pnlSplash.setLayout(new BorderLayout(5, 0));
        pnlSplash.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlSplash.add(lblPicture, BorderLayout.CENTER);
        pnlSplash.add(lblInfo, BorderLayout.SOUTH);

        // Put in tabs if advanced
        if (!advanced)
        {
            pnlMain.add(pnlSplash);
        }
        else
        {
            JTabbedPane tabMain = new JTabbedPane();
            pnlMain.add(tabMain, BorderLayout.CENTER);

            // Add the splash
            tabMain.add(pnlSplash, Msg.getApplicationTitle());

            // create and add the System Properties tab
            JTable tblProps = new JTable();
            MapTableModel mdlProps = new MapTableModel(System.getProperties());
            tblProps.setModel(mdlProps);

            JScrollPane scrProps = new JScrollPane();
            scrProps.setPreferredSize(new Dimension(500, 300));
            scrProps.getViewport().add(tblProps);

            JPanel pnlProps = new JPanel();
            pnlProps.setLayout(new BorderLayout());
            pnlProps.add(scrProps, BorderLayout.CENTER);
            pnlProps.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            tabMain.add(pnlProps, Msg.SYSTEM_PROPS_TAB_TITLE.toString());

        }
    }

    /**
     * Close this dialog
     */
    public void doAboutOK()
    {
        if (dlgMain != null)
        {
            dlgMain.dispose();
            dlgMain = null;
        }
    }

    /**
     * A method to be exposed by our children
     * @param parent The component to which to attach the new dialog
     */
    public void showInDialog(Component parent)
    {
        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlgMain.getContentPane().add(pnlMain);
        dlgMain.setTitle(Msg.getAboutInfo());
        dlgMain.setModal(true);
        dlgMain.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                doAboutOK();
            }
        });
        dlgMain.pack();
        dlgMain.setLocationRelativeTo(parent);
        dlgMain.setVisible(true);
    }

    /**
     * @return Returns whether the window should show an advanced view.
     */
    public static synchronized boolean isAdvanced()
    {
        return advanced;
    }

    /**
     * @param advanced Turn on an advanced view of the window.
     */
    public static synchronized void setAdvanced(boolean advanced)
    {
        AboutPane.advanced = advanced;
    }

    private static final String SPLASH_FONT = "SanSerif"; //$NON-NLS-1$

    private static final String ABOUT_OK = "AboutOK"; //$NON-NLS-1$

    private static boolean advanced;
    private JDialog dlgMain;
    private JPanel pnlMain;
}
