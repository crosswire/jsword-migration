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


/**
 * Set of constants for the types of RemoteMethod.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public enum MethodName
{
    GETBIBLES ("getBibles"), //$NON-NLS-1$
    GETDATA ("getData"), //$NON-NLS-1$
    FINDPASSAGE ("findPassage"); //$NON-NLS-1$

    /**
     * Only we should be doing this
     */
    private MethodName(String name)
    {
        this.name = name;
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
     * Lookup method to convert from a String
     */
    public static MethodName fromString(String name)
    {
        for (MethodName t : MethodName.values())
        {
            if (t.name.equalsIgnoreCase(name))
            {
                return t;
            }
        }
        assert false;
        return null;
    }

    /**
     * The name of the MethodName
     */
    private String name;
}