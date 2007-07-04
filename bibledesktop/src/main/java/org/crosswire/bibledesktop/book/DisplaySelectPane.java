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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.bibledesktop.book;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.EventListenerList;

import org.crosswire.bibledesktop.book.install.IndexResolver;
import org.crosswire.bibledesktop.passage.KeyChangeEvent;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.QuickHelpDialog;
import org.crosswire.common.swing.desktop.event.TitleChangedEvent;
import org.crosswire.common.swing.desktop.event.TitleChangedListener;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookComparators;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookProvider;
import org.crosswire.jsword.index.IndexStatus;
import org.crosswire.jsword.index.IndexStatusEvent;
import org.crosswire.jsword.index.IndexStatusListener;
import org.crosswire.jsword.index.search.DefaultSearchModifier;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.RocketPassage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * Passage Selection area.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DisplaySelectPane extends JPanel implements KeyChangeListener, BookSelectListener, BookProvider
{
    /**
     * General constructor
     */
    public DisplaySelectPane()
    {
        initialize();
    }

    /**
     * Initialize the GUI
     */
    private void initialize()
    {
        listeners = new EventListenerList();

        advanced = new AdvancedSearchPane();

        title = Msg.UNTITLED.toString(new Integer(base++));

        actions = new ActionFactory(DisplaySelectPane.class, this);

        isl = new IndexStatusListener()
        {
            public void statusChanged(IndexStatusEvent ev)
            {
                enableComponents();
            }
        };

        // search() and version() rely on this returning only Books indexed by verses
        biblePicker = new ParallelBookPicker(BookFilters.getBibles(), BookComparators.getInitialComparator());
        biblePicker.addBookListener(this);
        selected = biblePicker.getBooks();
        if (selected.length > 0)
        {
            selected[0].addIndexStatusListener(isl);
            key = selected[0].createEmptyKeyList();
        }
        else
        {
            // The application has started and there are no installed bibles.
            // Should always get a key from book, unless we need a PassageTally
            // But here we don't have a book yet.
            key = new RocketPassage();
        }

        JComboBox cboBooks = new JComboBox();
        JComboBox cboChaps = new JComboBox();
        BibleComboBoxModelSet quickSet = new BibleComboBoxModelSet(cboBooks, cboChaps, null);
        quickSet.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                BibleComboBoxModelSet set = (BibleComboBoxModelSet) ev.getSource();
                Verse start = set.getVerse();
                int book = start.getBook();
                int chapter = start.getChapter();
                try
                {
                    VerseRange range = new VerseRange(start, new Verse(book, chapter, BibleInfo.versesInChapter(book, chapter)));
                    txtSearch.setText(""); //$NON-NLS-1$
                    txtKey.setText(range.getName());
                    doGoPassage();
                }
                catch (NoSuchVerseException ex)
                {
                    assert false : ex;
                }
            }
        });

        JPanel quickPicker = new JPanel();
        quickPicker.setLayout(new FlowLayout());
        quickPicker.add(cboBooks);
        quickPicker.add(cboChaps);

        JLabel lblBible = actions.createJLabel(BIBLE);
        lblBible.setLabelFor(biblePicker);

        JLabel lblKey = actions.createJLabel(VIEW_LABEL);
        txtKey = new JTextField();
        txtKey.setAction(actions.getAction(PASSAGE_FIELD));
        txtKey.addKeyListener(new KeyAdapter()
        {
            /* (non-Javadoc)
             * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
             */
            /* @Override */
            public void keyTyped(KeyEvent ev)
            {
                if (ev.getKeyChar() == '\n' && ev.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
                {
                    showSelectDialog();
                }
            }
        });
        btnKey = new JButton(actions.getAction(MORE));
        btnKey.setIcon(ICON_SELECT);
        btnKeyGo = new JButton(actions.getAction(GO_PASSAGE));

        txtSearch = new JTextField();
        txtSearch.setAction(actions.getAction(SEARCH_FIELD));
        JLabel lblSearch = actions.createJLabel(SEARCH_LABEL);
        lblSearch.setLabelFor(txtSearch);
        btnSearch = new JButton(actions.getAction(GO_SEARCH));

        JButton btnHelp = actions.createJButton(HELP);
        dlgHelp = new QuickHelpDialog(GuiUtil.getFrame(this), Msg.HELP_TITLE.toString(), Msg.HELP_TEXT.toString());

        btnAdvanced = new JButton(actions.getAction(ADVANCED));
        btnIndex = new JButton(actions.getAction(INDEX));

        this.setLayout(new GridBagLayout());
        this.add(lblBible,    new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));
        this.add(biblePicker, new GridBagConstraints(2, 0, 2, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(quickPicker, new GridBagConstraints(4, 0, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        this.add(lblKey,      new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        this.add(txtKey,      new GridBagConstraints(2, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 1, 2), 0, 0));
        this.add(btnKey,      new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        this.add(btnKeyGo,    new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        this.add(btnHelp,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lblSearch,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        this.add(btnIndex,    new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(2, 0, 2, 2), 0, 0));
        this.add(txtSearch,   new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 3, 2), 0, 0));
        this.add(btnAdvanced, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 0, 2, 2), 0, 0));
        this.add(btnSearch,   new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        enableComponents();
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * What are the currently selected Books?
     */
    public Book[] getBooks()
    {
        return (Book[]) selected.clone();
    }

    /**
     * What is the first currently selected book?
     */
    public Book getFirstBook()
    {
        return selected != null && selected.length > 0 ? selected[0] : null;
    }

    /**
     *
     */
    public void clear()
    {
        setKey(selected.length == 0 ? new RocketPassage() : selected[0].createEmptyKeyList());
        setTitle(CLEAR);
    }

    /**
     *
     */
    public boolean isClear()
    {
        return title.indexOf(Msg.CLEAR.toString()) != -1;
    }

    /**
     * More (...) button was clicked
     */
    public void doMore()
    {
        showSelectDialog();
    }

    /**
     * Go button was clicked
     */
    public void doGoPassage()
    {
        doPassageAction();
    }

    /**
     * Go button was clicked
     */
    public void doGoSearch()
    {
        doSearchAction();
    }

    /**
     * Someone pressed return in the passage area
     */
    public void doPassageAction()
    {
        setKey(txtKey.getText());
        if (!key.isEmpty())
        {
            txtSearch.setText(""); //$NON-NLS-1$
            setTitle(PASSAGE);
        }
    }

    /**
     * Someone pressed return in the search area
     */
    public void doSearchAction()
    {
        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            String param = txtSearch.getText();
            if (param == null || param.length() == 0)
            {
                return;
            }

            boolean rank = advanced.isRanked();

            DefaultSearchModifier modifier = new DefaultSearchModifier();
            modifier.setRanked(rank);

            Key results = selected[0].find(new DefaultSearchRequest(param, modifier));
            int total = results.getCardinality();
            int partial = total;

            // we get PassageTallys for rank searches
            if (results instanceof PassageTally || rank)
            {
                PassageTally tally = (PassageTally) results;
                tally.setOrdering(PassageTally.ORDER_TALLY);
                int rankCount = getNumRankedVerses();
                if (rankCount > 0 && rankCount < total)
                {
                    tally.trimRanges(rankCount, RestrictionType.NONE);
                    partial = rankCount;
                }
            }

            if (total == 0)
            {
                Reporter.informUser(this, Msg.NO_HITS, new Object[] { param });
            }
            else
            {
                if (total == partial)
                {
                    Reporter.informUser(this, Msg.HITS, new Object[] {param, new Integer(total)});
                }
                else
                {
                    Reporter.informUser(this, Msg.PARTIAL_HITS, new Object[] {param, Integer.toString(partial), Integer.toString(total)});
                }
                setTitle(SEARCH);
                setKey(results);
            }
        }
        catch (BookException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone has clicked on the advanced search button
     */
    public void doAdvanced()
    {
        String reply = advanced.showInDialog(this, Msg.ADVANCED_TITLE.toString(), true, txtSearch.getText());
        if (reply != null)
        {
            txtSearch.setText(reply);
            doSearchAction();
        }
    }

    /**
     * Rank is an action, but we don't need to do anything because rank is
     * only used when search is clicked. But ActionFactory will complain if we
     * leave it out.
     */
    public void doRank()
    {
        // Do nothing
    }

    /**
     * Someone clicked help
     */
    public void doHelpAction()
    {
        dlgHelp.setVisible(true);
    }

    /**
     * Someone clicked one the index button
     */
    public void doIndex()
    {
        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        IndexResolver.scheduleIndex(selected[0], this);
        enableComponents();
    }

    /**
     * Sync the viewed passage with the passage text box
     */
    private void updateDisplay()
    {
        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        fireCommandMade(new DisplaySelectEvent(this, key));
    }

    /**
     * Accessor for the default name
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @return the picker
     */
    public ParallelBookPicker getBiblePicker()
    {
        return biblePicker;
    }

    public void setKey(String newKey)
    {
        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            setKey(selected[0].getKey(newKey));
        }
        catch (NoSuchKeyException e)
        {
            Reporter.informUser(this, e);
        }
    }

    public void setKey(Key newKey)
    {
        if (newKey == null || newKey.isEmpty())
        {
            if (!key.isEmpty())
            {
                key = selected[0].createEmptyKeyList();
                txtKey.setText(""); //$NON-NLS-1$
                txtSearch.setText(""); //$NON-NLS-1$

                updateDisplay();
                setTitle(CLEAR);
            }
        }
        else if (!newKey.equals(key))
        {
            key = newKey;
            String text = key.getName();
            txtKey.setText(text);
            updateDisplay();
            if (isClear())
            {
                setTitle(PASSAGE);
                txtSearch.setText(""); //$NON-NLS-1$
            }
        }
    }

    /**
     * Gets the number of verses that should be shown when a search result is
     * ranked. A value of 0 means show all.
     * @return Returns the numRankedVerses.
     */
    public static int getNumRankedVerses()
    {
        return numRankedVerses;
    }

    /**
     * Sets the number of verses that should be shown when a search result is
     * ranked. This can be a value in the range of 0 to maxNumRankedVerses.
     * Values outside this range are silently constrained to the range.
     * @param newNumRankedVerses The numRankedVerses to set.
     */
    public static void setNumRankedVerses(int newNumRankedVerses)
    {
        int count = newNumRankedVerses;
        if (count < 0)
        {
            count = 0;
        }
        else if (count > maxNumRankedVerses)
        {
            count = maxNumRankedVerses;
        }
        numRankedVerses = count;
    }

    /**
     * @return Returns the maxNumRankedVerses.
     */
    public static int getMaxNumRankedVerses()
    {
        return maxNumRankedVerses;
    }

    /**
     * @param newMaxNumRankedVerses The maxNumRankedVerses to set.
     */
    public static void setMaxNumRankedVerses(int newMaxNumRankedVerses)
    {
        int count = newMaxNumRankedVerses;
        if (count < numRankedVerses)
        {
            count = numRankedVerses;
        }
        maxNumRankedVerses = count;
    }

    private void setTitle(int newMode)
    {
        mode = newMode;
        switch (mode)
        {
        case CLEAR:
            title = Msg.UNTITLED.toString(new Integer(base++));
            break;
        case PASSAGE:
            title = key.getName();
            break;
        case SEARCH:
            title = txtSearch.getText();
            break;
        default:
            assert false;
        }
        if (title.length() == 0)
        {
            setTitle(CLEAR);
        }
        else
        {
            fireTitleChanged(new TitleChangedEvent(this, title));
        }
    }

    /**
     * Display a dialog indicating that no Bible is installed.
     */
    private void noBookInstalled()
    {
        String noBible = Msg.NO_INSTALLED_BIBLE.toString();
        JOptionPane.showMessageDialog(this, noBible, noBible, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Ensure that the right components are enabled
     */
    /*private*/ final void enableComponents()
    {
        boolean readable = selected != null && selected.length > 0;
        boolean searchable = readable && selected[0].getIndexStatus().equals(IndexStatus.DONE);
        boolean indexable = readable && selected[0].getIndexStatus().equals(IndexStatus.UNDONE);

        txtSearch.setEnabled(searchable);
        txtSearch.setBackground(searchable ? SystemColor.text : SystemColor.control);
        txtSearch.setVisible(searchable);
        btnAdvanced.setEnabled(searchable);
        btnSearch.setEnabled(searchable);
        txtKey.setEnabled(readable);
        txtKey.setBackground(readable ? SystemColor.text : SystemColor.control);
        btnKey.setEnabled(readable);
        btnKeyGo.setEnabled(readable);
        btnIndex.setVisible(indexable);
        btnIndex.setEnabled(indexable);
    }

    /**
     * Someone clicked the "..." button
     */
    /*private*/ final void showSelectDialog()
    {
        if (dlgSelect == null)
        {
            dlgSelect = new PassageSelectionPane();
        }

        String passg = dlgSelect.showInDialog(this, Msg.SELECT_PASSAGE_TITLE.toString(), true, txtKey.getText());
        if (passg != null)
        {
            txtKey.setText(passg);
            doPassageAction();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.BookSelectListener#booksChosen(org.crosswire.bibledesktop.book.BookSelectEvent)
     */
    public void booksChosen(BookSelectEvent ev)
    {
        Book[] books = ev.getBookProvider().getBooks();
        assert books.length > 0;

        Book newSelected = ev.getBookProvider().getFirstBook();

        if (selected.length > 0 && selected[0] != newSelected)
        {
            selected[0].removeIndexStatusListener(isl);
            newSelected.addIndexStatusListener(isl);
        }

        selected = books;

        enableComponents();

        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        fireVersionChanged(new DisplaySelectEvent(this, key));
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.KeyChangeListener#keyChanged(org.crosswire.bibledesktop.book.KeyChangeEvent)
     */
    public void keyChanged(KeyChangeEvent ev)
    {
        setKey(ev.getKey());
    }

    /**
     * Add a TitleChangedEvent listener
     */
    public synchronized void addTitleChangedListener(TitleChangedListener li)
    {
        listeners.add(TitleChangedListener.class, li);
    }

    /**
     * Remove a TitleChangedEvent listener
     */
    public synchronized void removeTitleChangedListener(TitleChangedListener li)
    {
        listeners.remove(TitleChangedListener.class, li);
    }

    /**
     * Listen for changes to the title
     * @param ev the event to throw
     */
    protected void fireTitleChanged(TitleChangedEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == TitleChangedListener.class)
            {
                ((TitleChangedListener) contents[i + 1]).titleChanged(ev);
            }
        }
    }

    /**
     * Add a DisplaySelectEvent listener
     */
    public synchronized void addCommandListener(DisplaySelectListener li)
    {
        listeners.add(DisplaySelectListener.class, li);
    }

    /**
     * Remove a DisplaySelectEvent listener
     */
    public synchronized void removeCommandListener(DisplaySelectListener li)
    {
        listeners.remove(DisplaySelectListener.class, li);
    }

    /**
     * Inform the command listeners
     */
    protected void fireCommandMade(DisplaySelectEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == DisplaySelectListener.class)
            {
                ((DisplaySelectListener) contents[i + 1]).passageSelected(ev);
            }
        }
    }

    /**
     * Inform the version listeners
     */
    protected void fireVersionChanged(DisplaySelectEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == DisplaySelectListener.class)
            {
                ((DisplaySelectListener) contents[i + 1]).bookChosen(ev);
            }
        }
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        // We don't serialize views
        selected = null;

        listeners = new EventListenerList();

        actions = new ActionFactory(DisplaySelectPane.class, this);

        isl = new IndexStatusListener()
        {
            public void statusChanged(IndexStatusEvent ev)
            {
                enableComponents();
            }
        };
        is.defaultReadObject();
    }

    // For the Passage card
    private static final String VIEW_LABEL = "ViewLabel"; //$NON-NLS-1$
    private static final String PASSAGE_FIELD = "PassageAction"; //$NON-NLS-1$
    private static final String MORE = "More"; //$NON-NLS-1$
    private static final String GO_PASSAGE = "GoPassage"; //$NON-NLS-1$
    private static final String HELP = "HelpAction"; //$NON-NLS-1$
    private static final String SEARCH_LABEL = "SearchLabel"; //$NON-NLS-1$
    private static final String GO_SEARCH = "GoSearch"; //$NON-NLS-1$
    private static final String SEARCH_FIELD = "SearchAction"; //$NON-NLS-1$
    private static final String ADVANCED = "Advanced"; //$NON-NLS-1$
    private static final String BIBLE = "Bible"; //$NON-NLS-1$
    private static final String INDEX = "Index"; //$NON-NLS-1$

    private static final ImageIcon ICON_SELECT = GuiUtil.getIcon("toolbarButtonGraphics/general/Edit16.gif"); //$NON-NLS-1$

    /**
     * Keep the selection up to date with indexing.
     */
    private transient IndexStatusListener isl;

    private static int base = 1;

    private String title;

    private QuickHelpDialog dlgHelp;

    private transient ActionFactory actions;

    private transient Book[] selected;
    /*
     * GUI Components
     */
    private PassageSelectionPane dlgSelect;
    private ParallelBookPicker biblePicker;
    protected JTextField txtKey;
    protected JTextField txtSearch;
    private JButton btnAdvanced;
    private JButton btnSearch;
    private JButton btnKey;
    private JButton btnKeyGo;
    private AdvancedSearchPane advanced;
    private JButton btnIndex;

    /**
     * The current state of the display: SEARCH, PASSAGE, CLEAR
     */
    private int mode;
    private static final int CLEAR = 0;
    private static final int PASSAGE = 1;
    private static final int SEARCH = 2;

    /**
     * The current passage.
     */
    private Key key;

    /**
     * Who is interested in things this DisplaySelectPane does
     */
    private transient EventListenerList listeners;

    /**
     * How may hits to show when the search results are ranked.
     */
    private static int numRankedVerses = 20;

    /**
     * What is the limit to which numRankedVerses can be set.
     */
    private static int maxNumRankedVerses = 200;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256446910616057650L;
}
