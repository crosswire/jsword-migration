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
package org.crosswire.jsword.book;

import java.util.Map;

import org.crosswire.jsword.book.index.IndexStatus;
import org.crosswire.jsword.book.index.IndexStatusListener;
import org.jdom.Document;

/**
 * A BookMetaData represents a method of translating the Bible. All Books with
 * the same BookMetaData should return identical text for any call to
 * <code>Bible.getText(VerseRange)</code>. The implication of this is that
 * there may be many instances of the Version "NIV", as there are several
 * different versions of the NIV - Original American-English, Anglicized,
 * and Inclusive Language editions at least.
 *
 * <p>BookMetaData like Strings must be compared using <code>.equals()<code>
 * instead of ==. A Bible must have the ability to handle a book unknown to
 * JSword. So Books must be able to add versions to the system, and the system
 * must cope with books that already exist.</p>
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public interface BookMetaData extends Comparable
{
    /**
     * The name of the book, for example "King James Version" or
     * "Bible in Basic English" or "Greek".
     * In general it should be possible to deduce the initials from the name by
     * removing all the non-capital letters. Although this is only a generalization.
     * This method should not return null or a blank string.
     * @return The name of this book
     */
    String getName();

    /**
     * What category of content is this, a Bible or a reference work like a
     * Dictionary or Commentary.
     * @return The category of book
     */
    BookCategory getBookCategory();

    /**
     * Accessor for the driver that runs this Book.
     * Note this method should only be used to delete() Books. Everything else
     * you should want to do to a Book should be available in other ways.
     */
    BookDriver getDriver();

    /**
     * The language of the book is the common name for the iso639 code.
     * @return the common name for the language
     */
    String getLanguage();

    /**
     * The initials of this book - how people familiar with this book will know
     * it, for example "NIV", "KJV".
     * @return The book's initials
     */
    String getInitials();

    /**
     * Calculated field: Get an OSIS identifier for the OsisText.setOsisIDWork()
     * and the Work.setOsisWork() methods.
     * The response will generally be of the form [Bible][Dict..].getInitials
     * @return The osis id of this book
     */
    String getOsisID();

    /**
     * Calculated field: The full name of the book, for example
     * The format is "name, (Driver)"
     * @return The full name of this book
     */
    String getFullName();

    /**
     * Indicate whether this book is supported by JSword.
     * Since the expectation is that all books are supported,
     * abstract implementations should return true and let
     * specific implementations return false if they cannot
     * support the book.
     * 
     * @return true if the book is supported
     */
    public boolean isSupported();

    /**
     * Indicate whether this book is enciphered and without a key.
     * Since the expectation is that most books are unenciphered,
     * abstract implementations should return false and let
     * specific implementations return true otherwise.
     * 
     * @return true if the book is enciphered
     */
    public boolean isEnciphered();

    /**
     * Indicate whether this book is questionable. A book may
     * be deemed questionable if it's quality or content has not
     * been confirmed.
     * Since the expectation is that all books are not questionable,
     * abstract implementations should return false and let
     * specific implementations return true if the book is questionable.
     * 
     * @return true if the book is questionable
     */
    public boolean isQuestionable();

    /**
     * Calculated field: The name of the name, which could be helpful to
     * distinguish similar Books available through 2 BookDrivers.
     * @return The driver name
     */
    String getDriverName();

    /**
     * Return the orientation of the language of the Book. If a book contains more than one language,
     * it refers to the dominate language of the book. This will be used to present
     * Arabic and Hebrew in their propper orientation.
     * @return true if the orientation for the dominate language is LeftToRight.
     */
    boolean isLeftToRight();

    /**
     * Return whether the feature is supported by the book.
     */
    boolean hasFeature(FeatureType feature);

    /**
     * Get a list of all the properties available to do with this Book.
     * The returned Properties will be read-only so any attempts to alter it
     * will fail.
     */
    Map getProperties();

    /**
     * Has anyone generated a search index for this Book?
     * @see org.crosswire.jsword.book.index.IndexManager
     */
    IndexStatus getIndexStatus();

    /**
     * This method does not alter the index status, however it is for Indexers
     * that are responsible for indexing and have changed the status themselves.
     * @see org.crosswire.jsword.book.index.IndexManager
     */
    void setIndexStatus(IndexStatus status);

    /**
     * Get an OSIS representation of information concerning this Book.
     */
    Document toOSIS();

    /**
     * Adds a <code>IndexStatusListener</code> to the listener list.
     * <p>A <code>IndexStatusEvent</code> will get fired in response
     * to <code>setIndexStatus</code>.
     * @param li the <code>IndexStatusListener</code> to be added
     */
    void addIndexStatusListener(IndexStatusListener li);

    /**
     * Removes a <code>IndexStatusListener</code> from the listener list.
     * @param li the <code>IndexStatusListener</code> to be removed
     */
    void removeIndexStatusListener(IndexStatusListener li);

    /**
     * The key for the type in the properties map
     */
    String KEY_CATEGORY = "Category"; //$NON-NLS-1$

    /**
     * The key for the book in the properties map
     */
    String KEY_BOOK = "Book"; //$NON-NLS-1$

    /**
     * The key for the driver in the properties map
     */
    String KEY_DRIVER = "Driver"; //$NON-NLS-1$

    /**
     * The key for the name in the properties map
     */
    String KEY_NAME = "Description"; //$NON-NLS-1$

    /**
     * The key for the name in the properties map
     */
    String KEY_LANGUAGE = "Language"; //$NON-NLS-1$

    /**
     * The key for the initials in the properties map
     */
    String KEY_INITIALS = "Initials"; //$NON-NLS-1$

    /**
     * The key for the indexed status in the properties map
     */
    String KEY_INDEXSTATUS = "IndexStatus"; //$NON-NLS-1$
}
