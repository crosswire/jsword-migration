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
public final class LimboMsg extends MsgBase {
    static final LimboMsg CLASS = new LimboMsg("MapField.Class");
    static final LimboMsg NAME = new LimboMsg("MapField.Name");
    static final LimboMsg COMPONENT_EDITOR = new LimboMsg("MapField.ComponentEditor");
    static final LimboMsg EDIT_CLASS = new LimboMsg("MapField.EditClass");
    static final LimboMsg CLASS_NOT_FOUND = new LimboMsg("MapField.ClassNotFound.");
    static final LimboMsg BAD_SUPERCLASS = new LimboMsg("MapField.BadSuperclass");
    static final LimboMsg NEW_CLASS = new LimboMsg("MapField.NewClass");

    static final LimboMsg EDIT = new LimboMsg("ColorField.Edit");
    static final LimboMsg BASIC = new LimboMsg("TabbedConfigEditor.Basic");
    static final LimboMsg PROPERTIES_POSN = new LimboMsg("WizardConfigEditor.PropertiesPosn");

    /**
     * Passthrough ctor
     */
    private LimboMsg(String name) {
        super(name);
    }
}
