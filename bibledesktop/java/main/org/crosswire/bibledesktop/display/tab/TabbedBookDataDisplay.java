package org.crosswire.bibledesktop.display.tab;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.bibledesktop.display.scrolled.ScrolledBookDataDisplay;
import org.crosswire.common.util.Reporter;
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

        // NOTE: when we tried dynamic laf update, these needed special treatment
        // There are times when tab_main or pnl_view are not in visible or
        // attached to the main widget hierachy, so when we change L&F the
        // changes do not get propogated through. The solution is to register
        // them with the L&F handler to be altered when the L&F changes.
        //LookAndFeelUtil.addComponentToUpdate(pnlView);
        //LookAndFeelUtil.addComponentToUpdate(tabMain);
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
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key newkey)
    {
        this.book = book;
        this.key = KeyUtil.getPassage(newkey);

        // Tabbed view or not we should clear out the old tabs
        tabMain.removeAll();
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
            pnlNew.setBookData(book, first);

            Component display = pnlNew.getComponent();

            tabMain.add(getTabName(first), display);
            tabMain.add(Msg.MORE.toString(), pnlMore);

            setCenterComponent(tabMain);
        }
        else
        {
            pnlView.setBookData(book, key);

            setCenterComponent(pnlView.getComponent());
        }

        // there was a time when we needed to do pnlMain.repaint();
        // but we don't seem to have a problem now
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

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        getInnerDisplayPane().copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public synchronized void addURLEventListener(URLEventListener listener)
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
        for (Iterator it = displays.iterator(); it.hasNext(); )
        {
            BookDataDisplay idp = (BookDataDisplay) it.next();
            idp.addURLEventListener(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public synchronized void removeURLEventListener(URLEventListener listener)
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
        for (Iterator it = displays.iterator(); it.hasNext(); )
        {
            BookDataDisplay idp = (BookDataDisplay) it.next();
            idp.removeURLEventListener(listener);
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
    protected void tabChanged()
    {
        try
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
            pnlNew.setBookData(book, next);

            Component display = pnlNew.getComponent();
            tabMain.add(getTabName(next), display);

            // Do we need a new more tab
            if (waiting != null)
            {
                tabMain.add(Msg.MORE.toString(), pnlMore);
            }

            // Select the real new tab in place of any more tabs
            tabMain.setSelectedComponent(display);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Accessor for the current TextComponent
     */
    private BookDataDisplay getInnerDisplayPane()
    {
        if (tabs)
        {
            return (BookDataDisplay) tabMain.getSelectedComponent();
        }
        else
        {
            return pnlView;
        }
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
            for (Iterator it = hyperlis.iterator(); it.hasNext(); )
            {
                URLEventListener li = (URLEventListener) it.next();
                display.addURLEventListener(li);
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
     * A list of all the HyperlinkListeners
     */
    private transient List hyperlis = null;

    /**
     * The passage that we are displaying (in one or more tabs)
     */
    private Passage key = null;

    /**
     * The verses that we have not created tabs for yet
     */
    private Passage waiting = null;

    /**
     * The version used for display
     */
    private Book book = null;

    /**
     * Are we using tabs?
     */
    private boolean tabs = false;

    /**
     * If we are using tabs, this is the main view
     */
    private JTabbedPane tabMain = new JTabbedPane();

    /**
     * If we are not using tabs, this is the main view
     */
    private BookDataDisplay pnlView = null;

    /**
     * A list of all the InnerDisplayPanes so we can control listeners
     */
    private List displays = new ArrayList();

    /**
     * Pointer to whichever of the above is currently in use
     */
    private Component center = null;

    /**
     * Blank thing for the "More..." button
     */
    private JPanel pnlMore = new JPanel();

    /**
     * The top level component
     */
    private JPanel pnlMain = new JPanel();
}
