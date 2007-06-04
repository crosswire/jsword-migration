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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;

/**
 * A picker of more than one book at a time.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ParallelBookPicker extends JPanel
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
        selected = new ArrayList();
        listeners = new EventListenerList();
        actions = new ActionFactory(ParallelBookPicker.class, this);


        // Add the first picker
        doAddPicker();
        doAddPicker();
    }

    /**
     * Add an new picker.
     */
    public void doAddPicker()
    {
        JPanel pickerPanel = new JPanel();

        int currentPickerCount = getComponentCount();

        // If there is more than one picker, then we need to remove the buttons from the
        // previous one, since we only want the last one to have the buttons.
        Container lastPanel = null;
        if (currentPickerCount > 0)
        {
            Component last = getComponent(currentPickerCount - 1);
            if (last instanceof Container)
            {
                lastPanel = (Container) last;
            }
        }

        BooksComboBoxModel mdlBook = new BooksComboBoxModel(filter, comparator);
        JComboBox cboBook = new JComboBox(mdlBook);
        cboBook.setRenderer(new BookListCellRenderer(true));
        cboBook.addItemListener(new SelectedItemListener(currentPickerCount));
        cboBook.addActionListener(new SelectedActionListener());
        pickerPanel.add(cboBook);
        add(pickerPanel);
        currentPickerCount++;

        // Before adding, we make sure that the previous entry's buttons are removed.
        if (currentPickerCount > 1)
        {
            while (lastPanel.getComponentCount() > 1)
            {
                lastPanel.remove(1);
            }
        }

        addButtons(pickerPanel, currentPickerCount);

        Book book = mdlBook.getSelectedBook();
        selected.add(book);
        if (book != null)
        {
            cboBook.setToolTipText(book.getName());
        }

        GuiUtil.refresh(this);
    }

    /**
     * Remove the last picker provided that there will be one that remains.
     */
    public void doRemovePicker()
    {
        int size = getComponentCount();
        if (size > 1)
        {
            remove(size - 1);
            size = getComponentCount();
            Component comp = getComponent(size - 1);
            if (comp instanceof JPanel)
            {
                addButtons((JPanel) comp, size - 1); 
            }
            GuiUtil.refresh(this);
        }

    }

    /**
     * What are the currently selected Books?
     */
    public Book[] getBooks()
    {
        return (Book[]) selected.toArray(new Book[selected.size()]);
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

    private void addButtons(JPanel pickerPanel, int currentPickerCount)
    {
        // If there are more than one picker, we allow a user to remove a picker
        if (currentPickerCount > 0)
        {
            pickerPanel.add(new JButton(actions.getAction("RemovePicker"))); //$NON-NLS-1$
        }

        // Only allow the user to add a certain amount of pickers.
        if (currentPickerCount < MAX_PICKERS)
        {
            pickerPanel.add(new JButton(actions.getAction("AddPicker"))); //$NON-NLS-1$
        }
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
        // We don't serialize views
        selected = null;

        listeners = new EventListenerList();

        is.defaultReadObject();
    }

    /**
     * An ItemListener for a particular combo box that tracks it's selected item.
     */
    final class SelectedItemListener implements ItemListener
    {
        /**
         * Track the selected item in the combo box indicated by index.
         * @param index
         */
        public SelectedItemListener(int index)
        {
            this.index = index;
        }

        /* (non-Javadoc)
         * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
         */
        public void itemStateChanged(ItemEvent ev)
        {
            if (ev.getStateChange() == ItemEvent.SELECTED)
            {
                JComboBox combo = (JComboBox) ev.getSource();
                
                selected.set(index, combo.getSelectedItem());

                fireBooksChosen(new BookSelectEvent(this, getBooks()));
                combo.setToolTipText(selected.toString());
            }
        }

        private int index;
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
    private BookFilter filter;

    /**
     * The comparator to order the books.
     */
    private Comparator comparator;

    /**
     * The selected items in each combo.
     */
    protected transient List selected;

    /**
     * Allow for adding and removing pickers.
     */
    private ActionFactory actions;

    /**
     * Who is interested in things this DisplaySelectPane does
     */
    private transient EventListenerList listeners;

    /**
     * What is the default maximum number of pickers.
     */
    private static final int MAX_PICKERS = 5;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 1633401996774729671L;
}
