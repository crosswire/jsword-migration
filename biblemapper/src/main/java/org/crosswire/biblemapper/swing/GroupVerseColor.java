/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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
package org.crosswire.biblemapper.swing;

import java.awt.Color;

import org.crosswire.jsword.passage.BibleInfo;

/**
 * GroupVerseColor gives colors to groups of books, so the pentetuch is
 * red, and so on.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class GroupVerseColor implements VerseColor
{
    /**
    * What Color should we use to represent this verse
    * @param book The book number (Gen=1, Rev=66)
    * @param chapter The chapter number
    * @param verse The verse number
    * @return The Color for this verse
    */
    public Color getColor(int book, int chapter, int verse)
    {
        return array[BibleInfo.getSection(book)-1];
    }

    /**
    * What Color would set off the Verses painted on it
    * @return An appropriate background color
    */
    public Color getBackground()
    {
        return Color.black;
    }

    /**
    * What Color should text be painted in
    * @return An appropriate font color
    */
    public Color getForeground()
    {
        return Color.white;
    }

    /**
    * The name for display in a combo box
    */
    @Override
    public String toString()
    {
        return "Groups";
    }

    /**
    * The colors of the different sections of the Bible. Watch out for
    * dependancies in BibleInfo with this array indexing.
    */
    private Color[] array =
    {
        Color.green,    // Pentateuch
        Color.cyan,     // History
        Color.white,    // Poetry
        Color.magenta,  // MajorProphets
        Color.pink,     // MinorProphets
        Color.red,      // GospelsAndActs
        Color.orange,   // Letters
        Color.yellow,   // Revelation
    };
}
