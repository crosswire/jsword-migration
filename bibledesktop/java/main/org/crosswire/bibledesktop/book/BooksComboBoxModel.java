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
 * ID: $ID$
 */
package org.crosswire.bibledesktop.book;

import javax.swing.ComboBoxModel;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.Defaults;

/**
 * The BibleModels class implements a number of swing DataModels
 * and gives access to the list of current Bibles.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BooksComboBoxModel extends BooksListModel implements ComboBoxModel
{
    /**
     * Basic Constructor
     */
    public BooksComboBoxModel()
    {
        this(null);
    }

    /**
     * Basic Constructor
     */
    public BooksComboBoxModel(BookFilter filter)
    {
        super(filter);

        postCacheData();

        if (getSize() > 0)
        {
            // The default is to have the first selected
            current = (Book) getElementAt(0);

            // If one of our favorite Books is in the list we ought to start
            // with that selected. We put the Bible last so it will override
            // the others if this is a broad list of books
            tryInitialSelection(Defaults.getCommentary());
            tryInitialSelection(Defaults.getDictionary());
            tryInitialSelection(Defaults.getBible());
        }
    }

    /**
     * @param book
     */
    private void tryInitialSelection(Book book)
    {
        if (book != null)
        {
            int i = books.indexOf(book);
            if (i != -1)
            {
                current = book;
            }
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selected)
    {
        this.current = (Book) selected;
        fireContentsChanged(this, -1, -1);
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem()
    {
        return current;
    }

    /**
     * Get the selected Bible
     * @return A Bible
     */
    public Book getSelectedBook()
    {
        return current;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.BooksListModel#cacheData()
     */
    protected final synchronized void postCacheData()
    {
        // Find the previously selected item
        boolean found = false;
        int size = getSize();
        for (int i = 0; i < size; i++)
        {
            if (getElementAt(i) == current)
            {
                found = true;
                break;
            }
        }

        // If it was not found then either set to first element or null
        if (!found)
        {
            if (getSize() > 0)
            {
                current = (Book) getElementAt(0);
            }
            else
            {
                current = null;
            }
        }
    }

    /**
     * The currently selected version
     */
    protected transient Book current;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3906362740397388593L;
}
