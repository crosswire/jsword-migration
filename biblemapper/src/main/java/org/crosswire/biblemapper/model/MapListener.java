/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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
package org.crosswire.biblemapper.model;

import java.util.EventListener;

/**
 * Implement MapListener and call Map.addMapListener() to recieve MapEvents
 * whenever the map changes.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface MapListener extends EventListener {
    /**
     * This method is called to indicate that a node on the map has moved.
     * 
     * @param ev
     *            Describes the change
     */
    void mapChanged(MapEvent ev);

    /**
     * This method is called to indicate that the whole map has changed
     * 
     * @param ev
     *            Describes the change
     */
    void mapRewritten(MapEvent ev);
}
