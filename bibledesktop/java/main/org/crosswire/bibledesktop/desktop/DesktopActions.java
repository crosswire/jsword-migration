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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.install.SitesPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.splitlist.SplitBookDataDisplay;
import org.crosswire.bibledesktop.display.tab.TabbedBookDataDisplay;
import org.crosswire.common.config.swing.ConfigEditorFactory;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.desktop.ViewVisitor;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.util.Project;

/**
 * DesktopAction is nothing more than a holder of the behavior
 * of the Desktop. It could easily be member methods in that class.
 * It is here simply to simplify the Desktop class and minimize
 * maintenance cost.
 *
 * Previously each of the "do" methods was a separate class.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at gmail dot com]
 */
public class DesktopActions
{
    /**
     * Create the actions for the desktop
     * @param desktop the desktop for which these actions apply
     */
    public DesktopActions(Desktop desktop)
    {
        this.desktop = desktop;
        actions = new ActionFactory(Desktop.class, this);
    }

    /**
     * Get a particular action by internal name
     * @param key the internal name for the action
     * @return the action requested or null if it does not exist
     */
    public Action getAction(String key)
    {
        return actions.getAction(key);
    }

    /**
     * @return the desktop to which these actions apply
     */
    public Desktop getDesktop()
    {
        return desktop;
    }

    /**
     * @return the Bible installer dialog
     */
    public SitesPane getSites()
    {
        if (sites == null)
        {
            sites = new SitesPane();
        }
        return sites;
    }

