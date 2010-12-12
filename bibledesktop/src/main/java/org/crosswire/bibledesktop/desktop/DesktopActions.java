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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ToolTipManager;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.install.InternetWarning;
import org.crosswire.bibledesktop.book.install.SitesPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.basic.SplitBookDataDisplay;
import org.crosswire.bibledesktop.display.basic.TabbedBookDataDisplay;
import org.crosswire.common.config.swing.ConfigEditorFactory;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.Actionable;
import org.crosswire.common.swing.CWOptionPane;
import org.crosswire.common.swing.desktop.LayoutPersistence;
import org.crosswire.common.swing.desktop.ViewVisitor;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.OSType;
import org.crosswire.common.util.ReflectionUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.util.WebWarning;

/**
 * DesktopAction is nothing more than a holder of the behavior of the Desktop.
 * It could easily be member methods in that class. It is here simply to
 * simplify the Desktop class and minimize maintenance cost.
 * 
 * Previously each of the "do" methods was a separate class.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DesktopActions implements Actionable {
    /**
     * Create the actions for the desktop
     * 
     * @param desktop
     *            the desktop for which these actions apply
     */
    public DesktopActions(Desktop desktop) {
        this.desktop = desktop;
        actions = new ActionFactory(Msg.class, this);

        osxRegistered = macOSXRegistration();
    }

    /**
     * Get a particular action by internal name
     * 
     * @param key
     *            the internal name for the action
     * @return the action requested or null if it does not exist
     */
    public Action getAction(String key) {
        return actions.getAction(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.Actionable#actionPerformed(java.lang.String)
     */
    public void actionPerformed(String action) {
        actions.actionPerformed(action);
    }

    /**
     * @return the desktop to which these actions apply
     */
    public Desktop getDesktop() {
        return desktop;
    }

    /**
     * Determines whether MacOSX has been registered.
     * 
     * @return true when there is full MacOSX integration.
     */
    public boolean isOSXRegistered() {
        return osxRegistered;
    }

    /**
     * @return the Bible installer dialog
     */
    public SitesPane getSites() {
        if (sites == null) {
            sites = new SitesPane();
        }
        return sites;
    }

    /**
     * Open a new passage window from a file.
     */
    public void doOpen() {
        try {
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            view.open();
        } catch (NoSuchVerseException e) {
            Reporter.informUser(getDesktop(), e);
        } catch (IOException e) {
            Reporter.informUser(getDesktop(), e);
        }
    }

    /**
     * Save the current passage window.
     */
    public void doSave() {
        try {
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            if (!view.maySave()) {
                // TRANSLATOR: The user is trying to save the passage in the visible
                // Bible View pane, but it is empty.
                Reporter.informUser(getDesktop(), Msg.gettext("No Passage to Save"));
                return;
            }

            view.save();
        } catch (IOException ex) {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Save the current passage window under a new name.
     */
    public void doSaveAs() {
        try {
            BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
            if (!view.maySave()) {
                // TRANSLATOR: The user is trying to save the passage in the visible
                // Bible View pane, but it is empty.
                Reporter.informUser(getDesktop(), Msg.gettext("No Passage to Save"));
                return;
            }

            view.saveAs();
        } catch (IOException ex) {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Save all the passage windows.
     */
    public void doSaveAll() {
        boolean ok = false;

        for (Component comp : getDesktop().getViews()) {
            BibleViewPane view = (BibleViewPane) comp;
            if (view.maySave()) {
                ok = true;
            }
        }

        if (!ok) {
            // TRANSLATOR: The user is trying to save the passage in all the
            // Bible View panes, but they are all empty.
            Reporter.informUser(getDesktop(), Msg.gettext("No Passage to Save"));
            return;
        }

        for (Component comp : getDesktop().getViews()) {
            try {
                BibleViewPane view = (BibleViewPane) comp;
                view.save();
            } catch (IOException ex) {
                Reporter.informUser(getDesktop(), ex);
            }
        }
    }

    /**
     * Exits the VM.
     */
    public void doExit() {
        LayoutPersistence.instance().saveLayout(desktop);
        System.exit(0);
    }

    /**
     * Copy the selected text from the "active" display area to the clipboard.
     */
    public void doCopy() {
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.copy();
    }

    /**
     * Go to previous passage.
     */
    public void doBack() {
        getDesktop().selectHistory(-1);
    }

    /**
     * Go to next passage.
     */
    public void doForward() {
        getDesktop().selectHistory(1);
    }

    public void doStrongs(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.STRONGS_NUMBERS.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doMorph(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.MORPH.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doVLine(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.START_VERSE_ON_NEWLINE.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doVNum() {
        XSLTProperty.VERSE_NUMBERS.setState(true);
        XSLTProperty.CV.setState(false);
        XSLTProperty.BCV.setState(false);
        XSLTProperty.NO_VERSE_NUMBERS.setState(false);
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doTinyVNum(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.TINY_VERSE_NUMBERS.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doBCVNum() {
        XSLTProperty.VERSE_NUMBERS.setState(false);
        XSLTProperty.CV.setState(false);
        XSLTProperty.BCV.setState(true);
        XSLTProperty.NO_VERSE_NUMBERS.setState(false);
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doCVNum() {
        XSLTProperty.VERSE_NUMBERS.setState(false);
        XSLTProperty.CV.setState(true);
        XSLTProperty.BCV.setState(false);
        XSLTProperty.NO_VERSE_NUMBERS.setState(false);
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doNoVNum() {
        XSLTProperty.VERSE_NUMBERS.setState(false);
        XSLTProperty.CV.setState(false);
        XSLTProperty.BCV.setState(false);
        XSLTProperty.NO_VERSE_NUMBERS.setState(true);
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    /**
     * Show differences between Bible Book versions.
     */
    public void doCompareToggle(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        getDesktop().setCompareShowing(toggle.getState());
    }

    public void doHeadings(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.HEADINGS.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doNotes(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.NOTES.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    public void doXRef(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        XSLTProperty.XREF.setState(toggle.isSelected());
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        da.getBookDataDisplay().refresh();
    }

    /**
     * View the HTML as interpreted by the current window. This HTML will not
     * return the styling present in the viewer. That is all class="" are
     * stripped out. Also you may find additional whitespace added to the
     * original.
     */
    public void doViewSource() {
        // Limit view source to the current tab.
        BibleViewPane view = (BibleViewPane) getDesktop().getViews().getSelected();
        SplitBookDataDisplay da = view.getPassagePane();
        BookDataDisplay bdd = da.getBookDataDisplay();
        if (bdd instanceof TabbedBookDataDisplay) {
            bdd = ((TabbedBookDataDisplay) bdd).getInnerDisplayPane();
        }

        Key key = bdd.getKey();

        if (key == null) {
            // TRANSLATOR: The user is trying to view the source of the passage in the visible
            // Bible View pane, but it is empty.
            Reporter.informUser(getDesktop(), Msg.gettext("No current passage to view"));
            return;
        }

        ViewSourcePane viewer = new ViewSourcePane(da.getBooks(), key);
        viewer.showInFrame(getDesktop());
    }

    /**
     * Opens the Book installer window (aka a SitesPane)
     */
    public void doBooks() {
        int webAccess = InternetWarning.GRANTED;
        if (WebWarning.instance().isShown()) {
            webAccess = InternetWarning.showDialog(desktop, "?");
        }

        if (webAccess == InternetWarning.GRANTED) {
            getSites().showInDialog(getDesktop());
        }
    }

    /**
     * Opens the Options window
     */
    public void doOptions() {
        URI configUri = CWProject.instance().getWritableURI("desktop", FileUtil.EXTENSION_PROPERTIES);
        ConfigEditorFactory.showDialog(desktop.getConfig(), desktop, configUri);
    }

    /**
     * For opening a help file.
     */
    public void doContents() {
        // TRANSLATOR: Someday we'll have real help but for now this points them to the Bible Desktop web site.
        CWOptionPane.showMessageDialog(getDesktop(), Msg.gettext("Currently on-line help is only available via the Bible Desktop's website:\nhttp://www.crosswire.org/bibledesktop"));
    }

    /**
     * For opening the About window
     */
    public void doAbout() {
        if (atp == null) {
            atp = new AboutPane();
        }

        atp.showInDialog(getDesktop());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doToolTipToggle(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        ToolTipManager.sharedInstance().setEnabled(toggle.isSelected());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doStatusToggle(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        desktop.showStatusBar(toggle.isSelected());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doSidebarToggle(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        final boolean show = toggle.isSelected();
        desktop.getViews().visit(new ShowSideBarVisitor(show));
    }

    /**
     * Show web journal or not.
     */
    public void doJournalToggle(ActionEvent ev) {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        desktop.showWebJournal(toggle.isSelected());
    }

    /**
     * Register the application with Apple EAWT, which provides support for the
     * Application Menu, with About, Preferences (Options) and Quit (Exit).
     * 
     * @return true on success
     */
    public boolean macOSXRegistration() {
        if (OSType.MAC.equals(OSType.getOSType())) {
            try {
                Class<?> osxAdapter = ClassUtil.forName("org.crosswire.common.aqua.OSXAdapter");
                Object[] registerOSXArgs = {
                        actions, DesktopActions.ABOUT, DesktopActions.OPTIONS, DesktopActions.EXIT
                };
                ReflectionUtil.invoke(osxAdapter, osxAdapter, "registerMacOSXApplication", registerOSXArgs);

                // To call a method taking a type of boolean, the type has to
                // match but the object has to be wrapped
                Class<?>[] enablePrefTypes = {
                    boolean.class
                };
                Object[] enablePrefArgs = {
                    Boolean.TRUE
                };
                ReflectionUtil.invoke(osxAdapter, osxAdapter, "enablePrefs", enablePrefArgs, enablePrefTypes);
                return true;
            } catch (NoClassDefFoundError e) {
                // This is thrown when EAWT or MacOSXadapter is not present.
                log.error("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
            } catch (ClassNotFoundException e) {
                // Should not happen
                log.error("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
            } catch (Exception e) {
                // Everything else.
                log.error("Exception while loading the OSXAdapter:", e);
            }
        }
        return false;
    }

    /**
     *
     */
    private static final class ShowSideBarVisitor implements ViewVisitor {
        /**
         * @param show
         */
        public ShowSideBarVisitor(boolean show) {
            this.show = show;
        }

        /* (non-Javadoc)
         * @see org.crosswire.common.swing.desktop.ViewVisitor#visitView(java.awt.Component)
         */
        public void visitView(Component component) {
            if (component instanceof BibleViewPane) {
                BibleViewPane view = (BibleViewPane) component;
                SplitBookDataDisplay sbDisplay = view.getPassagePane();
                sbDisplay.showSidebar(show);
            }
        }

        private boolean show;
    }

    // Enumeration of all the keys to known actions
    static final String FILE = "File";
    static final String EDIT = "Edit";
    static final String GO = "Go";
    static final String VIEW = "View";
    static final String TOOLS = "Tools";
    static final String HELP = "Help";
    static final String OPEN = "Open";
    static final String SAVE = "Save";
    static final String SAVE_AS = "SaveAs";
    static final String SAVE_ALL = "SaveAll";
    static final String EXIT = "Exit";
    static final String COPY = "Copy";
    static final String BACK = "Back";
    static final String FORWARD = "Forward";
    static final String COMPARE_TOGGLE = "CompareToggle";
    static final String TOOLTIP_TOGGLE = "ToolTipToggle";
    static final String STATUS_TOGGLE = "StatusToggle";
    static final String SIDEBAR_TOGGLE = "SidebarToggle";
    static final String JOURNAL_TOGGLE = "JournalToggle";
    static final String VERSE = "Verse";
    static final String VIEW_SOURCE = "ViewSource";
    static final String BOOKS = "Books";
    static final String OPTIONS = "Options";
    static final String CONTENTS = "Contents";
    static final String ABOUT = "About";

    /**
     * The desktop on which these actions work
     */
    protected Desktop desktop;

    /**
     * The factory for actions that this class works with
     */
    private ActionFactory actions;

    /**
     * Indicates whether there is MacOSX integration.
     */
    private boolean osxRegistered;

    /**
     * The About window
     */
    private AboutPane atp;

    /**
     * The Book installer window
     */
    private SitesPane sites;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(DesktopActions.class);
}
