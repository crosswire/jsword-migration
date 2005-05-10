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
 * ID: $ID$
 */
package org.crosswire.bibledesktop.passage;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.passage.Key;

/**
 * A specialization of DefaultTreeCellRenderer that knows how to get names from
 * Keys.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class KeyTreeCellRenderer extends DefaultTreeCellRenderer
{
    /* (non-Javadoc)
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isselected, boolean expanded, boolean leaf, int row, boolean focus)
    {
        super.getTreeCellRendererComponent(tree, value, isselected, expanded, leaf, row, focus);

        if (value instanceof KeyTreeNode)
        {
            KeyTreeNode keytn = (KeyTreeNode) value;
            Key key = keytn.getKey();
            setText(key.getName());
        }
        else
        {
            log.warn("value is not a key: " + value.getClass().getName()); //$NON-NLS-1$
        }

        return this;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(KeyTreeCellRenderer.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3545232531516765241L;
}
