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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.passage.KeyChangeEvent;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.bibledesktop.passage.KeySidebar;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * A quick Swing Bible display pane.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SplitBookDataDisplay extends JPanel
{
    /**
     * Initialize the SplitBookDataDisplay
     */
    public SplitBookDataDisplay(KeySidebar sidebar, BookDataDisplay child)
    {
        this.child = child;
        this.sidebar = sidebar;
        init();
    }

    /**
     * Create the GUI
     */
    private void init()
    {
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
     * @return Returns the sidebar.
     */
    public BookDataDisplay getBookDataDisplay()
    {
        return child;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book[] books, Key key)
    {
        boolean keyChanged = this.key == null || !this.key.equals(key);
        boolean bookChanged = this.books == null || !this.books.equals(books);

        this.books = books;
        this.key = key;

        // Only set the passage if it has changed
        if (keyChanged)
        {
            log.debug("new passage chosen: " + key.getName()); //$NON-NLS-1$
            fireKeyChanged(new KeyChangeEvent(this, key));
        }

        if (bookChanged || keyChanged)
        {
            if (bookChanged)
            {
                log.debug("new bible chosen: " + books); //$NON-NLS-1$
            }
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
        return key;
    }

    /**
     * @return the book
     */
    public Book[] getBooks()
    {
        return books;
    }

    /**
     * copy the child
     */
    public void copy()
    {
        child.copy();
    }

    /**
     * Add a command listener
     */
    public synchronized void addKeyChangeListener(KeyChangeListener listener)
    {
        List temp = new ArrayList(2);

        if (keyChangeListeners != null)
        {
            temp.addAll(keyChangeListeners);
        }

        if (!temp.contains(listener))
        {
            temp.add(listener);
            keyChangeListeners = temp;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void removeKeyChangeListener(KeyChangeListener listener)
    {
        if (keyChangeListeners != null && keyChangeListeners.contains(listener))
        {
            List temp = new ArrayList();
            temp.addAll(keyChangeListeners);

            temp.remove(listener);
            keyChangeListeners = temp;
        }
    }

    /**
     * Inform the command keyChangeListeners
     */
    protected synchronized void fireKeyChanged(KeyChangeEvent ev)
    {
        if (keyChangeListeners != null)
        {
            for (int i = 0; i < keyChangeListeners.size(); i++)
            {
                KeyChangeListener listener = (KeyChangeListener) keyChangeListeners.get(i);
                listener.keyChanged(ev);
            }
        }
    }

    /**
     * The whole passage that we are viewing
     */
    private Key key;

    /**
     * The listener for KeyChangeEvents
     */
    private transient List keyChangeListeners;

    /**
     * What books are we currently viewing?
     */
    private transient Book[] books;

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
