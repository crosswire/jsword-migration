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
package org.crosswire.jsword.book.search.parse;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;

/**
 * The Search Word for a Word to search for.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SubRightParamWord implements ParamWord {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.parse.ParamWord#getWord(org.crosswire
     * .jsword.book.search.parse.Searcher)
     */
    public String getWord(IndexSearcher engine) throws BookException {
        throw new BookException(Msg.RIGHT_PARAM);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.search.parse.ParamWord#getPassage(org.crosswire
     * .jsword.book.search.parse.Searcher)
     */
    public Key getKeyList(IndexSearcher engine) throws BookException {
        throw new BookException(Msg.RIGHT_BRACKETS);
    }
}
