package org.crosswire.bibledesktop.display.jdtb;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.event.HyperlinkListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.jdesktop.jdic.browser.WebBrowser;

/**
 * A JDK JTextPane implementation of an OSIS displayer.
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
        txtView = new WebBrowser();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        this.book = book;
        this.key = key;

        try
        {
            if (book == null && key == null)
            {
                txtView.setURL();
                return;
            }

            // Make sure Hebrew displays from Right to Left
            BookMetaData bmd = book.getBookMetaData();
            boolean direction = bmd.isLeftToRight();
            txtView.applyComponentOrientation(direction ? ComponentOrientation.LEFT_TO_RIGHT : ComponentOrientation.RIGHT_TO_LEFT);

            BookData bdata = book.getData(key);
            if (bdata == null)
            {
                txtView.setURL();
                return;
            }

            txtView.setURL(JDTBURLConnection.createURL(book, key));
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
        return txtView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    {
        txtView.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    {
        txtView.addMouseListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getKey()
     */
    public Key getKey()
    {
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getBook()
     */
    public Book getBook()
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

    /**
     * The display component
     */
    private WebBrowser txtView;
}