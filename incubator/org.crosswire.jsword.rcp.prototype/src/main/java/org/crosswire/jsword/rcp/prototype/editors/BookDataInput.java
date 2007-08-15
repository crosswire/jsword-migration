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
package org.crosswire.jsword.rcp.prototype.editors;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.rcp.prototype.factories.InputElementFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * This class is a book data-driven editor input. 
 * 
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class BookDataInput implements IEditorInput
{

    public static final String INPUT_TYPE = "book data";
    private String bookId;
    private String bookKey;

    public BookDataInput(String bookId, String bookKey)
    {
        this.bookId = bookId;
        this.bookKey = bookKey;
    }

    public boolean exists()
    {
        return false;
    }

    public ImageDescriptor getImageDescriptor()
    {
        return ImageDescriptor.getMissingImageDescriptor();
    }

    public String getName()
    {
        return bookId + ":" + bookKey;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable()
    {
        return new IPersistableElement()
        {
            public String getFactoryId()
            {
                return InputElementFactory.FACTORY_ID;
            }

            public void saveState(IMemento memento)
            {
                IMemento child = memento.createChild("input");
                child.putString("type", INPUT_TYPE);
                child.putString("bookId", bookId);
                child.putString("bookKey", bookKey);
            }
        };
    }

    /**
     * @param child
     * @return
     */
    public static BookDataInput createFromState(IMemento memento)
    {
        String bookId = memento.getString("bookId");
        String bookKey = memento.getString("bookKey");
        return new BookDataInput(bookId, bookKey);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText()
    {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter)
    {
        if (adapter.isAssignableFrom(BookDataInput.class))
        {
            return this;
        }

        if (adapter.equals(BookData.class))
        {
            Book book = Books.installed().getBook(bookId);
            if (book == null)
            {
                //TODO handle this better
                return null;
            }
            Key key = book.getValidKey(bookKey);
            return new BookData(book, key);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;

        }
        if (obj == null || !obj.getClass().equals(BookDataInput.class))
        {
            return false;
        }

        BookDataInput rhs = (BookDataInput) obj;

        return rhs.bookId.equals(this.bookId) && rhs.bookKey.equals(this.bookKey);
    }

    public int hashCode()
    {
        return (bookId.hashCode() << 7) + (bookKey.hashCode() << 3) + 13;
    }
}
