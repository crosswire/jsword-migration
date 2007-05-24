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
package org.crosswire.bibledesktop.display.textpane;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.MessageFormat;

import javax.swing.JTextPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.transform.TransformerException;

import org.crosswire.bibledesktop.desktop.XSLTProperty;
import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URIEvent;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.common.swing.AntiAliasedTextPane;
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
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class TextPaneBookDataDisplay implements BookDataDisplay, HyperlinkListener
{
    /**
     * Simple ctor
     */
    public TextPaneBookDataDisplay()
    {
        converter = ConverterFactory.getConverter();
        txtView = new AntiAliasedTextPane();
        txtView.setEditable(false);
        txtView.setEditorKit(new HTMLEditorKit());
        txtView.addHyperlinkListener(this);
        style = txtView.addStyle(HYPERLINK_STYLE, null);
        styledDoc = txtView.getStyledDocument();
        lastStart = -1;
        lastLength = -1;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#setBookData(org.crosswire.jsword.book.Book, org.crosswire.jsword.passage.Key)
     */
    public void setBookData(Book book, Key key)
    {
        this.book = book;
        this.key = key;

        refresh();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#refresh()
     */
    public void refresh()
    {
        if (book == null || key == null)
        {
            txtView.setText(""); //$NON-NLS-1$
            return;
        }

        // Make sure Hebrew displays from Right to Left
        BookMetaData bmd = book.getBookMetaData();
        if (bmd == null)
        {
            txtView.setText(""); //$NON-NLS-1$
            return;
        }

        boolean direction = bmd.isLeftToRight();
        txtView.applyComponentOrientation(direction ? ComponentOrientation.LEFT_TO_RIGHT : ComponentOrientation.RIGHT_TO_LEFT);

        try
        {
            BookData bdata = new BookData(book, key);
            SAXEventProvider osissep = bdata.getSAXEventProvider();
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);

            XSLTProperty.DIRECTION.setState(bmd.isLeftToRight() ? "ltr" : "rtl"); //$NON-NLS-1$ //$NON-NLS-2$

            URI loc = bmd.getLocation();
            XSLTProperty.BASE_URL.setState(loc == null ? "" : loc.toString()); //$NON-NLS-1$

            if (bmd.getBookCategory() == BookCategory.BIBLE)
            {
                XSLTProperty.setProperties(htmlsep);
            }
            else
            {
                XSLTProperty.CSS.setProperty(htmlsep);
                XSLTProperty.FONT.setProperty(htmlsep);
                XSLTProperty.BASE_URL.setProperty(htmlsep);
                XSLTProperty.DIRECTION.setProperty(htmlsep);
            }

            String text = XMLUtil.writeToString(htmlsep);
            /* BUG_PARADE(DMS): 4775730
             * This bug shows up before Java 5 in GenBook Practice "/Part 1/THE THIRD STAGE" and elsewhere.
             * It appears that it is a line too long issue.
             */
            /* Apply the fix if the text is too long and we are not Java 1.5 or greater */
            if (text.length() > 32768 && BookCategory.GENERAL_BOOK.equals(book.getBookCategory()))
            {
                String javaVersion = System.getProperty("java.specification.version"); //$NON-NLS-1$
                if (javaVersion == null || "1.5".compareTo(javaVersion) > 0) //$NON-NLS-1$
                {
                    text = text.substring(0, 32760) + "..."; //$NON-NLS-1$
                }
            }
            txtView.setText(text);
            txtView.select(0, 0);
        }
        catch (SAXException e)
        {
            Reporter.informUser(this, e);
        }
        catch (BookException e)
        {
            Reporter.informUser(this, e);
        }
        catch (TransformerException e)
        {
            Reporter.informUser(this, e);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        // SPEEDUP(DMS): This needs to be optimized. It takes too much CPU
        try
        {
            HyperlinkEvent.EventType type = ev.getEventType();
            JTextPane pane = (JTextPane) ev.getSource();

            String uri = ev.getDescription();
            String[] parts = getParts(uri);
            if (type == HyperlinkEvent.EventType.ACTIVATED)
            {
                // There are some errors which make an empty url
                if (parts[1].length() > 0)
                {
                    if (parts[1].charAt(0) == '#')
                    {
                        log.debug(MessageFormat.format(SCROLL_TO_URI, new Object[] { uri }));
                        // This must be relative to the current document
                        // in which case we assume that it is an in page reference.
                        // We ignore the frame case (example code within JEditorPane
                        // JavaDoc).
                        // Remove the leading #
                        uri = uri.substring(1);
                        pane.scrollToReference(uri);
                    }
                    else
                    {
                        // Fully formed, so we hand it off to be processed
                        fireActivateURI(new URIEvent(this, parts[0], parts[1]));
                    }
                }
            }
            else
            {
                // Must be either an enter or an exit event
                // simulate a link rollover effect, a CSS style not supported in JDK 1.4

                boolean isEnter = type == HyperlinkEvent.EventType.ENTERED;

                int start = lastStart;
                int length = lastLength;
                if (isEnter)
                {
                    javax.swing.text.Element textElement = ev.getSourceElement();
                    start = textElement.getStartOffset();
                    length = textElement.getEndOffset() - start;
                    lastStart = start;
                    lastLength = length;
                }

                StyleConstants.setUnderline(style, isEnter);
                styledDoc.setCharacterAttributes(start, length, style, false);

                if (isEnter)
                {
                    fireEnterURI(new URIEvent(this, parts[0], parts[1]));
                }
                else
                {
                    fireLeaveURI(new URIEvent(this, parts[0], parts[1]));
                }
            }
        }
        catch (MalformedURLException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    private String[] getParts(String reference) throws MalformedURLException
    {
        String protocol = RELATIVE_URI_PROTOCOL;
        String data = reference;
        int match = data.indexOf(':');
        if (match == -1)
        {
            // So there is no protocol, this must be relative to the current
            // in which case we assume that it is an in page reference.
            // We ignore the frame case (example code within JEditorPane
            // JavaDoc).
            if (data.charAt(0) != '#')
            {
                throw new MalformedURLException(Msg.BAD_PROTOCOL_URL.toString(data));
            }
        }
        else
        {
            protocol = data.substring(0, match);
            data = data.substring(match + 1);
        }

        if (data.startsWith(DOUBLE_SLASH))
        {
            data = data.substring(2);
        }

        return new String[] { protocol, data };
    }
    /**
     * Accessor for the Swing component
     */
    public Component getComponent()
    {
        return txtView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#copy()
     */
    public void copy()
    {
        txtView.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#addURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public synchronized void addURIEventListener(URIEventListener listener)
    {
        listenerList.add(URIEventListener.class, listener);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#removeURIEventListener(org.crosswire.bibledesktop.display.URIEventListener)
     */
    public synchronized void removeURIEventListener(URIEventListener listener)
    {
        listenerList.remove(URIEventListener.class, listener);
    }

    /**
     * Notify the listeners that the hyperlink (URI) has been activated.
     *
     * @param e the event
     * @see EventListenerList
     */
    public void fireActivateURI(URIEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == URIEventListener.class)
            {
                ((URIEventListener) listeners[i + 1]).activateURI(e);
            }
        }
    }

    /**
     * Notify the listeners that the hyperlink (URI) has been entered.
     *
     * @param e the event
     * @see EventListenerList
     */
    public void fireEnterURI(URIEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == URIEventListener.class)
            {
                ((URIEventListener) listeners[i + 1]).enterURI(e);
            }
        }
    }

    /**
     * Notify the listeners that the hyperlink (URI) has been left.
     *
     * @param e the event
     * @see EventListenerList
     */
    public void fireLeaveURI(URIEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == URIEventListener.class)
            {
                ((URIEventListener) listeners[i + 1]).leaveURI(e);
            }
        }
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    {
        txtView.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    {
        txtView.addMouseListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getKey()
     */
    public Key getKey()
    {
        return key;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getBook()
     */
    public Book getBook()
    {
        return book;
    }

    // Strings for hyperlinks
    private static final String HYPERLINK_STYLE = "Hyperlink"; //$NON-NLS-1$
    private static final String DOUBLE_SLASH = "//"; //$NON-NLS-1$
    private static final String SCROLL_TO_URI = "scrolling to: {0}"; //$NON-NLS-1$
    private static final String RELATIVE_URI_PROTOCOL = ""; //$NON-NLS-1$

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(TextPaneBookDataDisplay.class);

    /**
     * The current book
     */
    private Book book;

    /**
     * The current key
     */
    private Key key;

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
