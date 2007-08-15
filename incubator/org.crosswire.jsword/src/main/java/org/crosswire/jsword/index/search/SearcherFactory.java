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
 * ID: $Id: SearcherFactory.java 1505 2007-07-21 19:40:19Z dmsmith $
 */
package org.crosswire.jsword.index.search;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.index.Index;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.internal.osgi.IndexRegistry;

/**
 * Factory method for creating a new Searcher.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class SearcherFactory
{
    /**
     * Prevent instantiation
     */
    private SearcherFactory()
    {
    }

    /**
     * Create a new Searcher.
     */
    public static Searcher createSearcher(Book book) throws InstantiationException
    {
        try
        {
            IndexManager imanager = IndexManagerFactory.getIndexManager();
            Index index = imanager.getIndex(book);

            Searcher parser = IndexRegistry.getDefaultIndexSearcher();
            parser.init(index);

            return parser;
        }
        catch (BookException e)
        {
            log.error("createSearcher failed", e); //$NON-NLS-1$
            throw new InstantiationException();
        }
        catch (ClassCastException e)
        {
            log.error("createSearcher failed", e); //$NON-NLS-1$
            throw new InstantiationException();
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SearcherFactory.class);
}
