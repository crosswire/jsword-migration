package org.crosswire.bibledesktop.display.jdtb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.util.ConverterFactory;

/**
 * .
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
public class JDTBURLConnection extends URLConnection
{
    /**
     * @param url
     */
    protected JDTBURLConnection(URL url) throws IOException
    {
        super(url);

        try
        {
            String astext = url.toString();
            int sep1 = astext.indexOf(PROTOCOL_SEPARATOR1);
            int sep2 = astext.indexOf(PROTOCOL_SEPARATOR2);
            String bookName = astext.substring(sep1, sep2);
            String keyName = astext.substring(sep2);
            book = Books.installed().getBook(bookName);
    
            key = book.getKey(keyName);
        }
        catch (Exception ex)
        {
            throw new IOException(ex.toString());
        }
    
        converter = ConverterFactory.getConverter();
    }

    /* (non-Javadoc)
     * @see java.net.URLConnection#connect()
     */
    public void connect() throws IOException
    {
        try
        {
            BookData data = book.getData(key);
            BookMetaData bmd = book.getBookMetaData();
            boolean direction = bmd.isLeftToRight();

            SAXEventProvider osissep = data.getSAXEventProvider();
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            htmlsep.setParameter("direction", direction ? "ltr" : "rtl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            String outputText = XMLUtil.writeToString(htmlsep);
            byte[] bytes = outputText.getBytes();
            in = new ByteArrayInputStream(bytes);
        }
        catch (Exception ex)
        {
            throw new IOException(ex.toString());
        }
    }

    /* (non-Javadoc)
     * @see java.net.URLConnection#getInputStream()
     */
    public InputStream getInputStream() throws IOException
    {
        if (!connected)
        {
            connect();
        }

        return in;
    }

    /**
     * Create a URL from a Book and a Key
     * @throws MalformedURLException if URL creation fails
     */
    public static URL createURL(Book book, Key key) throws MalformedURLException
    {
        return new URL(PROTOCOL_NAME + PROTOCOL_SEPARATOR1 + book.getBookMetaData().getInitials() + PROTOCOL_SEPARATOR2 + key);
    }

    public static final String PROTOCOL_NAME = "book"; //$NON-NLS-1$
    private static final String PROTOCOL_SEPARATOR1 = ":///"; //$NON-NLS-1$
    private static final String PROTOCOL_SEPARATOR2 = "/"; //$NON-NLS-1$

    /**
     * Where the data comes from
     */
    private InputStream in = null;

    /**
     * The current book
     */
    private Book book = null;

    /**
     * The current key
     */
    private Key key = null;

    /**
     * To convert OSIS to HTML
     */
    private Converter converter = null;
}