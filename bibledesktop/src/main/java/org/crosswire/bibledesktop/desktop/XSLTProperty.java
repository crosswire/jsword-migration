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

import org.crosswire.common.xml.TransformingSAXEventProvider;


/**
 * Defines properties that control the behavior of translating OSIS to HTML.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at hotmail dot com]
 */
public enum XSLTProperty
{

    /** Determines whether Strong's Numbers should show */
    STRONGS_NUMBERS ("Strongs", false), //$NON-NLS-1$

    /** Determines whether Word Morphology (e.g. Robinson) should show */
    MORPH ("Morph", false), //$NON-NLS-1$

    /** Determines whether verses should start on a new line. */
    START_VERSE_ON_NEWLINE ("VLine", false), //$NON-NLS-1$

    /** Show verse numbers */
    VERSE_NUMBERS ("VNum", true), //$NON-NLS-1$

    /** Show chapter and verse numbers. */
    CV ("CVNum", false), //$NON-NLS-1$

    /** Show book, chapter and verse numbers. */
    BCV ("BCVNum", false), //$NON-NLS-1$

    /** Show no verse numbers */
    NO_VERSE_NUMBERS ("NoVNum", false), //$NON-NLS-1$

    /** Show verse numbers as a superscript. */
    TINY_VERSE_NUMBERS ("TinyVNum", true), //$NON-NLS-1$

    /** Should notes be shown */
    NOTES ("Notes", true), //$NON-NLS-1$

    /** Should cross references be shown */
    XREF ("XRef", true); //$NON-NLS-1$

    /**
     * @param name The name of this property
     */
    private XSLTProperty(String name, boolean defaultState)
    {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
    }

    /**
     * @return the name of the property
     */
    public String getName()
    {
        return name;
    }

    public boolean getDefault()
    {
        return defaultState; //$NON-NLS-1$
    }

    public boolean getState()
    {
        return state;
    }

    public void setState(boolean newState)
    {
        state = newState;
    }

    public void setProperty(TransformingSAXEventProvider provider)
    {
        provider.setParameter(name, Boolean.toString(state));
    }

    public static void setProperties(TransformingSAXEventProvider provider)
    {
        for (XSLTProperty t : XSLTProperty.values())
        {
            t.setProperty(provider);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
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
    private boolean defaultState;

    /**
     * The current state of the XSLTProperty
     */
    private boolean state;

}
