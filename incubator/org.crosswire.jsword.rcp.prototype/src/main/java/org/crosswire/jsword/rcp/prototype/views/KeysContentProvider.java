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

import java.util.Iterator;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class KeysContentProvider implements IStructuredContentProvider
{

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose()
    {

    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
    }

    public Object[] getElements(Object inputElement)
    {
        if (!(inputElement instanceof Book))
        {
            throw new IllegalArgumentException();
        }
        
        Book book = (Book) inputElement;
        Key keys = book.getGlobalKeyList();
        Object[] elements = new Object[keys.getCardinality()];
        int i = 0;
        for (Iterator it = keys.iterator(); it.hasNext();)
        {
            elements[i++] = it.next();
        }

        return elements;
    }
}
