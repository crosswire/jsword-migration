package org.crosswire.bibledesktop.display.splitlist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.passage.PassageGuiUtil;
import org.crosswire.bibledesktop.passage.PassageListModel;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.PassageConstants;

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
public class SplitBookDataDisplay implements BookDataDisplay
{
    /**
     * Initialize the SplitBookDataDisplay
     */
    public SplitBookDataDisplay(BookDataDisplay child)
    {
        this.child = child;
        init();
        setActive();
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        /*
        tree.setModel(model);
        tree.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                selection();
            }
        });
        */

        //*
        model = new PassageListModel();
        model.setMode(PassageListModel.LIST_RANGES);
        model.setRestriction(PassageConstants.RESTRICT_CHAPTER);

        list = new JList(model);
        list.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                selection();
            }
        });
        //*/

        JScrollPane scroll = new JScrollPane();
        //scroll.getViewport().add(tree);
        scroll.getViewport().add(list);

        ActionFactory actions = new ActionFactory(SplitBookDataDisplay.class, this);

        actDelete = actions.getAction(DELETE_SELECTED);
        actBlur1 = actions.getAction(BLUR1);
        actBlur5 = actions.getAction(BLUR5);

        JButton delete = new JButton(actDelete);
        delete.setText(null);
        JButton blur1 = new JButton(actBlur1);
        blur1.setText(null);
        JButton blur5 = new JButton(actBlur5);
        blur5.setText(null);

        JPanel mutate = new JPanel(new FlowLayout());
        mutate.add(delete);
        mutate.add(blur1);
        mutate.add(blur5);

        JPanel data = new JPanel(new BorderLayout());
        data.add(mutate, BorderLayout.NORTH);
        data.add(scroll, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane();
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.add(data, JSplitPane.LEFT);
        split.add(child.getComponent(), JSplitPane.RIGHT);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(0.0D);
        split.setBorder(null);
        split.setDividerSize(8);

        main = new JPanel(new BorderLayout());
        main.add(split, BorderLayout.CENTER);
    }

    /**
     * Blur (expand) the current passage action by one verse on each side.
     * This bound by the boundaries of the Chapter.
     */
    public void doBlur1()
    {
        doBlur(1);
    }

    /**
     * Blur (expand) the current passage action by five verses on each side.
     * This bound by the boundaries of the Chapter.
     */
    public void doBlur5()
    {
        doBlur(5);
    }

    /**
     * Blur (expand) the current passage action by amount verses on each side.
     * This bound by the boundaries of the Chapter.
     * @param amount The amount of blurring
     */
    private void doBlur(int amount)
    {
        key.blur(amount, PassageConstants.RESTRICT_CHAPTER);
        setBookData(book, key);

        updateParents();
    }

    /**
     * Remove the selected verses out of the PassagePane.
     */
    public void doDeleteSelected()
    {
        /*
        PassageGuiUtil.deleteSelectedVersesFromTree(tree);
        Key updated = model.getKey();
        setBookData(book, updated);
        Passage ref = PassageUtil.getPassage(updated);
        */

        PassageGuiUtil.deleteSelectedVersesFromList(list);

        setBookData(book, model.getPassage());

        updateParents();
    }

    /**
     * Update the editable area with the current value
     */
    private void updateParents()
    {
        // TODO(joe): update the editable area with the current value
        // BibleViewPane view = ...
        // DisplaySelectPane psel = view.getSelectPane();
        // psel.setPassage(ref);
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
    public void setBookData(Book book, Key key)
    {
        boolean keyChanged = this.key == null || !this.key.equals(key);
        boolean bookChanged = this.book == null || !this.book.equals(book);

        this.book = book;
        this.key = key;

        //model = new KeyTreeModel(key);
        // Only set the passage if it has changed
        if (keyChanged)
        {
            log.debug("new passage chosen: " + key.getName()); //$NON-NLS-1$
            model.setPassage(KeyUtil.getPassage(key));
        }

        if (bookChanged || keyChanged)
        {
            if (bookChanged)
            {
                log.debug("new bible chosen: " + book); //$NON-NLS-1$
            }
            child.setBookData(book, key);
        }

        setActive();
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
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            //Key selected = PassageGuiUtil.getSelectedKeys(tree);
            Object[] selected = list.getSelectedValues();

            Key local = null;
            if (selected.length == 0)
            {
                local = key;
            }
            else
            {
                Key selectedKey = book.createEmptyKeyList();

                for (int i = 0; i < selected.length; i++)
                {
                    selectedKey.addAll((Key) selected[i]);
                }

                // if there was a single selection then show the whole chapter
//                if (selected.length == 1)
//                {
//                    selectedKey.blur(5, PassageConstants.RESTRICT_CHAPTER);
//                }

                local = selectedKey;
            }

            child.setBookData(book, local);
            setActive();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Make sure the correct buttons are made active
     */
    private void setActive()
    {
        Object[] selected = list.getSelectedValues();

        // make sure the mutator buttons are correctly active
        actDelete.setEnabled(selected.length != 0);
        boolean blurable = model.getSize() != 0;
        actBlur1.setEnabled(blurable);
        actBlur5.setEnabled(blurable);
    }

    private static final String BLUR1 = "Blur1"; //$NON-NLS-1$
    private static final String BLUR5 = "Blur5"; //$NON-NLS-1$
    private static final String DELETE_SELECTED = "DeleteSelected"; //$NON-NLS-1$

    /**
     * The whole passage that we are viewing
     */
    private Key key;

    /**
     * What book are we currently viewing?
     */
    private Book book;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(SplitBookDataDisplay.class);

    /*
     * GUI Components
     */
    private JPanel main;
    private BookDataDisplay child;
    private JList list;
    private PassageListModel model;
    private Action actDelete;
    private Action actBlur1;
    private Action actBlur5;
    /*
    private JTree tree = new JTree();
    private KeyTreeModel model = null;
    */
}
