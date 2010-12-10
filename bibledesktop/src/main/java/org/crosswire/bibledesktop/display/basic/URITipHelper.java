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
 * ID: $Id: URITipHelper.java 1966 2010-1-11 01:15:14Z lanyjie $
 */
package org.crosswire.bibledesktop.display.basic;

import java.awt.Dimension;
import java.awt.Insets;
import java.net.URI;
import java.util.Locale;

import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.JToolTip;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.TransformerException;

import org.crosswire.bibledesktop.book.install.BookFont;
import org.crosswire.bibledesktop.desktop.Desktop;
import org.crosswire.bibledesktop.desktop.XSLTProperty;
import org.crosswire.bibledesktop.display.URIEvent;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.common.swing.AntiAliasedTextPane;
import org.crosswire.common.swing.GuiConvert;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.Defaults;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.xml.sax.SAXException;

/**
 * Implement URIEventListener to receive URIEvents whenever someone activates an
 * URI.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Yingjie Lan [lanyjie at yahoo dot com]
 */
public class URITipHelper implements URIEventListener {
    /**
     * ctor: after creation, add this as a listener to a BookDataDisplay, for
     * example: basic/TextPaneBookDataDisplay.java
     * 
     * This class can also have a list of URIEvent content retrievers, who
     * specialize in retrieving the content of a URI request and may also
     * perform some kind of processing, such as converting to html (return a
     * string if success, null o/w):
     * 
     * Such specialized retrievers will have two methods: public boolean
     * handles(String protocol); public String retrieve(URIEvent evt);
     */
    public URITipHelper() {
        tip = new FullHTMLTip();
    }

    public JToolTip fetchToolTip() {
        return tip;
    }

    /**
     * This is only called when the component needs to display the tip; note we
     * delay this expensive operation until needed; a typical use is to have it
     * in the getToolTipText() method of the managed component:
     * 
     * public String getToolTipText(){ return uritip.retrieve(); }
     */
    public String retrieve(Converter converter) {
        if (event == null) {
            return null;
        }
        if (txt != null) {
            return txt; // return cached.
        }
        String protocol = event.getScheme();
        Book book = null;
        if (protocol.equals(Desktop.GREEK_DEF_PROTOCOL)) {
            book = Defaults.getGreekDefinitions();
        } else if (protocol.equals(Desktop.HEBREW_DEF_PROTOCOL)) {
            book = Defaults.getHebrewDefinitions();
        } else if (protocol.equals(Desktop.GREEK_MORPH_PROTOCOL)) {
            book = Defaults.getGreekParse();
        } else if (protocol.equals(Desktop.HEBREW_MORPH_PROTOCOL)) {
            book = Defaults.getHebrewParse();
        }

        if (book == null || Books.installed().getBook(book.getName()) == null) {
            return "Book Unavailable!";
        }

        BookData bdata = null;

        try {
            bdata = new BookData(book, book.getKey(event.getURI()));
        } catch (NoSuchKeyException ex) {
            return ex.getDetailedMessage();
        }

        assert book == bdata.getFirstBook();

        BookMetaData bmd = book.getBookMetaData();
        if (bmd == null) {
            return "Book Meta Data Unavailable!";
        }

        // Make sure Hebrew displays from Right to Left
        // Set the correct direction
        boolean direction = bmd.isLeftToRight();
        GuiUtil.applyOrientation(tip.getTextView(), direction);

        // The content of the module determines how the display
        // should behave. It should not be the user's locale.
        // Set the correct locale
        tip.getTextView().setLocale(new Locale(bmd.getLanguage().getCode()));
        String fontSpec = GuiConvert.font2String(BookFont.instance().getFont(book));
        try {
            SAXEventProvider osissep = bdata.getSAXEventProvider();
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            XSLTProperty.DIRECTION.setState(direction ? "ltr" : "rtl");

            URI loc = bmd.getLocation();
            XSLTProperty.BASE_URL.setState(loc == null ? "" : loc.getPath());

            if (bmd.getBookCategory() == BookCategory.BIBLE) {
                XSLTProperty.setProperties(htmlsep);
            } else {
                XSLTProperty.CSS.setProperty(htmlsep);
                XSLTProperty.BASE_URL.setProperty(htmlsep);
                XSLTProperty.DIRECTION.setProperty(htmlsep);
            }
            // Override the default if needed
            htmlsep.setParameter(XSLTProperty.FONT.getName(), fontSpec);

            txt = XMLUtil.writeToString(htmlsep);
        } catch (SAXException e) {
            Reporter.informUser(this, e);
            e.printStackTrace();
            txt = e.getMessage();
        } catch (TransformerException e) {
            Reporter.informUser(this, e);
            e.printStackTrace();
            txt = e.getMessage();
        } catch (BookException e) {
            Reporter.informUser(this, e);
            e.printStackTrace();
            txt = e.getMessage();
        }
        return txt;
    }

    public String getTipTitle() {
        if (event == null) {
            return "Untitled Tip";
        }
        return event.getURI();
    }

    /**
     * This method is called to indicate that an URI can be processed.
     * 
     * @param ev Describes the URI
     */
    public void activateURI(URIEvent ev) {
        // if(!interested(ev)) return;
    }

    /**
     * This method is called to indicate that the mouse has entered the URI.
     * 
     * @param ev
     *            Describes the URI
     */
    public void enterURI(URIEvent ev) {
        if (!interested(ev)) {
            return;
        }
        // Get current delay
        formerDismissDelay = ToolTipManager.sharedInstance().getDismissDelay();
        // Set delay longer enough
        ToolTipManager.sharedInstance().setDismissDelay(myDismissDelay);

        event = ev; // register event
    }

    /**
     * This method is called to indicate that the mouse has left the URI.
     * 
     * @param ev
     *            Describes the URI
     */
    public void leaveURI(URIEvent ev) {

        if (!interested(ev)) {
            return;
        }

        ToolTipManager.sharedInstance().setDismissDelay(formerDismissDelay);

        event = null; // clear event
        txt = null; // clear txt
    }

    /**
     * @param ev
     *            if we are interested in this event
     */
    boolean interested(URIEvent ev) {
        // tell if it is interested in ev
        String protocol = ev.getScheme();
        if (protocol.equals(Desktop.GREEK_DEF_PROTOCOL)) {
            return true;
        }
        if (protocol.equals(Desktop.HEBREW_DEF_PROTOCOL)) {
            return true;
        }
        if (protocol.equals(Desktop.GREEK_MORPH_PROTOCOL)) {
            return true;
        }
        if (protocol.equals(Desktop.HEBREW_MORPH_PROTOCOL)) {
            return true;
        }
        return false;
    }

    private int formerDismissDelay;

    private int myDismissDelay = 60000;

    /**
     * The tool tip to help with.
     */
    private FullHTMLTip tip;

    /**
     * The most recent interested event, which is used for content retrieving.
     */
    private URIEvent event;
    private String txt;
}

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

    /**
     * randomly generated sid.
     */
    private static final long serialVersionUID = 6364125062683029727L;
    private JTextPane txtView;
}
