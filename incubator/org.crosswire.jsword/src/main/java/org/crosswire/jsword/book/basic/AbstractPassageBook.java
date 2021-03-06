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
 * ID: $Id: AbstractPassageBook.java 1466 2007-07-02 02:48:09Z dmsmith $
 */
package org.crosswire.jsword.book.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.book.filter.Filter;
import org.crosswire.jsword.book.filter.FilterException;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Element;

/**
 * An abstract implementation of Book that lets implementors just concentrate
 * on reading book data.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractPassageBook extends AbstractBook
{
    public AbstractPassageBook(BookMetaData bmd)
    {
        super(bmd);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#getOsisIterator(org.crosswire.jsword.passage.Key, boolean)
     */
    public Iterator getOsisIterator(Key key, boolean allowEmpty) throws BookException
    {
        // TODO(DMS): make the iterator be demand driven
        try
        {
            List content = new ArrayList();

            // For all the ranges in this Passage
            Passage ref = KeyUtil.getPassage(key);
            boolean hasRanges = ref.hasRanges(RestrictionType.CHAPTER);
            Iterator rit = ref.rangeIterator(RestrictionType.CHAPTER);

            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();

                // Only add the title if there are multiple ranges
                if (hasRanges)
                {
                    Element title = OSISUtil.factory().createTitle();
                    title.addContent(range.getName());
                    content.add(title);
                }

                // For all the verses in this range
                Iterator vit = range.iterator();
                while (vit.hasNext())
                {
                    Key verse = (Key) vit.next();
                    String txt = getRawText(verse);

                    // If the verse is empty then we shouldn't add the verse tag
                    if (allowEmpty || txt.length() > 0)
                    {
                        List osisContent = getFilter().toOSIS(this, verse, txt);
                        addOSIS(verse, content, osisContent);
                    }
                }
            }

            return content.iterator();
        }
        catch (FilterException ex)
        {
            throw new BookException(Msg.FILTER_FAIL, ex);
        }
    }

    /**
     * Add the OSIS elements to the div element. Note, this assumes that
     * the data is fully marked up.
     * @param key The key being added
     * @param div The div element to which the key's OSIS representation is being added
     * @param osisContent The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, Element div, List osisContent)
    {
        assert key != null;
        div.addContent(osisContent);
    }

    /**
     * Add the OSIS elements to the div element. Note, this assumes that
     * the data is fully marked up.
     * @param key The key being added
     * @param content The list to which the key's OSIS representation is being added
     * @param osisContent The OSIS representation of the key being added.
     */
    public void addOSIS(Key key, List content, List osisContent)
    {
        assert key != null;
        content.addAll(osisContent);
    }

    /**
     * What filter should be used to filter data in the format produced by this
     * Book?.
     * In some ways this method is more suited to BookMetaData however we do not
     * have a specialization of BookMetaData to fit AbstractPassageBook and it
     * doesn't like any higher in the hierachy at the moment so I will leave
     * this here.
     */
    protected abstract Filter getFilter();

    /**
     * For when we want to add writing functionality. This does not work.
     */
    public void setDocument(Key key, BookData bdata) throws BookException
    {
        // For all of the sections
        Iterator sit = OSISUtil.getFragment(bdata.getOsisFragment()).iterator();
        while (sit.hasNext())
        {
            Element div = (Element) sit.next();

            // For all of the Verses in the section
            for (Iterator vit = div.getContent().iterator(); vit.hasNext(); )
            {
                Object data = vit.next();
                if (data instanceof Element)
                {
                    Element overse = (Element) data;
                    String text = OSISUtil.getPlainText(overse);

                    setRawText(key, text);
                }
                else
                {
                    log.error("Ignoring non OSIS/Verse content of DIV."); //$NON-NLS-1$
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.Book#isWritable()
     */
    public boolean isWritable()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.passage.KeyFactory#getEmptyKeyList()
     */
    public final Key createEmptyKeyList()
    {
        return keyf.createEmptyKeyList();
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
        catch (NoSuchKeyException e)
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

    /**
     * Our key manager
     */
    private KeyFactory keyf = PassageKeyFactory.instance();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractPassageBook.class);

}
