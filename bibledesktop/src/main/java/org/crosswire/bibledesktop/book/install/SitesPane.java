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
package org.crosswire.bibledesktop.book.install;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.crosswire.common.progress.swing.JobsProgressBar;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.install.InstallerEvent;
import org.crosswire.jsword.book.install.InstallerListener;

/**
 * A panel for use within a SitesPane to display one set of Books that are
 * installed or could be installed.
 * <p>so start one of these call:
 * <pre>
 * sites = new SitesPane();
 * sites.showInDialog(parent);
 * </pre>
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SitesPane extends JPanel
{
    /**
     * Simple ctor
     */
    public SitesPane()
    {
        init();

        imanager = new InstallManager();
        installers = imanager.getInstallers();

        addAllInstallers();

        imanager.addInstallerListener(new InstallerListener()
        {
            /* (non-Javadoc)
             * @see org.crosswire.jsword.book.install.InstallerListener#installerAdded(org.crosswire.jsword.book.install.InstallerEvent)
             */
            public void installerAdded(InstallerEvent ev)
            {
                Installer installer = ev.getInstaller();
                String name = imanager.getInstallerNameForInstaller(installer);

                SitePane site = new SitePane(installer);
                tabMain.add(name, site);
            }

            /* (non-Javadoc)
             * @see org.crosswire.jsword.book.install.InstallerListener#installerRemoved(org.crosswire.jsword.book.install.InstallerEvent)
             */
            public void installerRemoved(InstallerEvent ev)
            {
                // This gets tricky because if you add a site with a new name
                // but the same details as an old one, then the old name goes
                // so we can't get the old name to remove it's tab (and anyway
                // we would have to do a search through all the tabs to find it
                // by name)
                // So we just nuke all the tabs and re-create them
                removeAllInstallers();
                addAllInstallers();
            }
        });
    }

    /**
     * Build the GUI components
     */
    private void init()
    {
        actions = new ActionFactory(SitesPane.class, this);

        tabMain = new JTabbedPane();
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(tabMain, BorderLayout.CENTER);
        this.add(new JobsProgressBar(true), BorderLayout.SOUTH);
    }

    /**
     * Re-create the list of installers
     */
    protected final void addAllInstallers()
    {
        // Now add panels for book installation sites
        Iterator iter = installers.keySet().iterator();
        while (iter.hasNext())
        {
            String name = (String) iter.next();
            Installer installer = (Installer) installers.get(name);

            SitePane site = new SitePane(installer);
            tabMain.add(name, site);
        }

        // Add the panel for the locally installed books
        tabMain.add(Msg.LOCAL_BOOKS.toString(), new SitePane());
    }

    /**
     * Remove all the non-local installers
     */
    protected void removeAllInstallers()
    {
        tabMain.removeAll();
    }

    /**
     * Add a site to the list of install sources.
     */
    public void doManageSites()
    {
        EditSitePane edit = new EditSitePane(imanager);
        edit.showInDialog(this);
    }

    /**
     * We are done, close the window
     */
    public void doSitesClose()
    {
        if (dlgMain != null)
        {
            dlgMain.setVisible(false);
        }
    }

    /**
     * Open this Panel in it's own dialog box.
     */
    public void showInDialog(Component parent)
    {
        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));
        dlgMain.setSize(new Dimension(750, 500));
        dlgMain.getContentPane().setLayout(new BorderLayout());
        dlgMain.getContentPane().add(this, BorderLayout.CENTER);
        dlgMain.getContentPane().add(createButtons(), BorderLayout.SOUTH);
        dlgMain.setTitle(Msg.AVAILABLE_BOOKS.toString());
        dlgMain.setResizable(true);
        //dlgMain.setModal(true);
        dlgMain.addWindowListener(new WindowAdapter()
        {
            /* (non-Javadoc)
             * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
             */
            /* @Override */
            public void windowClosed(WindowEvent ev)
            {
                doSitesClose();
            }
        });
        dlgMain.setLocationRelativeTo(parent);
        dlgMain.setVisible(true);
        dlgMain.toFront();
    }

    /**
     *
     */
    private Component createButtons()
    {
        if (pnlButtons == null)
        {
            JButton btnOK = new JButton(actions.getAction(CLOSE));

            JButton btnAdd = new JButton(actions.getAction(EDIT_SITE));

            pnlButtons = new JPanel();
            pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
            pnlButtons.add(btnAdd, null);
            pnlButtons.add(btnOK);
        }
        return pnlButtons;

    }

    private static final String CLOSE = "SitesClose"; //$NON-NLS-1$
    private static final String EDIT_SITE = "ManageSites"; //$NON-NLS-1$

    /**
     * The known installers fetched from InstallManager
     */
    private Map installers;

    /**
     * The current installer
     */
    protected transient InstallManager imanager;

    private transient ActionFactory actions;

    /*
     * GUI Components
     */
    private JDialog dlgMain;
    private JPanel pnlButtons;
    protected JTabbedPane tabMain;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258126947069605936L;
}
