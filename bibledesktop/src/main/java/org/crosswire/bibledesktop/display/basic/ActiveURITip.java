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
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z lanyjie $
 */

package org.crosswire.bibledesktop.display.basic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.Locale;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.border.TitledBorder;
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
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.SAXException;

/**
 * How it works: 1. When mouse clicked with the right button on a link, show the
 * popup tip. 2. Hide the tip when clicked anywhere else.
 * 
 * @see gnu.gpl.License for license details. The copyright to this program is
 *      held by it's authors.
 * @author Yingjie Lan [lanyjie at yahoo dot com]
 */
public class ActiveURITip extends MouseAdapter implements URIEventListener {

    public ActiveURITip(JTextPane own, Dimension dim) {
        converter = ConverterFactory.getConverter();
        owner = own;
        lazykit = (LazyHTMLEditorKit) own.getEditorKit();
        txtView = new AntiAliasedTextPane();
        txtView.setEditable(false);
        txtView.setEditorKit(new LazyHTMLEditorKit());
        title = new TitledBorder((String) null);
        scrView = new JScrollPane(txtView);
        scrView.setBackground(Color.yellow);
        scrView.setBorder(title);
        scrView.getViewport().setPreferredSize(dim);
        // listen to mouse events of the managed component
        // own.addMouseMotionListener(this);
        own.addMouseListener(this);
        // also hide popup if clicked inside popup
        // txtView.addMouseListener(this);
    }

    public void updateText(URIEvent event) {
        if (event == null)
            return;
        String txt = null;
        String protocol = event.getScheme();
        Book book = null;
        if (protocol.equals(Desktop.GREEK_DEF_PROTOCOL)) {
            book = Defaults.getGreekDefinitions();
            title.setTitle("Greek Definition");
        } else if (protocol.equals(Desktop.HEBREW_DEF_PROTOCOL)) {
            book = Defaults.getHebrewDefinitions();
            title.setTitle("Hebrew Definition");
        } else if (protocol.equals(Desktop.GREEK_MORPH_PROTOCOL)) {
            book = Defaults.getGreekParse();
            title.setTitle("Greek Morphology");
        } else if (protocol.equals(Desktop.HEBREW_MORPH_PROTOCOL)) {
            book = Defaults.getHebrewParse();
            title.setTitle("Hebrew Morphology");
        }

        if (book == null || Books.installed().getBook(book.getName()) == null) {
            txtView.setText("Book Unavailable!");
            title.setTitle("Exception");
            return;
        }

        BookData bdata = null;

        try {
            bdata = new BookData(book, book.getKey(event.getURI()));
        } catch (NoSuchKeyException ex) {
            txtView.setText(ex.getDetailedMessage());
            title.setTitle("Exception");
            return;
        }

        assert book == bdata.getFirstBook();

        BookMetaData bmd = book.getBookMetaData();
        if (bmd == null) {
            txtView.setText("Book Meta Data Unavailable!");
            title.setTitle("Exception");
            return;
        }

        // Make sure Hebrew displays from Right to Left
        boolean direction = bmd.isLeftToRight();
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
            txtView.setText(e.getMessage());
            title.setTitle("Exception");
        } catch (TransformerException e) {
            Reporter.informUser(this, e);
            e.printStackTrace();
            txtView.setText(e.getMessage());
            title.setTitle("Exception");
        } catch (BookException e) {
            Reporter.informUser(this, e);
            e.printStackTrace();
            txtView.setText(e.getMessage());
            title.setTitle("Exception");
        }
        // Set the correct direction
        GuiUtil.applyOrientation(txtView, direction);

        // The content of the module determines how the display
        // should behave. It should not be the user's locale.
        // Set the correct locale
        txtView.setLocale(new Locale(bmd.getLanguage().getCode()));
        txtView.setText(txt);
        txtView.setCaretPosition(0);
    }

    void showTip() { // when it is time to do so
        Point p = owner.getLocationOnScreen();
        Dimension d = scrView.getPreferredSize();
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        int x = p.x + lastx;
        int y = p.y + lasty;
        int horizDist = 30; // these numbers are hard coded
        int vertiDist = 10; // but they depend on font size.
        // always show tips in the 'better half'
        if (x + x > s.width) {
            x -= horizDist + d.width;
        } else {
            x += horizDist;
        }
        if (y + y > s.height) {
            y -= vertiDist + d.height;
        } else {
            y += vertiDist;
        }
        popup = PopupFactory.getSharedInstance().getPopup(owner, scrView, x, y);
        popup.show();
    }

    void hideTip() {
        if (popup != null) {
            popup.hide();
        }
        popup = null;
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

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#activateURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void activateURI(URIEvent ev) {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#enterURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void enterURI(URIEvent ev) {
        // TODO Auto-generated method stub
        System.out.println(ev);
        if (interested(ev) && lastb == MouseEvent.BUTTON3) {
            updateText(ev);
            this.showTip();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#leaveURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void leaveURI(URIEvent ev) {
        // TODO Auto-generated method stub
        this.hideTip();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // System.out.println(e);
        // hideTip();
        lastx = e.getX();
        lasty = e.getY();
        lastb = e.getButton();
        // mouseMoved would cause URI enter/exit events
        lazykit.getLinkCtrl().mouseMoved(e);
        // mouseClicked would cause URI activated events
        // lazykit.getLinkCtrl().mouseClicked(e);
    }

    private JTextPane owner;
    private LazyHTMLEditorKit lazykit;
    private JTextPane txtView;
    private JScrollPane scrView;
    private TitledBorder title;
    private Popup popup;

    private int lastx;
    private int lasty;
    private int lastb;

    private Converter converter;
}
