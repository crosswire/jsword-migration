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
package org.crosswire.bibledesktop.book;

import javax.swing.ComboBoxModel;

import org.crosswire.jsword.book.BookDriver;

/**
 * The DriverModels class implements ComboBoxModel by extending the
 * DriverListModel.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DriversComboBoxModel extends DriversListModel implements ComboBoxModel
{
    /**
     * Basic Constructor
     */
    public DriversComboBoxModel(boolean includeRo)
    {
        super(includeRo);

        if (drivers.length > 0)
        {
            current = drivers[0];
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object current)
    {
        this.current = current;
        fireContentsChanged(this, -1, -1);
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem()
    {
        return current;
    }

    /**
     * Given an item, work out the name of the Driver that it represents
     * @return A Driver
     */
    public BookDriver getSelectedDriver()
    {
        return drivers[getIndexOf(current)];
    }

    /**
     * The currently selected version
     */
    protected Object current;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3689068456540910136L;
}
