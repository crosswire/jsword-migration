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

import java.io.Serializable;

/**
 * A Position is simply an array of floats that specify a place for a Key to be.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Position implements Serializable {
    /**
     * Basic constructor
     */
    public Position(float[] pos) {
        this.pos = pos;
    }

    /**
     * Accessor for the array of positions
     * 
     * @return The array of positions
     */
    public float[] getPosition() {
        return pos;
    }

    /**
     * Accessor for the array of positions
     */
    public void setPosition(float[] pos) {
        this.pos = pos;
    }

    /** The array of floats */
    protected float[] pos;

    /** Serialization ID - a serialization of pos */
    static final long serialVersionUID = -2737633670295539140L;
}
