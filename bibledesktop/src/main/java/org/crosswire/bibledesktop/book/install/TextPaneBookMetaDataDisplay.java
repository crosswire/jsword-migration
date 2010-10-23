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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.bibledesktop.book.install;

import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.TransformerException;

import org.crosswire.bibledesktop.desktop.XSLTProperty;
import org.crosswire.common.swing.AntiAliasedTextPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.JDOMSAXEventProvider;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.SAXException;

/**
 * A JDK JTextPane implementation of an OSIS displayer.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class TextPaneBookMetaDataDisplay {
    /**
     * Simple ctor
     */
    public TextPaneBookMetaDataDisplay() {
        converter = ConverterFactory.getConverter();
        txtView = new AntiAliasedTextPane();
        txtView.setEditable(false);
        txtView.setEditorKit(new HTMLEditorKit());
    }

    /**
     * Change the book being displayed to a new one.
     * 
     * @param book
     */
    public void setBook(Book book) {
        if (book == null) {
            txtView.setText("");
            return;
        }

        try {

            SAXEventProvider osissep = new JDOMSAXEventProvider(book.toOSIS());
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            XSLTProperty.FONT.setProperty(htmlsep);
            String text = XMLUtil.writeToString(htmlsep);

            txtView.setText(text);
            txtView.select(0, 0);
        } catch (SAXException e) {
            Reporter.informUser(this, e);
        } catch (TransformerException e) {
            Reporter.informUser(this, e);
        }
    }

    /**
     * Accessor for the Swing component
     */
    public Component getComponent() {
        return txtView;
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(TextPaneBookMetaDataDisplay.class);

    /**
     * To convert OSIS to HTML
     */
    private Converter converter;

    /**
     * The display component
     */
    private JTextPane txtView;

}
