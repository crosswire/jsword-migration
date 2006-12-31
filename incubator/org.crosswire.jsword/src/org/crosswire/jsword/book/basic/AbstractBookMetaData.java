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
 * ID: $Id: AbstractBookMetaData.java 1183 2006-11-08 19:04:32Z dmsmith $
 */
package org.crosswire.jsword.book.basic;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.FeatureType;
import org.crosswire.jsword.index.IndexStatus;
import org.jdom.Document;

/**
 * An implementaion of the Propery Change methods from BookMetaData.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public abstract class AbstractBookMetaData implements BookMetaData
{
    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriver()
     */
    public BookDriver getDriver()
    {
        return driver;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getDriverName()
     */
    public String getDriverName()
    {
        if (getDriver() == null)
        {
            return null;
        }

        return getDriver().getDriverName();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#hasFeature(org.crosswire.jsword.book.FeatureType)
     */
    public boolean hasFeature(FeatureType feature)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getOsisID()
    {
        return getBookCategory().toString() + '.' + getInitials();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getFullName()
     */
    public String getFullName()
    {
        if (fullName == null)
        {
            fullName = computeFullName();
        }
        return fullName;
    }

    /**
     * 
     */
    private String computeFullName()
    {
        StringBuffer buf = new StringBuffer(getName());

        if (getDriver() != null)
        {
            buf.append(" (").append(getDriverName()).append(')'); //$NON-NLS-1$
        }

        return buf.toString();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isSupported()
     */
    public boolean isSupported()
    {
        return true;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isEnciphered()
     */
    public boolean isEnciphered()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isLocked()
     */
    public boolean isLocked()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#unlock(String)
     */
    public boolean unlock(String unlockKey)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getUnlockKey()
     */
    public String getUnlockKey()
    {
        return null;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#isQuestionable()
     */
    public boolean isQuestionable()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLanguage()
     */
    public String getLanguage()
    {
        return getProperty(KEY_LANGUAGE);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLibrary()
     */
    public URL getLibrary()
    {
        URL url = null;
        try
        {
            String loc = getProperty(KEY_LIBRARY_URL);
            if (loc != null)
            {
                url = new URL(loc);
            }
            return url;
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setLibrary(java.net.URL)
     */
    public void setLibrary(URL library)
    {
        putProperty(KEY_LIBRARY_URL, library.toExternalForm());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setLocation(java.net.URL)
     */
    public void setLocation(URL location)
    {
        putProperty(KEY_LOCATION_URL, location.toExternalForm());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getLocation()
     */
    public URL getLocation()
    {
        URL url = null;
        try
        {
            String loc = getProperty(KEY_LOCATION_URL);
            if (loc != null)
            {
                url = new URL(loc);
            }
            return url;
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperties()
     */
    public Map getProperties()
    {
        return Collections.unmodifiableMap(prop);
    }

    /**
     * @param newProperties
     */
    public void setProperties(Map newProperties)
    {
        prop = newProperties;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getProperty(java.lang.String)
     */
    public String getProperty(String key)
    {
        return (String) prop.get(key);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#putProperty(java.lang.String, java.lang.String)
     */
    public void putProperty(String key, String value)
    {
        prop.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#getIndexStatus()
     */
    public IndexStatus getIndexStatus()
    {
        return indexStatus;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#setIndexStatus(java.lang.String)
     */
    public void setIndexStatus(IndexStatus newValue)
    {
        indexStatus = newValue;
        prop.put(KEY_INDEXSTATUS, newValue.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookMetaData#toOSIS()
     */
    public Document toOSIS()
    {
        throw new UnsupportedOperationException("If you want to use this, implement it."); //$NON-NLS-1$
    }

    /**
     * @param driver The driver to set.
     */
    public void setDriver(BookDriver driver)
    {
        this.driver = driver;
    }

    /**
     * Get the language name from the language code. Note, this code does not support dialects.
     * @param iso639Code
     * @return the name of the language
     */
    public static String getLanguage(String ident, String iso639Code)
    {
        String lookup = iso639Code;
        if (lookup == null || lookup.length() == 0)
        {
            return getLanguage(ident, DEFAULT_LANG_CODE);
        }

        if (lookup.indexOf('_') != -1)
        {
            String[] locale = StringUtil.split(lookup, '_');
            return getLanguage(ident, locale[0]);
        }

        // If the language begins w/ an x then it is "Undetermined"
        // Also if it is not a 2 or 3 character code then it is not a valid
        // iso639 code.
        if (lookup.startsWith("x-") || lookup.startsWith("X-") || lookup.length() > 3) //$NON-NLS-1$ //$NON-NLS-2$
        {
            return getLanguage(ident, UNKNOWN_LANG_CODE);
        }

        try
        {
            return languages.getString(lookup);
        }
        catch (MissingResourceException e)
        {
            log.error("Not a valid language code:" + iso639Code + " in book " + ident); //$NON-NLS-1$ //$NON-NLS-2$
            return getLanguage(ident, UNKNOWN_LANG_CODE);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        // Since this can not be null
        if (obj == null)
        {
            return false;
        }

        // We might consider checking for equality against all BookMetaDatas?
        // However currently we dont.

        // Check that that is the same as this
        // Don't use instanceof since that breaks inheritance
        if (!obj.getClass().equals(this.getClass()))
        {
            return false;
        }

        // The real bit ...
        BookMetaData that = (BookMetaData) obj;

        return getBookCategory().equals(that.getBookCategory()) && getName().equals(that.getName());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
    {
        int result = this.getBookCategory().compareTo(((BookMetaData) obj).getBookCategory());
        if (result == 0)
        {
            result = this.getInitials().compareTo(((BookMetaData) obj).getInitials());
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        if (displayName == null)
        {
            StringBuffer buf = new StringBuffer("["); //$NON-NLS-1$
            buf.append(getInitials());
            buf.append("] - "); //$NON-NLS-1$
            buf.append(getFullName());
            displayName = buf.toString();
        }
        return displayName;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(AbstractBookMetaData.class);

    public static final String DEFAULT_LANG_CODE = "en"; //$NON-NLS-1$
    private static final String UNKNOWN_LANG_CODE = "und"; //$NON-NLS-1$

    private static/*final*/ResourceBundle languages;
    static
    {
        try
        {
            languages = ResourceBundle.getBundle("iso639", Locale.getDefault(), CWClassLoader.instance()); //$NON-NLS-1$;
        }
        catch (MissingResourceException e)
        {
            assert false;
        }
    }

    /**
     * The single key version of the properties
     */
    private Map prop = new LinkedHashMap();

    private BookDriver driver;
    private String fullName;
    private String displayName;
    private IndexStatus indexStatus = IndexStatus.UNDONE;
}