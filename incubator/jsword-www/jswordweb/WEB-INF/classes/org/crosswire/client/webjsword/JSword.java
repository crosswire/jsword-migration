package org.crosswire.client.webjsword;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.book.OSISUtil;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * @author Joe Walker [joe at eireneh dot com]
 */
public class JSword
{
    /**
     * The source to this method is an example of how to read the plain text of
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     */
    public String getPlainText(String biblename, String passage) throws BookException, NoSuchKeyException
    {
        Book book = getBook(biblename);

        Key key = book.getKey(passage);
        BookData data = new BookData(book,key);
	          return OSISUtil.getCanonicalText(data.getOsisFragment());  
    }

    /**
     * The source to this method is an example of how to read the plain text of
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     */
    public SAXEventProvider getOSIS(String biblename, String passage) throws BookException, NoSuchKeyException
    {
        Book book = getBook(biblename);

        Key key = book.getKey(passage);
        if (key.getChildCount() > 100)
        {
            if (key instanceof Passage)
            {
                Passage ref = (Passage) key;
                ref.trimVerses(100);
            }
            else
            {
                throw new IllegalArgumentException("Too many verses.");
            }
        }

        BookData data = new BookData(book,key);

        return data.getSAXEventProvider();
    }

    /**
     * The source to this method is an example of how to read the plain text of
     * a verse, and print it to stdout. Reading from a Commentary is just the
     * same as reading from a Bible.
     */
    public String getOSISString(String biblename, String passage) throws NoSuchKeyException
    {
        try
        {
            SAXEventProvider sep = getOSIS(biblename, passage);
            ContentHandler ser = new SerializingContentHandler();
            sep.provideSAXEvents(ser);
            return ser.toString();
        }
        catch (Exception ex)
        {
            //throw new BookException("Msg.JSWORD_SAXPARSE", ex);
		ex.printStackTrace();
        }
	return null;
    }

    /**
     * An example of how to search for various bits of data.
     */
    public String search(String biblename, String find) throws BookException
    {
        Book book = getBook(biblename);

        Key key = book.find(find);
        return key.getName();
    }

    /**
     * 
     */
    public String[] matches(String biblename, String starts) throws BookException
    {
        Book book = getBook(biblename);

        SortedSet index = (SortedSet) indexCache.get(book);
        if (index == null)
        {
            index = new TreeSet();

            Key keys = book.getGlobalKeyList();
            for (Iterator it = keys.iterator(); it.hasNext();)
            {
                Key subkey = (Key) it.next();
                String name = subkey.getName();
                index.add(name.toLowerCase());
            }
        }

        // Danger: there are probably some i18n issues with this trick - we
        // are getting everything that sorts bewteen the given elements. As
        // soon as people start searching for i18n chars then this might fail
        // I don't know enough about the i18n sort orders. I guess the default
        // locale has a lot to do with things
        starts = starts.toLowerCase();
        SortedSet reply = index.subSet(starts, starts + "zz");

        // Convert to an array. Normally we'd use Collection.toArray() however
        // we want to restrict the size of the reply, and that method insists
        // on giving you back everything even if you give it a smaller initial
        // array
        int size = reply.size();
        if (size > MAX_MATCHES)
        {
            size = MAX_MATCHES;
        }

        String[] a = new String[size];

        Iterator it = reply.iterator();
        for (int i=0; i<size; i++)
        {
            a[i] = (String) it.next();
        }

        if (a.length > size)
        {
            a[size] = null;
        }

        return a;
    }

    private Map indexCache = new HashMap();

    /**
     * This is an example of the different ways to select a Book from the
     * selection available.
     * @param filter The filter string
     * @see BookFilters#getCustom(java.lang.String)
     * @see Books
     */
    public String[][] getBooks(String filter)
    {
        List books = Books.installed().getBooks(BookFilters.getCustom(filter));

        List reply = new ArrayList();
        for (Iterator it = books.iterator(); it.hasNext();)
        {
            Book book = (Book) it.next();
            String[] rbook = new String[] { book.getInitials(), book.getName() }; 
            reply.add(rbook);
        }

        return (String[][]) reply.toArray(new String[reply.size()][]);
    }

    /**
     * @param biblename The book name to search for
     * @return The found book
     * @throws BookException If the book could not be found
     */
    private Book getBook(String biblename)// throws BookException
    {
        Book bible = Books.installed().getBook(biblename);
        if (bible == null)
        {
	System.out.println("exception at get book for biblename");
        //    throw new BookException("Msg.JSWORD_NOTFOUND", new Object[] { biblename });
        }
        return bible;
    }

    private static final int MAX_MATCHES = 10;
}
