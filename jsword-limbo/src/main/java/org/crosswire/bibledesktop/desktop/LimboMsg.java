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
 * ID: $Id: Msg.java 1225 2006-12-20 21:18:31Z dmsmith $
 */
package org.crosswire.bibledesktop.desktop;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class LimboMsg extends MsgBase
{
    // Strings for ComparePane
    static final LimboMsg COMPARE_DIALOG = new LimboMsg("ComparePane.Dialog"); //$NON-NLS-1$
    static final LimboMsg COMPARE_IDENT_QUESTION = new LimboMsg("ComparePane.IdentQuestion"); //$NON-NLS-1$
    static final LimboMsg COMPARE_IDENT_TITLE = new LimboMsg("ComparePane.IdentTitle"); //$NON-NLS-1$
    static final LimboMsg COMPARE_WORDS = new LimboMsg("ComparePane.Words"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final LimboMsg COMPARE_GO = new LimboMsg("ComparePane.Go"); //$NON-NLS-1$
    static final LimboMsg COMPARE_USING = new LimboMsg("ComparePane.Using"); //$NON-NLS-1$
    static final LimboMsg COMPARE_WORDS_TIP = new LimboMsg("ComparePane.WordsTip"); //$NON-NLS-1$
    static final LimboMsg COMPARE_TITLE = new LimboMsg("ComparePane.Title"); //$NON-NLS-1$
    static final LimboMsg COMPARE_VERSES = new LimboMsg("ComparePane.Verses"); //$NON-NLS-1$

    // Strings for CompareResultsPane
    static final LimboMsg RESULTS_TITLE = new LimboMsg("CompareResultsPane.Title"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final LimboMsg RESULTS_START = new LimboMsg("CompareResultsPane.Start"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final LimboMsg RESULTS_CLOSE = new LimboMsg("CompareResultsPane.Close"); //$NON-NLS-1$
    static final LimboMsg RESULTS_DIALOG = new LimboMsg("CompareResultsPane.Dialog"); //$NON-NLS-1$
    static final LimboMsg RESULTS_BOOKS = new LimboMsg("CompareResultsPane.Books"); //$NON-NLS-1$
    static final LimboMsg RESULTS_COMPARING = new LimboMsg("CompareResultsPane.Comparing"); //$NON-NLS-1$
    static final LimboMsg RESULTS_PASSAGE = new LimboMsg("CompareResultsPane.Passage"); //$NON-NLS-1$
    static final LimboMsg RESULTS_WORDS = new LimboMsg("CompareResultsPane.Words"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final LimboMsg RESULTS_STOP = new LimboMsg("CompareResultsPane.ResultsStop"); //$NON-NLS-1$

    // Strings for DebugPane
    static final LimboMsg DEBUG_STEPS = new LimboMsg("DebugPane.Steps"); //$NON-NLS-1$
    static final LimboMsg DEBUG_VIEWS = new LimboMsg("DebugPane.Views"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final LimboMsg DEBUG_GO = new LimboMsg("DebugPane.Go"); //$NON-NLS-1$
    static final LimboMsg DEBUG_METHOD = new LimboMsg("DebugPane.Method"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private LimboMsg(String name)
    {
        super(name);
    }

}
