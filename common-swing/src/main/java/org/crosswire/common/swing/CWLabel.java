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
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.common.swing;

import javax.swing.JLabel;

import org.crosswire.common.util.Logger;

/**
 * A CWLabel consists of a label and a mnemonic constructed from a string having
 * an optional mnemonic indicator. The indicator, '_', precedes the mnemonic
 * letter.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CWLabel {

    /**
     * Construct a CWLabel from a string. The string is assumed to have at most
     * one underscore, '_', and the letter that it precedes is taken as the
     * mnemonic.
     * 
     * @param text
     */
    public CWLabel(String text) {
        label = text;

        // A Mnemonic can be specified by a preceding _ in the name
        int pos = label.indexOf('_');
        int len = label.length();
        if (pos == len - 1) {
            // There is nothing following the _. Just remove it.
            label = label.substring(0, len - 1);
        } else if (pos >= 0 && pos < len - 1) {
            // Remove the _
            StringBuffer buffer = new StringBuffer(label.length() - 1);
            if (pos > 0) {
                buffer.append(label.substring(0, pos));
            }
            buffer.append(label.substring(pos + 1));

            label = buffer.toString();

            // the mnemonic is now at the position that the _ was.
            mnemonic = new Integer(label.charAt(pos));
        }

        if (label.length() == 0) {
            log.warn("text is missing for CWLabel");
            label = "?";
        }
    }

    public JLabel createJLabel() {
        JLabel theLabel = new JLabel();
        theLabel.setText(label);

        if (mnemonic != null) {
            theLabel.setDisplayedMnemonic(mnemonic.intValue());
        }

        return theLabel;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @return the mnemonic
     */
    public Integer getMnemonic() {
        return mnemonic;
    }

    private String label;
    private Integer mnemonic;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(CWLabel.class);

}
