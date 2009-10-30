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

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;

/**
 * PassageTableModel.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class VerseTreeNode extends ChapterTreeNode {
    /**
     * This constructor is for when we are really a BookTreeNode
     */
    protected VerseTreeNode(TreeNode parent, int book, int passage, int verse) throws NoSuchVerseException {
        super(parent, book, passage);
        this.verse = verse;
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    /* @Override */
    public void setPassage(Passage ref, boolean filter) {
        this.ref = ref;
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    /* @Override */
    public TreeNode getChildAt(int i) {
        return null; // VerseDisplay thing
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    /* @Override */
    public int getChildCount() {
        return 0;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, -1 will be returned.
     */
    /* @Override */
    public int getIndex(TreeNode node) {
        return -1;
    }

    /**
     * How we appear in the Tree
     */
    /* @Override */
    public String toString() {
        return Integer.toString(verse);
    }

    /**
     * The current Passage number
     */
    public int getVerse() {
        return verse;
    }

    /**
     * The Verse that this node referrs to
     */
    protected int verse;
}
