package org.crosswire.jsword.book.search.ser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.crosswire.common.progress.Job;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookUtil;
import org.crosswire.jsword.book.Search;
import org.crosswire.jsword.book.search.AbstractSearchEngine;
import org.crosswire.jsword.book.search.Index;
import org.crosswire.jsword.book.search.Parser;
import org.crosswire.jsword.book.search.ParserFactory;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageFactory;
import org.crosswire.jsword.passage.PassageUtil;
import org.crosswire.jsword.passage.Verse;

/**
 * A search engine - This is a stepping stone on the way to allowing use of
 * Lucene in place of our search engine.
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
public class SerSearchEngine extends AbstractSearchEngine implements Index
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#init(org.crosswire.jsword.book.Bible, java.net.URL)
     */
    public void init(Book newbook, URL newurl)
    {
        this.book = newbook;
        this.url = newurl;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#findPassage(org.crosswire.jsword.book.Search)
     */
    public Passage findPassage(Search search) throws BookException
    {
        checkActive();

        try
        {
            Parser parser = ParserFactory.createParser(this);
            return parser.search(search);
        }
        catch (InstantiationException ex)
        {
            throw new BookException(Msg.SEARCH_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.SearchEngine#delete()
     */
    public void delete()
    {
        checkActive();

        // LATER(joe): write delete()
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#getStartsWith(java.lang.String)
     */
    public Iterator getStartsWith(String word)
    {
        checkActive();

        word = word.toLowerCase();
        SortedMap submap = datamap.subMap(word, word + "\u9999"); //$NON-NLS-1$
        return submap.keySet().iterator();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.parse.Index#findWord(java.lang.String)
     */
    public Passage findWord(String word)
    {
        checkActive();

        if (word == null)
        {
            return PassageFactory.createPassage();
        }

        Section section = (Section) datamap.get(word.toLowerCase());
        if (section == null)
        {
            return PassageFactory.createPassage();
        }

        try
        {
            // Read blob
            byte[] blob = new byte[section.length];
            dataRaf.seek(section.offset);
            int read = dataRaf.read(blob);

            // Probably a bit harsh, but it would be wrong to just drop it.
            if (read != blob.length)
            {
                throw new IOException();
            }

            // De-serialize
            return PassageUtil.fromBinaryRepresentation(blob);
        }
        catch (Exception ex)
        {
            log.warn("Search failed on:"); //$NON-NLS-1$
            log.warn("  word=" + word); //$NON-NLS-1$
            log.warn("  offset=" + section.offset); //$NON-NLS-1$
            log.warn("  length=" + section.length); //$NON-NLS-1$
            Reporter.informUser(this, ex);

            return PassageFactory.createPassage();
        }
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

        URL indexIn = NetUtil.lengthenURL(url, FILE_INDEX);
        return NetUtil.isFile(indexIn);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#loadIndexes()
     */
    protected void loadIndexes()
    {
        try
        {
            URL dataUrl = NetUtil.lengthenURL(url, FILE_DATA);
            dataRaf = new RandomAccessFile(NetUtil.getAsFile(dataUrl), FileUtil.MODE_READ);

            URL indexUrl = NetUtil.lengthenURL(url, FILE_INDEX);
            BufferedReader indexIn = new BufferedReader(new InputStreamReader(indexUrl.openStream()));

            while (true)
            {
                String line = indexIn.readLine();
                if (line == null)
                {
                    break;
                }

                try
                {
                    int colon1 = line.indexOf(":"); //$NON-NLS-1$
                    int colon2 = line.lastIndexOf(":"); //$NON-NLS-1$
                    String word = line.substring(0, colon1);

                    long offset = Long.parseLong(line.substring(colon1 + 1, colon2));
                    int length = Integer.parseInt(line.substring(colon2 + 1));

                    Section section = new Section(offset, length);
                    datamap.put(word, section);
                }
                catch (NumberFormatException ex)
                {
                    log.error("NumberFormatException reading line: "+line, ex); //$NON-NLS-1$
                }
            }
        }
        catch (IOException ex)
        {
            log.error("Read failed on indexin", ex); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#unloadIndexes()
     */
    protected void unloadIndexes()
    {
        datamap.clear();
        dataRaf = null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.search.AbstractSearchEngine#generateSearchIndex(org.crosswire.common.progress.Job)
     */
    protected void generateSearchIndex(Job job) throws BookException
    {
        // create a word/passage hashmap
        Map matchmap = new HashMap();

        // If we get an error reading a verse dont give up straight away
        int errors = 0;

        // loop through all the verses
        int percent = -1;
        for (Iterator it = WHOLE.verseIterator(); it.hasNext();)
        {
            Verse verse = (Verse) it.next();
            try
            {
                int newpercent = PERCENT_READ * verse.getOrdinal() / BibleInfo.versesInBible();
                if (percent != newpercent)
                {
                    percent = newpercent;
                    job.setProgress(percent, Msg.FINDING_WORDS.toString(verse.getName()));
                }

                // loop through all the words in this verse
                Passage current = PassageFactory.createPassage();
                current.add(verse);
                String text = book.getData(current).getPlainText();
                String[] words = BookUtil.getWords(text);
                for (int i = 0; i < words.length; i++)
                {
                    // ensure there is a Passage for this word in the word/passage hashmap
                    Passage matches = (Passage) matchmap.get(words[i]);
                    if (matches == null)
                    {
                        matches = PassageFactory.createPassage();
                        matchmap.put(words[i], matches);
                    }

                    // add this verse to this words passage
                    matches.add(verse);
                }

                // This could take a long time ...
                Thread.yield();
                if (Thread.currentThread().isInterrupted())
                {
                    break;
                }
            }
            catch (Exception ex)
            {
                errors++;
                log.error("Error reading "+verse.getName()+" in "+book.getBookMetaData().getFullName()+": errors="+errors, ex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                if (errors > MAX_ERRORS)
                {
                    throw new BookException(Msg.REPEATED_READ_ERROR, ex);
                }
            }
        }

        // For the progress listener
        int count = 0;
        int words = matchmap.size();

        // Now we need to write the words into our index
        try
        {
            NetUtil.makeDirectory(url);
            URL dataUrl = NetUtil.lengthenURL(url, FILE_DATA);
            dataRaf = new RandomAccessFile(NetUtil.getAsFile(dataUrl), FileUtil.MODE_WRITE);
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }

        for (Iterator it = matchmap.keySet().iterator(); it.hasNext();)
        {
            String word = (String) it.next();
            Passage match = (Passage) matchmap.get(word);
            recordFoundPassage(word, match);

            // Fire a progress event?
            int newpercent = PERCENT_READ + (PERCENT_WRITE * count++ / words) / BibleInfo.versesInBible();
            if (percent != newpercent)
            {
                percent = newpercent;
                job.setProgress(percent, Msg.WRITING_WORDS.toString(word));
            }

            // This could take a long time ...
            Thread.yield();
            if (Thread.currentThread().isInterrupted())
            {
                break;
            }
        }

        // Store the indexes on disk
        try
        {
            job.setProgress(PERCENT_READ + PERCENT_WRITE, Msg.SAVING.toString());

            // Save the ascii Passage index
            URL indexurl = NetUtil.lengthenURL(url, FILE_INDEX);
            PrintWriter indexout = new PrintWriter(NetUtil.getOutputStream(indexurl));
            Iterator it = datamap.keySet().iterator();
            while (it.hasNext())
            {
                String word = (String) it.next();
                Section section = (Section) datamap.get(word);
                indexout.println(word + ":" + section.offset + ":" + section.length); //$NON-NLS-1$ //$NON-NLS-2$
            }
            indexout.close();
        }
        catch (IOException ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * Add to the main index data the references against this word
     * @param word The word to write
     * @param ref The references to the word
     */
    private void recordFoundPassage(String word, Passage ref) throws BookException
    {
        if (word == null)
        {
            return;
        }

        try
        {
            byte[] buffer = PassageUtil.toBinaryRepresentation(ref);

            Section section = new Section(dataRaf.getFilePointer(), buffer.length);

            dataRaf.write(buffer);
            datamap.put(word.toLowerCase(), section);
        }
        catch (Exception ex)
        {
            throw new BookException(Msg.WRITE_ERROR, ex);
        }
    }

    /**
     * The name of the data file
     */
    private static final String FILE_DATA = "ref.data"; //$NON-NLS-1$

    /**
     * The name of the index file
     */
    private static final String FILE_INDEX = "ref.index"; //$NON-NLS-1$

    /**
     * The Bible we are indexing
     */
    protected Book book;

    /**
     * The directory to which to write the index
     */
    private URL url;

    /**
     * The passages random access file
     */
    private RandomAccessFile dataRaf;

    /**
     * The hash of indexes into the passages file
     */
    private SortedMap datamap = new TreeMap();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SerSearchEngine.class);

    /**
     * The percentages taken but by different parts
     */
    private static final int PERCENT_READ = 60;
    private static final int PERCENT_WRITE = 39;
    // private static final int PERCENT_INDEX = 1;

    /**
     * When generating the index, how many tries before we give up?
     */
    private static final int MAX_ERRORS = 256;

    /**
     * The Whole Bible
     * LATER(joe): this should be getIndex();
     */
    private static final Passage WHOLE = PassageFactory.getWholeBiblePassage();

    /**
     * A simple class to hold an offset and length into the passages random
     * access file
     */
    public static class Section
    {
        protected Section(long offset, int length)
        {
            this.offset = offset;
            this.length = length;
        }

        protected long offset;
        protected int length;
    }
}
