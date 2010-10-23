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
package org.crosswire.jsword.book.stub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.basic.AbstractBook;
import org.crosswire.jsword.book.basic.DefaultBookMetaData;
import org.crosswire.jsword.book.filter.FilterFactory;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.jdom.Element;

/**
 * StubBook is a simple stub implementation of Book that is pretty much always
 * going to work because it has no dependancies on external files.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StubDictionary extends AbstractBook {
    /**
     * Basic constructor for a StubBook
     */
    public StubDictionary(StubBookDriver driver, String name, BookCategory type) {
        super(new DefaultBookMetaData(driver, name, type));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage
     * .Key, boolean)
     */
    public Iterator getOsisIterator(Key key, boolean allowEmpty) throws BookException {
        assert key != null;

        try {
            List content = new ArrayList();

            Element title = OSISUtil.factory().createTitle();
            title.addContent(key.getName());
            content.add(title);

            List osisContent = FilterFactory.getDefaultFilter().toOSIS(this, key, "stub implementation");
            content.add(osisContent);

            return content.iterator();
        } catch (Exception ex) {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#getRawText(org.crosswire.jsword.passage
     * .Key)
     */
    public String getRawText(Key key) throws BookException {
        StringBuffer buffer = new StringBuffer();
        return buffer.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
    public boolean isWritable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#contains(org.crosswire.jsword.passage.Key)
     */
    public boolean contains(Key key) {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.basic.AbstractPassageBook#setRawText(org.crosswire
     * .jsword.passage.Key, java.lang.String)
     */
    public void setRawText(Key key, String rawData) throws BookException {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.jsword.book.Book#setAliasKey(org.crosswire.jsword.passage
     * .Key, org.crosswire.jsword.passage.Key)
     */
    public void setAliasKey(Key alias, Key source) throws BookException {
        throw new BookException(Msg.DRIVER_READONLY);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.KeyFactory#getGlobalKeyList()
     */
    public Key getGlobalKeyList() {
        return keyf.getGlobalKeyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.KeyFactory#isValidKey(java.lang.String)
     */
    public Key getValidKey(String name) {
        try {
            return getKey(name);
        } catch (Exception e) {
            return createEmptyKeyList();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.KeyFactory#getKey(java.lang.String)
     */
    public Key getKey(String name) throws NoSuchKeyException {
        return keyf.getKey(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public Key createEmptyKeyList() {
        return keyf.createEmptyKeyList();
    }

    private KeyFactory keyf = new StubDictionaryKeyFactory();
}
