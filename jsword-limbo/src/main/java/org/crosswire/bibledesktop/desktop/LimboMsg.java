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

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class LimboMsg extends MsgBase {
    // Strings for ComparePane
    static final LimboMsg COMPARE_DIALOG = new LimboMsg("ComparePane.Dialog");
    static final LimboMsg COMPARE_IDENT_QUESTION = new LimboMsg("ComparePane.IdentQuestion");
    static final LimboMsg COMPARE_IDENT_TITLE = new LimboMsg("ComparePane.IdentTitle");
    static final LimboMsg COMPARE_WORDS = new LimboMsg("ComparePane.Words");
    // I18N: migrate this to an ActionFactory
    static final LimboMsg COMPARE_GO = new LimboMsg("ComparePane.Go");
    static final LimboMsg COMPARE_USING = new LimboMsg("ComparePane.Using");
    static final LimboMsg COMPARE_WORDS_TIP = new LimboMsg("ComparePane.WordsTip");
    static final LimboMsg COMPARE_TITLE = new LimboMsg("ComparePane.Title");
    static final LimboMsg COMPARE_VERSES = new LimboMsg("ComparePane.Verses");

    // Strings for CompareResultsPane
    static final LimboMsg RESULTS_TITLE = new LimboMsg("CompareResultsPane.Title");
    // I18N: migrate this to an ActionFactory
    static final LimboMsg RESULTS_START = new LimboMsg("CompareResultsPane.Start");
    // I18N: migrate this to an ActionFactory
    static final LimboMsg RESULTS_CLOSE = new LimboMsg("CompareResultsPane.Close");
    static final LimboMsg RESULTS_DIALOG = new LimboMsg("CompareResultsPane.Dialog");
    static final LimboMsg RESULTS_BOOKS = new LimboMsg("CompareResultsPane.Books");
    static final LimboMsg RESULTS_COMPARING = new LimboMsg("CompareResultsPane.Comparing");
    static final LimboMsg RESULTS_PASSAGE = new LimboMsg("CompareResultsPane.Passage");
    static final LimboMsg RESULTS_WORDS = new LimboMsg("CompareResultsPane.Words");
    // I18N: migrate this to an ActionFactory
    static final LimboMsg RESULTS_STOP = new LimboMsg("CompareResultsPane.ResultsStop");

    // Strings for DebugPane
    static final LimboMsg DEBUG_STEPS = new LimboMsg("DebugPane.Steps");
    static final LimboMsg DEBUG_VIEWS = new LimboMsg("DebugPane.Views");
    // I18N: migrate this to an ActionFactory
    static final LimboMsg DEBUG_GO = new LimboMsg("DebugPane.Go");
    static final LimboMsg DEBUG_METHOD = new LimboMsg("DebugPane.Method");

    /**
     * Passthrough ctor
     */
    private LimboMsg(String name) {
        super(name);
    }

}
