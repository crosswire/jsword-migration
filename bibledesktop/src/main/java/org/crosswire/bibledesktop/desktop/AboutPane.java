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

import gnu.gpl.License;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
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
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.html.HTMLEditorKit;

import org.crosswire.bibledesktop.BDMsg;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.AntiAliasedTextPane;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.MapTableModel;
import org.crosswire.common.util.CollectionUtil;

/**
 * AboutPane is a window that contains various advanced user tools in one place.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class AboutPane {
    /**
     * Basic constructor
     */
    public AboutPane() {
        init();
    }

    /**
     * Build the GUI components
     */
    private void init() {
        // TRANSLATOR: This image is of an English Bible. It can be replaced with a localized one.
        // It should be named splash_ll.png where ll is the 2 letter language code and put in the
        // images directory. Then point this to it.
        Icon icon = GuiUtil.getIcon(BDMsg.gettext("/images/splash.png"));

        JLabel lblPicture = new JLabel();
        lblPicture.setIcon(icon);
        lblPicture.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        lblPicture.setHorizontalAlignment(SwingConstants.CENTER);
        lblPicture.setVerticalAlignment(SwingConstants.CENTER);

        JLabel lblInfo = new JLabel();
        lblInfo.setFont(new Font(SPLASH_FONT, 1, 14));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        lblInfo.setOpaque(true);
        lblInfo.setHorizontalAlignment(SwingConstants.TRAILING);
        lblInfo.setText(BDMsg.getVersionInfo());

        ActionFactory actions = new ActionFactory(this);
        // TRANSLATOR: This is the text of an "OK" button that dismisses the dialog
        CWAction action = actions.addAction("AboutOK", BDMsg.gettext("OK"));
        // TRANSLATOR: This is the tooltip for an "OK" button that dismisses the dialog
        action.setTooltip(BDMsg.gettext("Close this window"));
        JButton btnOk = new JButton(action);

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

        JTabbedPane tabMain = new JTabbedPane();
        pnlMain.add(tabMain, BorderLayout.CENTER);

        // Add the splash
        String appName = BDMsg.getApplicationTitle();
        tabMain.add(pnlSplash, appName);

        License license = new License(appName);
        //        Font fixedFont = new Font("Monospaced", 0, 18);
        JTextPane warranty = new AntiAliasedTextPane();
        // warranty.setFont(fixedFont);
        warranty.setEditable(false);
        warranty.setEditorKit(new HTMLEditorKit());
        warranty.setText(license.getWarranty());
        warranty.setCaretPosition(0);
        JScrollPane warrantyScr = new CWScrollPane(warranty);
        warrantyScr.setPreferredSize(new Dimension(500, 300));
        JPanel warrantyPnl = new JPanel(new BorderLayout());
        warrantyPnl.add(warrantyScr, BorderLayout.CENTER);
        warrantyPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // TRANSLATOR: The label for the tab that shows BibleDesktop's GPL Non-Warranty
        tabMain.add(warrantyPnl, BDMsg.gettext("Warranty"));

        JTextPane details = new AntiAliasedTextPane();
        // details.setFont(fixedFont);
        details.setEditable(false);
        details.setEditorKit(new HTMLEditorKit());
        details.setText(license.getDetails());
        details.setCaretPosition(0);
        JScrollPane detailScr = new CWScrollPane(details);
        detailScr.setPreferredSize(new Dimension(500, 300));
        JPanel detailsPnl = new JPanel(new BorderLayout());
        detailsPnl.add(detailScr, BorderLayout.CENTER);
        detailsPnl.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        // TRANSLATOR: The label for the tab that shows BibleDesktop's GPL License
        tabMain.add(detailsPnl, BDMsg.gettext("License"));

        // Put in tabs if advanced
        if (advanced) {
            // create and add the System Properties tab
            JTable tblProps = new JTable();
            MapTableModel mdlProps = new MapTableModel(CollectionUtil.properties2Map(System.getProperties()));
            tblProps.setModel(mdlProps);

            JScrollPane scrProps = new CWScrollPane(tblProps);
            scrProps.setPreferredSize(new Dimension(500, 300));

            JPanel pnlProps = new JPanel();
            pnlProps.setLayout(new BorderLayout());
            pnlProps.add(scrProps, BorderLayout.CENTER);
            pnlProps.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            // TRANSLATOR: The label for the tab that shows Java's System Properties.
            // This is an advanced option not intended for end users.
            tabMain.add(pnlProps, BDMsg.gettext("System Properties"));
        }
        GuiUtil.applyDefaultOrientation(pnlMain);
    }

    /**
     * Close this dialog
     */
    public void doAboutOK() {
        if (dlgMain != null) {
            dlgMain.dispose();
            dlgMain = null;
        }
    }

    /**
     * A method to be exposed by our children
     * 
     * @param parent
     *            The component to which to attach the new dialog
     */
    public void showInDialog(Component parent) {
        Frame root = JOptionPane.getFrameForComponent(parent);
        dlgMain = new JDialog(root);

        dlgMain.getContentPane().add(pnlMain);
        dlgMain.setTitle(BDMsg.getAboutInfo());
        dlgMain.setModal(true);
        dlgMain.addWindowListener(new WindowAdapter() {
            /* (non-Javadoc)
             * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosed(WindowEvent ev) {
                doAboutOK();
            }
        });
        GuiUtil.applyDefaultOrientation(dlgMain);
        dlgMain.pack();
        dlgMain.setLocationRelativeTo(parent);
        dlgMain.setVisible(true);
    }

    /**
     * @return Returns whether the window should show an advanced view.
     */
    public static synchronized boolean isAdvanced() {
        return advanced;
    }

    /**
     * @param advanced
     *            Turn on an advanced view of the window.
     */
    public static synchronized void setAdvanced(boolean advanced) {
        AboutPane.advanced = advanced;
    }

    private static final String SPLASH_FONT = "SanSerif";

    private static boolean advanced;
    private JDialog dlgMain;
    private JPanel pnlMain;
}
