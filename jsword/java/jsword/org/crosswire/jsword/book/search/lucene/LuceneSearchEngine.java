package org.crosswire.jsword.book.search.lucene;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.crosswire.common.progress.Job;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.search.AbstractSearchEngine;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyList;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;

/**
 * Implement the SearchEngine using Lucene as the search engine.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class LuceneSearchEngine extends AbstractSearchEngine
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#init(org.crosswire.jsword.book.Bible, java.net.URL)
     */
    public void init(Book newbible, URL newurl) throws BookException
    {
        try
        {
            url = NetUtil.lengthenURL(newurl, DIR_LUCENE);
            book = newbible;

            if (isIndexed())
            {
                // Opening Lucene indexes is quite quick I think, so we can try
                // it to see if it works to report errors that we want to drop
                // later
                loadIndexes();
            }
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.LUCENE_INIT, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#findKeyList(org.crosswire.jsword.book.Search)
     */
    public KeyList findKeyList(Search search) throws BookException
    {
        checkActive();

        // LATER(joe): think about splitting out the parser.
        /*
        Parser parser = ParserFactory.createParser(this);
        return parser.search(search);
        */

        try
        {
            Analyzer analyzer = new StandardAnalyzer();
            Query query = QueryParser.parse(search.getMatch(), FIELD_BODY, analyzer);
            Hits hits = searcher.search(query);

            PassageTally tally = new PassageTally();
            for (int i = 0; i < hits.length(); i++)
            {
                Verse verse = new Verse(hits.doc(i).get(FIELD_NAME));
                int score = (int) (hits.score(i) * 100);
                tally.add(verse, score);
            }

            return tally;
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.SEARCH_FAILED, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.Index#getKey(java.lang.String)
     *
    public Key getKey(String name) throws NoSuchKeyException
    {
        return book.getKey(name);
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#getStartsWith(java.lang.String)
     *
    public Iterator getStartsWith(String word) throws BookException
    {
        // NOTE(joe): we could probably implement this, but only if we can split the parser out
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#findWord(java.lang.String)
     *
    public Passage findWord(String word) throws BookException
    {
        // NOTE(joe): we could probably implement this, but only if we can split the parser out
    }
    */

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#delete()
     */
    public void delete()
    {
        checkActive();

        // LATER(joe): write this
        /*
        Directory directory = FSDirectory.getDirectory("demo index", false);
        IndexReader reader = IndexReader.open(directory);

        //       Term term = new Term("path", "pizza");
        //       int deleted = reader.delete(term);

        //       System.out.println("deleted " + deleted +
        //           " documents containing " + term);

        for (int i = 0; i < reader.maxDoc(); i++)
            reader.delete(i);

        reader.close();
        directory.close();
        */
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#isIndexed()
     */
    protected boolean isIndexed()
    {
        if (isRunning())
        {
            return false;
        }

        URL index = NetUtil.lengthenURL(url, DIR_SEGMENTS);
        return NetUtil.isFile(index);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#generateSearchIndex(org.crosswire.common.progress.Job)
     */
    protected void generateSearchIndex(Job job) throws IOException, BookException, NoSuchKeyException
    {
        // An index is created by opening an IndexWriter with the
        // create argument set to true.
        IndexWriter writer = new IndexWriter(NetUtil.getAsFile(url), new StandardAnalyzer(), true);

        int percent = -1;
        for (Iterator it = WHOLE.verseIterator(); it.hasNext();)
        {
            Verse verse = (Verse) it.next();
            Key key = book.getKey(verse.getName());
            BookData data = book.getData(key);
            Reader reader = new StringReader(data.getPlainText());

            Document doc = new Document();
            doc.add(Field.Text(FIELD_NAME, verse.getName()));
            doc.add(Field.Text(FIELD_BODY, reader));

            writer.addDocument(doc);

            // report progress
            int newpercent = 95 * verse.getOrdinal() / BibleInfo.versesInBible();
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, Msg.INDEXING.toString(verse.getName()));
            }
        }

        job.setProgress(percent, Msg.OPTIMIZING.toString());

        writer.optimize();
        writer.close();

        loadIndexes();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#loadIndexes()
     */
    protected void loadIndexes() throws IOException
    {
        searcher = new IndexSearcher(NetUtil.getAsFile(url).getCanonicalPath());
    } 

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#unloadIndexes()
     */
    protected void unloadIndexes() throws IOException
    {
        searcher.close();
        searcher = null;
    }

    /**
     * The lucene search index directory
     */
    private static final String DIR_LUCENE = "lucene"; //$NON-NLS-1$

    /**
     * The segments directory
     */
    private static final String DIR_SEGMENTS = "segments"; //$NON-NLS-1$

    /**
     * The Lucene field for the verse name
     */
    private static final String FIELD_NAME = "name"; //$NON-NLS-1$

    /**
     * The Lucene field for the verse contents
     */
    private static final String FIELD_BODY = "body"; //$NON-NLS-1$

    /**
     * The Book that we are indexing
     */
    protected Book book;

    /**
     * The location of this index
     */
    private URL url;

    /**
     * The Lucene search engine
     */
    private Searcher searcher;

    /**
     * The Whole Bible
     * LATER(joe): this should be getIndex();
     */
    private static final Passage WHOLE = PassageFactory.getWholeBiblePassage();
}