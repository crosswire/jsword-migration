/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
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
package org.crosswire.bibledesktop.desktop;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.TransformingSAXEventProvider;


/**
 * Defines properties that control the behavior of translating OSIS to HTML.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public class XSLTProperty implements Serializable
{
    /**
     * Determines whether Strong's Numbers should show
     */
    public static final XSLTProperty STRONGS_NUMBERS = new XSLTProperty("Strongs", false); //$NON-NLS-1$

    /**
     * Determines whether Word Morphology (e.g. Robinson) should show
     */
    public static final XSLTProperty MORPH = new XSLTProperty("Morph", false); //$NON-NLS-1$

    /**
     * Determines whether verses should start on a new line.
     */
    public static final XSLTProperty START_VERSE_ON_NEWLINE = new XSLTProperty("VLine", false); //$NON-NLS-1$

    /**
     * Show verse numbers
     */
    public static final XSLTProperty VERSE_NUMBERS = new XSLTProperty("VNum", true); //$NON-NLS-1$

    /**
     * Show chapter and verse numbers.
     */
    public static final XSLTProperty CV = new XSLTProperty("CVNum", false); //$NON-NLS-1$

    /**
     * Show book, chapter and verse numbers.
     */
    public static final XSLTProperty BCV = new XSLTProperty("BCVNum", false); //$NON-NLS-1$

    /**
     * Show no verse numbers
     */
    public static final XSLTProperty NO_VERSE_NUMBERS = new XSLTProperty("NoVNum", false); //$NON-NLS-1$

    /**
     * Show verse numbers as a superscript.
     */
    public static final XSLTProperty TINY_VERSE_NUMBERS = new XSLTProperty("TinyVNum", true); //$NON-NLS-1$

    /**
     * Should headings be shown
     */
    public static final XSLTProperty HEADINGS = new XSLTProperty("Headings", true); //$NON-NLS-1$

    /**
     * Should notes be shown
     */
    public static final XSLTProperty NOTES = new XSLTProperty("Notes", true); //$NON-NLS-1$

    /**
     * Should cross references be shown
     */
    public static final XSLTProperty XREF = new XSLTProperty("XRef", true); //$NON-NLS-1$

    /**
     * What is the base of the current document.
     * Note this needs to be set each time the document is shown.
     */
    public static final XSLTProperty BASE_URL = new XSLTProperty("baseURL", "", true); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * What is the base of the current document.
     *  Note this needs to be set each time the document is shown.
     */
    public static final XSLTProperty DIRECTION = new XSLTProperty("direction", ""); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * What is the base of the current document.
     * Note this needs to be set each time the font changes.
     */
    public static final XSLTProperty FONT = new XSLTProperty("font", "Serif,0,14"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * What is the base of the current document.
     */
    public static final XSLTProperty CSS = new XSLTProperty("css", "", true); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * @param name The name of this property
     * @param defaultState The initial state of the property.
     */
    private XSLTProperty(String name, boolean defaultState)
    {
        this(name, Boolean.toString(defaultState));
    }

    /**
     * @param name The name of this property
     * @param defaultState The initial state of the property.
     */
    private XSLTProperty(String name, String defaultState)
    {
        this(name, defaultState, false);
    }

    /**
     * @param name The name of this property
     * @param defaultState The initial state of the property.
     */
    private XSLTProperty(String name, String defaultState, boolean asURL)
    {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.asURL = asURL;
    }

    /**
     * @return the name of the property
     */
    public String getName()
    {
        return name;
    }

    public boolean getDefaultState()
    {
        return Boolean.valueOf(defaultState).booleanValue();
    }

    public String getDefaultStringState()
    {
        return defaultState;
    }

    public boolean getState()
    {
        return Boolean.valueOf(state).booleanValue();
    }

    public String getStringState()
    {
        return state;
    }

    public void setState(boolean newState)
    {
        state = Boolean.toString(newState);
    }

    public void setState(String newState)
    {
        state = newState;
    }

    public void setProperty(TransformingSAXEventProvider provider)
    {
        if (state != null && state.length() > 0)
        {
            String theState = state;
            if (asURL)
            {
                try
                {
                    theState = NetUtil.getURI(new File(state)).toURL().toString();
                }
                catch (MalformedURLException ex)
                {
                    Reporter.informUser(this, ex);
                }
            }
            provider.setParameter(name, theState);
        }
    }

    public static void setProperties(TransformingSAXEventProvider provider)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            VALUES[i].setProperty(provider);
        }
    }

    /**
     * Lookup method to convert from a String
     */
    public static XSLTProperty fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            XSLTProperty o = VALUES[i];
            if (o.name.equalsIgnoreCase(name))
            {
                return o;
            }
        }
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static XSLTProperty fromInteger(int i)
    {
        return VALUES[i];
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    /**
     * The name of the XSLTProperty
     */
    private String name;

    /**
     * The default state of the XSLTProperty
     */
    private String defaultState;

    /**
     * The current state of the XSLTProperty
     */
    private String state;

    /**
     * Whether the string state should be converted to an URL when setting the property.
     */
    private boolean asURL;

    // Support for serialization
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final XSLTProperty[] VALUES =
    {
        STRONGS_NUMBERS,
        MORPH,
        START_VERSE_ON_NEWLINE,
        VERSE_NUMBERS,
        CV,
        BCV,
        NO_VERSE_NUMBERS,
        TINY_VERSE_NUMBERS,
        HEADINGS,
        NOTES,
        XREF,
        BASE_URL,
        DIRECTION,
        FONT,
        CSS,
    };

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257567325749326905L;
}
