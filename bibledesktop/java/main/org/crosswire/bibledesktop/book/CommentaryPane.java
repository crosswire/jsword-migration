package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.BookDataDisplayFactory;
import org.crosswire.bibledesktop.display.scrolled.ScrolledBookDataDisplay;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * Builds a set of tabs from the list of Books returned by a filtered list
 * of Books.
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
public class CommentaryPane extends JPanel implements BookDataDisplay
{
    /**
     * Simple constructor that uses all the Books
     */
    public CommentaryPane()
    {
        init();
    }

    /**
     * Initialise the GUI
     */
    private void init()
    {
        set.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        set.setBookComboBox(cboBooks);
        set.setChapterComboBox(cboChaps);
        set.setVerseComboBox(cboVerse);

        cboBooks.setToolTipText(Msg.SELECT_BOOK.toString());
        cboChaps.setToolTipText(Msg.SELECT_CHAPTER.toString());
        cboVerse.setToolTipText(Msg.SELECT_VERSE.toString());

        pnlSelect.setLayout(new FlowLayout());
        pnlSelect.add(cboBooks, null);
        pnlSelect.add(cboChaps, null);
        pnlSelect.add(cboVerse, null);

        cboComments.setModel(mdlcomments);
        cboComments.setRenderer(new BookListCellRenderer());
        cboComments.setPrototypeDisplayValue(BookListCellRenderer.PROTOTYPE_BOOK_NAME);
        cboComments.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updateDisplay();
            }
        });

        pnlTop.setLayout(new BorderLayout());
        pnlTop.add(pnlSelect, BorderLayout.NORTH);
        pnlTop.add(cboComments, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.add(pnlTop, BorderLayout.NORTH);
        this.add(display.getComponent(), BorderLayout.CENTER);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getComponent()
     */
    public Component getComponent()
    {
        return this;
    }

    /**
     * 
     */
    protected void updateDisplay()
    {
        BookMetaData bmd = (BookMetaData) cboComments.getSelectedItem();
        if (bmd == null)
        {
            return;
        }

        try
        {
            Verse verse = set.getVerse();
            display.setBookData(bmd.getBook(), verse);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        display.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.FocusablePart#getBook()
     */
    public Book getBook()
    {
        BookMetaData bmd = (BookMetaData) cboComments.getSelectedItem();
        if (bmd == null)
        {
            return null;
        }

        return bmd.getBook();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        BookMetaData bmd = book.getBookMetaData();
        cboComments.setSelectedItem(bmd);

        Passage newref = PassageUtil.getPassage(key);
        setPassage(newref);
    }

    /**
     * Accessor for the current passage
     */
    public void setPassage(Passage ref)
    {
        this.ref = ref;

        if (ref != null && ref.countVerses() > 0)
        {
            set.setVerse(ref.getVerseAt(0));
            updateDisplay();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        display.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        display.removeHyperlinkListener(li);
    }

    /**
     * Last displayed
     */
    protected Passage ref = null;

    /**
     * To get us just the Commentaries
     */
    private BookFilter filter = BookFilters.getCommentaries();

    /**
     * The BookData display component
     */
    private BookDataDisplay display = new ScrolledBookDataDisplay(BookDataDisplayFactory.createBookDataDisplay());

    /*
     * GUI components
     */
    private BooksComboBoxModel mdlcomments = new BooksComboBoxModel(filter);
    protected BibleComboBoxModelSet set = new BibleComboBoxModelSet();
    protected JComboBox cboComments = new JComboBox();
    private JComboBox cboBooks = new JComboBox();
    private JComboBox cboChaps = new JComboBox();
    private JComboBox cboVerse = new JComboBox();
    private JPanel pnlSelect = new JPanel();
    private JPanel pnlTop = new JPanel();
}
