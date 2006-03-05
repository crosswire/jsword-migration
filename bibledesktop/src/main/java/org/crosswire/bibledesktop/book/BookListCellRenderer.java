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

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.crosswire.jsword.book.Book;

/**
 * A custom list view that paints icons alongside the words.
 * This was a simple modification of DefaultListCellRenderer however something
 * has made us implement ListCellRenderer directory and I'm not sure what.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookListCellRenderer extends JLabel implements ListCellRenderer
{
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public BookListCellRenderer()
    {
        setOpaque(true);
        setBorder(noFocus);
    }

    /**
     * This is the only method defined by ListCellRenderer.  We just
     * reconfigure the Jlabel each time we're called.
     * @param list The JLists that we are part of
     * @param value Value to display
     * @param index Cell index
     * @param selected Is the cell selected
     * @param focus Does the list and the cell have the focus
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        if (selected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value == null)
        {
            setText(Msg.NONE.toString());
            setToolTipText(null);
            setIcon(null);
            setEnabled(false);
        }

        // Hack to allow us to use PROTOTYPE_BOOK_NAME as a prototype value
        if (value instanceof String)
        {
            String str = (String) value;

            setText(str);
            setToolTipText(null);
            setIcon(null);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocus); //$NON-NLS-1$
        }

        if (value instanceof Book)
        {
            Book book = (Book) value;

            String displayName = book.toString();
            setText(displayName);
            setToolTipText(displayName);

            setIcon(BookIcon.getIcon(book));
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder(focus ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocus); //$NON-NLS-1$
        }

        return this;
    }

    /**
     * border if we do not have focus
     */
    private static Border noFocus = BorderFactory.createEmptyBorder(1, 1, 1, 1);

    /**
     * Make sure that book names are not too wide
     */
    public static final String PROTOTYPE_BOOK_NAME = "012345678901234567890123456789"; //$NON-NLS-1$

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978138859576308017L;
}
