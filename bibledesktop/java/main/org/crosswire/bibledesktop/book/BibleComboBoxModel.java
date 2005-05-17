/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;

/**
 * A ComboBoxModel for selecting book/chapter/verse.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BibleComboBoxModel extends AbstractListModel implements ComboBoxModel
{
    /**
     * Simple ctor for choosing verses
     */
    protected BibleComboBoxModel(BibleComboBoxModelSet set, int level)
    {
        this.set = set;
        this.level = level;

        switch (level)
        {
        case LEVEL_BOOK:
            try
            {
                selected = BibleInfo.getLongBookName(set.getVerse().getBook());
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
            }
            break;

        case LEVEL_CHAPTER:
            selected = new Integer(set.getVerse().getChapter());
            break;

        case LEVEL_VERSE:
            selected = new Integer(set.getVerse().getVerse());
            break;

        default:
            assert false : level;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selected)
    {
        log.debug("setSelectedItem(" + selected + ") level=" + level); //$NON-NLS-1$ //$NON-NLS-2$

        switch (level)
        {
        case LEVEL_BOOK:
            String bsel = (String) selected;
            int book = BibleInfo.getBookNumber(bsel);
            assert book > 0;
            setBook(book);
            break;

        case LEVEL_CHAPTER:
            Integer csel = (Integer) selected;
            setChapter(csel.intValue());
            break;

        case LEVEL_VERSE:
            Integer vsel = (Integer) selected;
            setVerse(vsel.intValue());
            break;

        default:
            assert false : level;
        }

        this.selected = selected;
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem()
    {
        return selected;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        try
        {
            switch (level)
            {
            case LEVEL_BOOK:
                return BibleInfo.booksInBible();

            case LEVEL_CHAPTER:
                return BibleInfo.chaptersInBook(set.getVerse().getBook());

            case LEVEL_VERSE:
                return BibleInfo.versesInChapter(set.getVerse().getBook(), set.getVerse().getChapter());

            default:
                assert false : level;
                return 0;
            }
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        try
        {
            switch (level)
            {
            case LEVEL_BOOK:
                return BibleInfo.getLongBookName(index + 1);

            case LEVEL_CHAPTER:
            case LEVEL_VERSE:
                return new Integer(index + 1);

            default:
                assert false : level;
                return null;
            }
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return null;
        }
    }

    /**
     * Accessor for the book
     */
    public void setBook(int book)
    {
        try
        {
            // Try to honor current chapter and verse
            // Use 1 if it is not possible
            Verse old = set.getVerse();
            int chapter = old.getChapter();
            int verse = old.getVerse();

            if (BibleInfo.chaptersInBook(book) < chapter)
            {
                chapter = 1;
            }

            if (BibleInfo.versesInChapter(book, chapter) < verse)
            {
                verse = 1;
            }

            Verse update = new Verse(book, chapter, verse, true);
            set.setViewedVerse(update);
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
        }
    }

    /**
     * Accessor for the chapter
     */
    public void setChapter(int chapter)
    {
        try
        {
            // Try to honor current verse
            // Use 1 if it is not possible
            Verse old = set.getVerse();
            int book = old.getBook();
            int verse = old.getVerse();

            if (BibleInfo.versesInChapter(book, chapter) < verse)
            {
                verse = 1;
            }

            Verse update = new Verse(book, chapter, verse, true);
            set.setViewedVerse(update);
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
        }
    }

    /**
     * Accessor for the chapter
     */
    public void setVerse(int verse)
    {
        Verse old = set.getVerse();
        Verse update = new Verse(old.getBook(), old.getChapter(), verse, true);
        set.setViewedVerse(update);
    }

    /* (non-Javadoc)
     * @see javax.swing.AbstractListModel#fireContentsChanged(java.lang.Object, int, int)
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
        super.fireContentsChanged(source, index0, index1);
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BibleComboBoxModel.class);

    /**
     * For when the we are a book level combo
     */
    public static final int LEVEL_BOOK = 0;

    /**
     * For when the we are a chapter level combo
     */
    public static final int LEVEL_CHAPTER = 1;

    /**
     * For when the we are a verse level combo
     */
    public static final int LEVEL_VERSE = 2;

    /**
     * Shared settings
     */
    private transient BibleComboBoxModelSet set;

    /**
     * What is currently selected?
     */
    private Object selected;

    /**
     * Are we a book, chapter or verse selector
     */
    protected int level;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616449020667442997L;
}
