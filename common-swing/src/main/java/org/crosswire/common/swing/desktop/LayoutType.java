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
package org.crosswire.common.swing.desktop;

/**
 * Types of ViewLayouts. Currently there are two types of desktop layouts:
 * <ul>
 * <li>TDI - tabbed document interface.</li>
 * <li>MDI - multiple document interface (sub-windows)</li>
 * </ul>
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public enum LayoutType
{
    /** Tabbed View */
    TDI
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.swing.desktop.LayoutType#createLayout()
         */
        @Override
        public AbstractViewLayout createLayout()
        {
            return new TDIViewLayout();
        }
    },

    /** Multiple Document View */
    MDI
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.swing.desktop.LayoutType#createLayout()
         */
        @Override
        public AbstractViewLayout createLayout()
        {
            return new MDIViewLayout();
        }
    };

    /**
     * Return the layout
     *
     * @return the layout
     */
    public AbstractViewLayout getLayout()
    {
        // In order to get the proper LAF it needs to be created after the LAF is set
        // So we delay it until it is actually needed.
        if (layout == null)
        {
            layout = createLayout();
        }
        return layout;
    }

    /**
     * Create the appropriate kind of view layout
     * @return the created view layout
     */
    public abstract AbstractViewLayout createLayout();

    /**
     * Lookup method to convert from an integer
     */
    public static LayoutType fromInteger(int i)
    {
        for (LayoutType t : LayoutType.values())
        {
            if (t.ordinal() == i)
            {
                return t;
            }
        }
        assert false;
        return TDI;
    }

    /**
     * The actual layout
     */
    private AbstractViewLayout layout;
}
