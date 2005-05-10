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

import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;

/**
 * BibleTreeNode.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BibleTreeNode implements TreeNode
{
    /**
     *
     */
    public BibleTreeNode()
    {
        kids = new BookTreeNode[BibleInfo.booksInBible()];
    }

    /**
     *
     */
    public void setPassage(Passage ref, boolean filter)
    {
        this.ref = ref;

        try
        {
            if (filter)
            {
                kids = new BookTreeNode[ref.booksInPassage()];

                int currentBook = 0;
                int bookCount = 0;

                Iterator it = ref.iterator();
                while (it.hasNext())
                {
                    Verse verse = (Verse) it.next();
                    if (currentBook != verse.getBook())
                    {
                        currentBook = verse.getBook();
                        BookTreeNode node = new BookTreeNode(this, currentBook);
                        node.setPassage(ref, true);
                        kids[bookCount++] = node;
                    }
                }
            }
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
        }
    }

    /**
     * Returns the child <code>TreeNode</code> at index i
     */
    public TreeNode getChildAt(int i)
    {
        try
        {
            if (kids[i] != null)
            {
                return kids[i];
            }

            BookTreeNode node = new BookTreeNode(this, i + 1);
            node.setPassage(ref, false);
            kids[i] = node;

            return kids[i];
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return null;
        }
    }

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    public int getChildCount()
    {
        return kids.length;
    }

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent()
    {
        return this;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node)
    {
        if (!(node instanceof BookTreeNode))
        {
            return -1;
        }

        BookTreeNode book = (BookTreeNode) node;
        return book.getBook();
    }

    /**
     * Returns true if the receiver allows children.
     */
    public boolean getAllowsChildren()
    {
        return true;
    }

    /**
     * Returns true if the receiver is a leaf.
     */
    public boolean isLeaf()
    {
        return false;
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public Enumeration children()
    {
        return new NodeEnumeration();
    }

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    public String toString()
    {
        if (ref == null)
        {
            return Msg.WHOLE_BIBLE.toString();
        }

        return Msg.PART_BIBLE.toString(ref.getOverview());
    }

    /**
     * The Enumerate over an array
     */
    public class NodeEnumeration implements Enumeration
    {
        public boolean hasMoreElements()
        {
            return index < kids.length;
        }

        public Object nextElement()
        {
            return kids[index++];
        }

        private int index;
    }

    /**
     * If we are only displaying some of the verses
     */
    protected Passage ref;

    /**
     * The ChapterTreeNodes that we have created
     */
    protected TreeNode[] kids;
}
