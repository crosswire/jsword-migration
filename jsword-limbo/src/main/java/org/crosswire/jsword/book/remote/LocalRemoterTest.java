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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.jsword.book.remote;

import java.util.List;

import junit.framework.TestCase;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.jdom.Document;

/**
 * JUnit tests.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LocalRemoterTest extends TestCase {
    /**
     * Constructor for LocalRemoterTest.
     * 
     * @param arg0
     */
    public LocalRemoterTest(String arg0) {
        super(arg0);
    }

    private Remoter remote = new LocalRemoter();

    public void testGetBibles() throws Exception {
        BookFilter filter = BookFilters.getOnlyBibles();
        List lbooks = Books.installed().getBooks(filter);
        Book[] names1 = (Book[]) lbooks.toArray(new Book[lbooks.size()]);
        RemoteBookDriver rbd = new LocalRemoteBookDriver();

        RemoteMethod method = new RemoteMethod(MethodName.GETBIBLES);
        Document doc = remote.execute(method);
        Book[] names2 = Converter.convertDocumentToBooks(rbd, doc, new FixtureRemoter());

        assertEquals(names1.length, names2.length);
        for (int i = 0; i < names1.length; i++) {
            assertEquals(names1[i].getName(), names2[i].getName());
        }
    }

    public void assertEquals(Object[] o1, Object[] o2) {
        assertEquals(o1.length, o2.length);
        for (int i = 0; i < o1.length; i++) {
            assertEquals(o1[i], o2[i]);
        }
    }
}
