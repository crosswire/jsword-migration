/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
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
 * ID: $Id:KeyTreeModel.java 1253 2007-03-16 21:16:26Z dmsmith $
 */
package org.crosswire.bibledesktop.passage;

import javax.swing.tree.DefaultTreeModel;

import org.crosswire.jsword.passage.Key;

/**
 * A TreeModel that helps with working with Keys.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class KeyTreeModel extends DefaultTreeModel
{
    /**
     * Simple ctor
     * @param key The root TreeNode
     */
    public KeyTreeModel(Key key)
    {
        super(new KeyTreeNode(key, null));
        this.key = key;
    }

    /**
     * What key is this tree editing
     * @return Returns the key.
     */
    public Key getKey()
    {
        return key;
    }

    /**
     * Sets the key is this tree editing
     */
    public void setKey(Key key)
    {
        this.key = key;
        setRoot(new KeyTreeNode(key, null));
    }

    /**
     * The key that this tree is displaying.
     */
    private Key key;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977303235050353714L;
}
