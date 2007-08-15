/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: Books.java 1647 2007-08-06 01:35:25Z dmsmith $
 */
package org.crosswire.jsword.book;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.activate.Activator;
import org.crosswire.common.util.CollectionUtil;
import org.crosswire.common.util.EventListenerList;
import org.crosswire.jsword.internal.osgi.BookRegistry;
import org.crosswire.jsword.internal.osgi.BookRegistryEventListener;

/**
 * The Books class (along with Book) is the central point of contact
 * between the rest of the world and this set of packages.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Phillip [phillip at paristano dot org]
 */
public final class Books implements BookList
{
    /**
     * Create a singleton instance of the class.
     * This is private to ensure that only one can be created.
     * This also makes the class final!
     */
    private Books()
    {
        books = new BookSet();
        listeners = new EventListenerList();
        //Pull all the registered books now, and add our listener
        //to monitor changes.
        books.addAll(Arrays.asList(BookRegistry.getBooks()));
        BookRegistry.addChangeListener(new BookRegistryListener());
    }

    /**
     * Accessor for the singleton instance
     * @return The singleton instance
     */
    public static Books installed()
    {
        return instance;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks()
     */
    public synchronized List getBooks()
    {
        return new BookSet(books);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBook(java.lang.String)
     */
    public synchronized Book getBook(String name)
    {
        // Check name first
        // First check for exact matches
        Iterator iter = books.iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            if (name.equals(book.getName()))
            {
                return book;
            }
        }

        // Next check for case-insensitive matches
        iter = books.iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            if (name.equalsIgnoreCase(book.getName()))
            {
                return book;
            }
        }

        // Then check initials
        // First check for exact matches
        iter = books.iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            BookMetaData bmd = book.getBookMetaData();
            if (name.equals(bmd.getInitials()))
            {
                return book;
            }
        }

        // Next check for case-insensitive matches
        iter = books.iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            if (name.equalsIgnoreCase(book.getInitials()))
            {
                return book;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#getBooks(org.crosswire.jsword.book.BookFilter)
     */
    public synchronized List getBooks(BookFilter filter)
    {
        List temp = CollectionUtil.createList(new BookFilterIterator(getBooks(), filter));
        return new BookSet(temp);
    }

    /**
     * Get the maximum string length of a property
     * @param propertyKey The desired property
     * @return -1 if there is no match, otherwise the maximum length.
     */
    public int getMaxLength(String propertyKey)
    {
        int max = -1;
        List bookList = getBooks();
        Iterator iter = bookList.iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            Object property = book.getProperty(propertyKey);
            String value = property instanceof String ? (String) property : property.toString();
            max = Math.max(max, value == null ? -1 : value.length());
        }
        return max;
    }

    /**
     * Get the maximum string length of a property on a subset of books.
     * @param propertyKey The desired property
     * @param filter The filter
     * @return -1 if there is no match, otherwise the maximum length.
     */
    public int getMaxLength(String propertyKey, BookFilter filter)
    {
        int max = -1;
        List bookList = getBooks(filter);
        Iterator iter = bookList.iterator();
        while (iter.hasNext())
        {
            Book book = (Book) iter.next();
            Object property = book.getProperty(propertyKey);
            String value = property instanceof String ? (String) property : property.toString();
            max = Math.max(max, value == null ? -1 : value.length());
        }
        return max;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#addBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public synchronized void addBooksListener(BooksListener li)
    {
        listeners.add(BooksListener.class, li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookList#removeBooksListener(org.crosswire.jsword.book.BooksListener)
     */
    public synchronized void removeBooksListener(BooksListener li)
    {
        listeners.remove(BooksListener.class, li);
    }

    /**
     * Kick of an event sequence
     * @param source The event source
     * @param book The changed Book
     * @param added Is it added?
     */
    protected synchronized void fireBooksChanged(Object source, Book book, boolean added)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        BooksEvent ev = null;
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == BooksListener.class)
            {
                if (ev == null)
                {
                    ev = new BooksEvent(source, book, added);
                }

                if (added)
                {
                    ((BooksListener) contents[i + 1]).bookAdded(ev);
                }
                else
                {
                    ((BooksListener) contents[i + 1]).bookRemoved(ev);
                }
            }
        }
    }

    /**
     * Add a Book to the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public synchronized void addBook(Book book)
    {
        //log.debug("registering book: "+bmd.getName());

        books.add(book);
        fireBooksChanged(instance, book, true);
    }

    /**
     * Remove a Book from the current list of Books.
     * This method should only be called by BibleDrivers, it is not a method for
     * general consumption.
     */
    public synchronized void removeBook(Book book) throws BookException
    {
        //log.debug("unregistering book: "+bmd.getName());

        Activator.deactivate(book);

        boolean removed = books.remove(book);
        if (removed)
        {
            fireBooksChanged(instance, book, true);
        }
        else
        {
            throw new BookException(Msg.BOOK_NOREMOVE);
        }
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public synchronized BookDriver[] getDrivers()
    {
        return BookRegistry.getBookDrivers();
    }

    /**
     * Get an array of all the known drivers
     * @return Found int or the default value
     */
    public synchronized BookDriver[] getWritableDrivers()
    {
        return BookRegistry.getWritableDrivers();
    }


    public class BookRegistryListener implements BookRegistryEventListener
    {
        public void bookAdded(Book book)
        {
            addBook(book);
        }

        public void bookRemoved(Book book)
        {
            try
            {
                removeBook(book);
            }
            catch (BookException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * The list of Books
     */
    private BookSet books;

    /**
     * The list of listeners
     */
    private EventListenerList listeners;

    /**
     * The singleton instance.
     * This needs to be declared after all other statics it uses.
     */
    private static final Books instance = new Books();
}
