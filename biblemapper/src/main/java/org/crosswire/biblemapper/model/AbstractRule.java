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
 * AbstractRule. 
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractRule implements Rule
{
    /**
     * We sometimes need to take a single reply and multiply it up
     * according to the current scale.
     * @see org.crosswire.biblemapper.model.Rule#getScaledPosition(Map, int, int)
     */
    public Position[] getScaledPosition(Map map, int book, int chapter)
    {
        // get a copy of scale, since it can change out from under
        int size = scale;
        if (size == 0)
        {
            return new Position[0];
        }

        Position single = getDesiredPosition(map, book, chapter);
        Position[] reply = new Position[size];
        for (int i=0; i<size; i++)
        {
            reply[i] = single;
        }

        return reply;
    }

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method sets the preferred length of that returned array.
     * @param scale The preferred length of the desired position array
     */
    public void setScale(int scale)
    {
        this.scale = scale;
    }

    /**
     * Each call to getDesiredPosition() returns an array of Positions,
     * this method gets the preferred length of that returned array.
     * @return The preferred length of the desired position array
     */
    public int getScale()
    {
        return scale;
    }

    /** The length of the desired position array */
    private int scale = 0;
}
