package org.crosswire.bibledesktop.display.splitlist;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.bibledesktop.book.DisplaySelectPane;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.passage.PassageGuiUtil;
import org.crosswire.bibledesktop.passage.PassageListModel;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageConstants;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.VerseRange;

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
 * @version $Id$
 */
public class OuterDisplayPane implements BookDataDisplay
{
    /**
     * Initialize the OuterDisplayPane
     */
    public OuterDisplayPane(BookDataDisplay child)
    {
        this.child = child;
        init();
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        model.setMode(PassageListModel.LIST_RANGES);
        model.setRestriction(PassageConstants.RESTRICT_CHAPTER);

        list.setModel(model);
        list.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                selection();
            }
        });

        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.add(scroll, JSplitPane.LEFT);
        split.add(child.getComponent(), JSplitPane.RIGHT);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(0.0D);

        scroll.getViewport().add(list);

        main.setLayout(new BorderLayout());
        main.add(split, BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getComponent()
     */
    public Component getComponent()
    {
        return main;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key newkey) throws BookException
    {
        this.book = book;
        this.key = PassageUtil.getPassage(newkey);

        model.setPassage(key);
        child.setBookData(book, key);
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

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        child.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        child.removeHyperlinkListener(li);
    }

    /**
     * Delete the selected verses
     */
    public void deleteSelected(BibleViewPane view)
    {
        PassageGuiUtil.deleteSelectedVersesFromList(list);

        // Update the text box
        key = model.getPassage();
        DisplaySelectPane psel = view.getSelectPane();
        psel.setPassage(key);
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            Object[] ranges = list.getSelectedValues();

            Passage local = null;
            if (ranges.length == 0)
            {
                local = key;
            }
            else
            {
                local = PassageFactory.createPassage();
                for (int i=0; i<ranges.length; i++)
                {
                    local.add((VerseRange) ranges[i]);
                }

                // if there was a single selection then show the whole chapter
                if (ranges.length == 1)
                {
                    local.blur(1000, PassageConstants.RESTRICT_CHAPTER);
                }
            }

            setBookData(book, local);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The whole passage that we are viewing
     */
    private Passage key = null;

    /**
     * What book are we currently viewing?
     */
    private Book book = null;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(OuterDisplayPane.class);

    /*
     * GUI Components
     */
    private JSplitPane split = new JSplitPane();
    private JScrollPane scroll = new JScrollPane();
    private JPanel main = new JPanel();
    private JList list = new JList();
    private PassageListModel model = new PassageListModel();
    private BookDataDisplay child = null;
}
