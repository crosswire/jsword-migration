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
 * Copyright: 2006
 *     The copyright to this program is held by it's authors.
 *
 */

package org.crosswire.jsword.internal.osgi;

import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.lucene.LuceneIndexManager;
import org.crosswire.jsword.index.lucene.LuceneQueryBuilder;
import org.crosswire.jsword.index.lucene.LuceneQueryDecorator;
import org.crosswire.jsword.index.lucene.LuceneSearcher;
import org.crosswire.jsword.index.query.QueryBuilder;
import org.crosswire.jsword.index.query.QueryDecorator;
import org.crosswire.jsword.index.search.Searcher;
import org.osgi.framework.BundleContext;

/**
 * This class provides index-related data to the 
 * class within the bundle.
 * 
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class IndexRegistry
{

    /**
     * This method is the activator's entry point. This is only
     * called once per start of the bundle. 
     *  
     * @param context Our bundle's context.
     */
    static void register(BundleContext context)
    {
        context.registerService(Searcher.class.getName(), new LuceneSearcher(), ServiceUtil.createIdDictionary(ID_INDEX_SEARCHER, DEFAULT));
        context.registerService(IndexManager.class.getName(), new LuceneIndexManager(), ServiceUtil.createIdDictionary(ID_INDEX_MANAGER, DEFAULT));
        context.registerService(QueryBuilder.class.getName(), new LuceneQueryBuilder(), ServiceUtil.createIdDictionary(ID_INDEX_QUERY_BUILDER, DEFAULT));
        context.registerService(QueryDecorator.class.getName(), new LuceneQueryDecorator(), ServiceUtil.createIdDictionary(ID_INDEX_QUERY_DECORATOR, DEFAULT));

    }

    /**
     * This method is called when the bundle stops.
     * 
     * @param context
     */
    static void unregister(BundleContext context)
    {
        //Nothing to purge.
    }

    /**
     * This method returns the default query builder.
     * @return The default query builder.
     */
    public static QueryBuilder getDefaultIndexQueryBuilder() {
        return getIndexQueryBuilderById(DEFAULT);
    }

    /**
     * This method returns the default query decorator.
     * @return The default query decorator.
     */
    public static QueryDecorator getDefaultIndexQueryDecorator() {
        return getIndexQueryDecoratorById(DEFAULT);
    }

    /**
     * This method returns the default index manager.
     * @return The default index manager.
     */
    public static IndexManager getDefaultIndexManager() {
        return getIndexManagerById(DEFAULT);
    }
    
    /**
     * This method returns the default index searcher.
     * @return The default index searcher.
     */
    public static Searcher getDefaultIndexSearcher()
    {
        return getIndexSearcherById(DEFAULT);
    }
    
    private static QueryBuilder getIndexQueryBuilderById(String builderId)
    {
        return (QueryBuilder) ServiceUtil.getServiceById(QueryBuilder.class, ID_INDEX_QUERY_BUILDER, builderId);
    }
    
    private static QueryDecorator getIndexQueryDecoratorById(String decoratorId)
    {
        return (QueryDecorator) ServiceUtil.getServiceById(QueryDecorator.class, ID_INDEX_QUERY_DECORATOR, decoratorId);
    }

    private static IndexManager getIndexManagerById(String indexManagerId)
    {
        return (IndexManager) ServiceUtil.getServiceById(IndexManager.class, ID_INDEX_MANAGER, indexManagerId);
    }

    private static Searcher getIndexSearcherById(String searcherId)
    {
        return (Searcher) ServiceUtil.getServiceById(Searcher.class, ID_INDEX_SEARCHER, searcherId);
    }

    private static final String ID_INDEX_SEARCHER = "indexsearcher.id";
    private static final String ID_INDEX_MANAGER = "indexmanager.id";
    private static final String ID_INDEX_QUERY_DECORATOR = "indexquerydecorator.id";
    private static final String ID_INDEX_QUERY_BUILDER = "indexquerybuilder.id";
    private static final String DEFAULT = "default";
    
}
