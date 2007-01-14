/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2006
 *
 */
package org.crosswire.jsword.rcp.prototype.views;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class BookCategorySorter extends ViewerSorter
{
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
     */
    public int category(Object element)
    {
        BookCategory category;
        if (element instanceof Book)
        {
            category = ((Book) element).getBookCategory();
        } else if (element instanceof BookCategory) {
            category = (BookCategory) element;
        } else {
            return -1;
        }

        if (category.equals(BookCategory.COMMENTARY)) {
            return 10;
        } else if (category.equals(BookCategory.DAILY_DEVOTIONS)) {
            return 20;
        } else if (category.equals(BookCategory.DICTIONARY)) {
            return 30;
        } else if (category.equals(BookCategory.GLOSSARY)) {
            return 40;
        } else if (category.equals(BookCategory.OTHER)) {
            return 50;
        } else if (category.equals(BookCategory.QUESTIONABLE)) {
            return 60;
        }
        
        return super.category(element);
    }

}
