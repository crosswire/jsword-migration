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

import javax.swing.tree.TreeNode;

import org.crosswire.bibledesktop.BDMsg;
import org.crosswire.common.icu.NumberShaper;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * A PassageTreeNode extends TreeNode to Model a Passage.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class WholeBibleTreeNode implements TreeNode {
    /**
     * The start point for all WholeBibleTreeNodes.
     */
    public static WholeBibleTreeNode getRootNode() {
        return new WholeBibleTreeNode(null, VerseRange.getWholeBibleVerseRange(), Level.BIBLE);
    }

    /**
     * We could do some caching here if needs be.
     */
    protected static WholeBibleTreeNode getNode(TreeNode parent, BibleBook b, int c, int v) {
        Verse start = null;
        Verse end = null;
        Level thislevel = Level.BOOK;

        if (b == null) {
            assert false : b;
        } else if (c == -1) {
            thislevel = Level.BOOK;
            int ec = BibleInfo.chaptersInBook(b);
            int ev = BibleInfo.versesInChapter(b, ec);
            start = new Verse(b, 0, 0);
            end = new Verse(b, ec, ev);
        } else if (v == -1) {
            thislevel = Level.CHAPTER;
            int ev = BibleInfo.versesInChapter(b, c);
            start = new Verse(b, c, 0);
            end = new Verse(b, c, ev);
        } else {
            thislevel = Level.VERSE;
            start = new Verse(b, c, v);
            end = start;
        }

        VerseRange rng = new VerseRange(start, end);
        return new WholeBibleTreeNode(parent, rng, thislevel);
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    private WholeBibleTreeNode(TreeNode parent, VerseRange range, Level level) {
        if (parent != null) {
            this.parent = parent;
        } else {
            this.parent = this;
        }

        this.range = range;
        this.level = level;
        shaper = new NumberShaper();
    }

    /**
     * The current Passage number
     */
    public VerseRange getVerseRange() {
        return range;
    }

    /**
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent() {
        return parent;
    }

    /**
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren() {
        return level != Level.VERSE;
    }

    /**
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf() {
        return level == Level.VERSE;
    }

    /**
     * How we appear in the Tree
     */
    @Override
    public String toString() {
        switch (level) {
        case BIBLE:
            // TRANSLATOR: The top level of the tree of Bible books, chapters and verses.
            return BDMsg.gettext("The Bible");

        case BOOK:
            return range.getStart().getBook().getPreferredName();

        case CHAPTER:
            return shaper.shape(Integer.toString(range.getStart().getChapter()));

        case VERSE:
            return shaper.shape(Integer.toString(range.getStart().getVerse()));

        default:
            // TRANSLATOR: Unexpected error condition.
            return BDMsg.gettext("Error");
        }
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int i) {
        switch (level) {
        case BIBLE:
            BibleBook[] books = BibleInfo.getBooks();
            return WholeBibleTreeNode.getNode(this, books[i], -1, -1);

        case BOOK:
            return WholeBibleTreeNode.getNode(this, range.getStart().getBook(), i, -1);

        case CHAPTER:
            return WholeBibleTreeNode.getNode(this, range.getStart().getBook(), range.getStart().getChapter(), i);

        default:
            return null;
        }
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount() {
        switch (level) {
        case BIBLE:
            return BibleInfo.booksInBible();

        case BOOK:
            return BibleInfo.chaptersInBook(range.getStart().getBook()) + 1;

        case CHAPTER:
            return BibleInfo.versesInChapter(range.getStart().getBook(), range.getStart().getChapter()) + 1;

        default:
            return 0;
        }
    }

    /**
     * Returns the index of <code>node</code> in the receivers children. If the
     * receiver does not contain <code>node</code>, 0 will be returned.
     */
    public int getIndex(TreeNode node) {
        if (!(node instanceof WholeBibleTreeNode)) {
            return 0;
        }

        WholeBibleTreeNode vnode = (WholeBibleTreeNode) node;

        switch (level) {
        case BIBLE:
            return vnode.getVerseRange().getStart().getBook().ordinal();

        case BOOK:
            return vnode.getVerseRange().getStart().getChapter();

        case CHAPTER:
            return vnode.getVerseRange().getStart().getVerse();

        default:
            return 0;
        }
    }

    /**
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration<TreeNode> children() {
        return new WholeBibleEnumeration(this);
    }

    /**
     * Iterate over the Books
     */
    private class WholeBibleEnumeration implements Enumeration<TreeNode> {
        public WholeBibleEnumeration(WholeBibleTreeNode treeNode) {
            this.treeNode = treeNode;
        }
        public boolean hasMoreElements() {
            return count < treeNode.getChildCount();
        }

        public TreeNode nextElement() {
            count++;
            return treeNode.getChildAt(count);
        }

        private WholeBibleTreeNode treeNode;
        private int count;
    }

    private enum Level {
        BIBLE,
        BOOK,
        CHAPTER,
        VERSE,
    }

    /** Change the number representation as needed */
    private NumberShaper shaper;

    /** The range that this node refers to */
    private VerseRange range;

    /** Our parent tree node */
    private TreeNode parent;

    /** The level of this node one of: LEVEL_[BIBLE|BOOK|CHAPTER|VERSE] */
    private Level level;
}
