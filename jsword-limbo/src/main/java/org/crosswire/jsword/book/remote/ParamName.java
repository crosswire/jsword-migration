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
package org.crosswire.jsword.book.remote;

import java.io.Serializable;

/**
 * Some constants so that everyone can agree on the names for various methods.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public enum ParamName implements Serializable
{
	PARAM_BIBLE ("bible"), //$NON-NLS-1$
    PARAM_PASSAGE ("passage"), //$NON-NLS-1$
    PARAM_FINDSTRING ("word"); //$NON-NLS-1$

    /**
     * Only we should be doing this
     */
    private ParamName(String name)
    {
        this.name = name;
    }

    /**
     * Lookup method to convert from a String
     */
    public static ParamName fromString(String name)
    {
        for (ParamName t : ParamName.values())
        {
            if (t.name.equalsIgnoreCase(name))
            {
                return t;
            }
        }
        // cannot get here
        assert false;
        return null;
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
     * The name of the ParamName
     */
    private String name;
}
