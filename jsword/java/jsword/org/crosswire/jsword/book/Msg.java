package org.crosswire.jsword.book;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
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
 */
class Msg extends MsgBase
{
    static final Msg STRONGS_GREEK = new Msg("Greek:"); //$NON-NLS-1$
    static final Msg STRONGS_HEBREW = new Msg("Hebrew:"); //$NON-NLS-1$
    static final Msg STRONGS_PARSING = new Msg("Parsing:"); //$NON-NLS-1$

    static final Msg STRONGS_ERROR_PARSE = new Msg("Strongs number must be of the form <n>, <0n> or (n) where n is a number. Given \'{0}\'"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_NUMBER = new Msg("Could not get a number from \'{0}\'"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_HEBREW = new Msg("Hebrew numbers must be between 0 and {0,number,integer}. Given {1,number,integer}"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_GREEK = new Msg("Greek numbers must be between 0 and {0,number,integer}. Given {1,number,integer}"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_PARSING = new Msg("Parsing numbers must be greater than 0. Given {0,number,integer}"); //$NON-NLS-1$
    static final Msg STRONGS_ERROR_TYPE = new Msg("Strongs numbers must have a type in the range, 0-2. Given {0,number,integer}"); //$NON-NLS-1$

    static final Msg NO_COMMENTARIES = new Msg("No Commentaries found"); //$NON-NLS-1$
    static final Msg NO_DICTIONARIES = new Msg("No Dictionaries found"); //$NON-NLS-1$
    static final Msg NO_BIBLES = new Msg("No Bibles found"); //$NON-NLS-1$

    static final Msg BOOK_NOREMOVE = new Msg("Could not remove unregistered Book"); //$NON-NLS-1$
    static final Msg DUPLICATE_DRIVER = new Msg("Driver already registered"); //$NON-NLS-1$
    static final Msg DRIVER_NOREMOVE = new Msg("Could not remove unregistered Driver"); //$NON-NLS-1$

    static final Msg BIBLE_NOTFOUND = new Msg("Bible called \"{0}\" could not be found."); //$NON-NLS-1$
    static final Msg DICTIONRY_NOTFOUND = new Msg("Dictionary called \"{0}\" could not be found."); //$NON-NLS-1$
    static final Msg COMMENTARY_NOTFOUND = new Msg("Commentary called \"{0}\" could not be found."); //$NON-NLS-1$

    static final Msg MISSING_VERSE = new Msg("Verse element could not be found"); //$NON-NLS-1$
    static final Msg OSIS_BADID = new Msg("OsisID not valid: {0}"); //$NON-NLS-1$

    static final Msg OPEN_UNKNOWN = new Msg("Unknown"); //$NON-NLS-1$
    static final Msg OPEN_PD = new Msg("Public Domain"); //$NON-NLS-1$
    static final Msg OPEN_FREE = new Msg("Free"); //$NON-NLS-1$
    static final Msg OPEN_COPYABLE = new Msg("Copyable"); //$NON-NLS-1$
    static final Msg OPEN_COMMERCIAL = new Msg("Commercial"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
