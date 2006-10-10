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

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.bibledesktop.passage.KeyListListModel;
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
 * Builds a panel on which all the Dictionaries and their entries are visible.
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
        lstDicts.setSelectedValue(Defaults.getDailyDevotional(), true);
    }

    /**
     * GUI initialiser
     */
    private void init()
    {
        display = BookDataDisplayFactory.createBookDataDisplay();

        BookFilter filter =
            BookFilters.either(
                               BookFilters.either(BookFilters.getDictionaries(),
                                                  BookFilters.getCommentaries()),
                               BookFilters.getDailyDevotionals()
                               );
        BooksComboBoxModel mdlDicts = new BooksComboBoxModel(filter);

        lstDicts = new JList();
        lstDicts.setVisibleRowCount(6);
        lstDicts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstDicts.setModel(mdlDicts);
        lstDicts.setCellRenderer(new BookListCellRenderer());
        lstDicts.setPrototypeCellValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        lstDicts.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                newDictionary();
            }
        });

        JScrollPane scrDicts = new JScrollPane();
        scrDicts.setViewportView(lstDicts);

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

        pnlSelect = new JPanel();
        pnlSelect.setLayout(new FlowLayout());
        pnlSelect.add(cboBooks, null);
        pnlSelect.add(cboChaps, null);
        pnlSelect.add(cboVerse, null);

        lstEntries = new JList();
        lstEntries.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newEntry();
            }
        });
        scrEntries = new JScrollPane();
        scrEntries.setViewportView(lstEntries);

        JScrollPane scrDisplay = new JScrollPane();
        scrDisplay.setViewportView(display.getComponent());

        sptMain = new FixedSplitPane(false);
        sptMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        // Make the top 20% of the total
        sptMain.setResizeWeight(0.2D);
        sptMain.setTopComponent(new JPanel());
        sptMain.setBottomComponent(scrDisplay);

//        this.setResizeWeight(0.1D);
//        this.setMinimumSize(new Dimension(0, 0));
        this.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.setTopComponent(scrDicts);
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
        return (Key) lstEntries.getSelectedValue();
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
        return dict;
    }

    /**
     * See if the current dictionary has a mention of the word in question.
     * LATER(joe): add a background task to highlight other dictionaries that have the word.
     */
    public void setWord(String data)
    {
        try
        {
            if (dict == null)
            {
                return;
            }

            Key key = dict.getKey(data);
            if (key != null)
            {
                lstEntries.setSelectedValue(key, true);
            }
        }
        catch (NoSuchKeyException ex)
        {
            // ignore
        }
    }

    /**
     * Accessor for the current passage
     */
    public void setKey(Key key)
    {
        if (key != null && key instanceof Passage)
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
                lstdicts.setSelectedValue(tempdict, true);
                lstentries.setSelectedValue(key, true);
                return;
            }
            catch (BookException ex)
            {
                // ignore - we only wanted to see if it could be done.
            }
        }     
    */

    public void selectDictionary(Book book)
    {
        lstDicts.setSelectedValue(book, true);
    }

    /**
     * Called when someone selects a new Dictionary
     */
    protected void newDictionary()
    {
        Object selected = lstDicts.getSelectedValue();
        // Don't leave the scroller in the middle of the list!
        lstEntries.ensureIndexIsVisible(0);
        // Make sure that the list of keys is empty.
        lstEntries.setModel(new KeyListListModel(null));
        // Make sure that the display is emtpy.
        display.setBookData(null, null);
        if (selected != null)
        {
            Book book = (Book) selected;
            BookCategory category = book.getBookCategory();
            //divider snaps back to its starting point when a new component is set
            int dividerLocation = sptMain.getDividerLocation();
            if (category.equals(BookCategory.DICTIONARY)
                || category.equals(BookCategory.GLOSSARY)
                || category.equals(BookCategory.DAILY_DEVOTIONS))
            {
                dict = book;
                Key key = dict.getGlobalKeyList();

                KeyListListModel model = new KeyListListModel(key);
                lstEntries.setModel(model);

                if (dict instanceof PreferredKey)
                {
                    PreferredKey pref = (PreferredKey) dict;
                    Key prefkey = pref.getPreferred();

                    lstEntries.setSelectedValue(prefkey, true);
                }
                sptMain.setTopComponent(scrEntries);
            }
            else
            {
                updateDisplay();
                sptMain.setTopComponent(pnlSelect);
            }
            sptMain.setDividerLocation(dividerLocation);
        }
    }

    /**
     * 
     */
    protected void updateDisplay()
    {
        Book book = (Book) lstDicts.getSelectedValue();
        if (book == null)
        {
            log.warn("no selected dictionary"); //$NON-NLS-1$
            return;
        }

        Verse verse = set.getVerse();
        display.setBookData(book, verse);
    }

    /**
     * Called when someone selects a new entry from the current dictionary
     */
    protected void newEntry()
    {
        Key key = (Key) lstEntries.getSelectedValue();
        if (key != null)
        {
            display.setBookData(dict, key);
        }
    }

    /**
     * The display of OSIS data
     */
    private BookDataDisplay display;
    private transient Book dict;
    protected transient BibleComboBoxModelSet set;
    private JPanel pnlSelect;
    private JList lstDicts;
    private JSplitPane sptMain;
    private JScrollPane scrEntries;
    private JList lstEntries;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DictionaryPane.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616449020667442997L;
}
