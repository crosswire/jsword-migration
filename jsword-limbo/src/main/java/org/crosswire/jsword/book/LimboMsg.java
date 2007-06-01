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
 * ID: $Id: Msg.java 763 2005-07-27 23:26:43Z dmsmith $
 */
package org.crosswire.jsword.book;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class LimboMsg extends MsgBase
{
    static final LimboMsg OPEN_UNKNOWN = new LimboMsg("Openness.Unknown"); //$NON-NLS-1$
    static final LimboMsg OPEN_PD = new LimboMsg("Openness.PD"); //$NON-NLS-1$
    static final LimboMsg OPEN_FREE = new LimboMsg("Openness.Free"); //$NON-NLS-1$
    static final LimboMsg OPEN_COPYABLE = new LimboMsg("Openness.Copyable"); //$NON-NLS-1$
    static final LimboMsg OPEN_COMMERCIAL = new LimboMsg("Openness.Commercial"); //$NON-NLS-1$
    static final LimboMsg STRONGS_GREEK = new LimboMsg("Strongs.Greek"); //$NON-NLS-1$
    static final LimboMsg STRONGS_HEBREW = new LimboMsg("Strongs.Hebrew"); //$NON-NLS-1$
    static final LimboMsg STRONGS_PARSING = new LimboMsg("Strongs.Parsing"); //$NON-NLS-1$

    static final LimboMsg STRONGS_ERROR_PARSE = new LimboMsg("Strongs.ErrorParse"); //$NON-NLS-1$
    static final LimboMsg STRONGS_ERROR_NUMBER = new LimboMsg("Strongs.ErrorNumber"); //$NON-NLS-1$
    static final LimboMsg STRONGS_ERROR_HEBREW = new LimboMsg("Strongs.ErrorHebrew"); //$NON-NLS-1$
    static final LimboMsg STRONGS_ERROR_GREEK = new LimboMsg("Strongs.ErrorGreek"); //$NON-NLS-1$
    static final LimboMsg STRONGS_ERROR_PARSING = new LimboMsg("Strongs.ErrorParsing"); //$NON-NLS-1$
    static final LimboMsg STRONGS_ERROR_TYPE = new LimboMsg("Strongs.ErrorType"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private LimboMsg(String name)
    {
        super(name);
    }
}
