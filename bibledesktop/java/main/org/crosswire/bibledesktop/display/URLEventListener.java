package org.crosswire.bibledesktop.display;

import java.util.EventListener;

/**
 * Implement URLEventListener to recieve URLEvents whenever someone
 * activates an URL.
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
public interface URLEventListener extends EventListener
{
    /**
     * This method is called to indicate that an URL can be processed.
     * @param ev Describes the URL
     */
    public void activateURL(URLEvent ev);

    /**
     * This method is called to indicate that the mouse has entered the URL.
     * @param ev Describes the URL
     */
    public void enterURL(URLEvent ev);

    /**
     * This method is called to indicate that the mouse has left the URL.
     * @param ev Describes the URL
     */
    public void leaveURL(URLEvent ev);
}
