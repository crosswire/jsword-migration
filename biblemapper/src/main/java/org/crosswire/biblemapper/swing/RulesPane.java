/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.biblemapper.swing;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.crosswire.biblemapper.model.Rule;
import org.crosswire.common.swing.GuiUtil;

/**
 * RulesPane displays an array of Rules and allows the user to select the scale
 * used for each of them. I wanted to make this a JScrollPAne, but it doesn't
 * like being of any size so I swapped back to JPanel for the time being.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RulesPane extends JPanel {
    /**
     * Basic Constructor
     */
    public RulesPane(Rule[] rules) {
        add(pnlMain);

        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));

        for (int i = 0; i < rules.length; i++) {
            pnlMain.add(new RuleSlider(rules[i]));
        }
    }

    /**
     * Method setRules.
     * 
     * @param rules
     */
    public void setRules(Rule[] rules) {
        pnlMain.removeAll();

        for (int i = 0; i < rules.length; i++) {
            pnlMain.add(new RuleSlider(rules[i]));
        }

        GuiUtil.restrainedRePack(GuiUtil.getWindow(this));
    }

    private JPanel pnlMain = new JPanel();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3906651911955626041L;
}
