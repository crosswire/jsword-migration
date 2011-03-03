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
package org.crosswire.bibledesktop.display.basic;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.JTextPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.transform.TransformerException;

import org.crosswire.bibledesktop.BibleDesktopMsg;
import org.crosswire.bibledesktop.book.install.BookFont;
import org.crosswire.bibledesktop.desktop.XSLTProperty;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URIEvent;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.common.swing.AntiAliasedTextPane;
import org.crosswire.common.swing.GuiConvert;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Logger;
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
import org.crosswire.jsword.passage.Key;
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
public class TextPaneBookDataDisplay implements BookDataDisplay, HyperlinkListener {



    /**
     * Simple ctor
     */
    public TextPaneBookDataDisplay() {
        converter = ConverterFactory.getConverter();
        txtView = new AntiAliasedTextPane();
        txtView.setEditable(false);
        txtView.setEditorKit(new LazyHTMLEditorKit());
        txtView.addHyperlinkListener(this);
        style = txtView.addStyle(HYPERLINK_STYLE, null);
        styledDoc = txtView.getStyledDocument();
        lastStart = -1;
        lastLength = -1;

        this.addURIEventListener(
                new ActiveURITip(txtView,
                      new Dimension(400, 300)));
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#clearBookData()
     */
    public void clearBookData() {
        setBookData(null, null);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book[], org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book[] books, Key key) {
        if (books == null || books.length == 0 || books[0] == null || key == null) {
            bdata = null;
        } else if (bdata == null || !Arrays.equals(books, bdata.getBooks()) || !key.equals(bdata.getKey())) {
            bdata = new BookData(books, key, compareBooks);
        }

        refresh();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setCompareBooks(boolean)
     */
    public void setCompareBooks(boolean compare) {
        compareBooks = compare;
        if (bdata != null) {
            bdata = new BookData(bdata.getBooks(), bdata.getKey(), compareBooks);
            refresh();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh() {
        if (bdata == null) {
            txtView.setText("");
            return;
        }

        // Make sure Hebrew displays from Right to Left
        BookMetaData bmd = getFirstBook().getBookMetaData();
        if (bmd == null) {
            txtView.setText("");
            return;
        }

        // The content of the module determines how the display
        // should behave. It should not be the user's locale.
        // Set the correct direction
        boolean direction = bmd.isLeftToRight();
        GuiUtil.applyOrientation(txtView, direction);
        // Set the correct locale
        txtView.setLocale(new Locale(bmd.getLanguage().getCode()));

        String fontSpec = GuiConvert.font2String(BookFont.instance().getFont(getFirstBook()));
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

            String text = XMLUtil.writeToString(htmlsep);
            txtView.setText(text);
            txtView.select(0, 0);
        } catch (SAXException e) {
            Reporter.informUser(this, e);
        } catch (BookException e) {
            Reporter.informUser(this, e);
        } catch (TransformerException e) {
            Reporter.informUser(this, e);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent ev) {
        // SPEEDUP(DMS): This needs to be optimized. It takes too much CPU
        try {
            HyperlinkEvent.EventType type = ev.getEventType();
            JTextPane pane = (JTextPane) ev.getSource();

            String uri = ev.getDescription();
            String[] parts = getParts(uri);
            if (type == HyperlinkEvent.EventType.ACTIVATED) {
                // There are some errors which make an empty url
                if (parts[1].length() > 0) {
                    if (parts[1].charAt(0) == '#') {
                        log.debug(MessageFormat.format(SCROLL_TO_URI, new Object[] {
                            uri
                        }));
                        // This must be relative to the current document
                        // in which case we assume that it is an in page
                        // reference.
                        // We ignore the frame case (example code within
                        // JEditorPane
                        // JavaDoc).
                        // Remove the leading #
                        uri = uri.substring(1);
                        pane.scrollToReference(uri);
                    } else {
                        // Fully formed, so we hand it off to be processed
                        fireActivateURI(new URIEvent(this, parts[0], parts[1]));
                    }
                }
            } else {
                // Must be either an enter or an exit event
                // simulate a link rollover effect, a CSS style not supported in
                // JDK 1.4

                boolean isEnter = type == HyperlinkEvent.EventType.ENTERED;

                int start = lastStart;
                int length = lastLength;
                if (isEnter) {
                    javax.swing.text.Element textElement = ev.getSourceElement();
                    start = textElement.getStartOffset();
                    length = textElement.getEndOffset() - start;
                    lastStart = start;
                    lastLength = length;
                }

                StyleConstants.setUnderline(style, isEnter);
                styledDoc.setCharacterAttributes(start, length, style, false);

                if (isEnter) {
                    fireEnterURI(new URIEvent(this, parts[0], parts[1]));
                } else {
                    fireLeaveURI(new URIEvent(this, parts[0], parts[1]));
                }
            }
        } catch (MalformedURLException ex) {
            Reporter.informUser(this, ex);
        }
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BookDataDisplay.COMPARE_BOOKS)) {
            setCompareBooks(Boolean.valueOf(evt.getNewValue().toString()).booleanValue());
        }
    }

    private String[] getParts(String reference) throws MalformedURLException {
        String protocol = RELATIVE_URI_PROTOCOL;
        String data = reference;
        int match = data.indexOf(':');
        if (match == -1) {
            // So there is no protocol, this must be relative to the current
            // in which case we assume that it is an in page reference.
            // We ignore the frame case (example code within JEditorPane
            // JavaDoc).
            if (data.charAt(0) != '#') {
                // TRANSLATOR: Unexpected error condition: the cross reference was bad.
                // {0} is a placeholder for the bad URL.
                throw new MalformedURLException(BibleDesktopMsg.gettext("Missing : in {0}", data));
            }
        } else {
            protocol = data.substring(0, match);
            data = data.substring(match + 1);
        }

        if (data.startsWith(DOUBLE_SLASH)) {
            data = data.substring(2);
        }

        return new String[] {
                protocol, data
        };
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getComponent()
     */
    public Component getComponent() {
        return txtView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#copy()
     */
    public void copy() {
        txtView.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addKeyChangeListener(org.crosswire.bibledesktop.passage.KeyChangeListener)
     */
    public synchronized void addKeyChangeListener(KeyChangeListener listener) {
        listenerList.add(KeyChangeListener.class, listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeKeyChangeListener(org.crosswire.bibledesktop.passage.KeyChangeListener)
     */
    public synchronized void removeKeyChangeListener(KeyChangeListener listener) {
        listenerList.remove(KeyChangeListener.class, listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public synchronized void addURIEventListener(URIEventListener listener) {
        listenerList.add(URIEventListener.class, listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public synchronized void removeURIEventListener(URIEventListener listener) {
        listenerList.remove(URIEventListener.class, listener);
    }

    /**
     * Notify the listeners that the hyperlink (URI) has been activated.
     * 
     * @param e
     *            the event
     * @see EventListenerList
     */
    public void fireActivateURI(URIEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == URIEventListener.class) {
                ((URIEventListener) listeners[i + 1]).activateURI(e);
            }
        }
    }

    /**
     * Notify the listeners that the hyperlink (URI) has been entered.
     * 
     * @param e
     *            the event
     * @see EventListenerList
     */
    public void fireEnterURI(URIEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == URIEventListener.class) {
                ((URIEventListener) listeners[i + 1]).enterURI(e);
            }
        }
    }

    /**
     * Notify the listeners that the hyperlink (URI) has been left.
     * 
     * @param e
     *            the event
     * @see EventListenerList
     */
    public void fireLeaveURI(URIEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == URIEventListener.class) {
                ((URIEventListener) listeners[i + 1]).leaveURI(e);
            }
        }
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li) {
        txtView.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li) {
        txtView.addMouseListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getKey()
     */
    public Key getKey() {
        return bdata == null ? null : bdata.getKey();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getBook()
     */
    public Book[] getBooks() {
        return bdata.getBooks();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getFirstBook()
     */
    public Book getFirstBook() {
        return bdata.getFirstBook();
    }

    // Strings for hyperlinks
    private static final String HYPERLINK_STYLE = "Hyperlink";
    private static final String DOUBLE_SLASH = "//";
    private static final String SCROLL_TO_URI = "scrolling to: {0}";
    private static final String RELATIVE_URI_PROTOCOL = "";

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(TextPaneBookDataDisplay.class);

    /**
     * The book data being shown.
     */
    private BookData bdata;

    /**
     * Whether the books should be compared.
     */
    private boolean compareBooks;

    /**
     * To convert OSIS to HTML
     */
    private Converter converter;

    /**
     * The display component
     */
    private JTextPane txtView;

    /**
     * A sytle used to underline a hyperlink
     */
    private Style style;

    /**
     * location of last enter event
     */
    private int lastStart;

    /**
     * length of last enter event
     */
    private int lastLength;

    /**
     * The styled document of the JTextPane.
     */
    private StyledDocument styledDoc;

    /**
     * The listeners for handling URIs
     */
    private EventListenerList listenerList = new EventListenerList();

}
