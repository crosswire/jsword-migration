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
package org.crosswire.bibledesktop.book.install;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.crosswire.common.swing.CompositeIcon;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;

/**
 * Generates the appropriate icon for a book.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookIcon
{
    /**
     * Static class
     */
    private BookIcon()
    {
    }

    public static Icon getIcon(Book book)
    {
        Icon icon = ICON_OTHER;
        BookCategory type = book.getBookCategory();
        if (type.equals(BookCategory.BIBLE))
        {
            icon = ICON_BIBLE;
        }
        else if (type.equals(BookCategory.COMMENTARY))
        {
            icon = ICON_COMNT;
        }
        else if (type.equals(BookCategory.DICTIONARY))
        {
            icon = ICON_DICT;
        }
        else if (type.equals(BookCategory.GLOSSARY))
        {
            icon = ICON_GLOSS;
        }
        else if (type.equals(BookCategory.DAILY_DEVOTIONS))
        {
            icon = ICON_READ;
        }
        else
        {
            icon = ICON_OTHER;
        }

        if (book.isQuestionable())
        {
            icon = new CompositeIcon(icon, ICON_QUESTIONABLE, SwingConstants.CENTER);
        }

        if (!book.isSupported())
        {
            icon = new CompositeIcon(icon, ICON_UNSUPPORTED, SwingConstants.CENTER);
        }
        else if (book.isLocked())
        {
            icon = new CompositeIcon(icon, ICON_LOCKED, SwingConstants.CENTER);
        }

        return icon;
    }

    /**
     * The small version icon
     */
    private static final Icon ICON_BIBLE = GuiUtil.getIcon("images/book-b16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final Icon ICON_COMNT = GuiUtil.getIcon("images/book-c16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final Icon ICON_DICT = GuiUtil.getIcon("images/book-d16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final Icon ICON_READ = GuiUtil.getIcon("images/book-r16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final Icon ICON_GLOSS = GuiUtil.getIcon("images/book-g16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final Icon ICON_OTHER = GuiUtil.getIcon("images/book-o16.png"); //$NON-NLS-1$

    /**
     * The small version icon
     */
    private static final Icon ICON_QUESTIONABLE = GuiUtil.getIcon("images/overlay-q16.png"); //$NON-NLS-1$

    /**
     * An overlay icon
     */
    private static final Icon ICON_LOCKED = GuiUtil.getIcon("images/overlay-lock16.png"); //$NON-NLS-1$

    /**
     * An overlay icon
     */
    private static final Icon ICON_UNSUPPORTED = GuiUtil.getIcon("images/overlay-x16.png"); //$NON-NLS-1$

}
