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
package org.crosswire.bibledesktop.display.jdtb;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.net.URL;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * A JDK JTextPane implementation of an OSIS displayer.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JDTBBookDataDisplay implements BookDataDisplay
{
    /**
     * There has to be a better way to get to use custom URLs?
     */
    static
    {
        URL.setURLStreamHandlerFactory(new JDTBURLStreamHandlerFactory());
    }

    /**
     * Simple ctor
     */
    public JDTBBookDataDisplay()
    {
//        txtView = new WebBrowser();
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
        this.book = null;
        if (books != null && books.length > 0)
        {
            this.book = books[0];
        }

        this.key = key;

        refresh();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setCompareBooks(boolean)
     */
    public void setCompareBooks(boolean compare)
    {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        try
        {
            if (book == null && key == null)
            {
//                txtView.setURI();
                return;
            }

            // Make sure Hebrew displays from Right to Left
//            BookMetaData bmd = book.getBookMetaData();
//            boolean direction = bmd.isLeftToRight();
//            GuiUtil.applyOrientation(txtView, direction);

//            BookData bdata = new BookData(book, key);

//            txtView.setURI(JDTBURLConnection.createURL(book, key));
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Accessor for the Swing component
     */
    public Component getComponent()
    {
        return null; // txtView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#copy()
     */
    public void copy()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addKeyChangeListener(org.crosswire.bibledesktop.passage.KeyChangeListener)
     */
    public void addKeyChangeListener(KeyChangeListener listener)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeKeyChangeListener(org.crosswire.bibledesktop.passage.KeyChangeListener)
     */
    public void removeKeyChangeListener(KeyChangeListener listener)
    {
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public void addURIEventListener(URIEventListener listener)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public void removeURIEventListener(URIEventListener listener)
    {
    }

    /**
     * Forward the mouse listener to our child components
     */
/*    public void removeMouseListener(MouseListener li)
    {
//        txtView.removeMouseListener(li);
    }
*/
    /**
     * Forward the mouse listener to our child components
     */
/*    public void addMouseListener(MouseListener li)
    {
//        txtView.addMouseListener(li);
    }
*/
    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getKey()
     */
    public Key getKey()
    {
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getBooks()
     */
    public Book[] getBooks()
    {
        return new Book[] {book};
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getFirstBook()
     */
    public Book getFirstBook()
    {
        return book;
    }

    /**
     * The current book
     */
    private Book book;

    /**
     * The current key
     */
    private Key key;
}