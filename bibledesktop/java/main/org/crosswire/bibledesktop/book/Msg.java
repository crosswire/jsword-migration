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
package org.crosswire.bibledesktop.book;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class Msg extends MsgBase
{
    static final Msg EMPTY_FILE = new Msg("BibleViewPane.EmptyFile"); //$NON-NLS-1$
    static final Msg VERSE_LIST_DESC = new Msg("BibleViewPane.VerseListDesc"); //$NON-NLS-1$
    static final Msg BOOKS = new Msg("BibleViewPane.Books"); //$NON-NLS-1$
    static final Msg SELECT_BOOK = new Msg("BibleViewPane.SelectBook"); //$NON-NLS-1$
    static final Msg SELECT_CHAPTER = new Msg("BibleViewPane.SelectChapter"); //$NON-NLS-1$
    static final Msg SELECT_VERSE = new Msg("BibleViewPane.SelectVerse"); //$NON-NLS-1$
    static final Msg NONE = new Msg("BibleViewPane.None"); //$NON-NLS-1$

    // I18N: migrate this to an ActionFactory
    static final Msg CHOOSER_CANCEL = new Msg("BookChooser.Cancel"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final Msg CHOOSER_OK = new Msg("BookChooser.OK"); //$NON-NLS-1$
    static final Msg CHOOSER_TITLE = new Msg("BookChooser.Title"); //$NON-NLS-1$

    static final Msg BAD_VERSE = new Msg("DisplaySelectPane.BadVerse"); //$NON-NLS-1$
    static final Msg NO_INSTALLED_BIBLE = new Msg("DisplaySelectPane.NoInstalledBible"); //$NON-NLS-1$
    static final Msg SELECT_PASSAGE_TITLE = new Msg("DisplaySelectPane.SelectPassageTitle"); //$NON-NLS-1$
    static final Msg CLEAR = new Msg("DisplaySelectPane.Clear"); //$NON-NLS-1$
    static final Msg UNTITLED = new Msg("DisplaySelectPane.Untitled"); //$NON-NLS-1$
    static final Msg NO_HITS = new Msg("DisplaySelectPane.NoHits"); //$NON-NLS-1$
    static final Msg HITS = new Msg("DisplaySelectPane.Hits"); //$NON-NLS-1$
    static final Msg PARTIAL_HITS = new Msg("DisplaySelectPane.PartialHits"); //$NON-NLS-1$
    static final Msg ADVANCED_TITLE = new Msg("DisplaySelectPane.AdvancedTitle"); //$NON-NLS-1$
    static final Msg HELP_TEXT = new Msg("DisplaySelectPane.HelpText"); //$NON-NLS-1$
    static final Msg HELP_TITLE = new Msg("DisplaySelectPane.HelpTitle"); //$NON-NLS-1$

    static final Msg ERROR = new Msg("PassageSelectionPane.Error"); //$NON-NLS-1$
    static final Msg SUMMARY = new Msg("PassageSelectionPane.Summary"); //$NON-NLS-1$

    static final Msg PRESETS = new Msg("AdvancedSearchPane.Presets"); //$NON-NLS-1$
    static final Msg ADVANCED_SELECT_TITLE = new Msg("AdvancedSearchPane.SelectPassageTitle"); //$NON-NLS-1$
    static final Msg RANK = new Msg("AdvancedSearchPane.Rank"); //$NON-NLS-1$
    static final Msg RANK_ONE = new Msg("AdvancedSearchPane.RankOne"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
