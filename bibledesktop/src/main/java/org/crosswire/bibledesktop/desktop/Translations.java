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
 * ID: $Id: ViewSourcePane.java 1312 2007-05-03 21:39:51Z dmsmith $
 */
package org.crosswire.bibledesktop.desktop;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.crosswire.common.config.ChoiceFactory;
import org.crosswire.common.util.Languages;

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
     * Utility classes have private constructors.
     */
    private Translations()
    {
    }

    public static String[] getSupportedTranslations()
    {
        List names = new ArrayList();

        for (int i = 0; i < translations.length; i++)
        {
            names.add(Languages.getLanguage(translations[i]));
        }

        return (String[]) names.toArray(new String[names.size()]);
    }

    /**
     * Get the locale for the current translation.
     * @return the translation's locale
     */
    public static Locale getCurrentLocale()
    {
        return new Locale(translation);
    }

    /**
     * Get the current translation as a human readable string.
     * 
     * @return the current translation
     */
    public static String getCurrentTranslation()
    {
        return Languages.getLanguage(translation);
    }

    /**
     * Set the current translation, using human readable string.
     * 
     * @param translation the translation to use
     */
    public static void setCurrentTranslation(String translation)
    {
        String lang = DEFAULT_TRANSLATION;
        String currentLang = ""; //$NON-NLS-1$
        for (int i = 0; i < translations.length; i++)
        {
            currentLang = Languages.getLanguage(translations[i]);
            if (currentLang.equals(translation))
            {
                lang = translations[i];
            }
        }

        Translations.translation = lang;
    }

    public static void register()
    {
        ChoiceFactory.getDataMap().put(TRANSLATION_KEY, getSupportedTranslations());
    }

    /**
     * The key used in config.xml
     */
    private static final String TRANSLATION_KEY = "translation-codes"; //$NON-NLS-1$

    /**
     * The default translation, if the user has not chosen anything else.
     */
    private static final String DEFAULT_TRANSLATION = "en"; //$NON-NLS-1$

    /**
     * The language that BibleDesktop should use.
     */
    private static String translation = DEFAULT_TRANSLATION;

    /**
     * List of available languages.
     * TODO(DMS): externalize this list.
     */
    private static String[] translations = {
            "en", //$NON-NLS-1$
            "de", //$NON-NLS-1$
            "fa", //$NON-NLS-1$
    };

}
