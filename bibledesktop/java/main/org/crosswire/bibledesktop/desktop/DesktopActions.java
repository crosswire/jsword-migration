package org.crosswire.bibledesktop.desktop;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.SitesPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.common.config.swing.ConfigEditorFactory;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
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
    public void doNewWindow()
    {
        BibleViewPane view = new BibleViewPane();

        getDesktop().addBibleViewPane(view);

        view.addHyperlinkListener(getDesktop());
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
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * Close the current passage window.
     */
    public void doClose()
    {
        BibleViewPane view = getDesktop().getSelectedBibleViewPane();
        getDesktop().removeBibleViewPane(view);
    }

    /**
     * Close all the passage windows.
     */
    public void doCloseAll()
    {
        Iterator it = getDesktop().iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            getDesktop().removeBibleViewPane(view);
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
                Reporter.informUser(getDesktop().getJFrame(), Msg.NO_PASSAGE);
                return;
            }

            view.save();
        }
        catch (IOException ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
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
                Reporter.informUser(getDesktop().getJFrame(), Msg.NO_PASSAGE);
                return;
            }

            view.saveAs();
        }
        catch (IOException ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
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
            Reporter.informUser(getDesktop().getJFrame(), Msg.NO_PASSAGE);
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
                Reporter.informUser(getDesktop().getJFrame(), ex);
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
        BookDataDisplay da = getDesktop().getDisplayArea();
        da.copy();
    }

    /**
     * View the Tabbed Document Interface (TDI) interface.
     */
    public void doTabMode()
    {
        getDesktop().setLayoutType(Desktop.LAYOUT_TYPE_TDI);
    }

    /**
     * View the Multiple Document/Window Interface (MDI) interface.
     */
    public void doWindowMode()
    {
        getDesktop().setLayoutType(Desktop.LAYOUT_TYPE_MDI);
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
            BookDataDisplay da = getDesktop().getDisplayArea();
            Key key = da.getKey();

            if (key == null)
            {
                Reporter.informUser(getDesktop().getJFrame(), Msg.SOURCE_MISSING);
            }
            else
            {
                Book book = da.getBook();

                String orig = book.getRawData(key);

                BookData bdata = book.getData(key);
                
                SAXEventProvider osissep = bdata.getSAXEventProvider();
                SAXEventProvider htmlsep = converter.convert(osissep);
                String html = XMLUtil.writeToString(htmlsep);

                SerializingContentHandler osis = new SerializingContentHandler(true);
                osissep.provideSAXEvents(osis);

                ViewSourcePane viewer = new ViewSourcePane(orig, osis.toString(), html);
                viewer.showInFrame(getDesktop().getJFrame());
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(getDesktop().getJFrame(), ex);
        }
    }

    /**
     * Opens the Book installer window (aka a SitesPane)
     */
    public void doBooks()
    {
        getSites().showInDialog(getDesktop().getJFrame());
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
            ConfigEditorFactory.showDialog(desktop.getConfig(), desktop.getJFrame(), configUrl);

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
        JOptionPane.showMessageDialog(getDesktop().getJFrame(), Msg.NO_HELP);
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

        atp.showInDialog(getDesktop().getJFrame());
    }

    // Enumeration of all the keys to known actions
    static final String FILE = "File"; //$NON-NLS-1$
    static final String EDIT = "Edit"; //$NON-NLS-1$
    static final String VIEW = "View"; //$NON-NLS-1$
    static final String TOOLS = "Tools"; //$NON-NLS-1$
    static final String HELP = "Help"; //$NON-NLS-1$
    static final String NEW_WINDOW = "NewWindow"; //$NON-NLS-1$
    static final String OPEN = "Open"; //$NON-NLS-1$
    static final String CLOSE = "Close"; //$NON-NLS-1$
    static final String CLOSE_ALL = "CloseAll"; //$NON-NLS-1$
    static final String SAVE = "Save"; //$NON-NLS-1$
    static final String SAVE_AS = "SaveAs"; //$NON-NLS-1$
    static final String SAVE_ALL = "SaveAll"; //$NON-NLS-1$
    static final String EXIT = "Exit"; //$NON-NLS-1$
    static final String COPY = "Copy"; //$NON-NLS-1$
    static final String TAB_MODE = "TabMode"; //$NON-NLS-1$
    static final String WINDOW_MODE = "WindowMode"; //$NON-NLS-1$
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

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(DesktopActions.class);
}