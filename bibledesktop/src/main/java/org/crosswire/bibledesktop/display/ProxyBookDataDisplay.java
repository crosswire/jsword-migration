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
package org.crosswire.bibledesktop.display;

import java.awt.Component;
import java.beans.PropertyChangeEvent;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * An implementation of BookDataDisplay that simply proxies all requests to an
 * underlying BookDataDisplay.
 * <p>Useful for chaining a few BookDataDisplays together to add functionallity
 * component by component.</p>
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ProxyBookDataDisplay implements BookDataDisplay
{
    /**
     * Setup the proxy
     */
    public ProxyBookDataDisplay(BookDataDisplay proxy)
    {
        this.proxy = proxy;
    }

    /**
     * Accessor for the proxy
     * @return Returns the proxy.
     */
    protected BookDataDisplay getProxy()
    {
        return proxy;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        proxy.propertyChange(evt);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public void addURIEventListener(URIEventListener listener)
    {
        proxy.addURIEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public void removeURIEventListener(URIEventListener listener)
    {
        proxy.removeURIEventListener(listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#copy()
     */
    public void copy()
    {
        proxy.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return proxy.getComponent();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#clearBookData()
     */
    public void clearBookData()
    {
        setBookData(null, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book[], org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book[] books, Key key)
    {
        proxy.setBookData(books, key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        proxy.refresh();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    /* @Override */
    public String toString()
    {
        return proxy.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getKey()
     */
    public Key getKey()
    {
        return getProxy().getKey();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getBooks()
     */
    public Book[] getBooks()
    {
        return getProxy().getBooks();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getFirstBook()
     */
    public Book getFirstBook()
    {
        return getProxy().getFirstBook();
    }

    /**
     * The component to which we proxy
     */
    private BookDataDisplay proxy;
}
