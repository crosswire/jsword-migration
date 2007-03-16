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
package org.crosswire.bibledesktop.book;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.bibledesktop.passage.KeyListListModel;
import org.crosswire.bibledesktop.passage.KeyTreeModel;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PreferredKey;
import org.crosswire.jsword.passage.Verse;

/**
 * Builds a panel on which all the non-Bible books and their entries are visible.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class DictionaryPane extends JSplitPane implements BookDataDisplay
{
    /**
     * Setup the GUI 
     */
    public DictionaryPane()
    {
        init();

        // This must come after the setViewportView() calls so scrolling works
        bookList.setSelectedValue(Defaults.getDailyDevotional(), true);
    }

    /**
     * GUI initialiser
     */
    private void init()
    {
        display = BookDataDisplayFactory.createBookDataDisplay();

        Component bookPicker = createBookPicker();

        commentaryPicker = createCommentaryPicker();

        dictionaryKeyScroller = createDictionaryPicker();

        genBookKeyScroller = createGenBookPicker();

        JScrollPane scrDisplay = new JScrollPane(display.getComponent());

        sptMain = new FixedSplitPane(false);
        sptMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        // Make the top 20% of the total
        sptMain.setResizeWeight(0.2D);
        sptMain.setTopComponent(new JPanel());
        sptMain.setBottomComponent(scrDisplay);

//        this.setResizeWeight(0.1D);
//        this.setMinimumSize(new Dimension(0, 0));
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setTopComponent(bookPicker);
        this.setBottomComponent(sptMain);
        this.setBorder(null);

        Object thisUI = this.getUI();
        if (thisUI instanceof javax.swing.plaf.basic.BasicSplitPaneUI)
        {
            ((javax.swing.plaf.basic.BasicSplitPaneUI) thisUI).getDivider().setBorder(null);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        display.refresh();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        display.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return (Key) dictionaryKeyList.getSelectedValue();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addURLEventListener(URLEventListener listener)
    {
        display.addURLEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeURLEventListener(URLEventListener listener)
    {
        display.removeURLEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getBook()
     */
    public Book getBook()
    {
        return book;
    }

    /**
     * See if the current dictionary has a mention of the word in question.
     * LATER(joe): add a background task to highlight other dictionaries that have the word.
     */
    public void setWord(String data)
    {
        if (book == null)
        {
            return;
        }

        try
        {
            Key key = book.getKey(data);
            dictionaryKeyList.setSelectedValue(key, true);
        }
        catch (NoSuchKeyException ex)
        {
            return;
        }

    }

    /**
     * Accessor for the current passage
     */
    public void setKey(Key key)
    {
        if (key instanceof Passage)
        {
            Passage ref = (Passage) key;
            if (ref.countVerses() > 0)
            {
                set.setVerse(ref.getVerseAt(0));
            }
        }

        updateDisplay();
    }

    /*
        // Code to search for a word
        for (Iterator it = Books.getBooks(filter).iterator(); it.hasNext();)
        {
            DictionaryMetaData dmd = (DictionaryMetaData) it.next();
            Dictionary tempdict = dmd.getDictionary();
            try
            {
                Key key = tempdict.getKey(data);
                bookList.setSelectedValue(tempdict, true);
                lstentries.setSelectedValue(key, true);
                return;
            }
            catch (BookException ex)
            {
                // ignore - we only wanted to see if it could be done.
            }
        }     
    */

    public void selectBook(Book selectedBook)
    {
        bookList.setSelectedValue(selectedBook, true);
    }

    /**
     * Create a book picker of all non-bibles
     * @return The scrollable picker
     */
    private Component createBookPicker()
    {
        BookFilter filter = BookFilters.getNonBibles();
        BooksComboBoxModel mdlBooks = new BooksComboBoxModel(filter);

        bookList = new JList();
        bookList.setVisibleRowCount(6);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.setModel(mdlBooks);
        bookList.setCellRenderer(new BookListCellRenderer());
        bookList.setPrototypeCellValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        bookList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                newBook();
            }
        });

        return new JScrollPane(bookList);
    }

    /**
     * Build a tree for a GenBook
     */
    private Component createCommentaryPicker()
    {
        JComboBox cboBooks = new JComboBox();
        JComboBox cboChaps = new JComboBox();
        JComboBox cboVerse = new JComboBox();
        set = new BibleComboBoxModelSet(cboBooks, cboChaps, cboVerse);

        set.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        cboBooks.setToolTipText(Msg.SELECT_BOOK.toString());
        cboChaps.setToolTipText(Msg.SELECT_CHAPTER.toString());
        cboVerse.setToolTipText(Msg.SELECT_VERSE.toString());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(cboBooks, null);
        panel.add(cboChaps, null);
        panel.add(cboVerse, null);
        return panel;
    }
   
    /**
     * Build a tree for a GenBook
     */
    private Component createDictionaryPicker()
    {
        dictionaryKeyList = new JList();
        dictionaryKeyList.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newEntry();
            }
        });
        return new JScrollPane(dictionaryKeyList);
    }
   
    /**
     * Build a tree for a GenBook
     */
    private Component createGenBookPicker()
    {
        genBookKeyTree = new JTree();
        genBookKeyTree.setModel(new KeyTreeModel(null));
        genBookKeyTree.setShowsRootHandles(true);
        genBookKeyTree.setRootVisible(false);
        genBookKeyTree.putClientProperty("JTree.lineStyle", "Angled"); //$NON-NLS-1$//$NON-NLS-2$
        genBookKeyTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                // treeSelected();
            }
        });

        return new JScrollPane(genBookKeyTree);
    }

    /**
     * Called when someone selects a new Dictionary
     */
    /*private*/ final void newBook()
    {
        // First ensure that all the pickers make sense if the user
        // unselects the book
        if (book != null)
        {
            BookCategory currentCategory = book.getBookCategory();
            if (currentCategory.equals(BookCategory.DICTIONARY) || currentCategory.equals(BookCategory.GLOSSARY)
                || currentCategory.equals(BookCategory.DAILY_DEVOTIONS))
            {
                // Don't leave the scroller in the middle of the list!
                dictionaryKeyList.ensureIndexIsVisible(0);
                // Make sure that the list of keys is empty.
                dictionaryKeyList.setModel(new KeyListListModel(null));
            }
            else if (currentCategory.equals(BookCategory.GENERAL_BOOK))
            {
                // Don't leave the scroller in the middle of the list!
                genBookKeyTree.scrollRowToVisible(0);

                // Make sure that the list of keys is empty.
                KeyTreeModel model = new KeyTreeModel(null);
                genBookKeyTree.setModel(model);
            }
        }
        // Make sure that the display is emtpy.
        display.setBookData(null, null);

        Object selected = bookList.getSelectedValue();
        if (selected != null)
        {
            Book selectedBook = (Book) selected;
            BookCategory category = selectedBook.getBookCategory();
            //divider snaps back to its starting point when a new component is set
            int dividerLocation = sptMain.getDividerLocation();
            if (category.equals(BookCategory.COMMENTARY))
            {
                updateDisplay();
                sptMain.setTopComponent(commentaryPicker);
            }
            else if (category.equals(BookCategory.DICTIONARY)
                     || category.equals(BookCategory.GLOSSARY)
                     || category.equals(BookCategory.DAILY_DEVOTIONS))
            {
                book = selectedBook;
                Key key = book.getGlobalKeyList();

                KeyListListModel model = new KeyListListModel(key);
                dictionaryKeyList.setModel(model);

                if (book instanceof PreferredKey)
                {
                    PreferredKey pref = (PreferredKey) book;
                    Key prefkey = pref.getPreferred();

                    dictionaryKeyList.setSelectedValue(prefkey, true);
                }

                sptMain.setTopComponent(dictionaryKeyScroller);
            }
            else // currentCategory.equals(BookCategory.GENERAL_BOOK)
            {
                book = selectedBook;
                Key key = book.getGlobalKeyList();

                KeyTreeModel model = new KeyTreeModel(key);
                genBookKeyTree.setModel(model);

                sptMain.setTopComponent(genBookKeyScroller);
            }

            sptMain.setDividerLocation(dividerLocation);
        }
    }

    /**
     * 
     */
    /*protected*/ final void updateDisplay()
    {
        Book selectedBook = (Book) bookList.getSelectedValue();
        if (selectedBook == null)
        {
            log.warn("no selected book"); //$NON-NLS-1$
            return;
        }

        Verse verse = set.getVerse();
        display.setBookData(selectedBook, verse);
    }

    /**
     * Called when someone selects a new entry from the current dictionary
     */
    /*private*/ final void newEntry()
    {
        Key key = (Key) dictionaryKeyList.getSelectedValue();
        if (key != null)
        {
            display.setBookData(book, key);
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
        display = BookDataDisplayFactory.createBookDataDisplay();
        book = null;
        set = null;
        is.defaultReadObject();
    }

    /**
     * The display of OSIS data
     */
    private transient BookDataDisplay display;
    private transient Book book;
    protected transient BibleComboBoxModelSet set;
    private Component commentaryPicker;
    private Component genBookKeyScroller;
    private JTree genBookKeyTree;
    private JList bookList;
    private JSplitPane sptMain;
    private Component dictionaryKeyScroller;
    private JList dictionaryKeyList;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DictionaryPane.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616449020667442997L;
}
