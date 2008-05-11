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
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: MappedOptionsField.java 1464 2007-07-02 02:34:40Z dmsmith $
 */
package org.crosswire.common.config.swing;

import java.awt.Dimension;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.crosswire.common.config.Choice;
import org.crosswire.common.config.MappedChoice;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.MapComboBoxModel;
import org.crosswire.common.swing.MapEntryRenderer;
import org.crosswire.common.util.Logger;

/**
 * Allow the user to choose from a combo box.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class MappedOptionsField implements Field
{
    /**
     * Create an empty MappedOptionsField
     */
    public MappedOptionsField()
    {
        combo = new JComboBox(new String[] { Msg.NO_OPTIONS.toString() });
        // Set the preferred width. Note, the actual combo box will resize to the width of it's container
        combo.setPreferredSize(new Dimension(100, combo.getPreferredSize().height));
        GuiUtil.applyDefaultOrientation(combo);
    }

    /**
     * Some fields will need some extra info to display properly
     * like the options in an options field. FieldMap calls this
     * method with options provided by the choice.
     * @param param The options provided by the Choice
     */
    public void setChoice(Choice param)
    {
        if (!(param instanceof MappedChoice))
        {
            throw new IllegalArgumentException("Illegal type for Choice. Not a MappedChoice. " + param.getKey()); //$NON-NLS-1$
        }
        MappedChoice mc = (MappedChoice) param;
        Map map = mc.getOptions();
        if (map == null)
        {
            throw new IllegalArgumentException("getOptions() returns null for option: " + param.getKey()); //$NON-NLS-1$
        }
        combo.setModel(new MapComboBoxModel(map));
        combo.setRenderer(new MapEntryRenderer());
   }

    /**
     * Return a string for use in the properties file
     * @return The current value
     */
    public String getValue()
    {
        Object reply = combo.getSelectedItem();

        if (reply instanceof Map.Entry)
        {
            return ((Map.Entry) reply).getKey().toString();
        }
        return reply == null ? "" : reply.toString(); //$NON-NLS-1$
    }

    /**
     * Set the current value
     * @param value The new text
     */
    public void setValue(String value)
    {
        ComboBoxModel model = combo.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++)
        {
            Object match = model.getElementAt(i);
            if (match instanceof Map.Entry)
            {
                Map.Entry mapEntry = (Map.Entry) match;
                if (mapEntry.getKey().toString().equals(value) || mapEntry.getValue().toString().equals(value))
                {
                    combo.setSelectedItem(mapEntry);
                    return;
                }
            }
        }

        // Equate null and empty string
        Object selected = combo.getSelectedItem();
        if (value.length() > 0 && selected != null)
        {
            log.warn("Checked for options without finding: '" + value + "'. Defaulting to first option: " + selected);  //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    /**
     * Get the actual component that we can add to a Panel.
     * (This can well be this in an implementation).
     */
    public JComponent getComponent()
    {
        return combo;
    }

    /**
     * The component that we are wrapping in a field
     */
    private JComboBox combo;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(MappedOptionsField.class);
}
