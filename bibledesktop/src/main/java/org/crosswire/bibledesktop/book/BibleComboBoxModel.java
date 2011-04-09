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

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * A ComboBoxModel for selecting book/chapter/verse.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BibleComboBoxModel extends AbstractListModel implements ComboBoxModel {
    /**
     * The level of the book combo.
     */
    protected enum Level {
        /**
         * For when the we are a book level combo
         */
        BOOK,

        /**
         * For when the we are a chapter level combo
         */
        CHAPTER,

        /**
         * For when the we are a verse level combo
         */
        VERSE,
    }

    /**
     * Simple ctor for choosing verses
     */
    protected BibleComboBoxModel(BibleComboBoxModelSet set, Level level) {
        this.set = set;
        this.level = level;

        switch (level) {
        case BOOK:
            try {
                selected = set.getVerse().getBook().getBookName();
            } catch (NoSuchVerseException ex) {
                assert false : ex;
            }
            break;

        case CHAPTER:
            selected = Integer.valueOf(set.getVerse().getChapter());
            break;

        case VERSE:
            selected = Integer.valueOf(set.getVerse().getVerse());
            break;

        default:
            assert false : level;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selected) {
        log.debug("setSelectedItem(" + selected + ") level=" + level);

        switch (level) {
        case BOOK:
            BibleBook book = (BibleBook) selected;
            assert book != null;
            setBook(book);
            break;

        case CHAPTER:
            Integer csel = (Integer) selected;
            setChapter(csel.intValue());
            break;

        case VERSE:
            Integer vsel = (Integer) selected;
            setVerse(vsel.intValue());
            break;

        default:
            assert false : level;
        }

        this.selected = selected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem() {
        return selected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        try {
            switch (level) {
            case BOOK:
                return BibleInfo.booksInBible();

            case CHAPTER:
                return BibleInfo.chaptersInBook(set.getVerse().getBook());

            case VERSE:
                return BibleInfo.versesInChapter(set.getVerse().getBook(), set.getVerse().getChapter());

            default:
                assert false : level;
                return 0;
            }
        } catch (NoSuchVerseException ex) {
            assert false : ex;
            return 0;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        switch (level) {
        case BOOK:
            BibleBook[] books = BibleInfo.getBooks();
            return books[index];

        default:
            return Integer.valueOf(index + 1);

        }
    }

    /**
     * Accessor for the book
     */
    public void setBook(BibleBook book) {
        try {
            // Try to honor current chapter and verse
            // Use 1 if it is not possible
            Verse old = set.getVerse();
            int chapter = old.getChapter();
            int verse = old.getVerse();

            chapter = Math.min(chapter, BibleInfo.chaptersInBook(book));
            verse = Math.min(verse, BibleInfo.versesInChapter(book, chapter));

            Verse update = new Verse(book, chapter, verse);
            set.setVerse(update);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
        }
    }

    /**
     * Accessor for the chapter
     */
    public void setChapter(int chapter) {
        try {
            // Try to honor current verse
            // Use 1 if it is not possible
            Verse old = set.getVerse();
            BibleBook book = old.getBook();
            int verse = old.getVerse();

            verse = Math.min(verse, BibleInfo.versesInChapter(book, chapter));

            Verse update = new Verse(book, chapter, verse);
            set.setVerse(update);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
        }
    }

    /**
     * Accessor for the chapter
     */
    public void setVerse(int verse) {
        try {
            Verse old = set.getVerse();
            Verse update = new Verse(old.getBook(), old.getChapter(), verse);
            set.setVerse(update);
        } catch (NoSuchVerseException ex) {
            assert false : ex;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.AbstractListModel#fireContentsChanged(java.lang.Object,
     * int, int)
     */
    @Override
    protected void fireContentsChanged(Object source, int index0, int index1) {
        super.fireContentsChanged(source, index0, index1);
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        // Broken but we don't serialize views
        set = null;
        selected = null;
        is.defaultReadObject();
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BibleComboBoxModel.class);

    /**
     * Shared settings
     */
    private transient BibleComboBoxModelSet set;

    /**
     * What is currently selected?
     */
    private transient Object selected;

    /**
     * Are we a book, chapter or verse selector
     */
    protected Level level;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616449020667442997L;
}
