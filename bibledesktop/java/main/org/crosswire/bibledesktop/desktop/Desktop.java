package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.FocusManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.DictionaryPane;
import org.crosswire.bibledesktop.book.TitleChangedEvent;
import org.crosswire.bibledesktop.book.TitleChangedListener;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.util.ConfigurableSwingConverter;
import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.config.Config;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.swing.CatchingThreadGroup;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.LookAndFeelUtil;
import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.readings.ReadingsBookDriver;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;
import org.jdom.Document;
import org.jdom.JDOMException;

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
 * @version $Id$
 */
public class Desktop implements TitleChangedListener, HyperlinkListener
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
                    Desktop desktop = new Desktop();
                    desktop.getJFrame().pack();
                    GuiUtil.centerWindow(desktop.getJFrame());
                    desktop.getJFrame().toFront();
                    desktop.getJFrame().setVisible(true);

                    log.debug(EXITING);
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
        // Other jobs before we create any GUI
        LookAndFeelUtil.tweakLookAndFeel();

        // Calling Project.instance() will set up the project's home directory
        //     ~/.jsword
        // This will set it as a place to look for overrides for
        // ResourceBundles, properties and other resources
        Project project = Project.instance();

        // Create the frame but don't show it so anything that happens has
        // something to attach itself to
        frame = new JFrame();
        JOptionPane.setRootFrame(frame);

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
        generateConfig();

        startJob.setProgress(Msg.STARTUP_GENERATE.toString());
        createComponents();

        // GUI setup
        debug();
        init();

        if (initial.equals(LayoutType.MDI))
        {
            rdoViewMdi.setSelected(true);
        }
        if (initial.equals(LayoutType.TDI))
        {
            rdoViewTdi.setSelected(true);
        }

        // Sort out the current ViewLayout. We need to reset current to be
        // initial because the config system may well have changed initial
        current = initial;
        ensureAvailableBibleViewPane();

        // Configuration
        startJob.setProgress(Msg.STARTUP_GENERAL_CONFIG.toString());
        // NOTE: when we tried dynamic laf update, frame needed special treatment
        //LookAndFeelUtil.addComponentToUpdate(frame);

        // Keep track of the selected BookDataDisplay
        FocusManager.getCurrentManager().addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent ev)
            {
                BookDataDisplay da = recurseDisplayArea();
                if (da != null)
                {
                    last = da;
                }
            }
        });

        // And setup the initial display area, by getting the first
        // BibleViewPane and asking it for a PassagePane.
        // According to the iterator contract hasNext has to be called before next
        Iterator iter = iterateBibleViewPanes();
        iter.hasNext();
        last = ((BibleViewPane) iter.next()).getPassagePane();

        startJob.done();
        splash.close();

        frame.pack();

        // Nearly fill up the screen with BibleDesktop, but no larger than 1280x960
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Allow an extra 50x100 for window decoration.
        if (screenSize.width - maxWidth < 50)
        {
            maxWidth = screenSize.width - 50;
        }

        if (screenSize.height - maxHeight < 100)
        {
            maxHeight = screenSize.height - 100;
        }

        final JComponent contentPane = (JComponent) frame.getContentPane();
        contentPane.setPreferredSize(new Dimension(maxWidth, maxHeight));

        // News users probably wont have any Bibles installedso we give them a
        // hand getting to the installation diallog.
        List bibles = Books.installed().getBookMetaDatas(BookFilters.getBibles());
        if (bibles.size() == 0)
        {
            int reply = JOptionPane.showConfirmDialog(frame, Msg.NO_BIBLES_MESSAGE, Msg.NO_BIBLES_TITLE.toString(), JOptionPane.OK_CANCEL_OPTION,
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
        rdoViewTdi = new JRadioButtonMenuItem(actions.getAction(DesktopActions.TAB_MODE));
        rdoViewMdi = new JRadioButtonMenuItem(actions.getAction(DesktopActions.WINDOW_MODE));

        barStatus = new StatusBar();
        pnlTbar = new ToolBar();
        //barSide = new SidebarPane();
        //barBook = new ReferencedPane();
        reference = new DictionaryPane();
        sptBooks = new JSplitPane();
    }

    /**
     * Initialize the GUI, and display it.
     */
    private void init()
    {
        JMenu menuFile = new JMenu(actions.getAction(DesktopActions.FILE));
        menuFile.add(actions.getAction(DesktopActions.NEW_WINDOW)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.CLOSE)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.CLOSE_ALL)).addMouseListener(barStatus);
        menuFile.addSeparator();
        //menuFile.add(actFilePrint).addMouseListener(barStatus);
        //menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.SAVE_AS)).addMouseListener(barStatus);
        menuFile.add(actions.getAction(DesktopActions.SAVE_ALL)).addMouseListener(barStatus);
        menuFile.addSeparator();
        menuFile.add(actions.getAction(DesktopActions.EXIT)).addMouseListener(barStatus);
        menuFile.setToolTipText(null);

        JMenu menuEdit = new JMenu(actions.getAction(DesktopActions.EDIT));
        //menuEdit.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        menuEdit.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        //menuEdit.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        menuEdit.setToolTipText(null);

        rdoViewTdi.addMouseListener(barStatus);
        rdoViewMdi.addMouseListener(barStatus);
        //chkViewTbar.addMouseListener(barStatus);
        //chkViewTbar.setSelected(viewTool);

        ButtonGroup grpViews = new ButtonGroup();
        grpViews.add(rdoViewMdi);
        grpViews.add(rdoViewTdi);

        JMenu menuView = new JMenu(actions.getAction(DesktopActions.VIEW));
        menuView.add(rdoViewTdi);
        menuView.add(rdoViewMdi);
        //menuView.add(chkViewTbar);
        menuView.addSeparator();
        JCheckBoxMenuItem toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.TOOLBAR_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle);
        menuView.add(new JCheckBoxMenuItem(actions.getAction(DesktopActions.TOOLBAR_TEXT)));
        menuView.add(new JCheckBoxMenuItem(actions.getAction(DesktopActions.TOOLBAR_LARGE)));
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.TOOLTIP_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle);
        toggle = new JCheckBoxMenuItem(actions.getAction(DesktopActions.STATUS_TOGGLE));
        toggle.setSelected(true);
        menuView.add(toggle);
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

        pnlTbar.add(actions.getAction(DesktopActions.NEW_WINDOW)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.OPEN)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.SAVE)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        //pnlTbar.add(actions.getAction(DesktopActions.CUT)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.COPY)).addMouseListener(barStatus);
        //pnlTbar.add(actions.getAction(DesktopActions.PASTE)).addMouseListener(barStatus);
        pnlTbar.addSeparator();
        //pnlTbar.add(actions.getAction("Generate")).addMouseListener(barStatus);
        //pnlTbar.add(actions.getAction("Diff")).addMouseListener(barStatus);
        //pnlTbar.addSeparator();
        pnlTbar.add(actions.getAction(DesktopActions.CONTENTS)).addMouseListener(barStatus);
        pnlTbar.add(actions.getAction(DesktopActions.ABOUT)).addMouseListener(barStatus);

        //barBook.addHyperlinkListener(this);
        //barSide.addHyperlinkListener(this);
        reference.addHyperlinkListener(this);

        sptBooks.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        //sptBooks.setOneTouchExpandable(true);
        sptBooks.setDividerLocation(0.9D);
        //sptBooks.add(barBook, JSplitPane.RIGHT);
        sptBooks.add(reference, JSplitPane.RIGHT);
        sptBooks.add(new JPanel(), JSplitPane.LEFT);
        sptBooks.setResizeWeight(0.9D);
        sptBooks.setDividerSize(7);
        sptBooks.setOpaque(true);
        sptBooks.setBorder(null);

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                actions.getAction(DesktopActions.EXIT).actionPerformed(new ActionEvent(this, 0, EMPTY_STRING));
            }
        });

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // The toolbar needs to be in the outermost container, on the border
        // And the only other item in that container can be CENTER
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(pnlTbar, BorderLayout.NORTH);

        // Put everything else in its own panel
        corePanel = new JPanel(new BorderLayout());
        corePanel.add(barStatus, BorderLayout.SOUTH);
        corePanel.add(sptBooks, BorderLayout.CENTER);
        frame.getContentPane().add(corePanel, BorderLayout.CENTER);
        frame.setJMenuBar(barMenu);

        frame.setEnabled(true);
        frame.setTitle(Msg.getApplicationTitle());
    }

    /**
     * Adds BibleViewPane to the list in this Desktop.
     */
    public void addBibleViewPane(BibleViewPane view)
    {
        view.addTitleChangedListener(this);
        view.addHyperlinkListener(this);
        views.add(view);

        getViewLayout().add(view);

        setLayoutComponent(getViewLayout().getRootComponent());
        getViewLayout().getSelected().adjustFocus();
    }

    /**
     * Removes BibleViewPane from the list in this Desktop.
     */
    public void removeBibleViewPane(BibleViewPane view)
    {
        view.removeTitleChangedListener(this);
        view.removeHyperlinkListener(this);
        views.remove(view);

        getViewLayout().remove(view);

        // Just in case that was the last one
        ensureAvailableBibleViewPane();

        setLayoutComponent(getViewLayout().getRootComponent());
        //getViewLayout().getSelected().adjustFocus(); 
    }

    /**
     * Iterate through a copied list of views
     */
    public Iterator iterateBibleViewPanes()
    {
        Collection copy = new ArrayList(views);
        return copy.iterator();
    }

    /**
     * How many BibleViewPanes are there currently?
     */
    public int countBibleViewPanes()
    {
        return views.size();
    }

    /**
     * Find the selected BibleViewPane.
     * @return BibleViewPane
     */
    public BibleViewPane getSelectedBibleViewPane()
    {
        return getViewLayout().getSelected();
    }

    /**
     * Find the currently highlighted FocusablePart
     */
    public BookDataDisplay getDisplayArea()
    {
        BookDataDisplay da = recurseDisplayArea();
        if (da != null)
        {
            return da;
        }

        return last;
    }

    /**
     * Get the currently selected component and the walk up the component tree
     * trying to find a component that implements BookDataDisplay
     */
    protected BookDataDisplay recurseDisplayArea()
    {
        BookDataDisplay reply = null;

        Component comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        reply = searchForBookDataDisplay(comp);
        if (reply != null)
        {
            return reply;
        }

        comp = KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        reply = searchForBookDataDisplay(comp);
        if (reply != null)
        {
            return reply;
        }

        // So we couldn't get anything from the current context
        return null;
    }

    /**
     * 
     */
    private BookDataDisplay searchForBookDataDisplay(Component comp)
    {
        // So we've got the current component, we now need to walk up the tree
        // to find something that we recognize.
        while (comp != null)
        {
            if (comp instanceof BibleViewPane)
            {
                BibleViewPane bvp = (BibleViewPane) comp;
                return bvp.getPassagePane();
            }

            if (comp instanceof BookDataDisplay)
            {
                return (BookDataDisplay) comp;
            }

            comp = comp.getParent();
        }

        return null;
    }

    /**
     * What is the current layout?
     */
    private final ViewLayout getViewLayout()
    {
        return current.getLayout();
    }

    /**
     * Setup the current view
     */
    public void setLayoutType(LayoutType next)
    {
        // Check this is a change
        if (current.equals(next))
        {
            return;
        }

        // Go through the views removing them from the layout
        Iterator it = iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            getViewLayout().remove(view);
        }

        current = next;

        // Go through the views adding them to the layout SDIViewLayout may well add
        // a view, in which case the view needs to be set already so this must come
        // last.
        it = iterateBibleViewPanes();
        while (it.hasNext())
        {
            BibleViewPane view = (BibleViewPane) it.next();
            getViewLayout().add(view);
        }

        // Allow the current BibleViewPane to set the focus in the right place
        setLayoutComponent(getViewLayout().getRootComponent());
        getViewLayout().getSelected().adjustFocus();
    }

    /**
     * For the use of the various Layout components to update the UI with
     * their Layout component.
     */
    private void setLayoutComponent(Component next)
    {
        Component leftcurr = sptBooks.getLeftComponent();
        if (leftcurr == next)
        {
            return;
        }

        /*
        if (leftcurr != null)
        {
            // Not sure why we have to use a number in place of
            // the JSplitPane.LEFT string constant.
            // And not sure that we need to do this at all.
            //spt_books.remove(1);
        }
        */

        sptBooks.add(next, JSplitPane.LEFT);
    }

    /**
     * If there are no current BibleViewPanes then add one in.
     * final because the ctor calls this method
     */
    private final void ensureAvailableBibleViewPane()
    {
        // If there are no views in the pool, create one
        if (!iterateBibleViewPanes().hasNext())
        {
            BibleViewPane view = new BibleViewPane();
            addBibleViewPane(view);
        }
    }

    /**
     * What is the initial layout state?
     */
    public static int getInitialLayoutType()
    {
        return initial.toInteger();
    }

    /**
     * What should the initial layout state be?
     */
    public static void setInitialLayoutType(int initialLayout)
    {
        Desktop.initial = LayoutType.fromInteger(initialLayout);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        try
        {
            barStatus.hyperlinkUpdate(ev);

            HyperlinkEvent.EventType type = ev.getEventType();
            JTextPane pane = (JTextPane) ev.getSource();

            if (type == HyperlinkEvent.EventType.ACTIVATED)
            {
                String url = ev.getDescription();
                if (url.indexOf(':') == -1)
                {
                    // So there is no protocol, this must be relative to the current
                    // in which case we assume that it is an in page reference.
                    // We ignore the frame case (example code within JEditorPane
                    // JavaDoc).
                    if (url.charAt(0) == '#')
                    {
                        url = url.substring(1);
                    }

                    log.debug(MessageFormat.format(SCROLL_TO_URL, new Object[] { url }));
                    pane.scrollToReference(url);
                }
                else
                {
                    // Fully formed, so we open a new window
                    openHyperlink(ev.getDescription());
                }
            }
            else
            {
                // Must be either an enter or an exit event
                // simulate a link rollover effect, a CSS style not supported in JDK 1.4
                Element textElement = ev.getSourceElement();

                // Focus is needed to decorate Enter and Leave events
                pane.grabFocus();

                int start = textElement.getStartOffset();
                int length = textElement.getEndOffset() - start;

                Style style = pane.addStyle(HYPERLINK_STYLE, null);
                StyleConstants.setUnderline(style, type == HyperlinkEvent.EventType.ENTERED);
                StyledDocument doc = pane.getStyledDocument();
                doc.setCharacterAttributes(start, length, style, false);
            }
        }
        catch (MalformedURLException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Create a new view showing the contents of the given hyperlink
     */
    public void openHyperlink(String url) throws MalformedURLException
    {
        int match = url.indexOf(':');
        if (match == -1)
        {
            throw new MalformedURLException(Msg.BAD_PROTOCOL_URL.toString(url));
        }

        String protocol = url.substring(0, match);
        String data = url.substring(match + 1);
        if (data.startsWith(DOUBLE_SLASH))
        {
            data = data.substring(2);
        }

        try
        {
            if (protocol.equals(BIBLE_PROTOCOL))
            {
                BibleViewPane view = new BibleViewPane();
                addBibleViewPane(view);

                Book book = view.getSelectPane().getBook();
                if (book != null)
                {
                    Key key = book.getKey(data);
                    view.setKey(key);
                }
            }
            else if (protocol.equals(COMMENTARY_PROTOCOL))
            {
                Key key = reference.getBook().getKey(data);
                reference.setKey(key);
            }
            else if (protocol.equals(DICTIONARY_PROTOCOL))
            {
                // TODO: determine the right dictionary and switch to it.
                reference.setWord(data);
            }
            else
            {
                throw new MalformedURLException(Msg.UNKNOWN_PROTOCOL.toString(protocol));
            }
        }
        catch (NoSuchKeyException ex)
        {
            Reporter.informUser(this, ex);
        }
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
        getJFrame().pack(); // cause it to auto resize
    }

    /**
     * Show or hide the tool bar.
     * @param show boolean
     */
    public void showToolBar(boolean show)
    {
        Container contentPane = getJFrame().getContentPane();
        if (show)
        {
            // Honor the previous orientation
            // Don't know how to honor the last location
            if (pnlTbar.getOrientation() == SwingConstants.HORIZONTAL)
            {
                contentPane.add(pnlTbar, BorderLayout.NORTH);
            }
            else
            {
                contentPane.add(pnlTbar, BorderLayout.WEST);
            }
        }
        else
        {
            contentPane.remove(pnlTbar);
        }
        getJFrame().pack(); // cause it to auto resize
    }

    /**
     * Show or hide the tool bar text.
     * @param show boolean
     */
    public void showToolBarText(boolean show)
    {
        pnlTbar.showText(show);
    }

    /**
     * Show large or small tool bar icons.
     * @param show boolean
     */
    public void showToolBarLargeIcons(boolean show)
    {
        pnlTbar.showLargeIcons(show);
    }

    /**
     * Are the close buttons enabled?
     * @param enabled The enabled state
     */
    public void setCloseEnabled(boolean enabled)
    {
        actions.getAction(DesktopActions.CLOSE).setEnabled(enabled);
        actions.getAction(DesktopActions.CLOSE_ALL).setEnabled(enabled);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.TitleChangedListener#titleChanged(org.crosswire.bibledesktop.book.TitleChangedEvent)
     */
    public void titleChanged(TitleChangedEvent ev)
    {
        BibleViewPane bvp = (BibleViewPane) ev.getSource();
        getViewLayout().updateTitle(bvp);
    }

    /**
     * Accessor for the main desktop Frame
     */
    public JFrame getJFrame()
    {
        return frame;
    }

    /**
     * Load the config.xml file
     */
    public void generateConfig()
    {
        fillChoiceFactory();

        config = new Config(Msg.CONFIG_TITLE.toString());
        Document xmlconfig = null;
        try
        {
            xmlconfig = XMLUtil.getDocument(CONFIG_KEY);
        }
        catch (JDOMException e)
        {
            // Something went wrong before we've managed to get on our feet.
            // so we want the best possible shot at working out what failed.
            e.printStackTrace();
            ExceptionPane.showExceptionDialog(null, e);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            ExceptionPane.showExceptionDialog(null, e);
        }

        Locale defaultLocale = Locale.getDefault();
        ResourceBundle configResources = ResourceBundle.getBundle(CONFIG_KEY, defaultLocale, new CWClassLoader(Desktop.class));

        config.add(xmlconfig, configResources);

        try
        {
            config.setProperties(ResourceUtil.getProperties(DESKTOP_KEY));
        }
        catch (MalformedURLException e1)
        {
            e1.printStackTrace();
            ExceptionPane.showExceptionDialog(null, e1);
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            ExceptionPane.showExceptionDialog(null, e1);
        }

        config.localToApplication(true);
    }

    /**
     * @param maxHeight The maxHeight to set.
     */
    public static void setMaxHeight(int maxHeight)
    {
        Desktop.maxHeight = maxHeight;
    }

    /**
     * @return Returns the maxHeight.
     */
    public static int getMaxHeight()
    {
        return maxHeight;
    }

    /**
     * @return Returns the maxWidth.
     */
    public static int getMaxWidth()
    {
        return maxWidth;
    }

    /**
     * @param maxWidth The maxWidth to set.
     */
    public static void setMaxWidth(int maxWidth)
    {
        Desktop.maxWidth = maxWidth;
    }

    /**
     * Setup the choices so that the options dialog knows what there is to
     * select from.
     */
    protected void fillChoiceFactory()
    {
        refreshBooks();

        // Create the array of readings sets
        ChoiceFactory.getDataMap().put(READINGS_KEY, ReadingsBookDriver.getInstalledReadingsSets());

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
    protected void refreshBooks()
    {
        // Create the array of Bibles
        String[] bnames = getFullNameArray(BookFilters.getBibles());
        ChoiceFactory.getDataMap().put(BIBLE_KEY, bnames);

        // Create the array of Commentaries
        String[] cnames = getFullNameArray(BookFilters.getCommentaries());
        ChoiceFactory.getDataMap().put(COMMENTARY_KEY, cnames);

        // Create the array of Dictionaries
        String[] dnames = getFullNameArray(BookFilters.getDictionaries());
        ChoiceFactory.getDataMap().put(DICTIONARY_KEY, dnames);
    }

    /**
     * Convert a filter into an array of names of Books that pass the filter.
     */
    private String[] getFullNameArray(BookFilter filter)
    {
        List bmds = Books.installed().getBookMetaDatas(filter);
        List names = new ArrayList();

        for (Iterator it = bmds.iterator(); it.hasNext();)
        {
            BookMetaData bmd = (BookMetaData) it.next();
            names.add(bmd.getFullName());
        }

        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * @return The config set that this application uses to configure itself
     */
    public Config getConfig()
    {
        return config;
    }

    // Strings for the names of property files.
    private static final String SPLASH_PROPS = "splash"; //$NON-NLS-1$

    // Strings for hyperlinks
    private static final String BIBLE_PROTOCOL = "bible"; //$NON-NLS-1$
    private static final String DICTIONARY_PROTOCOL = "dict"; //$NON-NLS-1$
    private static final String COMMENTARY_PROTOCOL = "comment"; //$NON-NLS-1$
    private static final String HYPERLINK_STYLE = "Hyperlink"; //$NON-NLS-1$
    private static final String DOUBLE_SLASH = "//"; //$NON-NLS-1$
    private static final String SCROLL_TO_URL = "scrolling to: {0}"; //$NON-NLS-1$

    // Strings for debug messages
    private static final String EXITING = "desktop main exiting."; //$NON-NLS-1$

    // Empty String
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    // Various other strings used as keys
    private static final String CONFIG_KEY = "config"; //$NON-NLS-1$
    private static final String DESKTOP_KEY = "desktop"; //$NON-NLS-1$
    private static final String READINGS_KEY = "readings"; //$NON-NLS-1$
    private static final String CONV_KEY = "converters"; //$NON-NLS-1$
    private static final String CSWING_KEY = "cswing-styles"; //$NON-NLS-1$
    private static final String BIBLE_KEY = "bible-names"; //$NON-NLS-1$
    private static final String COMMENTARY_KEY = "commentary-names"; //$NON-NLS-1$
    private static final String DICTIONARY_KEY = "dictionary-names"; //$NON-NLS-1$

    /**
     * The configuration engine
     */
    private Config config;

    /**
     * The initial layout state
     */
    private static LayoutType initial = LayoutType.TDI;

    /**
     * The current way the views are laid out
     */
    private LayoutType current = initial;

    /**
     * <code>maxHeight</code> of the window
     */
    private static int maxHeight = 1024;

    /**
     * <code>maxWidth</code> of the window
     */
    private static int maxWidth = 2048;

    /**
     * The list of BibleViewPanes being viewed in tdi and mdi workspaces
     */
    private List views = new ArrayList();

    /**
     * The last selected BookDataDisplay
     */
    protected BookDataDisplay last;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(Desktop.class);

    protected DesktopActions actions;

    /*
     * GUI components
     */
    private JRadioButtonMenuItem rdoViewTdi;
    private JRadioButtonMenuItem rdoViewMdi;

    private JFrame frame;
    private JPanel corePanel;
    private ToolBar pnlTbar;
    private StatusBar barStatus;
    //private SidebarPane barSide;
    //private ReferencedPane barBook = null;
    private DictionaryPane reference = null;
    private JSplitPane sptBooks;
}
