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
package org.crosswire.bibledesktop.book;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookProvider;

/**
 * A picker of more than one book at a time.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ParallelBookPicker extends JPanel implements BookProvider
{

    /**
     * General constructor
     * @param filter the kinds of books to pick.
     * @param comparator the order to put the books in
     */
    public ParallelBookPicker(BookFilter filter, Comparator comparator)
    {
        this.filter = filter;
        this.comparator = comparator;
        initialize();
    }

    /**
     * Initialize the GUI
     */
    private void initialize()
    {
        setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
        listeners = new EventListenerList();
        actions = new ActionFactory(ParallelBookPicker.class, this);

        JPanel buttonBox = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        buttonBox.add(actions.createJButton("RemovePicker")); //$NON-NLS-1$
        buttonBox.add(actions.createJButton("AddPicker")); //$NON-NLS-1$
        add(buttonBox);

        // Add the first picker
        doAddPicker();
    }

    /**
     * Add an new picker.
     */
    public void doAddPicker()
    {
        BooksComboBoxModel mdlBook = new BooksComboBoxModel(filter, comparator);
        JComboBox cboBook = new JComboBox(mdlBook);
        cboBook.setRenderer(new BookListCellRenderer(true));
        cboBook.addItemListener(new SelectedItemListener(this));
        cboBook.addActionListener(new SelectedActionListener());
        add(cboBook);

        Book book = mdlBook.getSelectedBook();
        if (book != null)
        {
            cboBook.setToolTipText(book.getName());
        }

        enableButtons();
        GuiUtil.applyDefaultOrientation(this);
        GuiUtil.refresh(this);
        fireBooksChosen(new BookSelectEvent(this));
    }

    /**
     * Remove the last picker provided that there will be one that remains.
     */
    public void doRemovePicker()
    {
        // There should always be 2 components present:
        // the first picker and the panel holding the add/remove buttons
        int size = getComponentCount();
        if (size > 2)
        {
            remove(size - 1);
            enableButtons();
            GuiUtil.refresh(this);
            fireBooksChosen(new BookSelectEvent(this));
        }

    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookProvider#getBooks()
     */
    public Book[] getBooks()
    {
        List books = new ArrayList();
        int count = getComponentCount();
        for (int i = 1; i < count; i++)
        {
            Component comp = getComponent(i);
            if (comp instanceof JComboBox)
            {
                JComboBox combo = (JComboBox) comp;
                Object book = combo.getSelectedItem();
                if (book != null)
                {
                    books.add(book);
                }
            }
        }
        return (Book[]) books.toArray(new Book[books.size()]);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookProvider#getFirstBook()
     */
    public Book getFirstBook()
    {
        int count = getComponentCount();
        for (int i = 1; i < count; i++)
        {
            Component comp = getComponent(i);
            if (comp instanceof JComboBox)
            {
                JComboBox combo = (JComboBox) comp;
                return (Book) combo.getSelectedItem();
            }
        }
        return null;
    }

    /**
     * @return the maxPickers
     */
    public static int getMaxPickers()
    {
        return maxPickers;
    }

    /**
     * @param maxPickers the maxPickers to set
     */
    public static void setMaxPickers(int maxPickers)
    {
        ParallelBookPicker.maxPickers = maxPickers;
    }

    /**
     * Add a BookSelectListener listener
     */
    public synchronized void addBookListener(BookSelectListener li)
    {
        listeners.add(BookSelectListener.class, li);
    }

    /**
     * Remove a BookSelectListener listener
     */
    public synchronized void removeBookListener(BookSelectListener li)
    {
        listeners.remove(BookSelectListener.class, li);
    }

    /**
     * Inform the version listeners
     */
    protected void fireBooksChosen(BookSelectEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == BookSelectListener.class)
            {
                ((BookSelectListener) contents[i + 1]).booksChosen(ev);
            }
        }
    }

    public void enableButtons()
    {
        int count = getComponentCount() - 1;
        actions.getAction("RemovePicker").setEnabled(count > 1); //$NON-NLS-1$
        actions.getAction("AddPicker").setEnabled(count < maxPickers); //$NON-NLS-1$
        getComponent(0).setVisible(maxPickers >= 2 || count > maxPickers);
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
        listeners = new EventListenerList();
        actions = new ActionFactory(ParallelBookPicker.class, this);

        is.defaultReadObject();
    }

    /**
     * An ItemListener for a particular combo box that tracks it's selected item.
     */
    final class SelectedItemListener implements ItemListener, BookProvider
    {
        SelectedItemListener(ParallelBookPicker picker)
        {
            this.picker = picker;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(ItemEvent ev)
        {
            if (ev.getStateChange() == ItemEvent.SELECTED)
            {
                JComboBox combo = (JComboBox) ev.getSource();

                Book selected = (Book) combo.getSelectedItem();

                fireBooksChosen(new BookSelectEvent(this));
                combo.setToolTipText(selected.getName());
            }
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookProvider#getBooks()
         */
        public Book[] getBooks()
        {
            return picker.getBooks();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BookProvider#getFirstBook()
         */
        public Book getFirstBook()
        {
            return picker.getFirstBook();
        }

        private ParallelBookPicker picker;
    }

    /**
     * Ensures that something is always selected.
     */
    static final class SelectedActionListener implements ActionListener
    {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JComboBox cbo = (JComboBox) e.getSource();
            if (cbo.getSelectedIndex() == -1 && cbo.getItemCount() > 0)
            {
                cbo.setSelectedIndex(0);
            }
        }
    }

    /**
     * The filter to apply
     */
    private transient BookFilter filter;

    /**
     * The comparator to order the books.
     */
    private transient Comparator comparator;

    /**
     * Allow for adding and removing pickers.
     */
    private transient ActionFactory actions;

    /**
     * Who is interested in things this DisplaySelectPane does
     */
    private transient EventListenerList listeners;

    /**
     * What is the default maximum number of pickers.
     */
    private static final int MAX_PICKERS = 5;

    /**
     * The maximum number of pickers.
     */
    private static int maxPickers = MAX_PICKERS;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 1633401996774729671L;
}
