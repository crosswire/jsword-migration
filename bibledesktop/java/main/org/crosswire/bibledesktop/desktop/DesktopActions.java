package org.crosswire.bibledesktop.desktop;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.SitesPane;
import org.crosswire.bibledesktop.display.splitlist.SplitBookDataDisplay;
import org.crosswire.common.config.swing.ConfigEditorFactory;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;

/**
 * DesktopAction is nothing more than a holder of the behavior
 * of the Desktop. It could easily be member methods in that class.
 * It is here simply to simplify the Desktop class and minimize
 * maintenance cost.
 *
 * Previously each of the "do" methods was a separate class.
 *
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at hotmail dot com]
 * @version $Id$
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
        converter = ConverterFactory.getConverter();
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
     * For creating a new window.
     */
    public void doNewTab()
    {
        BibleViewPane view = new BibleViewPane();

        getDesktop().addBibleViewPane(view);

//        view.addHyperlinkListener(getDesktop());
    }

    /**
     * Open a new passage window from a file.
     */
    public void doOpen()
    {
        try
        {
            BibleViewPane view = getDesktop().getSelectedBibleViewPane();
            view.open();
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop(), ex);
        }
    }

    /**
     * Close the current passage window.
     */
    public void doClearView()
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        view.clear();
    }
    /**
     * Close all the passage windows.
     */
    public void doCloseOtherViews()
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane aView = (BibleViewPane) it.next();
            if (aView != view)
            {
                getDesktop().removeBibleViewPane(aView);
            }

        }
    }

    /**
     * Save the current passage window.
     */
    public void doSave()
    {
        try
        {
            BibleViewPane view = getDesktop().getSelectedBibleViewPane();
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
            BibleViewPane view = getDesktop().getSelectedBibleViewPane();
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

        Iterator it = getDesktop().iterateBibleViewPanes();
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

        it = getDesktop().iterateBibleViewPanes();
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
        SplitBookDataDisplay da = getDesktop().getDisplayArea();
        da.copy();
    }

    /**
     * View the Tabbed Document Interface (TDI) interface.
     */
    public void doTabMode()
    {
        getDesktop().setLayoutType(LayoutType.TDI);
    }

    /**
     * View the Multiple Document/Window Interface (MDI) interface.
     */
    public void doWindowMode()
    {
        getDesktop().setLayoutType(LayoutType.MDI);
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
            SplitBookDataDisplay da = getDesktop().getDisplayArea();
            Key key = da.getKey();

            if (key == null)
            {
                Reporter.informUser(getDesktop(), Msg.SOURCE_MISSING);
                return;
            }

            Book book = da.getBook();

            String orig = book.getRawData(key);

            BookData bdata = book.getData(key);

            BookMetaData bmd = book.getBookMetaData();
            boolean direction = bmd.isLeftToRight();

            SAXEventProvider osissep = bdata.getSAXEventProvider();
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            htmlsep.setParameter("direction", direction ? "ltr" : "rtl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            String html = XMLUtil.writeToString(htmlsep);

            SerializingContentHandler osis = new SerializingContentHandler(true);
            osissep.provideSAXEvents(osis);

            ViewSourcePane viewer = new ViewSourcePane(orig, osis.toString(), html);
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
            desktop.fillChoiceFactory();
            BooksListener cbl = new BooksListener()
            {
                public void bookAdded(BooksEvent ev)
                {
                    desktop.refreshBooks();
                }

                public void bookRemoved(BooksEvent ev)
                {
                    desktop.refreshBooks();
                }
            };
            Books.installed().addBooksListener(cbl);

            URL configUrl = Project.instance().getWritablePropertiesURL("desktop"); //$NON-NLS-1$
            ConfigEditorFactory.showDialog(desktop.getConfig(), desktop, configUrl);

            Books.installed().removeBooksListener(cbl);
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
            atp = new AboutPane(getDesktop());
        }

        atp.showInDialog(getDesktop());
    }

    /**
     * Show or hide the tool bar.
     */
    public void doToolBarToggle(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        desktop.showToolBar(toggle.isSelected());
    }

    /**
     * Show or hide the tool bar text.
     */
    public void doToolBarText(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        desktop.showToolBarText(toggle.isSelected());
    }

    /**
     * Show large or small tool bar icons.
     */
    public void doToolBarLarge(ActionEvent ev)
    {
        JCheckBoxMenuItem toggle = (JCheckBoxMenuItem) ev.getSource();
        desktop.showToolBarLargeIcons(toggle.isSelected());
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
        desktop.showSidebar(toggle.isSelected());
    }

    // Enumeration of all the keys to known actions
    static final String FILE = "File"; //$NON-NLS-1$
    static final String EDIT = "Edit"; //$NON-NLS-1$
    static final String VIEW = "View"; //$NON-NLS-1$
    static final String TOOLS = "Tools"; //$NON-NLS-1$
    static final String HELP = "Help"; //$NON-NLS-1$
    static final String NEW_TAB = "NewTab"; //$NON-NLS-1$
    static final String OPEN = "Open"; //$NON-NLS-1$
    static final String CLEAR_VIEW = "ClearView"; //$NON-NLS-1$
    static final String CLOSE_OTHER_VIEWS = "CloseOtherViews"; //$NON-NLS-1$
    static final String SAVE = "Save"; //$NON-NLS-1$
    static final String SAVE_AS = "SaveAs"; //$NON-NLS-1$
    static final String SAVE_ALL = "SaveAll"; //$NON-NLS-1$
    static final String EXIT = "Exit"; //$NON-NLS-1$
    static final String COPY = "Copy"; //$NON-NLS-1$
    static final String TAB_MODE = "TabMode"; //$NON-NLS-1$
    static final String WINDOW_MODE = "WindowMode"; //$NON-NLS-1$
    static final String TOOLBAR_TOGGLE = "ToolBarToggle"; //$NON-NLS-1$
    static final String TOOLBAR_TEXT = "ToolBarText"; //$NON-NLS-1$
    static final String TOOLBAR_LARGE = "ToolBarLarge"; //$NON-NLS-1$
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
    private ActionFactory actions;

    /**
     * To convert OSIS to HTML
     */
    private Converter converter;

    /**
     * The About window
     */
    private AboutPane atp;

    /**
     * The Book installer window
     */
    private SitesPane sites;
}
