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
 * FrictionRule simply tries to make the nodes stay where they are.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FrictionRule extends AbstractRule {
    /**
     * We default to a scale of 1
     */
    public FrictionRule() {
        setScale(1);
    }

    /**
     * @see org.crosswire.biblemapper.model.AbstractRule#setScale(int)
     */
    @Override
    public void setScale(int scale) {
        if (scale == 0) {
            super.setScale(1);
        } else {
            super.setScale(scale);
        }
    }

    /**
     * Specify where it would like a node to be positioned in space.
     * 
     * @param map
     *            The Map to select a node from
     * @return An array of desired positions.
     */
    public Position getDesiredPosition(Map map, int book, int chapter) {
        float[] arr = map.getPositionArrayCopy(book, chapter);
        return new Position(arr);
    }
}
