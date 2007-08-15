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
 * ID: $Id: FilterFactory.java 1505 2007-07-21 19:40:19Z dmsmith $
 */
package org.crosswire.jsword.book.filter;

import org.crosswire.jsword.internal.osgi.FilterRegistry;

/**
 * A simple container for all the known filters.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class FilterFactory
{
    /**
     * Prevent instantiation
     */
    private FilterFactory()
    {
    }

    /**
     * Find a filter given a lookup string. If lookup is null or the filter is
     * not found then the default filter will be used.
     */
    public static Filter getFilter(String lookup)
    {
        if (lookup == null) {
            return FilterRegistry.getDefaultFilter();
        }
        
        Filter filter = FilterRegistry.getFilterById(lookup);
        if (filter == null)
        {
            //The requested filter wasn't found. Drop back
            //to the default filter.
            filter = FilterRegistry.getDefaultFilter();
        }

        return filter;
    }

    /**
     * Retrieve the default filter.
     */
    public static Filter getDefaultFilter()
    {
        return FilterRegistry.getDefaultFilter();
    }
}
