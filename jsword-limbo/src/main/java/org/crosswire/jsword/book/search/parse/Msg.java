/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
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
package org.crosswire.jsword.book.search.parse;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase {
    static final Msg ADD_BLANK = new Msg("AddCommandWord.AddBlank");
    static final Msg RIGHT_PARAM = new Msg("PassageRightParamWord.RightParam");
    static final Msg RIGHT_BRACKETS = new Msg("PassageRightParamWord.RightBrackets");
    static final Msg LEFT_PARAM = new Msg("PassageLeftParamWord.LeftParam");
    static final Msg LEFT_BRACKETS = new Msg("PassageLeftParamWord.LeftBrackets");
    static final Msg STARTS_WORD = new Msg("StartsParamWord.StartsWord");
    static final Msg STARTS_BLANK = new Msg("StartsParamWord.StartsBlank");
    static final Msg RETAIN_BLANK = new Msg("RetainCommandWord.RetainBlank");
    static final Msg REMOVE_BLANK = new Msg("RemoveCommandWord.RemoveBlank");
    static final Msg GRAMMAR_WORD = new Msg("GrammarParamWord.GrammarWord");
    static final Msg GRAMMAR_BLANK = new Msg("GrammarParamWord.GrammarBlank");
    static final Msg BLUR_BLANK = new Msg("BlurCommandWord.BlurBlank");
    static final Msg BLUR_FORMAT = new Msg("BlurCommandWord.BlurFormat");
    static final Msg ENGINE_SYNTAX = new Msg("IndexSearcher.EngineSyntax");
    static final Msg ILLEGAL_PASSAGE = new Msg("PassageLeftParamWord.IllegalPassage");
    static final Msg UNMATCHED_ESCAPE = new Msg("CustomTokenizer.UnmatchedEscape");
    static final Msg SINGLE_PARAM = new Msg("PhraseParamWord.SingleParam");
    static final Msg NO_THESAURUS = new Msg("PhraseParamWord.NoThesaurus");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
