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
package org.crosswire.bibledesktop.reference;

import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import org.crosswire.common.util.IteratorEnumeration;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;

/**
 * A Book in the (possibly filtered) list of books in the reference tree.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ReferenceBookTreeNode implements TreeNode
{
    /**
     * Simple ctor
     */
    public ReferenceBookTreeNode(ReferenceTreeModel model, TreeNode parent, Book book)
    {
        this.model = model;
        this.parent = parent;
        this.book = book;
        this.keys = book.getGlobalKeyList();
    }

    /**
     * Simple ctor
     */
    public ReferenceBookTreeNode(ReferenceTreeModel model, TreeNode parent, Book book, Key keys)
    {
        this.model = model;
        this.parent = parent;
        this.book = book;
        this.keys = keys;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return book.getName();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildCount()
     */
    public int getChildCount()
    {
        return keys.getChildCount();
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getAllowsChildren()
     */
    public boolean getAllowsChildren()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#isLeaf()
     */
    public boolean isLeaf()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#children()
     */
    public Enumeration children()
    {
        return new IteratorEnumeration(keys.iterator());
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getParent()
     */
    public TreeNode getParent()
    {
        return parent;
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getChildAt(int)
     */
    public TreeNode getChildAt(int childIndex)
    {
        Key key = keys.get(childIndex);
        return new ReferenceKeyTreeNode(model, book, this, key);
    }

    /* (non-Javadoc)
     * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
     */
    public int getIndex(TreeNode node)
    {
        ReferenceKeyTreeNode keynode = (ReferenceKeyTreeNode) node;
        Key key = keynode.getKey();
        return keys.indexOf(key);
    }

    /**
     * The Book object that we are wrapping
     */
    public Book getBook()
    {
        return book;
    }

    /**
     * The full list of keys or a shortened list if we are filtering
     */
    public Key getKeyList()
    {
        return keys;
    }

    /**
     * Set a shortened list of keys to filter by
     */
    public void setKeyList(Key keys)
    {
        this.keys = keys;
        model.fireTreeNodesChanged(this, new Object[] { parent, this, }, new int[0], null);
    }

    /**
     * Our daddy in the tree
     */
    private TreeNode parent;

    /**
     * The list of Keys that we should display
     */
    private Key keys;

    /**
     * The tree model to which we report changes
     */
    private ReferenceTreeModel model;
    
    /**
     * The book that we are representing
     */
    private Book book;
}