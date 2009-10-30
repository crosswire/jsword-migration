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

import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.crosswire.common.util.IteratorEnumeration;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * A PassageTreeNode extends TreeNode to Model a Passage.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageTreeNode implements TreeNode, PassageListener {
    /**
     * Simple ctor
     */
    public PassageTreeNode(Passage ref, JTree tree) {
        this.ref = ref;
        this.tree = tree;
        ref.addPassageListener(this);
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int index) {
        return new VerseRangeTreeNode(ref.getRangeAt(index, RestrictionType.CHAPTER));
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount() {
        return ref.countRanges(RestrictionType.CHAPTER);
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent() {
        return this;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, -1 will be returned.
     */
    public int getIndex(TreeNode node) {
        int count = 0;
        Iterator it = ref.rangeIterator(RestrictionType.NONE);

        while (it.hasNext()) {
            if (it.next() == node) {
                return count;
            }

            count++;
        }

        return -1;
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren() {
        return true;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf() {
        return false;
    }

    /**
     * Sent after stuff has been added to the Passage. More info about what and
     * where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapuslating the event information
     */
    public void versesAdded(PassageEvent ev) {
    }

    /**
     * Sent after stuff has been removed from the Passage. More info about what
     * and where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapuslating the event information
     */
    public void versesRemoved(PassageEvent ev) {
    }

    /**
     * Sent after verses have been symultaneously added and removed from the
     * Passage. More info about what and where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapuslating the event information
     */
    public void versesChanged(PassageEvent ev) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.nodeStructureChanged(this);
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children() {
        return new IteratorEnumeration(ref.rangeIterator(RestrictionType.NONE));
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    /* @Override */
    public String toString() {
        return ref.getOverview();
    }

    /**
     * The Passage to be displayed
     */
    protected Passage ref;

    /**
     * The Passage to be displayed
     */
    protected JTree tree;
}
