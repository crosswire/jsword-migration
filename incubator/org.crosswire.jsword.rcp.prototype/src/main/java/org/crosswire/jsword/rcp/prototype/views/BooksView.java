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

import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class BooksView extends ViewPart
{
    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.booksview";
    private TableViewer viewer;

    public BooksView()
    {
        super();
    }

    public void createPartControl(Composite parent)
    {
        viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setSorter(new BookCategorySorter());
        viewer.setLabelProvider(new BookLabelProvider());
        getSite().setSelectionProvider(viewer);
        viewer.setInput(Books.installed().getBooks(BookFilters.getNonBibles()));
    }

    public void setFocus()
    {
        if (viewer != null && !viewer.getControl().isDisposed())
        {
            viewer.getControl().setFocus();
        }
    }
}
