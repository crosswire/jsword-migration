package org.crosswire.bibledesktop.display.splitlist;

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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
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
    public void setBookData(Book book, Key key)
    {
        boolean keyChanged = this.key == null || !this.key.equals(key);
        boolean bookChanged = this.book == null || !this.book.equals(book);

        this.book = book;
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
                log.debug("new bible chosen: " + book); //$NON-NLS-1$
            }
            child.setBookData(book, key);
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


    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getBook()
     */
    public Book getBook()
    {
        return book;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        child.copy();
    }

//    /* (non-Javadoc)
//     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
//     */
//    public void addHyperlinkListener(HyperlinkListener li)
//    {
//        child.addHyperlinkListener(li);
//    }
//
//    /* (non-Javadoc)
//     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
//     */
//    public void removeHyperlinkListener(HyperlinkListener li)
//    {
//        child.removeHyperlinkListener(li);
//    }

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
    protected void fireKeyChanged(KeyChangeEvent ev)
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
     * What book are we currently viewing?
     */
    private Book book;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SplitBookDataDisplay.class);

    /*
     * GUI Components
     */
    private KeySidebar sidebar;
    private JSplitPane split;
    private BookDataDisplay child;
}
