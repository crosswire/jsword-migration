package org.crosswire.jsword.book.search;

import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;

/**
 * An index into a body of text that knows what words exist and where they are.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface Index
{
    /**
     * For a given word find a list of references to it.
     * If the <code>word</code> being searched for is null then an empty Key
     * <b>MUST</b> be returned. Users of this index may use this functionality
     * to get empty KeyLists which they then use to aggregate other searches
     * done on this index.
     * @param word The text to search for
     * @return The references to the word
     */
    public Key findWord(String word) throws BookException;

    /**
     * An index must be able to create KeyLists for users in a similar way to
     * the Book that it is indexing.
     * @param name The string to convert to a Key
     * @return A new Key representing the given string, if possible
     * @throws NoSuchKeyException If the string can not be turned into a Key
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(String)
     */
    public Key getKey(String name) throws NoSuchKeyException;
}
