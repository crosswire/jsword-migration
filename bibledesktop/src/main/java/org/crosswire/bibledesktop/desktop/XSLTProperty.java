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
import java.net.MalformedURLException;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.TransformingSAXEventProvider;

/**
 * Defines properties that control the behavior of translating OSIS to HTML.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [ dmsmith555 at yahoo dot com]
 */
public enum XSLTProperty {
    /**
     * Determines whether Strong's Numbers should show
     */
    STRONGS_NUMBERS("Strongs", false),

    /**
     * Determines whether Word Morphology (e.g. Robinson) should show
     */
   MORPH("Morph", false),

    /**
     * Determines whether verses should start on a new line.
     */
    START_VERSE_ON_NEWLINE("VLine", false),

    /**
     * Show verse numbers
     */
    VERSE_NUMBERS("VNum", true),

    /**
     * Show chapter and verse numbers.
     */
    CV("CVNum", false),

    /**
     * Show book, chapter and verse numbers.
     */
    BCV("BCVNum", false),

    /**
     * Show no verse numbers
     */
    NO_VERSE_NUMBERS("NoVNum", false),

    /**
     * Show verse numbers as a superscript.
     */
    TINY_VERSE_NUMBERS("TinyVNum", true),

    /**
     * Should headings be shown
     */
    HEADINGS("Headings", true),

    /**
     * Should notes be shown
     */
    NOTES("Notes", true),

    /**
     * Should cross references be shown
     */
    XREF("XRef", true),

    /**
     * What is the base of the current document. Note this needs to be set each
     * time the document is shown.
     */
    BASE_URL("baseURL", "", true),

    /**
     * What is the base of the current document. Note this needs to be set each
     * time the document is shown.
     */
    DIRECTION("direction", ""),

    /**
     * What is the base of the current document. Note this needs to be set each
     * time the font changes.
     */
    FONT("font", "Serif-PLAIN-14"),

    /**
     * What is the base of the current document.
     */
    CSS("css", "", true);

    /**
     * @param name
     *            The name of this property
     * @param defaultState
     *            The initial state of the property.
     */
    private XSLTProperty(String name, boolean defaultState) {
        this(name, Boolean.toString(defaultState));
    }

    /**
     * @param name
     *            The name of this property
     * @param defaultState
     *            The initial state of the property.
     */
    private XSLTProperty(String name, String defaultState) {
        this(name, defaultState, false);
    }

    /**
     * @param name
     *            The name of this property
     * @param defaultState
     *            The initial state of the property.
     */
    private XSLTProperty(String name, String defaultState, boolean asURL) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.asURL = asURL;
    }

    /**
     * @return the name of the property
     */
    public String getName() {
        return name;
    }

    public boolean getDefaultState() {
        return Boolean.valueOf(defaultState).booleanValue();
    }

    public String getDefaultStringState() {
        return defaultState;
    }

    public boolean getState() {
        return Boolean.valueOf(state).booleanValue();
    }

    public String getStringState() {
        return state;
    }

    public void setState(boolean newState) {
        state = Boolean.toString(newState);
    }

    public void setState(String newState) {
        state = newState;
    }

    public void setProperty(TransformingSAXEventProvider provider) {
        if (state != null && state.length() > 0) {
            String theState = state;
            if (asURL) {
                try {
                    theState = NetUtil.getURI(new File(state)).toURL().toString();
                } catch (MalformedURLException ex) {
                    Reporter.informUser(this, ex);
                }
            }
            provider.setParameter(name, theState);
        }
    }

    public static void setProperties(TransformingSAXEventProvider provider) {
        for (XSLTProperty v : values()) {
            v.setProperty(provider);
        }
    }

    /**
     * Lookup method to convert from a String
     */
    public static XSLTProperty fromString(String name) {
        for (XSLTProperty v : values()) {
            if (v.name().equalsIgnoreCase(name)) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /**
     * Lookup method to convert from an integer
     */
    public static XSLTProperty fromInteger(int i) {
        for (XSLTProperty v : values()) {
            if (v.ordinal() == i) {
                return v;
            }
        }

        // cannot get here
        assert false;
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
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
     * Whether the string state should be converted to an URL when setting the
     * property.
     */
    private boolean asURL;
}
