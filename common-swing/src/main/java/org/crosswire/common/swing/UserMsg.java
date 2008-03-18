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
 * ID: $Id: Msg.java 1464 2007-07-02 02:34:40Z dmsmith $
 */
package org.crosswire.common.swing;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class UserMsg extends MsgBase
{
    static final UserMsg ERROR_OCCURED = new UserMsg("ExceptionPane.ErrorOccurred"); //$NON-NLS-1$
    static final UserMsg DETAILS = new UserMsg("ExceptionPane.Details"); //$NON-NLS-1$
    static final UserMsg NO_FILE = new UserMsg("ExceptionPane.NoFile"); //$NON-NLS-1$
    static final UserMsg ERROR = new UserMsg("ExceptionPane.Error"); //$NON-NLS-1$
    static final UserMsg CAUSED_BY = new UserMsg("ExceptionPane.CausedBy"); //$NON-NLS-1$
    static final UserMsg NO_DESC = new UserMsg("ExceptionPane.NoDesc"); //$NON-NLS-1$
    static final UserMsg SOURCE_NOT_FOUND = new UserMsg("ExceptionPane.SourceNotFound"); //$NON-NLS-1$
    static final UserMsg SOURCE_FOUND = new UserMsg("ExceptionPane.SourceFound"); //$NON-NLS-1$
    static final UserMsg SOURCE_ATTEMPT = new UserMsg("ExceptionPane.SourceAttempt"); //$NON-NLS-1$

    static final UserMsg SELECT_FONT = new UserMsg("FontChooser.SelectFont"); //$NON-NLS-1$
    static final UserMsg BOLD = new UserMsg("FontChooser.Bold"); //$NON-NLS-1$
    static final UserMsg ITALIC = new UserMsg("FontChooser.Italic"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private UserMsg(String name)
    {
        super(name);
    }
}
