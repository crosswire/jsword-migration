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
package org.crosswire.jsword.book.raw;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.PropertyMap;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.basic.AbstractBookDriver;
import org.crosswire.jsword.book.basic.BookRoot;

/**
 * This represents all of the RawBibles.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RawBookDriver extends AbstractBookDriver {
    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public Book[] getBooks() {
        URI dir = null;
        String[] names = null;
        try {
            dir = BookRoot.findBibleRoot(getDriverName());

            if (!NetUtil.isDirectory(dir)) {
                log.debug("Missing raw directory: " + dir);
                return new Book[0];
            }

            if (dir == null) {
                names = new String[0];
            } else {
                names = NetUtil.list(dir, new NetUtil.IsDirectoryURIFilter(dir));
            }
        } catch (MalformedURLException e1) {
            names = new String[0];
        } catch (IOException e) {
            names = new String[0];
        }

        List books = new ArrayList();

        for (int i = 0; i < names.length; i++) {
            try {
                URI uri = NetUtil.lengthenURI(dir, names[i]);
                URI propURI = NetUtil.lengthenURI(uri, RawConstants.FILE_BIBLE_PROPERTIES);

                PropertyMap prop = NetUtil.loadProperties(propURI);

                Book book = new RawBook(this, prop, uri);

                books.add(book);
            } catch (IOException e) {
                continue;
            }
        }

        return (Book[]) books.toArray(new Book[books.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.BookDriver#getDriverName()
     */
    public String getDriverName() {
        return "raw";
    }

    /**
     * The log stream
     */
    private static Logger log = Logger.getLogger(RawBookDriver.class);
}
