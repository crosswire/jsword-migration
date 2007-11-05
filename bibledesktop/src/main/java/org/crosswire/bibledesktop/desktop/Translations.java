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

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.util.Languages;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.jsword.util.Project;

/**
 * Translations provides a list of languages that BibleDesktop has been translated into.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class Translations
{
    /**
     * Singleton classes have private constructors.
     */
    private Translations()
    {
        try
        {
            Properties props = ResourceUtil.getProperties(getClass());
            translation = props.getProperty(TRANSLATION_KEY, DEFAULT_TRANSLATION);
        }
        catch (IOException e)
        {
            translation = DEFAULT_TRANSLATION;
        }
    }

    /**
     * All access to Translations is through this single instance.
     * 
     * @return the singleton instance
     */
    public static Translations instance()
    {
        return instance;
    }

    /**
     * Gets a listing of all the translations that Bible Desktop supports.
     * 
     * @return an string array of translations in locale friendly names.
     */
    public Map getSupported()
    {
        Map names = new LinkedHashMap();

        for (int i = 0; i < translations.length; i++)
        {
            names.put(translations[i], Languages.getLanguage(translations[i]));
        }

        return names;
    }

    /**
     * Get the locale for the current translation.
     * @return the translation's locale
     */
    public Locale getCurrentLocale()
    {
        return new Locale(translation);
    }

    /**
     * Get the current translation as a human readable string.
     * 
     * @return the current translation
     */
    public String getCurrent()
    {
        return Languages.getLanguage(translation);
    }

    /**
     * Set the current translation, using human readable string.
     * 
     * @param translation the translation to use
     */
    public void setCurrent(String newTranslation)
    {
        String lang = DEFAULT_TRANSLATION;
        String currentLang = ""; //$NON-NLS-1$
        for (int i = 0; i < translations.length; i++)
        {
            String trans = translations[i];
            currentLang = Languages.getLanguage(translation);
            if (trans.equals(newTranslation) || currentLang.equals(newTranslation))
            {
                lang = trans;
                break;
            }
        }

        try
        {
            translation = lang;
            Properties props = new Properties();
            props.put(TRANSLATION_KEY, translation);
            URI outputURI = Project.instance().getWritablePropertiesURI(getClass().getName());
            NetUtil.storeProperties(props, outputURI, "BibleDesktop UI Translation"); //$NON-NLS-1$
        }
        catch (IOException ex)
        {
            log.error("Failed to save BibleDesktop UI Translation", ex); //$NON-NLS-1$
        }
    }

    /**
     * Set the locale for the program to the one the user has selected.
     * But don't set it to the default translation, so that the user's
     * actual locale, is used for Bible book names.
     * 
     * This only makes sense after config has called setCurrentTranslation.
     */
    public void setLocale()
    {
        if (!translation.equals(Translations.DEFAULT_TRANSLATION))
        {
            Locale.setDefault(getCurrentLocale());
        }
    }

    /**
     * Register this class with the common config engine.
     */
    public void register()
    {
        ChoiceFactory.getDataMap().put(TRANSLATION_KEY, getSupportedTranslations());
    }

    /**
     * Get the current translation as a human readable string.
     * 
     * @return the current translation
     */
    public static String getCurrentTranslation()
    {
        return Translations.instance().getCurrent();
    }

    /**
     * Set the current translation, using human readable string.
     * 
     * @param translation the translation to use
     */
    public static void setCurrentTranslation(String newTranslation)
    {
        Translations.instance().setCurrent(newTranslation);
    }

    /**
     * Gets a listing of all the translations that Bible Desktop supports.
     * 
     * @return an string array of translations in locale friendly names.
     */
    public static Map getSupportedTranslations()
    {
        return Translations.instance().getSupported();
    }

    /**
     * The key used in config.xml
     */
    private static final String TRANSLATION_KEY = "translation-codes"; //$NON-NLS-1$

    /**
     * The default translation, if the user has not chosen anything else.
     */
    public static final String DEFAULT_TRANSLATION = "en"; //$NON-NLS-1$

    /**
     * The language that BibleDesktop should use.
     */
    private String translation = DEFAULT_TRANSLATION;

    /**
     * List of available languages.
     * TODO(DM): Externalize this list.
     */
    private String[] translations =
        {
            "en", //$NON-NLS-1$
            "de", //$NON-NLS-1$
            "fa", //$NON-NLS-1$
            "tr", //$NON-NLS-1$
            "vi", //$NON-NLS-1$
        };

    private static Translations instance = new Translations();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(Translations.class);
}
