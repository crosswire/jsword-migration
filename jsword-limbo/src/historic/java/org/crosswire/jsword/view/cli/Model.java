package org.crosswire.jsword.view.cli;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.Config;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookFilter;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.util.Project;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * Quick helper for writing scripts to control JSword.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class Model
{
    public Model() throws MalformedURLException, JDOMException, IOException
    {
        config = new Config("Tool Shed Options");
        Document xmlconfig = Project.instance().getDocument("config");
        config.add(xmlconfig);

        try
        {
            config.setProperties(Project.instance().getProperties("cli"));
            config.localToApplication(true);
        }
        catch (Exception ex)
        {
            // If there is no stored config, dont worry
        }
    }

    public String dictList()
    {
        List dicts = Books.installed().getBooks(BookFilters.getDictionaries());
        Book dict = (Book) dicts.get(0);

        KeyList set = dict.getGlobalKeyList();

        StringBuffer buffer = new StringBuffer();
        for (Iterator it = set.iterator(); it.hasNext();)
        {
            Key key = (Key) it.next();
            buffer.append(key.getName());

            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    /**
     * This was private and called by methods that used Defaults (now
     * deprecated) we need to replace those methods, and probably still call
     * this method.
     */
    public String getData(String sref, Book book) throws NoSuchKeyException, BookException
    {
        Key key = book.getKey(sref);
        BookData bdata = book.getData(key);
        return bdata.getPlainText();
    }
    
    public String search(String str) throws BookException
    {
        List dicts = Books.installed().getBooks(BookFilters.getBibles());
        Book book = (Book) dicts.get(0);
        
        Key key = book.find(new Search(str, false));
        return key.getName();
    }
    
    public String match(String str) throws BookException
    {
        List dicts = Books.installed().getBooks(BookFilters.getBibles());
        Book book = (Book) dicts.get(0);

        Key key = book.find(new Search(str, true));
        return key.getName();
    }
    
    public String bibles()
    {
        return display(BookFilters.getBibles());
    }

    public String dicts()
    {
        return display(BookFilters.getDictionaries());
    }
    
    public String comments()
    {
        return display(BookFilters.getCommentaries());
    }
    
    public String books(BookFilter filter)
    {
        return display(filter);
    }
    
    private String display(BookFilter filter)
    {
        StringBuffer buffer = new StringBuffer();

        List list = Books.installed().getBooks(filter);
        for (Iterator it = list.iterator(); it.hasNext();)
        {
            Book book = (Book) it.next();
            buffer.append(book.getName());
            
            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }
    
    public String config()
    {
        StringBuffer buffer = new StringBuffer();
        
        for (Iterator it = config.getNames(); it.hasNext();)
        {
            String key = (String) it.next();
            Choice choice = config.getChoice(key);

            buffer.append(key+" = "+choice.getString());

            if (it.hasNext())
            {
                buffer.append('\n');
            }
        }

        return buffer.toString();
    }

    public String config(String key, String value) throws Exception
    {
        Choice choice = config.getChoice(key);
        choice.setString(value);

        return choice.getString();
    }

    public String save() throws IOException
    {
        URL url = Project.instance().getWritablePropertiesURL("cli");
        config.applicationToLocal();
        config.localToPermanent(url);
        
        return "OK";
    }

    private Config config;
}
