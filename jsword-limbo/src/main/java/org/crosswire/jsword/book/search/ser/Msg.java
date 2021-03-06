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
package org.crosswire.jsword.book.search.ser;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase {
    static final Msg INDEX_START = new Msg("SerIndex.Indexing");
    static final Msg SER_INIT = new Msg("SerIndex.Init");
    static final Msg DELETE_FAILED = new Msg("SerIndex.DeleteFailed");
    static final Msg WRITE_ERROR = new Msg("SerIndex.WriteError");
    static final Msg FINDING_WORDS = new Msg("SerIndex.FindingWords");
    static final Msg WRITING_WORDS = new Msg("SerIndex.WritingWords");
    static final Msg SAVING = new Msg("SerIndex.Saving");
    static final Msg INSTALL_FAIL = new Msg("SerIndex.InstallFailed");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
