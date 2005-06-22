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

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * An interface for all components that can display BookData.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface BookDataDisplay
{
    /**
     * Copy the selection to the clipboard
     */
    void copy();

    /**
     * Add a listener for when someone clicks on a browser 'link'
     * @param listener The listener to add
     */
    void addURLEventListener(URLEventListener listener);

    /**
     * Remove a listener for when someone clicks on a browser 'link'
     * @param listener The listener to remove
     */
    void removeURLEventListener(URLEventListener listener);

    /**
     * Accessor for the Swing component
     */
    Component getComponent();

    /**
     * Set the BookData to be displayed.
     * The data to be displayed is specified as a book and key rather than the
     * more obvious BookData (the result of reading a book using a key)
     * since some displays may wish so split up the display and only look up
     * smaller sections at a time.
     * @param book The Book to read data from
     * @param key The key to read from the given book
     */
    void setBookData(Book book, Key key);

    /**
     * Cause the BookData to be re-displayed.
     */
    void refresh();

    /**
     * The Book Key that we are displaying, or null if we are not displaying
     * anything
     * @return The current key
     */
    Key getKey();

    /**
     * Accessor for the Book used in the current display, or null if we are not
     * displaying anything.
     * @return The current book
     */
    Book getBook();
}
