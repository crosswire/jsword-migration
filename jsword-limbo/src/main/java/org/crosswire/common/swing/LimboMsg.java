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
final class LimboMsg extends MsgBase
{
    static final LimboMsg ERROR_READING = new LimboMsg("BeanPanel.ErrorReading"); //$NON-NLS-1$

    // I18N: migrate this to an ActionFactory
    static final LimboMsg CLOSE = new LimboMsg("EirPanel.Close"); //$NON-NLS-1$

    static final LimboMsg NO_PROBLEMS = new LimboMsg("ExceptionShelf.NoProblems"); //$NON-NLS-1$
    static final LimboMsg STATUS = new LimboMsg("ExceptionShelf.Status"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final LimboMsg REMOVE = new LimboMsg("ExceptionShelf.Remove"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private LimboMsg(String name)
    {
        super(name);
    }
}
