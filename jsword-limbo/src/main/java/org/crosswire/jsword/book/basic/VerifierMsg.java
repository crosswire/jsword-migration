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
 * ID: $Id: Msg.java 763 2005-07-27 19:26:43 -0400 (Wed, 27 Jul 2005) dmsmith $
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
public final class VerifierMsg extends MsgBase
{
    static final VerifierMsg VERIFY_START = new VerifierMsg("Verifier.Start"); //$NON-NLS-1$
    static final VerifierMsg VERIFY_VERSES = new VerifierMsg("Verifier.Verses"); //$NON-NLS-1$
    static final VerifierMsg VERIFY_VERSE = new VerifierMsg("Verifier.Verse"); //$NON-NLS-1$
    static final VerifierMsg VERIFY_PASSAGES = new VerifierMsg("Verifier.Passages"); //$NON-NLS-1$
    static final VerifierMsg VERIFY_WORD = new VerifierMsg("Verifier.Word"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private VerifierMsg(String name)
    {
        super(name);
    }
}
