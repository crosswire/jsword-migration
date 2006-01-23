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
package org.crosswire.jsword.book.search.lucene;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.crosswire.jsword.book.query.Query;
import org.crosswire.jsword.book.query.QueryBuilder;
import org.crosswire.jsword.book.query.basic.AndNotQuery;
import org.crosswire.jsword.book.query.basic.AndQuery;
import org.crosswire.jsword.book.query.basic.BaseQuery;
import org.crosswire.jsword.book.query.basic.BlurQuery;
import org.crosswire.jsword.book.query.basic.NullQuery;
import org.crosswire.jsword.book.query.basic.RangeQuery;

/**
 * A query can have a optional range specifier and an optional blur specifier.
 * The range specifier can be +[range], -[range] or just [range].
 * This must stand at the beginning of the query and may be surrounded by whitespace.
 * The blur specifier is either ~ or ~n, where ~ means adjacent verses,
 * but ~n means to blur by n verses.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public final class LuceneQueryBuilder implements QueryBuilder
{
    /**
     * Prevent Instansiation
     */
    public LuceneQueryBuilder()
    {
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.query.QueryBuilder#parse(java.lang.String)
     */
    public Query parse(String aSearch)
    {
        Query query = NULL_QUERY;

        String sought = aSearch;
        if (sought == null || sought.length()  == 0)
        {
            return query;
        }

        int i = 0;

        Query range = null;
        String rangeModifier = ""; //$NON-NLS-1$
        // Look for a range +[...], -[...], or [...]
        Matcher rangeMatcher = RANGE_PATTERN.matcher(sought);
        if (rangeMatcher.find())
        {
            rangeModifier = rangeMatcher.group(1);
            range = new RangeQuery(rangeMatcher.group(2));
            sought = sought.substring(rangeMatcher.end());
        }

        // Look for a blur ~n
        Matcher blurMatcher = BLUR_PATTERN.matcher(sought);
        if (blurMatcher.find())
        {
            // Did we have ~ or ~n?
            int blurFactor = 1;
            String blur = blurMatcher.group(1);
            if (blur.length() > 0)
            {
                blurFactor = Integer.valueOf(blur).intValue();
            }
            Query left = new BaseQuery(sought.substring(i, blurMatcher.start()));
            Query right = new BaseQuery(sought.substring(blurMatcher.end()));
            query = new BlurQuery(left, right, blurFactor);
        }
        else if (sought.length() > 0)
        {
            query = new BaseQuery(sought);
        }

        if (range != null && !NULL_QUERY.equals(query))
        {
            if (rangeModifier.length() == 0 || rangeModifier.charAt(0) == '+')
            {
                query = new AndQuery(range, query);
            }
            else
            {
                // AndNot needs to be after what it is restricting
                query = new AndNotQuery(query, range);
            }
        }

        return query;
    }

    /**
     * The pattern of a range. This is anything that is
     * contained between a leading [] (but not containing a [ or ]),
     * with a + or - optional prefix,
     * perhaps surrounded by whitespace.
     */
    private static final Pattern RANGE_PATTERN = Pattern.compile("^\\s*([-+]?)\\[([^\\[\\]]+)\\]\\s*"); //$NON-NLS-1$

    /**
     * The pattern of a blur. A '~', optionally followed by a number, representing the number of verses.
     */
    private static final Pattern BLUR_PATTERN = Pattern.compile("\\s~(\\d*)?\\s"); //$NON-NLS-1$

    /**
     * A query that returns nothing.
     */
    private static final Query NULL_QUERY = new NullQuery();

}
