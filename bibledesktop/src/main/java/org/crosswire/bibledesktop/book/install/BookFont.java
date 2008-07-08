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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */

package org.crosswire.bibledesktop.book.install;

import java.awt.Font;

import org.crosswire.bibledesktop.desktop.XSLTProperty;
import org.crosswire.common.swing.FontStore;
import org.crosswire.common.swing.GuiConvert;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.Language;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookMetaData;

/**
 *
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookFont extends FontStore
{

    /**
     * Create a persistent Book Font Store.
     */
    private BookFont()
    {
        super("BookFonts", CWProject.instance().getWritableProjectDir()); //$NON-NLS-1$
    }

    public static BookFont instance()
    {
        return fonts;
    }

    /**
     * Set the font for the book.
     * 
     * @param book the book
     * @param font the font
     */
    public void setFont(Book book, Font font)
    {
        super.setFont(book.getInitials(), font);
    }

    /**
     * Get the most appropriate font for the book.
     * 
     * @param book the book
     * @return the font
     */
    public Font getFont(Book book)
    {
        String fontName = (String) book.getBookMetaData().getProperty(BookMetaData.KEY_FONT);
        String fontSpec = XSLTProperty.FONT.getStringState();
        if (fontName != null)
        {
            Font bookFont = GuiConvert.deriveFont(fontSpec, fontName);
            // Make sure it is installed. Java does substitution. Make sure we got what we wanted.
            if (bookFont.getFamily().equalsIgnoreCase(fontName))
            {
                fontSpec = GuiConvert.font2String(bookFont);
            }
        }

        return fonts.getFont(book.getInitials(), book.getLanguage(), fontSpec);
    }

    public Font getFont(Language language)
    {
        return fonts.getFont(null, language, null);
    }

    private static BookFont fonts = new BookFont();
}
