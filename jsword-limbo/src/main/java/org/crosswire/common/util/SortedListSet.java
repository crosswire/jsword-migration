/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
package org.crosswire.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Maintains a sorted list of unique objects. It is expected
 * that the objects implement Comparable. Methods that take an index
 * to indicate an insertion point are ignored.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SortedListSet<E extends Comparable<E>> extends ArrayList<E> implements Set<E>, List<E>
{
    /**
     * Create an empty SortedListSet of default size.
     */
    public SortedListSet()
    {
        super();
    }

    /**
     * Create an empty SortedListSet of the stated capacity
     * @param initialCapacity
     */
    public SortedListSet(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
     * @param c
     */
    public SortedListSet(Collection<? extends E> c)
    {
        this(c.size());
        // Might be better to add all then sort.
        addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, E element)
    {
        // ignore the requested index
        add(element);
    }

    /* (non-Javadoc)
     * @see java.util.Collection#add(java.lang.Object)
     */
    @Override
    public boolean add(E o)
    {
        // Add the item only if it is not in the list.
        // Add it into the list so that it is in sorted order.
        int pos = Collections.binarySearch(this, o);
        if (pos < 0)
        {
            super.add(-pos - 1, o);
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Collection#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> c)
    {
        // Might be better to add the list to the end
        // and then sort the list.
        // This can be revisited if the list performs badly.
        boolean added = false;
        for (E e : c)
        {
            if (add(e))
            {
                added = true;
            }
        }
        return added;
    }

    /* (non-Javadoc)
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c)
    {
        // Ignore the index
        return addAll(c);
    }

    /* (non-Javadoc)
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E element)
    {
        // remove the item at the index (keep it to return it),
        // then insert the item into the sorted list.
        E item = remove(index);
        add(element);
        return item;
    }

    /**
     * Get a filtered list set.
     * @param filter The criteria by which to filter.
     * @return a filtered SortedListSet.
     */
    @SuppressWarnings("unchecked")
    public SortedListSet<E> filter(Filter filter)
    {
        // create a copy of the list and
        // remove everything that fails the test.
        SortedListSet<E> listSet = (SortedListSet) clone();
        Iterator iter = listSet.iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            if (!filter.test(obj))
            {
                iter.remove();
            }
        }
        return listSet;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258413945407484212L;
}