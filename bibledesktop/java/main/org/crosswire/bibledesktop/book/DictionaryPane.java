package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.passage.KeyListListModel;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PreferredKey;
import org.crosswire.jsword.passage.Verse;

/**
 * Builds a panel on which all the Dictionaries and their entries are visible.
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
 * @version $Id$
 */
public class DictionaryPane extends JPanel implements BookDataDisplay
{
    /**
     * Setup the GUI 
     */
    public DictionaryPane()
    {
        init();

        // This must come after the setViewportView() calls so scrolling works
        lstDicts.setSelectedValue(Defaults.getDictionaryMetaData(), true);
    }

    /**
     * GUI initialiser
     */
    private void init()
    {
        lstDicts.setVisibleRowCount(4);
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
        scrDicts.setViewportView(lstDicts);

        set.setBookComboBox(cboBooks);
        set.setChapterComboBox(cboChaps);
        set.setVerseComboBox(cboVerse);
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

        pnlSelect.setLayout(new FlowLayout());
        pnlSelect.add(cboBooks, null);
        pnlSelect.add(cboChaps, null);
        pnlSelect.add(cboVerse, null);

        lstEntries.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                newEntry();
            }
        });
        scrEntries.setViewportView(lstEntries);

        scrDisplay.setViewportView(display.getComponent());

        sptMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        // Make the top 20% of the total
        sptMain.setResizeWeight(0.2D);
        sptMain.setTopComponent(new JPanel());
        sptMain.setBottomComponent(scrDisplay);
        sptMain.setBorder(null);
        sptMain.setDividerSize(8);

        this.setLayout(new BorderLayout(5, 5));
        this.add(scrDicts, BorderLayout.NORTH);
        this.add(sptMain, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        throw new UnsupportedOperationException(); //$NON-NLS-1$
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
    public void addHyperlinkListener(HyperlinkListener li)
    {
        display.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        display.removeHyperlinkListener(li);
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

    /**
     * Called when someone selects a new Dictionary
     */
    protected void newDictionary()
    {
        Object selected = lstDicts.getSelectedValue();
        if (selected != null)
        {
            BookMetaData dmd = (BookMetaData) selected;
            if (dmd.getType().equals(BookType.DICTIONARY))
            {
                dict = dmd.getBook();
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
                sptMain.setTopComponent(pnlSelect);
            }
        }
    }

    /**
     * 
     */
    protected void updateDisplay()
    {
        BookMetaData bmd = (BookMetaData) lstDicts.getSelectedValue();
        if (bmd == null)
        {
            log.warn("no selected commentary"); //$NON-NLS-1$
            return;
        }

        try
        {
            Verse verse = set.getVerse();
            display.setBookData(bmd.getBook(), verse);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Called when someone selects a new entry from the current dictionary
     */
    protected void newEntry()
    {
        try
        {
            Key key = (Key) lstEntries.getSelectedValue();
            if (key != null)
            {
                display.setBookData(dict, key);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The display of OSIS data
     */
    private BookDataDisplay display = BookDataDisplayFactory.createBookDataDisplay();

    private BookFilter filter = BookFilters.either(BookFilters.getDictionaries(), BookFilters.getCommentaries());
    private BooksComboBoxModel mdlDicts = new BooksComboBoxModel(filter);
    private Book dict = null;

    protected BibleComboBoxModelSet set = new BibleComboBoxModelSet();
    private JComboBox cboBooks = new JComboBox();
    private JComboBox cboChaps = new JComboBox();
    private JComboBox cboVerse = new JComboBox();
    private JPanel pnlSelect = new JPanel();
    private JScrollPane scrDicts = new JScrollPane();
    private JList lstDicts = new JList();
    private JSplitPane sptMain = new FixedSplitPane();
    private JScrollPane scrEntries = new JScrollPane();
    private JScrollPane scrDisplay = new JScrollPane();
    private JList lstEntries = new JList();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DictionaryPane.class);

    /**
     * SERIALUID(dms): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;
}
