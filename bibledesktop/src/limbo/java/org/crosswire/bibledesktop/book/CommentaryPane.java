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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.bibledesktop.display.scrolled.ScrolledBookDataDisplay;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

/**
 * Builds a set of tabs from the list of Books returned by a filtered list
 * of Books.
 *  
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CommentaryPane extends JPanel implements BookDataDisplay
{
    /**
     * Simple constructor that uses all the Books
     */
    public CommentaryPane()
    {
        init();
    }

    /**
     * Initialise the GUI
     */
    private void init()
    {
        set.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        set.setBookComboBox(cboBooks);
        set.setChapterComboBox(cboChaps);
        set.setVerseComboBox(cboVerse);

        cboBooks.setToolTipText(Msg.SELECT_BOOK.toString());
        cboChaps.setToolTipText(Msg.SELECT_CHAPTER.toString());
        cboVerse.setToolTipText(Msg.SELECT_VERSE.toString());

        pnlSelect.setLayout(new FlowLayout());
        pnlSelect.add(cboBooks, null);
        pnlSelect.add(cboChaps, null);
        pnlSelect.add(cboVerse, null);

        cboComments.setModel(mdlcomments);
        cboComments.setRenderer(new BookListCellRenderer());
        cboComments.setPrototypeDisplayValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        cboComments.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        pnlTop.setLayout(new BorderLayout());
        pnlTop.add(pnlSelect, BorderLayout.NORTH);
        pnlTop.add(cboComments, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(pnlTop, BorderLayout.NORTH);
        this.add(display.getComponent(), BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * 
     */
    protected void updateDisplay()
    {
        Book book = (Book) cboComments.getSelectedItem();
        if (book == null)
        {
            return;
        }

        try
        {
            Verse verse = set.getVerse();
            display.setBookData(book, verse);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
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
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getBook()
     */
    public Book getBook()
    {
        return (Book) cboComments.getSelectedItem();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        cboComments.setSelectedItem(book);

        setKey(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        display.refresh();
    }

    /**
     * Accessor for the current passage
     */
    public void setKey(Key key)
    {
        this.key = key;

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

    /**
     * Last displayed
     */
    protected Key key;

    /**
     * To get us just the Commentaries
     */
    private BookFilter filter = BookFilters.getCommentaries();

    /**
     * The BookData display component
     */
    private BookDataDisplay display = new ScrolledBookDataDisplay(BookDataDisplayFactory.createBookDataDisplay());

    /*
     * GUI components
     */
    private BooksComboBoxModel mdlcomments = new BooksComboBoxModel(filter);
    protected BibleComboBoxModelSet set = new BibleComboBoxModelSet();
    protected JComboBox cboComments = new JComboBox();
    private JComboBox cboBooks = new JComboBox();
    private JComboBox cboChaps = new JComboBox();
    private JComboBox cboVerse = new JComboBox();
    private JPanel pnlSelect = new JPanel();
    private JPanel pnlTop = new JPanel();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258411737760804915L;
}
