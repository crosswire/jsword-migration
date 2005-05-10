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
package org.crosswire.bibledesktop.passage;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.crosswire.jsword.passage.Key;

/**
 * A simple implementation of ListModel that is backed by a SortedSet.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class KeyListListModel extends AbstractListModel implements ListModel
{
    /**
     * Constructor for ListListModel.
     */
    public KeyListListModel(Key keys)
    {
        this.keys = keys;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return keys != null ? keys.getChildCount() : 0;
    }

    /**
     * There must be a faster way of doing this?
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return keys != null ? keys.get(index) : null;
    }

    private Key keys;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3546356240990286645L;
}
