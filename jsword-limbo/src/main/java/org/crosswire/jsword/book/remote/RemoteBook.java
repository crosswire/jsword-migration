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
package org.crosswire.jsword.book.remote;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.jdom.Document;
import org.xml.sax.SAXException;

/**
 * A Biblical source that comes from some form of remoting code.
 * <p>LATER(joe): Currently this will not work for non Passage based Keys.
 * 
 * The remoting mechanism is defined by an implementation of RemoteBibleDriver.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RemoteBook extends AbstractBook
{
    /**
     * Basic constructor for a SerBook
     */
    public RemoteBook(Remoter remoter, RemoteBookDriver driver, String name, BookCategory type)
    {
        BookMetaData bmd = new DefaultBookMetaData(driver, name, type);
        setBookMetaData(bmd);

        this.remoter = remoter;
        this.driver = driver;

        log.debug("Started RemoteBook"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#getData(org.crosswire.jsword.passage.Passage)
     */
    public BookData getData(Key key) throws BookException
    {
        try
        {
            Passage ref = KeyUtil.getPassage(key);

            RemoteMethod method = new RemoteMethod(MethodName.GETDATA);
            method.addParam(ParamName.PARAM_BIBLE, driver.getID(getBookMetaData()));
            method.addParam(ParamName.PARAM_PASSAGE, ref.getName());

            Document doc = remoter.execute(method);
            SAXEventProvider provider = new JDOMSAXEventProvider(doc);

            return new BookData(provider, this, key);
        }
        catch (RemoterException ex)
        {
            throw new BookException(Msg.REMOTE_FAIL, ex);
        }
        catch (SAXException ex)
        {
            throw new BookException(Msg.REMOTE_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getRawData(org.crosswire.jsword.passage.Key)
     */
    public String getRawData(Key key) throws BookException
    {
        StringBuffer buffer = new StringBuffer();
        return buffer.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Bible#findPassage(org.crosswire.jsword.book.Search)
     */
    @Override
    public Key find(String search) throws BookException
    {
        try
        {
            RemoteMethod method = new RemoteMethod(MethodName.FINDPASSAGE);
            method.addParam(ParamName.PARAM_BIBLE, driver.getID(getBookMetaData()));
            method.addParam(ParamName.PARAM_FINDSTRING, search);
            Document doc = remoter.execute(method);

            return Converter.convertDocumentToKeyList(doc, this);
        }
        catch (ConverterException ex)
        {
            throw new BookException(Msg.PARSE_FAIL, ex);
        }
        catch (RemoterException ex)
        {
            throw new BookException(Msg.REMOTE_FAIL, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public final Key getGlobalKeyList()
    {
        return keyf.getGlobalKeyList();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#isValidKey(java.lang.String)
     */
    public Key getValidKey(String name)
    {
        try
        {
            return getKey(name);
        }
        catch (Exception e)
        {
            return createEmptyKeyList();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public final Key getKey(String text) throws NoSuchKeyException
    {
        return keyf.getKey(text);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public Key createEmptyKeyList()
    {
        return keyf.createEmptyKeyList();
    }

    /**
     * Our key manager
     */
    private KeyFactory keyf = PassageKeyFactory.instance();

    /**
     * We need to be able to get IDs
     */
    private RemoteBookDriver driver;

    /**
     * So we can request remote services
     */
    private Remoter remoter;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RemoteBook.class);
}