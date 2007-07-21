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
 * ID: $Id$
 */
package org.crosswire.bibledesktop.passage;

import javax.swing.JList;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A Simple extension to JList to customize it to hold a Passage and
 * provide Passage related actions.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class PassageGuiUtil
{
    /**
     * Prevent instantiation
     */
    private PassageGuiUtil()
    {
    }

    /**
     * Remove all of the selected verses from the passage
     */
    public static void deleteSelectedVersesFromList(JList list)
    {
        RangeListModel rlm = (RangeListModel) list.getModel();

        Passage ref = rlm.getPassage();
        Object[] selected = list.getSelectedValues();
        for (int i = 0; i < selected.length; i++)
        {
            VerseRange range = (VerseRange) selected[i];
            ref.remove(range);
        }

        list.setSelectedIndices(new int[0]);
    }

    /**
     * @param tree The tree to delete selected verses from
     */
//    public static void deleteSelectedVersesFromTree(JTree tree)
//    {
//        Key selected = getSelectedKeys(tree);
//
//        KeyTreeModel mdl = (KeyTreeModel) tree.getModel();
//        Key root = mdl.getKey();
//
//        for (Iterator it = selected.iterator(); it.hasNext(); )
//        {
//            Key key = (Key) it.next();
//            root.removeAll(key);
//        }
//
//        mdl.setKey(root);
//    }

    /**
     * @return The selected keys in the tree
     */
//    public static Key getSelectedKeys(JTree tree)
//    {
//        Key selected = new DefaultKeyList();
//        TreePath[] paths = tree.getSelectionPaths();
//
//        for (int i = 0; i < paths.length; i++)
//        {
//            KeyTreeNode node = (KeyTreeNode) paths[1].getLastPathComponent();
//            selected.addAll(node.getKey());
//        }
//
//        return selected;
//    }
}
