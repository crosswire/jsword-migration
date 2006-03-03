/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
package org.crosswire.jsword.book;

import org.crosswire.common.util.MsgBase;

/**
 * A definition of how open a Bible is. Can is be freely copied or is
 * it proprietary.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public enum Openness
{
    /** If the data of unknown distribution status */
    UNKNOWN (Msg.OPEN_UNKNOWN),

    /** If the data free of copyright restrictions */
    PD (Msg.OPEN_PD),

    /** Does the data have a license that permits free use */
    FREE (Msg.OPEN_FREE),

    /** Is the data freely redistributable */
    COPYABLE (Msg.OPEN_COPYABLE),

    /** Is the data sold for commercial profit */
    COMMERCIAL (Msg.OPEN_COMMERCIAL);

    /** Prevent anyone else from doing this */
    private Openness(MsgBase msg)
    {
        name = msg.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * The name of the Openness
     */
    private String name;
}
