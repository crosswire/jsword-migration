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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.crosswire.common.icu.NumberShaper;

/**
 * Render a list of Bible Book names with their full name as a tooltip
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class NumberCellRenderer extends DefaultListCellRenderer
{
    /**
     * Constructs a default renderer for a list of numbers.
     */
    public NumberCellRenderer()
    {
        this.shaper = new NumberShaper();
        GuiUtil.applyDefaultOrientation(this);
    }

    /* (non-Javadoc)
     * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean focus)
    {
        // Do the default rendering
        Component comp = super.getListCellRendererComponent(list, value, index, selected, focus);

        // Do our rendering
        setToolTipText(null);

        if (value == null)
        {
            setText(""); //$NON-NLS-1$
            setEnabled(false);
        }
        else
        {
            setText(shaper.shape(value.toString()));
        }

        GuiUtil.applyDefaultOrientation(comp);
        return comp;
    }

    /**
     * Used to display numbers in the user's expected representations.
     */
    private NumberShaper shaper;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978138859576308017L;
}
