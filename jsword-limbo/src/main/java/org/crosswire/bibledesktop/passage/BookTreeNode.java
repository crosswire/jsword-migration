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

import java.util.Iterator;

import javax.swing.tree.TreeNode;

import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleInfo;

/**
 * PassageTableModel.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookTreeNode extends BibleTreeNode
{
    /**
     * This constructor is for when we are really a BookTreeNode
     */
    protected BookTreeNode(TreeNode parent, int book) throws NoSuchVerseException
    {
        this.parent = parent;
        this.book = book;

        kids = new ChapterTreeNode[BibleInfo.chaptersInBook(book)];
    }

    /**
     * This constructor is for when we are really a BookTreeNode
     */
    public void setPassage(Passage ref, boolean filter)
    {
        this.ref = ref;

        if (filter)
        {
            try
            {
                kids = new ChapterTreeNode[ref.chaptersInPassage(book)];

                int currentRef = 0;
                int count = 0;

                Iterator it = ref.iterator();
                while (it.hasNext())
                {
                    Verse verse = (Verse) it.next();

                    if ((book == 0 || verse.getBook() == book)
                        && currentRef != verse.getChapter())
                    {
                        currentRef = verse.getChapter();

                        ChapterTreeNode node = new ChapterTreeNode(this, book, currentRef);
                        node.setPassage(ref, true);
                        kids[count++] = node;
                    }
                }
            }
            catch (NoSuchVerseException ex)
            {
                assert false : ex;
            }
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

            ChapterTreeNode node = new ChapterTreeNode(this, book, i + 1);
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
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    public TreeNode getParent()
    {
        return parent;
    }

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    public int getIndex(TreeNode node)
    {
        if (!(node instanceof ChapterTreeNode))
        {
            return -1;
        }

        ChapterTreeNode chap = (ChapterTreeNode) node;
        return chap.getChapter();
    }

    /**
     * How we appear in the Tree
     */
    public String toString()
    {
        try
        {
            String bookName = BibleInfo.getBookName(book);
            if (ref == null)
            {
                return bookName;
            }

            int chapters = ref.chaptersInPassage(book);
            if (chapters == 0)
            {
                return bookName;
            }

            return bookName + " (" + chapters + ')';  //$NON-NLS-1$ 
        }
        catch (NoSuchVerseException ex)
        {
            assert false : ex;
            return "!Error!"; //$NON-NLS-1$
        }
    }

    /**
     * The current book number (Genesis=1)
     */
    public int getBook()
    {
        return book;
    }

    /**
     * The Book that this node referrs to
     */
    protected int book;

    /** The base of this tree */
    protected TreeNode parent;
}

