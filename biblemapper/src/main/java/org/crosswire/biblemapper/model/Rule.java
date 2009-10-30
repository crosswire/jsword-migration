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

/**
 * A Rule has the ability to specify where it would like a node to be positioned
 * in space.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface Rule {
    /**
     * Specify where it would like a node to be positioned in space. The
     * Position is added to the results from all Rules and averaged out. A reply
     * of null indicated no preference.
     * 
     * @param map
     *            The Map to select a node from
     * @param book
     *            The book number
     * @param chapter
     *            The chapter
     * @return Desired position.
     */
    Position getDesiredPosition(Map map, int book, int chapter);

    /**
     * Specify where it would like a node to be positioned in space weighted buy
     * the current scale
     * 
     * @param map
     *            The Map to select a node from
     * @param book
     *            The book number
     * @param chapter
     *            The chapter
     * @return Desired position.
     */
    Position[] getScaledPosition(Map map, int book, int chapter);

    /**
     * Each call to getDesiredPosition() returns an array of Positions, this
     * method sets the preferred length of that returned array.
     * 
     * @param scale
     *            The preferred length of the desired position array
     * @see #getDesiredPosition(Map, int, int)
     */
    void setScale(int scale);

    /**
     * Each call to getDesiredPosition() returns an array of Positions, this
     * method gets the preferred length of that returned array.
     * 
     * @return The preferred length of the desired position array
     * @see #getDesiredPosition(Map, int, int)
     */
    int getScale();
}
