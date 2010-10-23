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
package org.crosswire.common.config.swing;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase {
    static final Msg NAME = new Msg("StringArrayField.Name");
    static final Msg COMPONENT_EDITOR = new Msg("StringArrayField.ComponentEditor");
    static final Msg EDIT_CLASS = new Msg("StringArrayField.EditClass");
    static final Msg NEW_CLASS = new Msg("StringArrayField.NewClass");

    static final Msg ERROR = new Msg("OptionsField.Error");
    static final Msg NO_OPTIONS = new Msg("OptionsField.NoOptions");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
