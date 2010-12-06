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

import org.crosswire.common.util.OSType;

/**
 * CWLabel is a utility class to create JLabels from text with an optional
 * mnemonic indicator. A preceding '_' indicates a mnemonic.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CWLabel {

    /**
     * Utility class. Prevent instantiation.
     */
    private CWLabel() {
    }

    /**
     * Construct a JLabel from text. A preceding '_' indicates a mnemonic.
     * Mnemonics are ignored on MacOS X.
     * 
     * @param text
     *            the text of the label, with an optional mnemonic indicator
     * @return a JLabel
     */
     public static JLabel createJLabel(String text) {
        String label = text;

        // A Mnemonic can be specified by a preceding _ in the name
        char mnemonic = '\0';
        int pos = label.indexOf('_');
        int len = label.length();
        if (pos == len - 1) {
            // There is nothing following the _. Just remove it.
            label = label.substring(0, len - 1);
        } else if (pos >= 0 && pos < len - 1) {
            // Remove the _
            StringBuilder buffer = new StringBuilder(label.length() - 1);
            if (pos > 0) {
                buffer.append(label.substring(0, pos));
            }
            buffer.append(label.substring(pos + 1));

            label = buffer.toString();

            // the mnemonic is now at the position that the _ was.
            mnemonic = label.charAt(pos);
        }

        if (label.length() == 0) {
            label = "?";
        }

        JLabel theLabel = new JLabel();
        theLabel.setText(label);

        // Mac's don't have mnemonics
        if (mnemonic != '\0' && !OSType.MAC.equals(OSType.getOSType())) {
            theLabel.setDisplayedMnemonic(mnemonic);
        }

        return theLabel;
    }
}
