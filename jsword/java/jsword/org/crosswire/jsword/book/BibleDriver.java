
package org.crosswire.jsword.book;

import org.crosswire.jsword.book.events.ProgressListener;

/**
 * The BibleDriver class is an gateway to all the instances of the Books
 * controlled by this name.
 * <p>In an ideal world users would not need to touch an BibleDrivers, however
 * there are times (create() when the user needs to select the Driver to handle
 * the new data) when this is not possible.
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public interface BibleDriver extends BookDriver
{
    /**
     * Create a new Bible based on a source
     * @param name The name of the version to create
     * @param li Somewhere to repost progress (can be null)
     * @return The new WritableBible
     * @exception BookException If the name is not valid
     */
    public Bible create(Bible source, ProgressListener li) throws BookException;

    /**
     * This method should only be used by Bibles at startup to register the
     * Bibles known at start time.
     * Generally there will be a better way of doing whatever you want to do if
     * you use this method.
     * @return A list of the known Bibles
     */
    public BibleMetaData[] getBibles();
}
