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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: BibleNameCellRenderer.java 1471 2007-07-03 21:01:02Z dmsmith $
 */
package org.crosswire.common.swing;

import java.awt.Component;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Render a Map Entry as it's value.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class MapEntryRenderer extends DefaultListCellRenderer
{
    /* (non-Javadoc)
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        Object displayObject = value;
        if (value instanceof Map.Entry)
        {
            Map.Entry mapEntry = (Map.Entry) value;
            displayObject = mapEntry.getValue();
        }

        Component comp = super.getListCellRendererComponent(list, displayObject, index, selected, focus);
        GuiUtil.applyDefaultOrientation(comp);
        return comp;
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978138859576308017L;
}
