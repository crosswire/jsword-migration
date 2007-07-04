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

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.biblemapper.model.Rule;
import org.crosswire.common.util.Logger;

/**
 * RuleSlider allows the user to edit the scale for a given rule.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RuleSlider extends JPanel
{
	/**
     * Basic constructor
     */
    public RuleSlider(Rule rule)
    {
        this.rule = rule;

        init();

        String fullname = rule.getClass().getName();
        int last_dot = fullname.lastIndexOf('.');
        if (last_dot == -1)
        {
            last_dot = 0;
        }

        title = fullname.substring(last_dot+1);
        bdrRule.setTitle(title);

        sdrRule.setValue(rule.getScale());
        txtRule.setText(""+rule.getScale());
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        bdrRule = BorderFactory.createTitledBorder("Rule");

        sdrRule.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev)
            {
                changed();
            }
        });
        sdrRule.setPaintLabels(true);
        sdrRule.setPaintTicks(true);
        sdrRule.setMinorTickSpacing(16);
        sdrRule.setMajorTickSpacing(32);
        sdrRule.setMaximum(256);
        sdrRule.setOrientation(SwingConstants.HORIZONTAL);
        sdrRule.setValue(0);

        txtRule.setText("256");
        txtRule.setEditable(false);

        this.setLayout(new BorderLayout());
        this.setBorder(bdrRule);
        this.add(sdrRule, BorderLayout.CENTER);
        this.add(txtRule, BorderLayout.LINE_END);
    }

    /**
     * When someone slides the slider
     */
    protected void changed()
    {
        rule.setScale(sdrRule.getValue());

        int check = rule.getScale();
        if (check != sdrRule.getValue())
        {
            sdrRule.setValue(check);
        }

        txtRule.setText(""+check);

        log.info(title+": "+check);
    }

    /**
     * The rule that we notify of any changes
     */
    private Rule rule;

    /* GUI Components */
    private JSlider sdrRule = new JSlider();
    private JTextField txtRule = new JTextField(3);
    private String title = "-";
    private TitledBorder bdrRule;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(RuleSlider.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3544953268381038132L;
}
