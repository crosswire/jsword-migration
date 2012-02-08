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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URI;
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
import javax.swing.event.SwingPropertyChangeSupport;

import org.crosswire.bibledesktop.BDMsg;
import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.DisplaySelectEvent;
import org.crosswire.bibledesktop.book.DisplaySelectListener;
import org.crosswire.bibledesktop.book.DisplaySelectPane;
import org.crosswire.bibledesktop.book.MultiBookPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URIEvent;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.bibledesktop.display.basic.SplitBookDataDisplay;
import org.crosswire.bibledesktop.util.ConfigurableSwingConverter;
import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.history.History;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.CWOptionPane;
import org.crosswire.common.swing.CatchingThreadGroup;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.swing.desktop.LayoutPersistence;
import org.crosswire.common.swing.desktop.LayoutType;
import org.crosswire.common.swing.desktop.TDIViewLayout;
import org.crosswire.common.swing.desktop.ToolBar;
import org.crosswire.common.swing.desktop.ViewGenerator;
import org.crosswire.common.swing.desktop.ViewManager;
import org.crosswire.common.swing.desktop.event.ViewEvent;
import org.crosswire.common.swing.desktop.event.ViewEventListener;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.OSType;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.Translations;
import org.crosswire.common.xml.Converter;
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
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * The Desktop is the user's view of BibleDesktop.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author Mark Goodwin [mark at thorubio dot org]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Desktop extends JFrame implements URIEventListener, ViewEventListener, DisplaySelectListener, ViewGenerator {
    // This must be the first static in the program.
    // To ensure this we place it at the top of the class!
    // This will set it as a place to look for overrides for
    // ResourceBundles, properties and other resources
    private static final CWProject PROJECT = CWProject.instance();

    static {
        CWProject.setHome("jsword.home", ".jsword", "JSword");
    }

    /**
     * Central start point.
     * 
     * @param args
     *            The command line arguments
     */
    public static void main(String[] args) {
        try {
            ThreadGroup group = new CatchingThreadGroup("BibleDesktopUIGroup");
            Thread t = new DesktopThread(group);
            t.start();
        } catch (Exception ex) {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            ex.printStackTrace(System.err);
            ExceptionPane.showExceptionDialog(null, ex);
        }
    }

    /**
     * Construct a Desktop.
     */
    public Desktop() {
        // Set the name that is used for Layout Persistence
        setName("Desktop");

        // The first thing that has to be done is to set the locale.
        Translations.instance().setLocale();

        URI predictURI = PROJECT.getWritableURI(SPLASH_PROPS, FileUtil.EXTENSION_PROPERTIES);
        Progress startJob = JobManager.createJob("Startup");
        // TRANSLATOR: Progress label shown on BibleDesktop startup.
        startJob.beginJob(BDMsg.gettext("Startup"), predictURI);

        // Load the configuration. And create the lists of installed books.
        // This has to be done before any GUI components are created
        // This includes code that is invoked by it.
        // This has to be done after setting the locale.
        generateConfig();

        // Make this be the root frame of OptionDialogs
        JOptionPane.setRootFrame(this);

        // Grab errors
        Reporter.grabAWTExecptions(true);

        // Create the Desktop Actions
        desktopActions = new DesktopActions(this);
        actions = desktopActions.getActions();

        // TRANSLATOR: Progress label shown while BibleDesktop
        // creates the GUI components
        startJob.setSectionName(BDMsg.gettext("Generating Components"));
        buildActionMap();
        createComponents();

        // If necessary, make changes to the UI to help with debugging
        debug();

        // TRANSLATOR: Progress label shown while BibleDesktop
        // creates the GUI layout with panes and panels,
        // and creates a few other GUI things
        startJob.setSectionName(BDMsg.gettext("General configuration"));
        createLayout();

        // Listen for book changes so that the Options can be kept current
        BooksListener cbl = new BooksListener() {
            public void bookAdded(BooksEvent ev) {
                generateConfig();
            }

            public void bookRemoved(BooksEvent ev) {
                generateConfig();
            }
        };
        Books.installed().addBooksListener(cbl);

        // Set the left-to-right or right-to-left orientation for this and all
        // sub-components
        GuiUtil.applyDefaultOrientation(this);

        startJob.done();
    }

    /**
     * Sometimes we need to make some changes to debug the GUI.
     */
    private void debug() {
        // this.getContentPane().addContainerListener(new
        // DebugContainerListener());

        // javax.swing.RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);
        // ((javax.swing.JComponent) getContentPane()).setDebugGraphicsOptions(javax.swing.DebugGraphics.LOG_OPTION);
    }

    /**
     * Call all the constructors
     */
    private void createComponents() {
        barStatus = new StatusBar();
        reference = new MultiBookPane();
        sptBooks = new FixedSplitPane(false);

        changeSupport = new SwingPropertyChangeSupport(this);
        views = new ViewManager(this);
        views.setActionFactory(getViewActions(views));
        views.addViewEventListener(this);
        history = new History();
    }

    private ActionFactory getViewActions(ViewManager viewMgr) {
        ActionFactory viewActions = new ActionFactory(viewMgr);

        // TRANSLATOR: This is the label for the view option to show multiple tabs
        CWAction cwAction = viewActions.addAction(ViewManager.TAB_MODE, BDMsg.gettext("Tabbed Mode"));
        // TRANSLATOR: This is the tooltip for the view option to show multiple tabs
        cwAction.setTooltip(BDMsg.gettext("View passages using tabs"));

        // TRANSLATOR: This is the label for the view option to show multiple windows
        cwAction = viewActions.addAction(ViewManager.WINDOW_MODE, BDMsg.gettext("Sub-Window Mode"));
        // TRANSLATOR: This is the tooltip for the view option to show multiple windows
        cwAction.setTooltip(BDMsg.gettext("View passages using sub-windows"));

        // TRANSLATOR: This is the label for the menu and/or button to open a new Bible view
        cwAction = viewActions.addAction(ViewManager.NEW_TAB, BDMsg.gettext("New Bible View"));
        // TRANSLATOR: This is the tooltip for the view option to open a new Bible view
        cwAction.setTooltip(BDMsg.gettext("Open a new Bible View"));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/New16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/New24.gif");
        cwAction.setAccelerator("N,ctrl");

        // TRANSLATOR: This is the label for the menu and/or button to close the current Bible view.
        cwAction = viewActions.addAction(ViewManager.CLOSE_VIEW, BDMsg.gettext("Close the Current View"));
        cwAction.setAccelerator("0x73,ctrl");

        // TRANSLATOR: This is the label for the menu and/or button to clear the contents of the current Bible view
        cwAction = viewActions.addAction(ViewManager.CLEAR_VIEW, BDMsg.gettext("Clear the Current View"));
        cwAction.setTooltip(BDMsg.gettext("Clear the current view's passages"));

        // TRANSLATOR: This is the label for the menu and/or button to close all Bible views.
        cwAction = viewActions.addAction(ViewManager.CLOSE_ALL_VIEWS, BDMsg.gettext("Close All Views"));
        cwAction.setTooltip(BDMsg.gettext("Close all passages"));

        // TRANSLATOR: This is the label for the menu and/or button to close Bible views other than the current one.
        cwAction = viewActions.addAction(ViewManager.CLOSE_OTHER_VIEWS, BDMsg.gettext("Close Other Views"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to close Bible views other than the current one.
        cwAction.setTooltip(BDMsg.gettext("Close all the other passages."));
        return viewActions;
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void createLayout() {
        addWindowListener(new WindowAdapter() {
            /* (non-Javadoc)
             * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosed(WindowEvent ev) {
                actions.findAction("Exit").actionPerformed(new ActionEvent(this, 0, EMPTY_STRING));
            }
        });

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        TDIViewLayout tdi = (TDIViewLayout) LayoutType.TDI.getLayout();
        tdi.addPopup(createPopupMenu());

        reference.addURIEventListener(this);

        sptBooks.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptBooks.setRightComponent(reference);
        sptBooks.setLeftComponent(views.getDesktop());
        sptBooks.setResizeWeight(0.8D);
        sptBooks.setOpaque(true);
        sptBooks.setBorder(null);

        // The toolbar needs to be in the outermost container, on the border
        // And the only other item in that container can be CENTER
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        ToolBar toolbar = createToolBar();
        contentPane.add(toolbar, BorderLayout.NORTH);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
        mainPanel.add(sptBooks, BorderLayout.CENTER);

        // Put everything else in its own panel
        corePanel = new JPanel(new BorderLayout());
        corePanel.add(mainPanel, BorderLayout.CENTER);
        corePanel.add(barStatus, BorderLayout.SOUTH);
        contentPane.add(corePanel, BorderLayout.CENTER);
        setJMenuBar(createMenuBar(toolbar));

        setIconImage(ICON_APP.getImage());
        setEnabled(true);
        setTitle(BDMsg.getApplicationTitle());
    }

    private JMenuBar createMenuBar(ToolBar toolbar) {
        JMenuBar barMenu = new JMenuBar();
        barMenu.add(createFileMenu());
        barMenu.add(createEditMenu());
        barMenu.add(createViewMenu(toolbar));
        barMenu.add(createNavigateMenu());
        barMenu.add(createToolsMenu());
        barMenu.add(createHelpMenu());
        return barMenu;
    }

    private void buildActionMap() {
        // File menu and it's items
        // TRANSLATOR: This is the label of the top level "File" menu
        CWAction cwAction = actions.addAction("File", BDMsg.gettext("File"));

        // TRANSLATOR: This is the label for the menu and/or button to open a save passage list.
        cwAction = actions.addAction("Open", BDMsg.gettext("Open"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to open a save passage list.
        cwAction.setTooltip(BDMsg.gettext("Open a saved passage."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/Open16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/Open24.gif");
        cwAction.setAccelerator("O,ctrl");

        // TRANSLATOR: This is the label for the menu and/or button to save a passage list for the current Bible view.
        cwAction = actions.addAction("Save", BDMsg.gettext("Save"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to save a passage list for the current Bible view.
        cwAction.setTooltip(BDMsg.gettext("Save the current passage."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/Save16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/Save24.gif");
        cwAction.setAccelerator("S,ctrl");

        // TRANSLATOR: This is the label for the menu and/or button to save a passage list under a different name.
        cwAction = actions.addAction("SaveAs", BDMsg.gettext("Save As"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to save a passage list under a different name.
        cwAction.setTooltip(BDMsg.gettext("Save the current passage under a different name."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/SaveAs16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/SaveAs24.gif");
        cwAction.setAccelerator("A,ctrl,shift");

        // TRANSLATOR: This is the label for the menu and/or button to save a passage list for each Bible view.
        cwAction = actions.addAction("SaveAll", BDMsg.gettext("Save All"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to save a passage list for each Bible view.
        cwAction.setTooltip(BDMsg.gettext("Save all passages."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/SaveAll16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/SaveAll24.gif");
        cwAction.setAccelerator("S,ctrl,shift");

        // TRANSLATOR: This is the label for the menu and/or button to exit Bible Desktop.
        cwAction = actions.addAction("Exit", BDMsg.gettext("Exit"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to exit Bible Desktop.
        cwAction.setTooltip(BDMsg.gettext("Exit the Desktop application."));
        cwAction.setAccelerator("0x73,alt");

        // Edit menu and it's items
        // TRANSLATOR: This is the label of the top level "Edit" menu
        cwAction = actions.addAction("Edit",  BDMsg.gettext("Edit"));

        // TRANSLATOR: This is the label for the standard Cut menu and/or button item
//      cwAction = actions.addAction("Cut", UserMsg.gettext("Cut"));
//      // TRANSLATOR: This is the tooltip for the standard Cut menu and/or button item
//      cwAction.setTooltip(UserMsg.gettext("Cut the selection."));
//      cwAction.setSmallIcon("toolbarButtonGraphics/general/Cut16.gif");
//      cwAction.setLargeIcon("toolbarButtonGraphics/general/Cut24.gif");
//      cwAction.setAccelerator("X,ctrl");

        // TRANSLATOR: This is the label for the standard Copy menu and/or button item
        cwAction = actions.addAction("Copy", BDMsg.gettext("Copy"));
        // TRANSLATOR: This is the tooltip for the standard Copy menu and/or button item
        cwAction.setTooltip(BDMsg.gettext("Copy the selection."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/Copy16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/Copy24.gif");
        cwAction.setAccelerator("C,ctrl");

        // TRANSLATOR: This is the label for the standard Paste menu and/or button item
//      cwAction = actions.addAction("Paste", UserMsg.gettext("Paste"));
//      // TRANSLATOR: This is the tooltip for the standard Paste menu and/or button item
//      cwAction.setTooltip(UserMsg.gettext("Paste the selection."));
//      cwAction.setSmallIcon("toolbarButtonGraphics/general/Paste16.gif");
//      cwAction.setLargeIcon("toolbarButtonGraphics/general/Paste24.gif");
//      cwAction.setAccelerator("V,ctrl");

        // Navigate menu and it's items
        // TRANSLATOR: This is the label of the top level "Navigate" menu
        cwAction = actions.addAction("Navigate", BDMsg.gettext("Navigate"));

        // TRANSLATOR: This is the label for the menu and/or button to navigate to a prior Bible View content
        cwAction = actions.addAction("Back", BDMsg.gettext("Back"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to navigate to a prior Bible View content
        cwAction.setTooltip(BDMsg.gettext("Go back to previous passage."));
        cwAction.setSmallIcon("toolbarButtonGraphics/navigation/Back16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/navigation/Back24.gif");

        // TRANSLATOR: This is the label for the menu and/or button to navigate to a next Bible View content
        cwAction = actions.addAction("Forward", BDMsg.gettext("Forward"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to navigate to a next Bible View content
        cwAction.setTooltip(BDMsg.gettext("Go forward to next passage."));
        cwAction.setSmallIcon("toolbarButtonGraphics/navigation/Forward16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/navigation/Forward24.gif");

        // Verse sub-menu and it's items
        // TRANSLATOR: This is the label of the "Verse Numbers" sub-menu
        cwAction = actions.addAction("Verse", BDMsg.gettext("Verse Numbers"));
        // TRANSLATOR: This is the tooltip of the "Verse Numbers" sub-menu
        cwAction.setTooltip(BDMsg.gettext("Set the style for verse numbers"));
        // TRANSLATOR: This is the label for the radio button to show verse numbers
        cwAction = actions.addAction(XSLTProperty.VERSE_NUMBERS.getName(), BDMsg.gettext("Show Verse Numbers"));
        // TRANSLATOR: This is the label for the radio button to show chapter and verse numbers
        cwAction = actions.addAction(XSLTProperty.CV.getName(), BDMsg.gettext("Show Chapter and Verse Numbers"));
        // TRANSLATOR: This is the label for the radio button to show book name with chapter and verse numbers
        cwAction = actions.addAction(XSLTProperty.BCV.getName(), BDMsg.gettext("Show Book, Chapter and Verse Numbers"));
        // TRANSLATOR: This is the label for the radio button to hide verse numbers 
        cwAction = actions.addAction(XSLTProperty.NO_VERSE_NUMBERS.getName(), BDMsg.gettext("Hide Verse Numbers"));

        // View menu and it's items
        // TRANSLATOR: This is the label of the top level "View" menu
        cwAction = actions.addAction("View", BDMsg.gettext("View"));
        // TRANSLATOR: This is the label for the checkbox to toggle between showing tiny and large verse numbers.
        cwAction = actions.addAction(XSLTProperty.TINY_VERSE_NUMBERS.getName(), BDMsg.gettext("Use Small Verse Numbers"));

        // TRANSLATOR: This is the label for the checkbox to start each verse on a new line
        cwAction = actions.addAction(XSLTProperty.START_VERSE_ON_NEWLINE.getName(), BDMsg.gettext("Start Verses on Separate Lines"));
        // TRANSLATOR: This is the tooltip for the checkbox to start each verse on a new line
        cwAction.setTooltip(BDMsg.gettext("Start each verses on a new line"));

        // TRANSLATOR: This is the label for the checkbox to show/hide differences between parallel Bibles
        cwAction = actions.addAction("CompareToggle", BDMsg.gettext("Show Differences"));
        // TRANSLATOR: This is the tooltip for the checkbox to show/hide differences between parallel Bibles
        cwAction.setTooltip(BDMsg.gettext("Toggle display of differences between different Bibles"));

        // TRANSLATOR: This is the label for the checkbox to show/hide headings
        cwAction = actions.addAction(XSLTProperty.HEADINGS.getName(), BDMsg.gettext("Show Headings"));
        // TRANSLATOR: This is the label for the checkbox to show/hide notes
        cwAction = actions.addAction(XSLTProperty.NOTES.getName(), BDMsg.gettext("Show Study Notes"));
        // TRANSLATOR: This is the label for the checkbox to show/hide cross references
        cwAction = actions.addAction(XSLTProperty.XREF.getName(), BDMsg.gettext("Use Cross Reference Linkings"));
        // TRANSLATOR: This is the label for the checkbox to show/hide Strong's Numbers
        cwAction = actions.addAction(XSLTProperty.STRONGS_NUMBERS.getName(), BDMsg.gettext("Show Strong's Numbers"));
        // TRANSLATOR: This is the label for the checkbox to show/hide word morphology
        cwAction = actions.addAction(XSLTProperty.MORPH.getName(), BDMsg.gettext("Show Word Morphology"));

        // TRANSLATOR: This is the label for the checkbox to show/hide tooltips
        cwAction = actions.addAction("ToolTipToggle", BDMsg.gettext("Show Tool Tips"));
        // TRANSLATOR: This is the tooltip for the checkbox to show/hide tooltips
        // which, of course, only shows when tooltips are showing :)
        cwAction.setTooltip(BDMsg.gettext("Toggle display of tool tips"));
        cwAction.setAccelerator("T,ctrl");

        // TRANSLATOR: This is the label for the checkbox to show/hide the status area
        cwAction = actions.addAction("StatusToggle", BDMsg.gettext("Show the Status Area"));
        // TRANSLATOR: This is the tooltip for the checkbox to show/hide the status area
        cwAction.setTooltip(BDMsg.gettext("Toggle display of the status area"));

        // TRANSLATOR: This is the label for the checkbox to show/hide the passage side bar
        cwAction = actions.addAction("SidebarToggle", BDMsg.gettext("Show the Passage Sidebar"));
        // TRANSLATOR: This is the tooltip for the checkbox to show/hide the passage side bar
        cwAction.setTooltip(BDMsg.gettext("Toggle display of the Passage Sidebar"));
        cwAction.setAccelerator("B,ctrl");

        // TRANSLATOR: This is the label for the menu and/or button to show the view source dialog
        cwAction = actions.addAction("ViewSource", BDMsg.gettext("View Source"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to show the view source dialog
        cwAction.setTooltip(BDMsg.gettext("View the HTML and OSIS source to the current window"));
        cwAction.setAccelerator("U,ctrl");

        // Tools menu and it's items
        // TRANSLATOR: This is the label of the top level "Tools" menu
        cwAction = actions.addAction("Tools", BDMsg.gettext("Tools"));

        // TRANSLATOR: This is the label for the menu and/or button to show the book installer dialog
        cwAction = actions.addAction("Books", BDMsg.gettext("Books"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to show the book installer dialog
        cwAction.setTooltip(BDMsg.gettext("Display/Install Books"));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/Import16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/Import24.gif");

        // TRANSLATOR: This is the label for the menu and/or button to show the Options/Preferences dialog
        // Note: on a Mac, it is called Preferences located on the "Bible Desktop" menu
        // and the operating system provides the translation.
        cwAction = actions.addAction("Options", BDMsg.gettext("Options"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to show the Options/Preferences dialog
        cwAction.setTooltip(BDMsg.gettext("Alter system settings."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/Properties16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/Properties24.gif");

        // Help menu and it's items
        // TRANSLATOR: This is the label of the top level "Help" menu
        cwAction = actions.addAction("Help", BDMsg.gettext("Help"));

        // TRANSLATOR: This is the label for the menu and/or button to show the Help dialog
        cwAction = actions.addAction("Contents", BDMsg.gettext("Contents"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to show the Help dialog
        cwAction.setTooltip(BDMsg.gettext("Help file contents."));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/Help16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/Help24.gif");
        cwAction.setAccelerator("0x70");

        // TRANSLATOR: This is the label for the menu and/or button to show the About dialog
        // Note: on a Mac, it is located on the "Bible Desktop" menu
        // and the operating system provides the translation.
        cwAction = actions.addAction("About", BDMsg.gettext("About"));
        // TRANSLATOR: This is the tooltip for the menu and/or button to show the About dialog
        cwAction.setTooltip(BDMsg.gettext("Information about Bible Desktop"));
        cwAction.setSmallIcon("toolbarButtonGraphics/general/About16.gif");
        cwAction.setLargeIcon("toolbarButtonGraphics/general/About24.gif");
    }

    /**
     * Create the file menu
     * 
     * @return the file menu
     */
    private JMenu createFileMenu() {
        JMenu menu = new JMenu(actions.findAction("File"));
        menu.setToolTipText(null);

        menu.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLOSE_VIEW)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLEAR_VIEW)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLOSE_OTHER_VIEWS)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLOSE_ALL_VIEWS)).addMouseListener(barStatus);

        menu.addSeparator();
        menu.add(actions.findAction("Open")).addMouseListener(barStatus);
        menu.add(actions.findAction("Save")).addMouseListener(barStatus);
        menu.add(actions.findAction("SaveAs")).addMouseListener(barStatus);
        menu.add(actions.findAction("SaveAll")).addMouseListener(barStatus);

        // Mac OSX provides "Quit" on the Program menu
        if (!desktopActions.isOSXRegistered()) {
            menu.addSeparator();
            menu.add(actions.findAction("Exit")).addMouseListener(barStatus);
        }

        return menu;
    }

    private JMenu createEditMenu() {
        JMenu menu = new JMenu(actions.findAction("Edit"));
        menu.setToolTipText(null);
//      menuEdit.add(actions.findAction("Cut")).addMouseListener(barStatus);
        menu.add(actions.findAction("Copy")).addMouseListener(barStatus);
//      menuEdit.add(actions.findAction("Paste")).addMouseListener(barStatus);

        return menu;
    }

    private JMenu createNavigateMenu() {
        JMenu menu = new JMenu(actions.findAction("Navigate"));
        menu.setToolTipText(null);

        menu.add(actions.findAction("Back")).addMouseListener(barStatus);
        menu.add(actions.findAction("Forward")).addMouseListener(barStatus);

        return menu;
    }

    private JRadioButtonMenuItem createRadioButton(ButtonGroup group, XSLTProperty prop) {
        JRadioButtonMenuItem radio = new JRadioButtonMenuItem(actions.findAction(prop.getName()));
        group.add(radio);
        radio.setSelected(prop.getDefaultState());
        return radio;
    }

    private JCheckBoxMenuItem createCheckbox(XSLTProperty prop) {
        JCheckBoxMenuItem toggle = new JCheckBoxMenuItem(actions.findAction(prop.getName()));
        toggle.setSelected(prop.getDefaultState());
        return toggle;
    }

    private JMenu createVerseMenu() {
        JMenu menu = new JMenu(actions.findAction("Verse"));
        ButtonGroup group = new ButtonGroup();
        menu.add(createRadioButton(group, XSLTProperty.VERSE_NUMBERS)).addMouseListener(barStatus);
        menu.add(createRadioButton(group, XSLTProperty.CV)).addMouseListener(barStatus);
        menu.add(createRadioButton(group, XSLTProperty.BCV)).addMouseListener(barStatus);
        menu.add(createRadioButton(group, XSLTProperty.NO_VERSE_NUMBERS)).addMouseListener(barStatus);
        return menu;
    }

    /**
     * Create the view menu.
     * 
     * @return the view menu.
     */
    private JMenu createViewMenu(ToolBar toolbar) {
        JMenu menu = new JMenu(actions.findAction("View"));
        menu.add(createCheckbox(XSLTProperty.TINY_VERSE_NUMBERS)).addMouseListener(barStatus);
        menu.add(createCheckbox(XSLTProperty.START_VERSE_ON_NEWLINE)).addMouseListener(barStatus);
        menu.add(createVerseMenu());

        menu.addSeparator();

        menu.add(new JCheckBoxMenuItem(actions.findAction("CompareToggle"))).addMouseListener(barStatus);
        menu.add(createCheckbox(XSLTProperty.HEADINGS)).addMouseListener(barStatus);
        menu.add(createCheckbox(XSLTProperty.NOTES)).addMouseListener(barStatus);
        menu.add(createCheckbox(XSLTProperty.XREF)).addMouseListener(barStatus);
        menu.add(createCheckbox(XSLTProperty.STRONGS_NUMBERS)).addMouseListener(barStatus);
        menu.add(createCheckbox(XSLTProperty.MORPH)).addMouseListener(barStatus);

        menu.addSeparator();

        menu.add(views.getTdiView()).addMouseListener(barStatus);
        menu.add(views.getMdiView()).addMouseListener(barStatus);

        menu.addSeparator();

        menu.add(toolbar.getShowToggle()).addMouseListener(barStatus);
        menu.add(toolbar.getTextToggle()).addMouseListener(barStatus);
        menu.add(toolbar.getIconSizeToggle()).addMouseListener(barStatus);

        JCheckBoxMenuItem toggle = new JCheckBoxMenuItem(actions.findAction("ToolTipToggle"));
        toggle.setSelected(true);
        menu.add(toggle).addMouseListener(barStatus);

        toggle = new JCheckBoxMenuItem(actions.findAction("StatusToggle"));
        toggle.setSelected(true);
        menu.add(toggle).addMouseListener(barStatus);

        sidebarToggle = new JCheckBoxMenuItem(actions.findAction("SidebarToggle"));
        sidebarToggle.setSelected(isSidebarShowing());
        menu.add(sidebarToggle).addMouseListener(barStatus);

        if (viewSourceShowing) {
            menu.addSeparator();

            menu.add(actions.findAction("ViewSource")).addMouseListener(barStatus);
            menu.setToolTipText(null);
        }

        return menu;
    }

    private JMenu createToolsMenu() {
        JMenu menu = new JMenu(actions.findAction("Tools"));
        menu.setToolTipText(null);

        menu.add(actions.findAction("Books")).addMouseListener(barStatus);

        // Mac OSX provides "Preferences" on the Program menu
        if (!desktopActions.isOSXRegistered()) {
            menu.add(actions.findAction("Options")).addMouseListener(barStatus);
        }

        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JMenu(actions.findAction("Help"));
        menu.setToolTipText(null);
        menu.add(actions.findAction("Contents")).addMouseListener(barStatus);

        // Mac provides the About action on the Program menu.
        if (!desktopActions.isOSXRegistered()) {
            menu.addSeparator();
            menu.add(actions.findAction("About")).addMouseListener(barStatus);
        }

        return menu;
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLOSE_VIEW)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLEAR_VIEW)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLOSE_OTHER_VIEWS)).addMouseListener(barStatus);
        menu.add(views.getContextAction(ViewManager.CLOSE_ALL_VIEWS)).addMouseListener(barStatus);

        menu.addSeparator();

        menu.add(actions.findAction("Open")).addMouseListener(barStatus);
        menu.add(actions.findAction("Save")).addMouseListener(barStatus);
        menu.add(actions.findAction("SaveAs")).addMouseListener(barStatus);
        menu.add(actions.findAction("SaveAll")).addMouseListener(barStatus);

        return menu;
    }

    private ToolBar createToolBar() {
        ToolBar menu = new ToolBar(this);

        menu.add(views.getContextAction(ViewManager.NEW_TAB)).addMouseListener(barStatus);
        menu.add(actions.findAction("Open")).addMouseListener(barStatus);
        menu.add(actions.findAction("Save")).addMouseListener(barStatus);
        menu.addSeparator();
//      toolbar.add(actions.findAction("Cut").addMouseListener(barStatus);
        menu.add(actions.findAction("Copy")).addMouseListener(barStatus);
//      toolbar.add(actions.findAction("Paste")).addMouseListener(barStatus);
        menu.addSeparator();
        menu.add(actions.findAction("Back")).addMouseListener(barStatus);
        menu.add(actions.findAction("Forward")).addMouseListener(barStatus);
        menu.addSeparator();
        menu.add(actions.findAction("Contents")).addMouseListener(barStatus);

        // Mac OSX provides "About" on the Program menu
        if (!desktopActions.isOSXRegistered()) {
            menu.add(actions.findAction("About")).addMouseListener(barStatus);
        }

        menu.setRollover(true);

        return menu;
    }

    /**
     * Get the size of the content panel and make that the preferred size.
     */
    public void establishPreferredSize() {
        Container contentPane = getContentPane();
        if (contentPane instanceof JComponent) {
            ((JComponent) contentPane).setPreferredSize(contentPane.getSize());
            //log.warn("The size of the contentpane is: " + contentPane.getSize());
        }
    }

    /**
     * @return Returns the views.
     */
    public ViewManager getViews() {
        return views;
    }

    public Component createView() {
        boolean show = sidebarToggle == null ? isSidebarShowing() : sidebarToggle.isSelected();
        BibleViewPane view = new BibleViewPane(show);
        BookDataDisplay display = view.getPassagePane().getBookDataDisplay();
        display.addURIEventListener(this);
        display.addURIEventListener(barStatus);
        display.setCompareBooks(compareShowing);
        changeSupport.addPropertyChangeListener(BookDataDisplay.COMPARE_BOOKS, display);
        DisplaySelectPane dsp = view.getSelectPane();
        dsp.addCommandListener(this);
        return view;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.ViewEventListener#viewRemoved(org.crosswire.common.swing.desktop.ViewEvent)
     */
    public void viewRemoved(ViewEvent event) {
        BibleViewPane view = (BibleViewPane) event.getSource();
        BookDataDisplay display = view.getPassagePane().getBookDataDisplay();
        display.removeURIEventListener(this);
        display.removeURIEventListener(barStatus);
        changeSupport.removePropertyChangeListener(BookDataDisplay.COMPARE_BOOKS, display);
        DisplaySelectPane dsp = view.getSelectPane();
        dsp.removeCommandListener(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#bookChosen(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void bookChosen(DisplaySelectEvent ev) {
        // Do nothing
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#passageSelected(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void passageSelected(DisplaySelectEvent ev) {
        Key key = ev.getKey();
        if (key != null && !key.isEmpty()) {
            // add the string because keys are heavy weights
            history.add(key.getName());
        }
    }

    public void selectHistory(int i) {
        Object obj = history.go(i);
        if (obj != null) {
            activateURI(new URIEvent(this, Desktop.BIBLE_PROTOCOL, (String) obj));
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#activateURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void activateURI(URIEvent ev) {
        barStatus.activateURI(ev);
        String protocol = ev.getScheme();
        String data = ev.getURI();

        try {
            if (protocol.equals(BIBLE_PROTOCOL)) {
                // Does a view contain the passage already?
                BibleViewPane clearView = null;
                for (Component comp : views) {
                    BibleViewPane view = (BibleViewPane) comp;
                    if (view.isClear()) {
                        clearView = view;
                        continue;
                    }
                    Book book = view.getSelectPane().getFirstBook();
                    if (book != null) {
                        Key key = book.getKey(data);
                        String dataPassage = key.getName();
                        if (view.getTitle().equals(dataPassage)) {
                            // We found the passage so just select it
                            views.select(view);
                            return;
                        }
                    }
                }

                // If we didn't find a view and BibleViews are reused,
                // then pretend that the selected view is clear.
                if (isBibleViewReused()) {
                    BibleViewPane view = (BibleViewPane) views.getSelected();
                    if (view != null) {
                        clearView = view;
                    }
                }

                // Do we have an empty view we can use?
                if (clearView != null) {
                    Book book = clearView.getSelectPane().getFirstBook();
                    if (book != null) {
                        Key key = book.getKey(data);
                        // force it to be a clear view, if it is not really.
                        clearView.setKey(book.createEmptyKeyList());
                        clearView.setKey(key);
                        views.select(clearView);
                    }
                    return;
                }

                // If we got this far we need to create a view
                // and load it up.
                BibleViewPane view = (BibleViewPane) views.addView();

                Book book = view.getSelectPane().getFirstBook();
                if (book != null) {
                    Key key = book.getKey(data);
                    view.setKey(key);
                }
            } else if (protocol.equals(COMMENTARY_PROTOCOL)) {
                Book book = Defaults.getCommentary();
                if (book != null && Books.installed().getBook(book.getName()) != null) {
                    reference.selectBook(book);
                    Book[] books = reference.getBooks();
                    Key key = books[0].getKey(data);
                    reference.setKey(key);
                }
            } else if (protocol.equals(GREEK_DEF_PROTOCOL)) {
                jump(Defaults.getGreekDefinitions(), data);
            } else if (protocol.equals(HEBREW_DEF_PROTOCOL)) {
                jump(Defaults.getHebrewDefinitions(), data);
            } else if (protocol.equals(GREEK_MORPH_PROTOCOL)) {
                jump(Defaults.getGreekParse(), data);
            } else if (protocol.equals(HEBREW_MORPH_PROTOCOL)) {
                jump(Defaults.getHebrewParse(), data);
            } else if (protocol.equals(DICTIONARY_PROTOCOL)) {
                jump(Defaults.getDictionary(), data);
            } else {
                // TRANSLATOR: Uncommon error condition: JSword has provided a link that is not handled.
                Reporter.informUser(this, new MalformedURLException(BDMsg.gettext("Unknown protocol {0}", protocol)));
            }
        } catch (NoSuchKeyException ex) {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Open the requested book and go to the requested key.
     * 
     * @param book
     *            The book to use
     * @param data
     *            The key to find
     */
    private void jump(Book book, String data) {
        // TODO(DM): If it is not installed, offer to install it.
        if (book != null && Books.installed().getBook(book.getName()) != null) {
            reference.selectBook(book);
            reference.setWord(data);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#enterURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void enterURI(URIEvent ev) {
        // We don't care about enter events
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#leaveURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void leaveURI(URIEvent ev) {
        // We don't care about leave events
    }

    /**
     * Show or hide the status bar.
     * 
     * @param show
     *            boolean
     */
    public void showStatusBar(boolean show) {
        if (show) {
            corePanel.add(barStatus, BorderLayout.SOUTH);
        } else {
            corePanel.remove(barStatus);
        }
        validate();
    }

    /**
     * Are the close buttons enabled?
     * 
     * @param enabled
     *            The enabled state
     */
    public void setCloseEnabled(boolean enabled) {
        views.getContextAction(ViewManager.CLEAR_VIEW).setEnabled(enabled);
        views.getContextAction(ViewManager.CLOSE_OTHER_VIEWS).setEnabled(enabled);
    }

    /**
     * Load the config.xml file
     */
    public final void generateConfig() {
        // Get the list of books for each book type.
        fillChoiceFactory();

        // TRANSLATOR: The window title of BibleDesktop's preference/option dialog.
        config = new Config(BDMsg.gettext("Desktop Options"));
        try {
            Document xmlconfig = XMLUtil.getDocument(CONFIG_KEY);

            Locale defaultLocale = Locale.getDefault();
            ResourceBundle configResources = ResourceBundle.getBundle(CONFIG_KEY, defaultLocale, CWClassLoader.instance(Desktop.class));

            config.add(xmlconfig, configResources);

            try {
                config.setProperties(ResourceUtil.getProperties(DESKTOP_KEY));
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
                ExceptionPane.showExceptionDialog(null, ex);
            }

            config.localToApplication();
            config.addPropertyChangeListener(new PropertyChangeListener() {
                /* (non-Javadoc)
                 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
                 */
                public void propertyChange(PropertyChangeEvent evt) {
                    // When the font changes update all the visible locations
                    // using it.
                    if ("BibleDisplay.ConfigurableFont".equals(evt.getPropertyName()))
                    {
                        BibleViewPane view = (BibleViewPane) getViews().getSelected();
                        SplitBookDataDisplay da = view.getPassagePane();
                        da.getBookDataDisplay().refresh();

                        reference.refresh();
                    }

                    if ("BibleDisplay.MaxPickers".equals(evt.getPropertyName()))
                    {
                        BibleViewPane view = (BibleViewPane) getViews().getSelected();
                        DisplaySelectPane selector = view.getSelectPane();
                        selector.getBiblePicker().enableButtons();
                    }
                }
            });
        } catch (IOException e) {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            e.printStackTrace(System.err);
            ExceptionPane.showExceptionDialog(null, e);
        } catch (JDOMException e) {
            // Ditto
            e.printStackTrace(System.err);
            ExceptionPane.showExceptionDialog(null, e);
        }

    }

    public void checkForBooks() {
        // News users probably wont have any Bibles installed so we give them a
        // hand getting to the installation dialog.
        List<Book> bibles = Books.installed().getBooks(BookFilters.getBibles());
        if (bibles.isEmpty()) {
            // TRANSLATOR: Title of dialog asking the user to install at least one Bible.
            String title = BDMsg.gettext("Install Bibles?");
            StringBuilder msg = new StringBuilder(200);
            // TRANSLATOR: Tell the user that they have no Bibles installed and 
            // give them the option to do it now.
            msg.append(BDMsg.gettext("You have no Bibles installed. Do you wish to install some now?"));
            msg.append("\n");
            // TRANSLATOR: Since they have no Bibles installed, give instructions on how to do it later.
            msg.append("(This is also available from Books in the Tools menu)");
            int reply = CWOptionPane.showConfirmDialog(this, msg, title, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (reply == JOptionPane.OK_OPTION) {
                desktopActions.doBooks();
            }
        }
    }

    /**
     * @param show
     *            Whether to show the KeySidebar at start up.
     */
    public static void setSidebarShowing(boolean show) {
        sidebarShowing = show;
    }

    /**
     * @return Whether to show the KeySidebar at start up.
     */
    public static boolean isSidebarShowing() {
        return sidebarShowing;
    }

    /**
     * @param show
     *            Whether to show the view source in menu at start up.
     */
    public static void setViewSourceShowing(boolean show) {
        viewSourceShowing = show;
    }

    /**
     * @return Whether to show the view source in menu at start up.
     */
    public static boolean isViewSourceShowing() {
        return viewSourceShowing;
    }

    /**
     * @param show
     *            Whether to show differences between versions of the Bible
     */
    public void setCompareShowing(boolean show) {
        boolean old = compareShowing;
        compareShowing = show;
        changeSupport.firePropertyChange(BookDataDisplay.COMPARE_BOOKS, old, compareShowing);
    }

    /**
     * @return Whether to show differences between versions of the Bible
     */
    public boolean isCompareShowing() {
        return compareShowing;
    }

    /**
     * @param reuse
     *            Whether reuse the current BibleView.
     */
    public static void setBibleViewReused(boolean reuse) {
        reuseBibleView = reuse;
    }

    /**
     * @return Whether links use the current BibleView.
     */
    public static boolean isBibleViewReused() {
        return reuseBibleView;
    }

    /**
     * @param override
     *            The path to the CSS that should be used to override.
     */
    public static void setCSSOverride(String override) {
        XSLTProperty.CSS.setState(override);
    }

    /**
     * @return the current override
     */
    public static String getCSSOverride() {
        return XSLTProperty.CSS.getStringState();
    }

    /**
     * Setup the choices so that the options dialog knows what there is to
     * select from.
     */
    /*private*/final void fillChoiceFactory() {
        // Get the list of books for each book type.
        refreshBooks();

        Translations.instance().register();

        // And the array of allowed osis>html converters
        Map<String, Class<Converter>> converters = ConverterFactory.getKnownConverters();
        Set<String> keys = converters.keySet();
        String[] names = keys.toArray(new String[keys.size()]);
        ChoiceFactory.getDataMap().put(CONV_KEY, names);

        // The choice of configurable XSL stylesheets
        ConfigurableSwingConverter cstyle = new ConfigurableSwingConverter();
        String[] cstyles = cstyle.getStyles();
        ChoiceFactory.getDataMap().put(CSWING_KEY, cstyles);
    }

    /**
     * Setup the book choices
     */
    protected final void refreshBooks() {
        // Instantiating Defaults finds all of the installed books.
        // Calling refreshBooks() gets the list of books for each book type.
        Defaults.refreshBooks();

        // Has the number of reference books changed?
        boolean hasDictionaries = Defaults.getDictionary() != null;
        boolean hasCommentaries = Defaults.getCommentary() != null;
        boolean newRefBooks = hasDictionaries || hasCommentaries;
        if (newRefBooks != hasRefBooks) {
            // This method is called during setup
            if (reference != null) {
                if (!newRefBooks) {
                    sptBooks.setDividerLocation(8000);
                } else {
                    int norm = (int) (sptBooks.getMaximumDividerLocation() * 0.8);
                    sptBooks.setDividerLocation(norm);
                }
                // reference.setVisible(newRefBooks != 0);
                // sptBooks.setDividerLocation(0.8D);
            }

            hasRefBooks = newRefBooks;
        }
    }

    /**
     * @return The config set that this application uses to configure itself
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        config = null;
        history = null;
        desktopActions = new DesktopActions(this);
        actions = new ActionFactory(desktopActions);
        buildActionMap();
        views = new ViewManager(this);
        views.addViewEventListener(this);
        is.defaultReadObject();
    }

    /**
     * Helper class to run the application in a thread group and capture errors.
     */
    private static final class DesktopThread extends Thread {
        DesktopThread(ThreadGroup group) {
            super(group, "BibleDesktopUIThread");
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            // The first thing that has to be done is to set the locale.
            Translations.instance().setLocale();

            // These Mac properties give the application a Mac behavior
            if (OSType.MAC.equals(OSType.getOSType())) {
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                System.setProperty("com.apple.mrj.application.apple.menu.about.name", BDMsg.getApplicationTitle());
                System.setProperty("com.apple.mrj.application.live-resize", "true");
            }

            ExceptionPane.setHelpDeskListener(true);
            LookAndFeelUtil.initialize();

            Splash splash = new Splash();
            Desktop desktop = new Desktop();

            // Restore window size, position, and layout if previously opened,
            // otherwise use defaults.
            LayoutPersistence layoutPersistence = LayoutPersistence.instance();
            if (layoutPersistence.isLayoutPersisted(desktop)) {
                layoutPersistence.restoreLayout(desktop);
            } else {
                GuiUtil.defaultDesktopSize(desktop);
                GuiUtil.centerOnScreen(desktop);
            }

            // Now bring up the app and offer to install books if the user has
            // none.
            SwingUtilities.invokeLater(new DesktopRunner(desktop, splash));
        }
    }

    /**
     * Helper class to actually display the application at the right time.
     */
    private static class DesktopRunner implements Runnable {
        /**
         * @param aDesktop
         * @param aSplash
         */
        public DesktopRunner(Desktop aDesktop, Splash aSplash) {
            desktop = aDesktop;
            splash = aSplash;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            splash.close();
            desktop.setVisible(true);
            desktop.establishPreferredSize();
            desktop.pack();
            desktop.checkForBooks();
        }

        private Desktop desktop;
        private Splash splash;
    }

    private boolean hasRefBooks;

    // Strings for the names of property files.
    private static final String SPLASH_PROPS = "splash";

    // Strings for URL protocols/URI schemes
    public static final String BIBLE_PROTOCOL = "bible";
    public static final String DICTIONARY_PROTOCOL = "dict";
    public static final String GREEK_DEF_PROTOCOL = "gdef";
    public static final String HEBREW_DEF_PROTOCOL = "hdef";
    public static final String GREEK_MORPH_PROTOCOL = "gmorph";
    public static final String HEBREW_MORPH_PROTOCOL = "hmorph";
    public static final String COMMENTARY_PROTOCOL = "comment";

    // Empty String
    private static final String EMPTY_STRING = "";

    // Various other strings used as keys
    private static final String CONFIG_KEY = "config";
    private static final String DESKTOP_KEY = "desktop";
    private static final String CONV_KEY = "converters";
    private static final String CSWING_KEY = "cswing-styles";

    /**
     * The configuration engine
     */
    private transient Config config;

    /**
     * Whether to show the Key Sidebar at startup
     */
    private static boolean sidebarShowing;

    /**
     * Whether to show the view source in the menu at startup
     */
    private static boolean viewSourceShowing;

    /**
     * Whether to show differences between versions of the Bible
     */
    private boolean compareShowing;

    /**
     * Whether to current BibleView should be used for links
     */
    private static boolean reuseBibleView = true;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Desktop.class);

    /**
     * The factory for actions that this class works with
     */
    private transient volatile ActionFactory actions;
    /**
     * The DesktopActions is the holder for the actions, merely to keep the size of this file smaller.
     */
    protected transient DesktopActions desktopActions;

    /**
     * The application icon
     */
    private static final ImageIcon ICON_APP = GuiUtil.getIcon("images/BibleDesktop16.png");

    private transient ViewManager views;
    private JPanel corePanel;
    private JCheckBoxMenuItem sidebarToggle;
    private StatusBar barStatus;
    protected MultiBookPane reference;
    private JSplitPane sptBooks;
    private JPanel mainPanel;
    private transient History history;
    private PropertyChangeSupport changeSupport;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977014029116191800L;
}
