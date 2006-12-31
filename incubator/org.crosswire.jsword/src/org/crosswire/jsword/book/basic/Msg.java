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
package org.crosswire.jsword.book.basic;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase
{
    static final Msg DRIVER_READONLY = new Msg("AbstractBookDriver.DriverReadonly"); //$NON-NLS-1$
    static final Msg INDEX_FAIL = new Msg("AbstractBookDriver.IndexFail"); //$NON-NLS-1$
    static final Msg FILTER_FAIL = new Msg("AbstractPassageBook.FilterFail"); //$NON-NLS-1$
    static final Msg VERIFY_START = new Msg("Verifier.Start"); //$NON-NLS-1$
    static final Msg VERIFY_VERSES = new Msg("Verifier.Verses"); //$NON-NLS-1$
    static final Msg VERIFY_VERSE = new Msg("Verifier.Verse"); //$NON-NLS-1$
    static final Msg VERIFY_PASSAGES = new Msg("Verifier.Passages"); //$NON-NLS-1$
    static final Msg VERIFY_WORDS = new Msg("Verifier.Words"); //$NON-NLS-1$
    static final Msg WORD = new Msg("Verifier.Word"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}