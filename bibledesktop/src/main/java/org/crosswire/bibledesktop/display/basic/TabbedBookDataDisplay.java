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
package org.crosswire.bibledesktop.display.basic;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.display.ScrolledBookDataDisplay;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.Passage;

/**
 * An inner component of Passage pane that can't show the list.
 * <p>At some stage we should convert this code to remove Passage so it
 * will work with all Books and not just Bibles. Code is included
 * (commented out) on how this could be done.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class TabbedBookDataDisplay implements BookDataDisplay
{
    /**
     * Simple Constructor
     */
    public TabbedBookDataDisplay()
    {
        pnlView = createInnerDisplayPane();

        init();

        center = pnlView.getComponent();
        pnlMain.add(center, BorderLayout.CENTER);
    }

    /**
     * GUI creation
     */
    private void init()
    {
        tabMain.setTabPlacement(SwingConstants.BOTTOM);
        tabMain.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                tabChanged();
            }
        });

        pnlMain.setLayout(new BorderLayout());
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return pnlMain;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#clearBookData()
     */
    public void clearBookData()
    {
        setBookData(null, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book[] books, Key newkey)
    {
        this.books = books;
        this.key = KeyUtil.getPassage(newkey);

        // Tabbed view or not we should clear out the old tabs
        tabMain.removeAll();
        views.clear();
        displays.clear();
        displays.add(pnlView);

        // So use purely Keys and not Passage, create a utility to cut up
        // a key into a number of keys.
        //   private Key keys;
        //   private Passage waiting;
        //   ...
        //   keys = null; // OSISUtil.pagenate(key, pagesize * 10);
        //   tabs = (keys.size() > 1);
        // And then inside the if:
        //   Key first = (Key) keys.get(0);
        // in place of the first/waiting code.
        // Then down in tabChanged()
        //   // What do we display next
        //   int countTabs = tabMain.getTabCount();
        //   Key next = (Key) keys.get(countTabs);
        // And a bit lower:
        //   // Do we need a new more tab
        //   if (countTabs >= keys.size())

        // Do we need a tabbed view
        tabs = key != null && key.countVerses() > pageSize;
        if (tabs)
        {
            // Calc the verses to display in this tab
            Passage first = (Passage) key.clone();
            waiting = first.trimVerses(pageSize);

            // Create the first tab
            BookDataDisplay pnlNew = createInnerDisplayPane();
            pnlNew.setBookData(books, first);

            Component display = pnlNew.getComponent();
            views.put(display, pnlNew);

            tabMain.add(getTabName(first), display);
            tabMain.add(Msg.MORE.toString(), pnlMore);

            setCenterComponent(tabMain);
        }
        else
        {
            pnlView.setBookData(books, key);

            setCenterComponent(pnlView.getComponent());
        }

        // Since we changed the contents of the page we need to cause it to repaint
        GuiUtil.refresh(center);
        GuiUtil.refresh(pnlMain);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        // Now go through all the known tabs and refresh each
        Iterator iter = displays.iterator();
        while (iter.hasNext())
        {
            BookDataDisplay bdd = (BookDataDisplay) iter.next();
            bdd.refresh();
        }
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
    public Book[] getBooks()
    {
        return books;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getFirstBook()
     */
    public Book getFirstBook()
    {
        return books != null && books.length > 0 ? books[0] : null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#copy()
     */
    public void copy()
    {
        getInnerDisplayPane().copy();
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
        // Now go through all the known syncs and add this one in
        Iterator iter = displays.iterator();
        while (iter.hasNext())
        {
            BookDataDisplay bdd = (BookDataDisplay) iter.next();
            bdd.propertyChange(evt);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public synchronized void addURIEventListener(URIEventListener listener)
    {
        // First add to our list of listeners so when we get more event syncs
        // we can add this new listener to the new sync
        List temp = new ArrayList();
        if (hyperlis == null)
        {
            temp.add(listener);
            hyperlis = temp;
        }
        else
        {
            temp.addAll(hyperlis);

            if (!temp.contains(listener))
            {
                temp.add(listener);
                hyperlis = temp;
            }
        }

        // Now go through all the known syncs and add this one in
        Iterator iter = displays.iterator();
        while (iter.hasNext())
        {
            BookDataDisplay bdd = (BookDataDisplay) iter.next();
            bdd.addURIEventListener(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public synchronized void removeURIEventListener(URIEventListener listener)
    {
        // First remove from the list of listeners
        if (hyperlis != null && hyperlis.contains(listener))
        {
            List temp = new ArrayList();
            temp.addAll(hyperlis);
            temp.remove(listener);
            hyperlis = temp;
        }

        // Now remove from all the known syncs
        Iterator iter = displays.iterator();
        while (iter.hasNext())
        {
            BookDataDisplay bdd = (BookDataDisplay) iter.next();
            bdd.removeURIEventListener(listener);
        }
    }

    /**
     * Make a new component reside in the center of this panel
     */
    private void setCenterComponent(Component comp)
    {
        // And show it is needed
        if (center != comp)
        {
            pnlMain.remove(center);
            center = comp;
            pnlMain.add(center, BorderLayout.CENTER);
        }
    }

    /**
     * Tabs changed, generate some stuff
     */
    /*private*/ final void tabChanged()
    {
        // This is someone clicking on more isnt it?
        if (tabMain.getSelectedComponent() != pnlMore)
        {
            return;
        }

        // First remove the old more ... tab that the user has just selected
        tabMain.remove(pnlMore);

        // What do we display next
        Passage next = waiting;
        waiting = next.trimVerses(pageSize);

        // Create a new tab
        BookDataDisplay pnlNew = createInnerDisplayPane();
        pnlNew.setBookData(books, next);

        Component display = pnlNew.getComponent();
        views.put(display, pnlNew);

        tabMain.add(getTabName(next), display);

        // Do we need a new more tab
        if (waiting != null)
        {
            tabMain.add(Msg.MORE.toString(), pnlMore);
        }

        // Select the real new tab in place of any more tabs
        tabMain.setSelectedComponent(display);
    }

    /**
     * Accessor for the current TextComponent
     */
    public BookDataDisplay getInnerDisplayPane()
    {
        if (tabs)
        {
            Object o = tabMain.getSelectedComponent();
            JScrollPane sp = (JScrollPane) o;
            return (BookDataDisplay) views.get(sp);
        }
        return pnlView;
    }

    /**
     * Tab creation helper
     */
    private synchronized BookDataDisplay createInnerDisplayPane()
    {
        BookDataDisplay display = new ScrolledBookDataDisplay(BookDataDisplayFactory.createBookDataDisplay());
        displays.add(display);

        // Add all the known listeners to this new BookDataDisplay
        if (hyperlis != null)
        {
            Iterator iter = hyperlis.iterator();
            while (iter.hasNext())
            {
                URIEventListener li = (URIEventListener) iter.next();
                display.addURIEventListener(li);
            }
        }

        return display;
    }

    /**
     * Accessor for the page size
     */
    public static void setPageSize(int pageSize)
    {
        TabbedBookDataDisplay.pageSize = pageSize;
    }

    /**
     * Accessor for the page size
     */
    public static int getPageSize()
    {
        return pageSize;
    }

    /**
     * Ensure that the tab names are not too long - 25 chars max
     * @param key The key to get a short name from
     * @return The first 9 chars followed by ... followed by the last 9
     */
    private static String getTabName(Key key)
    {
        String tabname = key.getName();
        int len = tabname.length();
        if (len > TITLE_LENGTH)
        {
            tabname = tabname.substring(0, 9) + " ... " + tabname.substring(len - 9, len); //$NON-NLS-1$
        }

        return tabname;
    }

    /**
     * What is the max length for a tab title
     */
    private static final int TITLE_LENGTH = 25;

    /**
     * How many verses on a tab.
     */
    private static int pageSize = 50;

    /**
     * A list of all the URIEventListeners
     */
    private List hyperlis;

    /**
     * The passage that we are displaying (in one or more tabs)
     */
    private Passage key;

    /**
     * The verses that we have not created tabs for yet
     */
    private Passage waiting;

    /**
     * The version used for display
     */
    private Book[] books;

    /**
     * Are we using tabs?
     */
    private boolean tabs;

    /**
     * If we are using tabs, this is the main view
     */
    private JTabbedPane tabMain = new JTabbedPane();

    /**
     * If we are not using tabs, this is the main view
     */
    private BookDataDisplay pnlView;

    /**
     * An map of compnents to their views
     */
    private Map views = new HashMap();

    /**
     * A list of all the InnerDisplayPanes so we can control listeners
     */
    private List displays = new ArrayList();

    /**
     * Pointer to whichever of the above is currently in use
     */
    private Component center;

    /**
     * Blank thing for the "More..." button
     */
    private JPanel pnlMore = new JPanel();

    /**
     * The top level component
     */
    private JPanel pnlMain = new JPanel();
}
