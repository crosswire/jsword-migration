package org.crosswire.jsword.util;

import java.util.Map;

import org.crosswire.common.util.LogicError;
import org.crosswire.common.xml.Converter;

/**
 * A factory for Converters.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 * @see org.crosswire.common.xml.Converter
 */
public class ConverterFactory
{
    /**
     * Prevent instantiation
     */
    private ConverterFactory()
    {
    }

    /**
     * Generate a converter for the current converter name
     */
    public static final Converter getConverter()
    {
        try
        {
            Class clazz = (Class) Project.instance().getImplementorsMap(Converter.class).get(name);
            if (clazz == null)
            {
                throw new NullPointerException("No converter called: "+name);
            }

            Converter converter = (Converter) clazz.newInstance();
            return converter;
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * Get a map of the known converters, by looking up the answers in Project
     */
    public static final Map getKnownConverters()
    {
        return Project.instance().getImplementorsMap(Converter.class);
    }

    /**
     * For config to set the currently preferred converter implementation
     */
    public static final void setCurrentConverterName(String name)
    {
        ConverterFactory.name = name;
    }

    /**
     * For config to read the currently preferred converter implementation
     */
    public static final String getCurrentConverterName()
    {
        return name;
    }

    /**
     * Current default converter implentation
     */
    private static String name = "Configurable";
}