    /**
     * Open a new passage window from a file.
     */
    public void doOpen()
    {
        try
        {
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            view.open();
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Save the current passage window.
     */
    public void doSave()
    {
        try
        {
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            if (!view.maySave())
            {
                Reporter.informUser(getDesktop(), Msg.NO_PASSAGE);
                return;
            }

            view.save();
        }
        catch (IOException ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Save the current passage window under a new name.
     */
    public void doSaveAs()
    {
        try
        {
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            if (!view.maySave())
            {
                Reporter.informUser(getDesktop(), Msg.NO_PASSAGE);
                return;
            }

            view.saveAs();
        }
        catch (IOException ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Save all the passage windows.
     */
    public void doSaveAll()
    {
        boolean ok = false;

        Iterator it = getDesktop().getViews().iterator();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            if (view.maySave())
            {
                ok = true;
            }
        }

        if (!ok)
        {
            Reporter.informUser(getDesktop(), Msg.NO_PASSAGE);
            return;
        }

        it = getDesktop().getViews().iterator();
        while (it.hasNext())
        {
            try
            {
                BibleViewPane view = (BibleViewPane) it.next();
                view.save();
            }
            catch (IOException ex)
            {
                Reporter.informUser(getDesktop(), ex);
            }
        }
    }

    /**
     * Exits the VM.
     */
    public void doExit()
    {
        System.exit(0);
    }

    /**
     * Copy the selected text from the "active" display area to the clipboard.
     */
    public void doCopy()
    {
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.copy();
    }

    /**
     * Go to previous passage.
     */
    public void doBack()
    {
        getDesktop().selectHistory(-1);
    }

    /**
     * Go to next passage.
     */
    public void doForward()
    {
        getDesktop().selectHistory(1);
    }

    public void doStrongs(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.STRONGS_NUMBERS.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doMorph(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.MORPH.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doVLine(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.START_VERSE_ON_NEWLINE.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doVNum(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.VERSE_NUMBERS.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doTinyVNum(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.TINY_VERSE_NUMBERS.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doNotes(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.NOTES.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doXRef(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.XREF.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    /**
     * View the HTML as interpreted by the current window.
     * This HTML will not return the styling present in the viewer.
     * That is all class="" are stripped out.
     * Also you may find additional whitespace added to the original.
     */
    public void doViewSource()
    {
        try
        {
            // Limit view source to the current tab.
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            SplitBookDataDisplay da = view.getPassagePane();
            BookDataDisplay bdd = da.getBookDataDisplay();
            if (bdd instanceof TabbedBookDataDisplay)
            {
                bdd = ((TabbedBookDataDisplay) bdd).getInnerDisplayPane();
            }

            Key key = bdd.getKey();

            if (key == null)
            {
                Reporter.informUser(getDesktop(), Msg.SOURCE_MISSING);
                return;
            }

            Book book = da.getBook();

            ViewSourcePane viewer = new ViewSourcePane(book, key);
            viewer.showInFrame(getDesktop());
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Opens the Book installer window (aka a SitesPane)
     */
    public void doBooks()
    {
        getSites().showInDialog(getDesktop());
    }

    /**
     * Opens the Options window
     */
    public void doOptions()
    {
        try
        {
            URL configUrl = Project.instance().getWritablePropertiesURL("desktop"); //$NON-NLS-1$
            ConfigEditorFactory.showDialog(desktop.getConfig(), desktop, configUrl);
        }
        catch (Exception ex)
        {
            Reporter.informUser(desktop, ex);
        }
    }

    /**
     * For opening a help file.
     */
    public void doContents()
    {
        JOptionPane.showMessageDialog(getDesktop(), Msg.NO_HELP);
    }

    /**
     * For opening the About window
     */
    public void doAbout()
    {
        if (atp == null)
        {
            atp = new AboutPane();
        }

        atp.showInDialog(getDesktop());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doToolTipToggle(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        ToolTipManager.sharedInstance().setEnabled(toggle.isSelected());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doStatusToggle(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        desktop.showStatusBar(toggle.isSelected());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doSidebarToggle(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        final boolean show = toggle.isSelected();
        desktop.getViews().visit(new ShowSideBarVisitor(show));
    }

    /**
     *
     */
    private static final class ShowSideBarVisitor implements ViewVisitor
    {
        /**
         * @param show
         */
        public ShowSideBarVisitor(boolean show)
        {
            this.show = show;
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.swing.desktop.ViewVisitor#visitView(java.awt.Component)
         */
        public void visitView(Component component)
        {
            BibleViewPane view = (BibleViewPane) component;
            SplitBookDataDisplay sbDisplay = view.getPassagePane();
            sbDisplay.showSidebar(show);
        }

        private boolean show;
    }

    // Enumeration of all the keys to known actions
    static final String FILE = "File"; //$NON-NLS-1$
    static final String EDIT = "Edit"; //$NON-NLS-1$
    static final String GO = "Go"; //$NON-NLS-1$
    static final String VIEW = "View"; //$NON-NLS-1$
    static final String TOOLS = "Tools"; //$NON-NLS-1$
    static final String HELP = "Help"; //$NON-NLS-1$
    static final String OPEN = "Open"; //$NON-NLS-1$
    static final String SAVE = "Save"; //$NON-NLS-1$
    static final String SAVE_AS = "SaveAs"; //$NON-NLS-1$
    static final String SAVE_ALL = "SaveAll"; //$NON-NLS-1$
    static final String EXIT = "Exit"; //$NON-NLS-1$
    static final String COPY = "Copy"; //$NON-NLS-1$
    static final String BACK = "Back"; //$NON-NLS-1$
    static final String FORWARD = "Forward"; //$NON-NLS-1$
    static final String TOOLTIP_TOGGLE = "ToolTipToggle"; //$NON-NLS-1$
    static final String STATUS_TOGGLE = "StatusToggle"; //$NON-NLS-1$
    static final String SIDEBAR_TOGGLE = "SidebarToggle"; //$NON-NLS-1$
    static final String VIEW_SOURCE = "ViewSource"; //$NON-NLS-1$
    static final String BOOKS = "Books"; //$NON-NLS-1$
    static final String OPTIONS = "Options"; //$NON-NLS-1$
    static final String CONTENTS = "Contents"; //$NON-NLS-1$
    static final String ABOUT = "About"; //$NON-NLS-1$

    /**
     * The desktop on which these actions work
     */
    protected Desktop desktop;

    /**
     * The factory for actions that this class works with
     */
    private transient ActionFactory actions;

    /**
     * The About window
     */
    private AboutPane atp;

    /**
     * The Book installer window
     */
    private SitesPane sites;
}
