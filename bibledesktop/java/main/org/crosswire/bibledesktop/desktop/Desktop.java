package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.DictionaryPane;
import org.crosswire.bibledesktop.book.DisplaySelectEvent;
import org.crosswire.bibledesktop.book.DisplaySelectListener;
import org.crosswire.bibledesktop.book.DisplaySelectPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URLEvent;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.bibledesktop.util.ConfigurableSwingConverter;
import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.history.History;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.swing.CatchingThreadGroup;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.swing.desktop.LayoutType;
import org.crosswire.common.swing.desktop.TDIViewLayout;
import org.crosswire.common.swing.desktop.ToolBar;
import org.crosswire.common.swing.desktop.ViewGenerator;
import org.crosswire.common.swing.desktop.ViewManager;
import org.crosswire.common.swing.desktop.event.ViewEvent;
import org.crosswire.common.swing.desktop.event.ViewEventListener;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LucidRuntimeException;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;
import org.jdom.Document;

/**
 * A container for various tools, particularly the BibleGenerator and
 * the Tester. These tools are generally only of use to developers, and
 * not to end users.
 *
 * <p>2 Things to think about, if you change the LaF when you have run
 * some tests already, then the window can grow quite a lot. Also do we
 * want to disable the Exit button if work is going on?</p>
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
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class Desktop extends JFrame implements URLEventListener, ViewEventListener, DisplaySelectListener, ViewGenerator
{
    /**
     * Central start point.
     * @param args The command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            ThreadGroup group = new CatchingThreadGroup("BibleDesktopUIGroup"); //$NON-NLS-1$
            Thread t = new Thread(group, "BibleDesktopUIThread") //$NON-NLS-1$
            {
                public void run()
                {
                    ExceptionPane.setHelpDeskListener(true);
                    LookAndFeelUtil.initialize();

                    Desktop desktop = new Desktop();

                    // change the size and location before showing the application.
                    GuiUtil.setSize(desktop, getDefaultSize());
                    GuiUtil.centerWindow(desktop);

                    // Don't use pack.
                    // It uses preferred dimensions, which are not used here.
                    desktop.toFront();
                    desktop.setVisible(true);
                }
            };
            t.start();
        }
        catch (Exception ex)
        {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            ex.printStackTrace();
            ExceptionPane.showExceptionDialog(null, ex);
        }
    }

    /**
     * Construct a Desktop.
     */
    public Desktop()
    {
        // Calling Project.instance() will set up the project's home directory
        //     ~/.jsword
        // This will set it as a place to look for overrides for
        // ResourceBundles, properties and other resources
        Project project = Project.instance();

        // Load the configuration.
        // This has to be done before any gui components are created are created.
        // This includes code that is invoked by it.
        generateConfig();

        // Listen for book changes so that the Options can be kept current
        BooksListener cbl = new BooksListener()
        {
            public void bookAdded(BooksEvent ev)
            {
                generateConfig();
            }

            public void bookRemoved(BooksEvent ev)
            {
                generateConfig();
            }
        };
        Books.installed().addBooksListener(cbl);

        // Make this be the root frame of optiondialogs
        JOptionPane.setRootFrame(this);

        // Grab errors
        Reporter.grabAWTExecptions(true);

        // Splash screen
        URL predicturl = project.getWritablePropertiesURL(SPLASH_PROPS);
        Splash splash = new Splash();
        Job startJob = JobManager.createJob(Msg.STARTUP_TITLE.toString(), predicturl, true);
        splash.pack();

        // Create the Desktop Actions
        actions = new DesktopActions(this);

        startJob.setProgress(Msg.STARTUP_CONFIG.toString());

        startJob.setProgress(Msg.STARTUP_GENERATE.toString());
        createComponents();

        // GUI setup
        debug();
        init();

        // This is technically overkill, but it does hide the reference pane if
        // there are no reference works
        refreshBooks();

        // Configuration
        startJob.setProgress(Msg.STARTUP_GENERAL_CONFIG.toString());

        startJob.done();
        splash.close();

        // News users probably wont have any Bibles installedso we give them a
        // hand getting to the installation diallog.
        List bibles = Books.installed().getBooks(BookFilters.getBibles());
        if (bibles.size() == 0)
        {
            int reply = JOptionPane.showConfirmDialog(this, Msg.NO_BIBLES_MESSAGE, Msg.NO_BIBLES_TITLE.toString(), JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
            if (reply == JOptionPane.OK_OPTION)
            {
                actions.doBooks();
            }
        }
    }

    /**
     * Sometimes we need to make some changes to debug the GUI.
     */
    private void debug()
    {
        //this.getContentPane().addContainerListener(new DebugContainerListener());

        //javax.swing.RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        //((javax.swing.JComponent) getContentPane()).setDebugGraphicsOptions(javax.swing.DebugGraphics.LOG_OPTION);
    }

    /**
     * Call all the constructors
     */
    private void createComponents()
    {
        barStatus = new StatusBar();
        pnlTbar = new ToolBar(this);
        //barSide = new SidebarPane();
        //barBook = new ReferencedPane();
        reference = new DictionaryPane();
        sptBooks = new FixedSplitPane();
        views = new ViewManager(this);
        views.addViewEventListener(this);
        history = new History();
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void init()
    {
        JMenu menuFile = new JMenu(actions.getAction(DesktopActions.FILE));
        menuFile.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(views.getContextAction(ViewManager.CLOSE_VIEW)).addMouseListener(barStatus);
        menuFile.add(views.getContextAction(ViewManager.CLEAR_VIEW)).addMouseListener(barStatus);
        menuFile.add(views.getContextAction(ViewManager.CLOSE_OTHER_VIEWS)).addMouseListener(barStatus);
        menuFile.add(views.getContextAction(ViewManager.CLOSE_ALL_VIEWS)).addMouseListener(barStatus);
        menuFile.addSeparator();
        //menuFile.add(actFilePrint).addMouseListener(barStatus);
        //menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.SAVE_AS)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.SAVE_ALL)).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.EXIT)).addMouseListener(barStatus);
        menuFile.setToolTipText(null);

        JPopupMenu popup = new JPopupMenu();
        popup.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        popup.add(views.getContextAction(ViewManager.CLOSE_VIEW)).addMouseListener(barStatus);
        popup.add(views.getContextAction(ViewManager.CLEAR_VIEW)).addMouseListener(barStatus);
        popup.add(views.getContextAction(ViewManager.CLOSE_OTHER_VIEWS)).addMouseListener(barStatus);
        popup.add(views.getContextAction(ViewManager.CLOSE_ALL_VIEWS)).addMouseListener(barStatus);
        popup.addSeparator();
        popup.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        popup.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        popup.add(actions.getAction(DesktopActions.SAVE_AS)).addMouseListener(barStatus);
        popup.add(actions.getAction(DesktopActions.SAVE_ALL)).addMouseListener(barStatus);

        TDIViewLayout tdi = (TDIViewLayout) LayoutType.TDI.getLayout();
        tdi.addPopup(popup);

        JMenu menuEdit = new JMenu(actions.getAction(DesktopActions.EDIT));
        //menuEdit.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        menuEdit.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        //menuEdit.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        menuEdit.setToolTipText(null);

        JMenu menuGo = new JMenu(actions.getAction(DesktopActions.GO));
        menuGo.add(actions.getAction(DesktopActions.BACK)).addMouseListener(barStatus);
        menuGo.add(actions.getAction(DesktopActions.FORWARD)).addMouseListener(barStatus);

        JMenu menuView = new JMenu(actions.getAction(DesktopActions.VIEW));
        JCheckBoxMenuItem toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.START_VERSE_ON_NEWLINE.getName()));
        toggle.setSelected(XSLTProperty.START_VERSE_ON_NEWLINE.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.VERSE_NUMBERS.getName()));
        toggle.setSelected(XSLTProperty.VERSE_NUMBERS.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.TINY_VERSE_NUMBERS.getName()));
        toggle.setSelected(XSLTProperty.TINY_VERSE_NUMBERS.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.NOTES.getName()));
        toggle.setSelected(XSLTProperty.NOTES.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.XREF.getName()));
        toggle.setSelected(XSLTProperty.XREF.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.STRONGS_NUMBERS.getName()));
        toggle.setSelected(XSLTProperty.STRONGS_NUMBERS.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.MORPH.getName()));
        toggle.setSelected(XSLTProperty.MORPH.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        menuView.addSeparator();
        menuView.add(views.getTdiView()).addMouseListener(barStatus);
        menuView.add(views.getMdiView()).addMouseListener(barStatus);
        //menuView.add(chkViewTbar);
        menuView.addSeparator();
        menuView.add(pnlTbar.getShowToggle()).addMouseListener(barStatus);
        menuView.add(pnlTbar.getTextToggle()).addMouseListener(barStatus);
        menuView.add(pnlTbar.getIconSizeToggle()).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.TOOLTIP_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.STATUS_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.SIDEBAR_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle).addMouseListener(barStatus);
        menuView.addSeparator();
        menuView.add(actions.getAction(DesktopActions.VIEW_SOURCE)).addMouseListener(barStatus);
        menuView.setToolTipText(null);

        JMenu menuTools = new JMenu(actions.getAction(DesktopActions.TOOLS));
        //menuTools.add(actions.getAction(DesktopActions.GENERATE)).addMouseListener(barStatus);
        //menuTools.add(actions.getAction(DesktopActions.DIFF)).addMouseListener(barStatus);
        //menuTools.addSeparator();
        menuTools.add(actions.getAction(DesktopActions.BOOKS)).addMouseListener(barStatus);
        menuTools.add(actions.getAction(DesktopActions.OPTIONS)).addMouseListener(barStatus);
        menuTools.setToolTipText(null);

        JMenu menuHelp = new JMenu(actions.getAction(DesktopActions.HELP));
        menuHelp.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        menuHelp.addSeparator();
        menuHelp.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);
        menuHelp.setToolTipText(null);

        JMenuBar barMenu = new JMenuBar();
        barMenu.add(menuFile);
        barMenu.add(menuEdit);
        barMenu.add(menuView);
        barMenu.add(menuTools);
        barMenu.add(menuTools);
        barMenu.add(menuHelp);

        pnlTbar.setRollover(true);
        pnlTbar.setFloatable(true);

        pnlTbar.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        //pnlTbar.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        //pnlTbar.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        pnlTbar.add(actions.getAction(DesktopActions.BACK)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.FORWARD)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        //pnlTbar.add(actions.getAction("Generate")).addMouseListener(barStatus);
        //pnlTbar.add(actions.getAction("Diff")).addMouseListener(barStatus);
        //pnlTbar.addSeparator();
        pnlTbar.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);

        //barBook.addHyperlinkListener(this);
        //barSide.addHyperlinkListener(this);
        reference.addURLEventListener(this);

        sptBooks.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptBooks.setRightComponent(reference);
        sptBooks.setLeftComponent(views.getDesktop());
        sptBooks.setResizeWeight(0.8D);
        sptBooks.setDividerSize(7);
        sptBooks.setOpaque(true);
        sptBooks.setBorder(null);

        addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                actions.getAction(DesktopActions.EXIT).actionPerformed(new ActionEvent(this, 0, EMPTY_STRING));
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // The toolbar needs to be in the outermost container, on the border
        // And the only other item in that container can be CENTER
        final JComponent contentPane = (JComponent) getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(pnlTbar, BorderLayout.NORTH);

        // Put everything else in its own panel
        corePanel = new JPanel(new BorderLayout());
        corePanel.add(barStatus, BorderLayout.SOUTH);
        corePanel.add(sptBooks, BorderLayout.CENTER);
        contentPane.add(corePanel, BorderLayout.CENTER);
        setJMenuBar(barMenu);

        setIconImage(ICON_APP.getImage());
        setEnabled(true);
        setTitle(Msg.getApplicationTitle());
    }

    /**
     * @return Returns the views.
     */
    public ViewManager getViews()
    {
        return views;
    }

    public Component createView()
    {
        BibleViewPane view = new BibleViewPane();
        BookDataDisplay display = view.getPassagePane().getBookDataDisplay();
        display.addURLEventListener(this);
        display.addURLEventListener(barStatus);
        DisplaySelectPane dsp = view.getSelectPane();
        dsp.addCommandListener(this);
        return view;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.ViewEventListener#viewRemoved(org.crosswire.common.swing.desktop.ViewEvent)
     */
    public void viewRemoved(ViewEvent event)
    {
        BibleViewPane view = (BibleViewPane) event.getSource();
        BookDataDisplay display = view.getPassagePane().getBookDataDisplay();
        display.removeURLEventListener(this);
        display.removeURLEventListener(barStatus);
        DisplaySelectPane dsp = view.getSelectPane();
        dsp.removeCommandListener(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#bookChosen(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void bookChosen(DisplaySelectEvent ev)
    {
        // Do nothing
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#passageSelected(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void passageSelected(DisplaySelectEvent ev)
    {
        Key key = ev.getKey();
        if (key != null && !key.isEmpty())
        {
            // add the string because keys are heavyweights
            history.add(key.getName());
        }
    }

    public void selectHistory(int i)
    {
        Object obj = history.go(i);
        if (obj != null)
        {
            activateURL(new URLEvent(this, Desktop.BIBLE_PROTOCOL, (String) obj));
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URLEventListener#processURL(org.crosswire.bibledesktop.display.URLEvent)
     */
    public void activateURL(URLEvent ev)
    {
        barStatus.activateURL(ev);
        String protocol = ev.getProtocol();
        String data = ev.getURL();

        try
        {
            if (protocol.equals(BIBLE_PROTOCOL))
            {
                // Does a view contain the passage already?
                Iterator iter = views.iterator();
                BibleViewPane clearView = null;
                while (iter.hasNext())
                {
                    BibleViewPane view = (BibleViewPane) iter.next();
                    if (view.isClear())
                    {
                        clearView = view;
                        continue;
                    }
                    Book book = view.getSelectPane().getBook();
                    if (book != null)
                    {
                        Key key = book.getKey(data);
                        String dataPassage = key.getName();
                        if (view.getTitle().equals(dataPassage))
                        {
                            // We found the passage so just select it
                            views.select(view);
                            return;
                        }
                    }
                }

                // Do we have an empty view we can use?
                if (clearView != null)
                {
                    Book book = clearView.getSelectPane().getBook();
                    if (book != null)
                    {
                        Key key = book.getKey(data);
                        clearView.setKey(key);
                        views.select(clearView);
                    }
                    return;
                }

                // If we got this far we need to create a view
                // and load it up.
                BibleViewPane view = (BibleViewPane) views.addView();

                Book book = view.getSelectPane().getBook();
                if (book != null)
                {
                    Key key = book.getKey(data);
                    view.setKey(key);
                }
            }
            else if (protocol.equals(COMMENTARY_PROTOCOL))
            {
                Book book = Defaults.getCommentary();
                if (book != null && Books.installed().getBook(book.getName()) != null)
                {
                    reference.selectDictionary(book);
                    Key key = reference.getBook().getKey(data);
                    reference.setKey(key);
                }
            }
            else if (protocol.equals(GREEK_DEF_PROTOCOL))
            {
                jump(Defaults.getGreekDefinitions(), data);
            }
            else if (protocol.equals(HEBREW_DEF_PROTOCOL))
            {
                jump(Defaults.getHebrewDefinitions(), data);
            }
            else if (protocol.equals(GREEK_MORPH_PROTOCOL))
            {
                jump(Defaults.getGreekParse(), data);
            }
            else if (protocol.equals(HEBREW_MORPH_PROTOCOL))
            {
                jump(Defaults.getHebrewParse(), data);
            }
            else if (protocol.equals(DICTIONARY_PROTOCOL))
            {
                jump(Defaults.getDictionary(), data);
            }
            else
            {
                Reporter.informUser(this, new MalformedURLException(Msg.UNKNOWN_PROTOCOL.toString(protocol)));
            }
        }
        catch (NoSuchKeyException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Open the requested book and go to the requested key.
     * @param book The book to use
     * @param data The key to find
     */
    private void jump(Book book, String data)
    {
        // TODO(DM): If it is not installed, offer to install it.
        if (book != null && Books.installed().getBook(book.getName()) != null)
        {
            reference.selectDictionary(book);
            reference.setWord(data);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URLEventListener#enterURL(org.crosswire.bibledesktop.display.URLEvent)
     */
    public void enterURL(URLEvent ev)
    {
        // We don't care about enter events
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URLEventListener#leaveURL(org.crosswire.bibledesktop.display.URLEvent)
     */
    public void leaveURL(URLEvent ev)
    {
        // We don't care about leave events
    }

    /**
     * Show or hide the status bar.
     * @param show boolean
     */
    public void showStatusBar(boolean show)
    {
        if (show)
        {
            corePanel.add(barStatus, BorderLayout.SOUTH);
        }
        else
        {
            corePanel.remove(barStatus);
        }
        validate();
    }

    /**
     * Are the close buttons enabled?
     * @param enabled The enabled state
     */
    public void setCloseEnabled(boolean enabled)
    {
        views.getContextAction(ViewManager.CLEAR_VIEW).setEnabled(enabled);
        views.getContextAction(ViewManager.CLOSE_OTHER_VIEWS).setEnabled(enabled);
    }

    /**
     * Load the config.xml file
     */
    public final void generateConfig()
    {
        fillChoiceFactory();
        config = new Config(Msg.CONFIG_TITLE.toString());
        Document xmlconfig = null;
        try
        {
            xmlconfig = XMLUtil.getDocument(CONFIG_KEY);
        }
        catch (Exception ex)
        {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            ex.printStackTrace();
            ExceptionPane.showExceptionDialog(null, ex);
        }

        Locale defaultLocale = Locale.getDefault();
        ResourceBundle configResources = ResourceBundle.getBundle(CONFIG_KEY, defaultLocale, new CWClassLoader(Desktop.class));

        config.add(xmlconfig, configResources);

        try
        {
            config.setProperties(ResourceUtil.getProperties(DESKTOP_KEY));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            ExceptionPane.showExceptionDialog(null, ex);
        }

        URL configUrl = Project.instance().getWritablePropertiesURL("desktop"); //$NON-NLS-1$
        try
        {
            config.localToApplication();
            config.localToPermanent(configUrl);
        }
        catch (IOException ex)
        {
            throw new LucidRuntimeException(Msg.CONFIG_SAVE_FAILED, ex, new Object[] { configUrl });
        }

    }

    /**
     * @param maxHeight The maxHeight to set.
     */
    public static void setMaxHeight(int maxHeight)
    {
        defaultSize.height = maxHeight;
    }

    /**
     * @return Returns the maxHeight.
     */
    public static int getMaxHeight()
    {
        return defaultSize.height;
    }

    /**
     * @return Returns the maxWidth.
     */
    public static int getMaxWidth()
    {
        return defaultSize.width;
    }

    /**
     * @param maxWidth The maxWidth to set.
     */
    public static void setMaxWidth(int maxWidth)
    {
        defaultSize.width = maxWidth;
    }
    /**
     * @return Returns the defaultSize.
     */
    public static Dimension getDefaultSize()
    {
        return defaultSize;
    }

    /**
     * @param newDefaultSize The defaultSize to set.
     */
    public static void setDefaultSize(Dimension newDefaultSize)
    {
        defaultSize = newDefaultSize;
    }

    /**
     * Setup the choices so that the options dialog knows what there is to
     * select from.
     */
    protected void fillChoiceFactory()
    {
        refreshBooks();

        // And the array of allowed osis>html converters
        Map converters = ConverterFactory.getKnownConverters();
        Set keys = converters.keySet();
        String[] names = (String[]) keys.toArray(new String[keys.size()]);
        ChoiceFactory.getDataMap().put(CONV_KEY, names);

        // The choice of configurable XSL stylesheets
        ConfigurableSwingConverter cstyle = new ConfigurableSwingConverter();
        String[] cstyles = cstyle.getStyles();
        ChoiceFactory.getDataMap().put(CSWING_KEY, cstyles);
    }

    /**
     * Setup the book choices
     */
    protected final void refreshBooks()
    {
        Defaults.refreshBooks();

        // Has the number of reference books changed?
        boolean hasDictionaries = Defaults.getDictionary() != null;
        boolean hasCommentaries = Defaults.getCommentary() != null;
        boolean newRefBooks = hasDictionaries || hasCommentaries;
        if (newRefBooks != hasRefBooks)
        {
            // This method is called during setup
            if (reference != null)
            {
                if (!newRefBooks)
                {
                    sptBooks.setDividerLocation(8000);
                }
                else
                {
                    int norm = (int) (sptBooks.getMaximumDividerLocation() * 0.8);
                    sptBooks.setDividerLocation(norm);
                }
                //reference.setVisible(newRefBooks != 0);
                //sptBooks.setDividerLocation(0.8D);
            }

            hasRefBooks = newRefBooks;
        }
    }

    /**
     * @return The config set that this application uses to configure itself
     */
    public Config getConfig()
    {
        return config;
    }

    private boolean hasRefBooks;

    // Strings for the names of property files.
    private static final String SPLASH_PROPS = "splash"; //$NON-NLS-1$

    // Strings for URL protocols
    private static final String BIBLE_PROTOCOL = "bible"; //$NON-NLS-1$
    private static final String DICTIONARY_PROTOCOL = "dict"; //$NON-NLS-1$
    private static final String GREEK_DEF_PROTOCOL = "gdef"; //$NON-NLS-1$
    private static final String HEBREW_DEF_PROTOCOL = "hdef"; //$NON-NLS-1$
    private static final String GREEK_MORPH_PROTOCOL = "gmorph"; //$NON-NLS-1$
    private static final String HEBREW_MORPH_PROTOCOL = "hmorph"; //$NON-NLS-1$
    private static final String COMMENTARY_PROTOCOL = "comment"; //$NON-NLS-1$

    // Empty String
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    // Various other strings used as keys
    private static final String CONFIG_KEY = "config"; //$NON-NLS-1$
    private static final String DESKTOP_KEY = "desktop"; //$NON-NLS-1$
    private static final String CONV_KEY = "converters"; //$NON-NLS-1$
    private static final String CSWING_KEY = "cswing-styles"; //$NON-NLS-1$

    /**
     * The configuration engine
     */
    private transient Config config;

    /**
     * The default dimension for this frame
     */
    private static Dimension defaultSize = new Dimension(1280, 960);

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Desktop.class);

    protected transient DesktopActions actions;

    /**
     * The application icon
     */
    private static final ImageIcon ICON_APP = GuiUtil.getIcon("images/icon16.png"); //$NON-NLS-1$

    private transient ViewManager views;
    private JPanel corePanel;
    private ToolBar pnlTbar;
    private StatusBar barStatus;
    private DictionaryPane reference;
    private JSplitPane sptBooks;
    private transient History history;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977014029116191800L;
}
