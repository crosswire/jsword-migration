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
 * ID: $Id$
 */
package org.crosswire.bibledesktop.display.basic;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.text.html.HTMLEditorKit;

import org.crosswire.common.swing.AntiAliasedTextPane;

/**
 * A specialization of a JToolTip
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Yingjie Lan [lanyjie at yahoo dot com]
 */
class FullHTMLTip extends JToolTip {
    public FullHTMLTip() {
        this.setLayout(new java.awt.CardLayout());
        txtView = new AntiAliasedTextPane();
        txtView.setEditable(false);
        txtView.setEditorKit(new HTMLEditorKit());
        this.add(txtView, "HTMLTip");
    }

    @Override
    public Dimension getPreferredSize() {
        Insets ist = getBorder().getBorderInsets(txtView);
        Dimension d = txtView.getPreferredSize();
        d.width += ist.left + ist.right;
        d.height += ist.top + ist.bottom;
        return d;
    }

    @Override
    public void setTipText(String tipText) {
        txtView.setText(tipText);
    }

    /**
     * @return the text view
     */
    public JTextPane getTextView() {
        return txtView;
    }

    private JTextPane txtView;

    /**
     * randomly generated sid.
     */
    private static final long serialVersionUID = 6364125062683029727L;
}
