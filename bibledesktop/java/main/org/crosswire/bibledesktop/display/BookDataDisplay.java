package org.crosswire.bibledesktop.display;

import java.awt.Component;

import javax.swing.event.HyperlinkListener;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * An interface for all components that can display BookData.
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
public interface BookDataDisplay
{
    /**
     * Copy the selection to the clipboard
     */
    public void copy();

    /**
     * Add a listener for when someone clicks on a browser 'link'
     * @param li The listener to add
     */
    public void addHyperlinkListener(HyperlinkListener li);

    /**
     * Remove a listener for when someone clicks on a browser 'link'
     * @param li The listener to remove
     */
    public void removeHyperlinkListener(HyperlinkListener li);

    /**
     * Accessor for the Swing component
     */
    public Component getComponent();

    /**
     * Set the BookData to be displayed.
     * The data to be displayed is specified as a book and key rather than the
     * more obvious BookData (the result of reading a book using a key)
     * since some displays may wish so split up the display and only look up
     * smaller sections at a time.
     * @param book The Book to read data from
     * @param key The key to read from the given book
     */
    public void setBookData(Book book, Key key) throws BookException;

    /**
     * The Book Key that we are displaying, or null if we are not displaying
     * anything
     * @return The current key
     */
    public Key getKey();

    /**
     * Accessor for the Book used in the current display, or null if we are not
     * displaying anything.
     * @return The current book
     */
    public Book getBook();
}
