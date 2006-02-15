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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.DictionaryPane;
import org.crosswire.bibledesktop.book.DisplaySelectEvent;
import org.crosswire.bibledesktop.book.DisplaySelectListener;
import org.crosswire.bibledesktop.book.DisplaySelectPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URLEvent;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.bibledesktop.journal.BlogClientFrame;
import org.crosswire.bibledesktop.signal.ResizeJournalSignal;
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
import org.werx.framework.bus.BusStart;
import org.werx.framework.bus.ReflectionBus;

/**
 * The Desktop is the user's view of BibleDesktop.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author DM Smith [dmsmith555 at yahoo dot com]
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
                    new BusStart();
                    ExceptionPane.setHelpDeskListener(true);
                    LookAndFeelUtil.initialize();

                    final Splash splash = new Splash();

                    final Desktop desktop = new Desktop();

                    // change the size and location before showing the application.
                    GuiUtil.setSize(desktop, getDefaultSize());
                    GuiUtil.centerWindow(desktop);

                    // Now bring up the app and offer to install books if the user has none.
                    SwingUtilities.invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                splash.close();
                                desktop.setVisible(true);
                                desktop.establishPreferredSize();
                                desktop.pack();
                                desktop.checkForBooks();
                            }
                        });
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

        // Splash screen
        URL predicturl = project.getWritablePropertiesURL(SPLASH_PROPS);
        Job startJob = JobManager.createJob(Msg.STARTUP_TITLE.toString(), predicturl, true);

        // Load the configuration.
        // This has to be done before any gui components are created.
        // (Other than the splash)
        // This includes code that is invoked by it.
        startJob.setProgress(Msg.STARTUP_CONFIG.toString());
        generateConfig();

        // Make this be the root frame of optiondialogs
        JOptionPane.setRootFrame(this);

        // Grab errors
        Reporter.grabAWTExecptions(true);

        // Create the Desktop Actions
        actions = new DesktopActions(this);

        startJob.setProgress(Msg.STARTUP_GENERATE.toString());
        createComponents();

        // Configuration
        startJob.setProgress(Msg.STARTUP_GENERAL_CONFIG.toString());
        // GUI setup
        debug();
        init();

        ReflectionBus.plug(this);

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

        startJob.done();
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
        ReflectionBus.plug(barStatus);

        //barSide = new SidebarPane();
        //barBook = new ReferencedPane();
        reference = new DictionaryPane();
        sptBooks = new FixedSplitPane(false);
        sptBlog = new FixedSplitPane(false);
        blogPanel = BlogClientFrame.getInstance();

        views = new ViewManager(this);
        views.addViewEventListener(this);
        history = new History();
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void init()
    {
        addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                actions.getAction(DesktopActions.EXIT).actionPerformed(new ActionEvent(this, 0, EMPTY_STRING));
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        TDIViewLayout tdi = (TDIViewLayout) LayoutType.TDI.getLayout();
        tdi.addPopup(createPopupMenu());

        //barBook.addHyperlinkListener(this);
        //barSide.addHyperlinkListener(this);
        reference.addURLEventListener(this);

        sptBooks.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptBooks.setRightComponent(reference);
        sptBooks.setLeftComponent(views.getDesktop());
        sptBooks.setResizeWeight(0.8D);
        sptBooks.setOpaque(true);
        sptBooks.setBorder(null);

        sptBlog.setOrientation(JSplitPane.VERTICAL_SPLIT);
        sptBlog.setTopComponent(sptBooks);
        sptBlog.setBottomComponent(blogPanel);
        sptBlog.setResizeWeight(0.8D);
        sptBlog.setOpaque(true);
        sptBlog.setBorder(null);

        // The toolbar needs to be in the outermost container, on the border
        // And the only other item in that container can be CENTER
        JComponent contentPane = (JComponent) getContentPane();
        contentPane.setLayout(new BorderLayout());
        ToolBar toolbar = createToolBar();
        contentPane.add(toolbar, BorderLayout.NORTH);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
        if (webJournalShowing)
        {
            mainPanel.add(sptBlog, BorderLayout.CENTER);
        }
        else
        {
            mainPanel.add(sptBooks, BorderLayout.CENTER);
        }

        // Put everything else in its own panel
        corePanel = new JPanel(new BorderLayout());
        corePanel.add(mainPanel, BorderLayout.CENTER);
        corePanel.add(barStatus, BorderLayout.SOUTH);
        contentPane.add(corePanel, BorderLayout.CENTER);
        setJMenuBar(createMenuBar(toolbar));

        setIconImage(ICON_APP.getImage());
        setEnabled(true);
        setTitle(Msg.getApplicationTitle());
    }
    
    /**
     * Cause the Journal to reset itself to preferred size
     */
    public void channel(ResizeJournalSignal signal)
    {
        sptBlog.resetToPreferredSizes();
    }

    private JMenuBar createMenuBar(ToolBar toolbar)
    {
        JMenuBar barMenu = new JMenuBar();
        barMenu.add(createFileMenu());
        barMenu.add(createEditMenu());
        barMenu.add(createViewMenu(toolbar));
        barMenu.add(createGoMenu());
        barMenu.add(createToolsMenu());
        barMenu.add(createHelpMenu());
        return barMenu;
    }

    private JPopupMenu createPopupMenu()
    {
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
        return popup;
    }

    private ToolBar createToolBar()
    {
        ToolBar toolbar = new ToolBar(this);
        toolbar.setRollover(true);
        toolbar.setFloatable(true);

        toolbar.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        toolbar.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        toolbar.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        toolbar.addSeparator();
        //toolbar.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        toolbar.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        //toolbar.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        toolbar.addSeparator();
        toolbar.add(actions.getAction(DesktopActions.BACK)).addMouseListener(barStatus);
        toolbar.add(actions.getAction(DesktopActions.FORWARD)).addMouseListener(barStatus);
        toolbar.addSeparator();
        //toolbar.add(actions.getAction("Generate")).addMouseListener(barStatus);
        //toolbar.add(actions.getAction("Diff")).addMouseListener(barStatus);
        //toolbar.addSeparator();
        toolbar.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        toolbar.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);

        return toolbar;
    }
    /**
     * Create the file menu
     * @return the file menu
     */
    private JMenu createFileMenu()
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
        return menuFile;
    }

    private JMenu createEditMenu()
    {
        JMenu menuEdit = new JMenu(actions.getAction(DesktopActions.EDIT));
        //menuEdit.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        menuEdit.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        //menuEdit.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        menuEdit.setToolTipText(null);
        return menuEdit;
    }

    private JMenu createGoMenu()
    {
        JMenu menuGo = new JMenu(actions.getAction(DesktopActions.GO));
        menuGo.add(actions.getAction(DesktopActions.BACK)).addMouseListener(barStatus);
        menuGo.add(actions.getAction(DesktopActions.FORWARD)).addMouseListener(barStatus);
        return menuGo;
    }
    /**
     * Create the view menu.
     * @return the view menu.
     */
    private JMenu createViewMenu(ToolBar toolbar)
    {
        JMenu menuView = new JMenu(actions.getAction(DesktopActions.VIEW));
        JCheckBoxMenuItem toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.TINY_VERSE_NUMBERS.getName()));
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.TINY_VERSE_NUMBERS.getName()));
        toggle.setSelected(XSLTProperty.TINY_VERSE_NUMBERS.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(XSLTProperty.START_VERSE_ON_NEWLINE.getName()));
        toggle.setSelected(XSLTProperty.START_VERSE_ON_NEWLINE.getDefault());
        menuView.add(toggle).addMouseListener(barStatus);
        JMenu verseMenu = new JMenu(actions.getAction(DesktopActions.VERSE));
        menuView.add(verseMenu);
        ButtonGroup grpNumbering = new ButtonGroup();
        JRadioButtonMenuItem radio = new JRadioButtonMenuItem(actions.getAction(XSLTProperty.VERSE_NUMBERS.getName()));
        grpNumbering.add(radio);
        radio.setSelected(XSLTProperty.VERSE_NUMBERS.getDefault());
        verseMenu.add(radio).addMouseListener(barStatus);
        radio = new JRadioButtonMenuItem(actions.getAction(XSLTProperty.CV.getName()));
        grpNumbering.add(radio);
        radio.setSelected(XSLTProperty.CV.getDefault());
        verseMenu.add(radio).addMouseListener(barStatus);
        radio = new JRadioButtonMenuItem(actions.getAction(XSLTProperty.BCV.getName()));
        grpNumbering.add(radio);
        radio.setSelected(XSLTProperty.BCV.getDefault());
        verseMenu.add(radio).addMouseListener(barStatus);
        radio = new JRadioButtonMenuItem(actions.getAction(XSLTProperty.NO_VERSE_NUMBERS.getName()));
        grpNumbering.add(radio);
        radio.setSelected(XSLTProperty.NO_VERSE_NUMBERS.getDefault());
        verseMenu.add(radio).addMouseListener(barStatus);

        menuView.addSeparator();
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
        menuView.add(toolbar.getShowToggle()).addMouseListener(barStatus);
        menuView.add(toolbar.getTextToggle()).addMouseListener(barStatus);
        menuView.add(toolbar.getIconSizeToggle()).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.TOOLTIP_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.STATUS_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle).addMouseListener(barStatus);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.JOURNAL_TOGGLE));
        toggle.setSelected(isWebJournalShowing());
        menuView.add(toggle).addMouseListener(barStatus);
        sidebarToggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.SIDEBAR_TOGGLE));
        sidebarToggle.setSelected(isSidebarShowing());
        menuView.add(sidebarToggle).addMouseListener(barStatus);
        menuView.addSeparator();
        menuView.add(actions.getAction(DesktopActions.VIEW_SOURCE)).addMouseListener(barStatus);
        menuView.setToolTipText(null);
        return menuView;
    }

    private JMenu createToolsMenu()
    {
        JMenu menuTools = new JMenu(actions.getAction(DesktopActions.TOOLS));
        //menuTools.add(actions.getAction(DesktopActions.GENERATE)).addMouseListener(barStatus);
        //menuTools.add(actions.getAction(DesktopActions.DIFF)).addMouseListener(barStatus);
        //menuTools.addSeparator();
        menuTools.add(actions.getAction(DesktopActions.BOOKS)).addMouseListener(barStatus);
        menuTools.add(actions.getAction(DesktopActions.OPTIONS)).addMouseListener(barStatus);
        menuTools.setToolTipText(null);
        return menuTools;
    }

    private JMenu createHelpMenu()
    {
        JMenu menuHelp = new JMenu(actions.getAction(DesktopActions.HELP));
        menuHelp.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        menuHelp.addSeparator();
        menuHelp.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);
        menuHelp.setToolTipText(null);
        return menuHelp;
    }
    /**
     * Get the size of the content panel and make that the preferred size.
     */
    public void establishPreferredSize()
    {
        JComponent contentPane = (JComponent) getContentPane();
        contentPane.setPreferredSize(contentPane.getSize());

        log.warn("The size of the contentpane is: " + contentPane.getSize()); //$NON-NLS-1$
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
        boolean show = sidebarToggle == null ? isSidebarShowing() : sidebarToggle.isSelected();
        BibleViewPane view = new BibleViewPane(show);
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

                // If we didn't find a view and BibleViews are reused,
                // then pretend that the selected view is clear.
                if (isBibleViewReused())
                {
                    BibleViewPane view = (BibleViewPane) views.getSelected();
                    if (view != null)
                    {
                        clearView = view;
                    }
                }

                // Do we have an empty view we can use?
                if (clearView != null)
                {
                    Book book = clearView.getSelectPane().getBook();
                    if (book != null)
                    {
                        Key key = book.getKey(data);
                        clearView.setKey(book.createEmptyKeyList()); // force it to be a clear view, if it is not really.
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
     * Show or hide the web journal.
     * @param show boolean
     */
    public void showWebJournal(boolean show)
    {
        if (show)
        {
            mainPanel.remove(sptBooks);
            sptBlog.setTopComponent(sptBooks);
            mainPanel.add(sptBlog, BorderLayout.CENTER);
        }
        else
        {
            mainPanel.remove(sptBlog);
            mainPanel.add(sptBooks, BorderLayout.CENTER);
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

    public void checkForBooks()
    {
        // News users probably wont have any Bibles installed so we give them a
        // hand getting to the installation dialog.
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
     * @param show Whether to show the KeySidebar at start up.
     */
    public static void setSidebarShowing(boolean show)
    {
        sidebarShowing = show;
    }

    /**
     * @return Whether to show the KeySidebar at start up.
     */
    public static boolean isSidebarShowing()
    {
        return sidebarShowing;
    }

    /**
     * @param show Whether to show the web journal at start up.
     */
    public static void setWebJournalShowing(boolean show)
    {
        webJournalShowing = show;
    }

    /**
     * @return Whether to show the web journal at start up.
     */
    public static boolean isWebJournalShowing()
    {
        return webJournalShowing;
    }

    /**
     * @param reuse Whether reuse the current BibleView.
     */
    public static void setBibleViewReused(boolean reuse)
    {
        reuseBibleView = reuse;
    }

    /**
     * @return Whether links use the current BibleView.
     */
    public static boolean isBibleViewReused()
    {
        return reuseBibleView;
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
     * Whether to show the Key Sidebar at startup
     */
    private static boolean sidebarShowing;

    /**
     * Whether to show the web journal at startup
     */
    private static boolean webJournalShowing = true;

    /**
     * Whether to current BibleView should be used for links
     */
    private static boolean reuseBibleView = true;

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
    private BlogClientFrame blogPanel;
    private JSplitPane sptBlog;
    private JCheckBoxMenuItem sidebarToggle;
    private StatusBar barStatus;
    private DictionaryPane reference;
    private JSplitPane sptBooks;
    private JPanel mainPanel;
    private transient History history;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977014029116191800L;
}
