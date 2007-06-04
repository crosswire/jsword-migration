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

import java.util.EventObject;

import org.crosswire.jsword.book.Book;

/**
 * A BookSelectEvent happens whenever a user selects a book.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookSelectEvent extends EventObject
{
    /**
     * For when a command has been made
     * @param source The thing that started this off
     * @param books The selected books
     */
    public BookSelectEvent(Object source, Book[] books)
    {
        super(source);
        this.books = books;
    }

    /**
     * Get the type of command
     * @return The type of command
     */
    public Book[] getBooks()
    {
        return books;
    }

    /**
     * The new list of Books
     */
    private transient Book[] books;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 6018474028417993389L;
}
