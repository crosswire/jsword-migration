
package org.crosswire.jsword.book.remote;

import java.net.MalformedURLException;
import java.text.ParseException;

import org.crosswire.jsword.book.Bible;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.basic.AbstractBibleMetaData;

/**
 * An extension to AbstractBibleMetaData that adds a unique ID for each instance.
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
public class RemoteBibleMetaData extends AbstractBibleMetaData
{
    /**
     * Constructor for RemoteBibleMetaData.
     */
    public RemoteBibleMetaData(Remoter remoter, String id, String name, String edition, String initials, String pubstr, String openstr, String licencestr) throws ParseException, MalformedURLException
    {
        super(name, edition, initials, pubstr, openstr, licencestr);
        this.remoter = remoter;
        this.id = id;
    }

    /**
     * Featch a currently existing Bible, read-only
     * @param name The name of the version to create
     * @exception BookException If the name is not valid
     */
    public Bible getBible() throws BookException
    {
        return new RemoteBible(remoter, this);
    }

    /**
     * The unique (self generated) id for this instance
     */
    public String getID()
    {
        return id;
    }

    /**
     * Accessor for the driver name
     */
    public String getDriverName()
    {
        return remoter.getRemoterName();
    }

    /**
     * Remote access method
     */
    protected Remoter remoter;

    /**
     * The ID for this instance
     */
    private String id;

    /**
     * The expected speed at which this implementation gets correct answers.
     * Perhaps we should allow this to be customized more by concrete
     * implementations?
     * @see org.crosswire.jsword.book.BookMetaData#getSpeed()
     */
    public int getSpeed()
    {
        return 6;
    }
}
