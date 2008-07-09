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
 * ID: $Id:SplitBookDataDisplay.java 1369 2007-06-01 13:35:27Z dmsmith $
 */
package org.crosswire.bibledesktop.display.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.EventListenerList;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.bibledesktop.passage.KeySidebar;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookProvider;
import org.crosswire.jsword.passage.Key;

/**
 * A SplitBookDataDisplay consists of a KeySidebar and a BookDataDisplay in a SplitPane.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SplitBookDataDisplay extends JPanel implements BookProvider
{
    /**
     * Initialize the SplitBookDataDisplay
     */
    public SplitBookDataDisplay(KeySidebar sidebar, BookDataDisplay child)
    {
        this.child = child;
        this.sidebar = sidebar;
        listenerList = new EventListenerList();

        split = new FixedSplitPane();
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.setLeftComponent(sidebar);
        split.setRightComponent(child.getComponent());
        split.setOneTouchExpandable(true);
        split.setDividerLocation(0.0D);
        split.setBorder(null);
        split.setDividerSize(8);

        setLayout(new BorderLayout());
        add(split, BorderLayout.CENTER);
    }

    /**
     * @return Returns the sidebar.
     */
    public KeySidebar getSidebar()
    {
        return sidebar;
    }

    /**
     * @return Returns the display area.
     */
    public BookDataDisplay getBookDataDisplay()
    {
        return child;
    }

    /**
     * Set the books and/or key to display.
     * @param books
     * @param key
     */
    public void setBookData(Book[] books, Key key)
    {
        boolean keyChanged = child.getKey() == null || !child.getKey().equals(key);
        boolean bookChanged = child.getBooks() == null || !Arrays.equals(child.getBooks(), books);

       // Only set the passage if it has changed
        if (keyChanged)
        {
            log.debug("new passage chosen: " + key.getName()); //$NON-NLS-1$
        }

        if (bookChanged)
        {
            log.debug("new book(s) chosen: " + StringUtil.toString(books)); //$NON-NLS-1$
        }

        if (bookChanged || keyChanged)
        {
            child.setBookData(books, key);
        }
    }

    /**
     * Show or hide the passage sidebar.
     * @param show boolean
     */
    public void showSidebar(boolean show)
    {
        Component childComponent = child.getComponent();
        if (show)
        {
            remove(childComponent);
            split.add(childComponent, JSplitPane.RIGHT);
            add(split);
        }
        else
        {
            remove(split);
            split.remove(childComponent);
            add(childComponent);
        }

        // Force it to layout again.
        validate();
    }

    /**
     * @return the key
     */
    public Key getKey()
    {
        return child.getKey();
    }

    /**
     * @return the book
     */
    public Book[] getBooks()
    {
        return child.getBooks();
    }

    /**
     * Get the first book being displayed
     */
    public Book getFirstBook()
    {
        return child.getFirstBook();
    }

    /**
     * copy the child
     */
    public void copy()
    {
        child.copy();
    }

    /**
     * Add a listener for changes in the Key.
     * 
     * @param listener the listener to add
     */
    public synchronized void addKeyChangeListener(KeyChangeListener listener)
    {
        child.addKeyChangeListener(listener);
    }

    /**
     * Remove a listener for changes in the Key.
     * 
     * @param listener the listener to remove
     */
    public synchronized void removeKeyChangeListener(KeyChangeListener listener)
    {
        child.removeKeyChangeListener(listener);
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        // Broken but we don't serialize views
        child = null;
        is.defaultReadObject();
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SplitBookDataDisplay.class);

    /*
     * GUI Components
     */
    private KeySidebar sidebar;
    private JSplitPane split;
    private transient BookDataDisplay child;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257283643176202806L;
}
