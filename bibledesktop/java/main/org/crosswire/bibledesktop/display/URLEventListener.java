/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.bibledesktop.display;

import java.util.EventListener;

/**
 * Implement URLEventListener to recieve URLEvents whenever someone
 * activates an URL.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public interface URLEventListener extends EventListener
{
    /**
     * This method is called to indicate that an URL can be processed.
     * @param ev Describes the URL
     */
    void activateURL(URLEvent ev);

    /**
     * This method is called to indicate that the mouse has entered the URL.
     * @param ev Describes the URL
     */
    void enterURL(URLEvent ev);

    /**
     * This method is called to indicate that the mouse has left the URL.
     * @param ev Describes the URL
     */
    void leaveURL(URLEvent ev);
}
