package org.crosswire.bibledesktop.display;

import java.util.EventObject;

/**
 * A KeyChangeEvent happens whenever a user changes a key.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class URLEvent extends EventObject
{
    /**
     * For when a command has been made
     * @param source The thing that started this off
     */
    public URLEvent(Object source, String protocol, String url)
    {
        super(source);

        this.protocol = protocol;
        this.url = url;
    }

    /**
     * @return Returns the protocol.
     */
    public String getProtocol()
    {
        return protocol;
    }

    /**
     * @return Returns the url.
     */
    public String getUrl()
    {
        return url;
    }

    private String protocol;
    private String url;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978710575457187634L;
}
