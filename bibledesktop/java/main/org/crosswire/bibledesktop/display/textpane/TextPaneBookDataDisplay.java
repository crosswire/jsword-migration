package org.crosswire.bibledesktop.display.textpane;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.MouseListener;
import java.net.MalformedURLException;
import java.text.MessageFormat;

import javax.swing.JTextPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.URLEvent;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.common.swing.AntiAliasedTextPane;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.util.ConverterFactory;

/**
 * A JDK JTextPane implementation of an OSIS displayer.
 *
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
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
            BookData bdata = book.getData(key);
            if (bdata == null)
            {
                txtView.setText(""); //$NON-NLS-1$
                return;
            }

            SAXEventProvider osissep = bdata.getSAXEventProvider();
            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            htmlsep.setParameter("direction", direction ? "ltr" : "rtl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            String text = XMLUtil.writeToString(htmlsep);

            txtView.setText(text);
            txtView.select(0, 0);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event.HyperlinkEvent)
     */
    public void hyperlinkUpdate(HyperlinkEvent ev)
    {
        // SPEED(DMS): This needs to be optimized. It takes too much CPU
        try
        {
            HyperlinkEvent.EventType type = ev.getEventType();
            JTextPane pane = (JTextPane) ev.getSource();

            String[] parts = getParts(ev.getDescription());
            if (type == HyperlinkEvent.EventType.ACTIVATED)
            {
                String url = ev.getDescription();
                if (parts[1].charAt(0) == '#')
                {
                    log.debug(MessageFormat.format(SCROLL_TO_URL, new Object[] { url }));
                    // This must be relative to the current document
                    // in which case we assume that it is an in page reference.
                    // We ignore the frame case (example code within JEditorPane
                    // JavaDoc).
                    // Remove the leading #
                    url = url.substring(1);
                    pane.scrollToReference(url);
                }
                else
                {
                    // Fully formed, so we hand it off to be processed
                    fireActivateURL(new URLEvent(this, parts[0], parts[1]));
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
                    fireEnterURL(new URLEvent(this, parts[0], parts[1]));
                }
                else
                {
                    fireLeaveURL(new URLEvent(this, parts[0], parts[1]));
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
        String protocol = RELATIVE_URL_PROTOCOL;
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

        String[] parts = { protocol, data };
        return parts;
    }
    /**
     * Accessor for the Swing component
     */
    public Component getComponent()
    {
        return txtView;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.FocusablePart#copy()
     */
    public void copy()
    {
        txtView.copy();
    }

    /**
     * Adds a hyperlink listener for notification of any changes, for example
     * when a link is selected and entered.
     *
     * @param listener the listener
     */
    public synchronized void addURLEventListener(URLEventListener listener)
    {
        listenerList.add(URLEventListener.class, listener);
    }

    /**
     * Removes a hyperlink listener.
     *
     * @param listener the listener
     */
    public synchronized void removeURLEventListener(URLEventListener listener)
    {
        listenerList.remove(URLEventListener.class, listener);
    }

    /**
     * Notify the listeners that the hyperlink (URL) has been activated.
     *
     * @param e the event
     * @see EventListenerList
     */
    public void fireActivateURL(URLEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == URLEventListener.class)
            {
                ((URLEventListener) listeners[i + 1]).activateURL(e);
            }
        }
    }

    /**
     * Notify the listeners that the hyperlink (URL) has been entered.
     *
     * @param e the event
     * @see EventListenerList
     */
    public void fireEnterURL(URLEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == URLEventListener.class)
            {
                ((URLEventListener) listeners[i + 1]).enterURL(e);
            }
        }
    }

    /**
     * Notify the listeners that the hyperlink (URL) has been left.
     *
     * @param e the event
     * @see EventListenerList
     */
    public void fireLeaveURL(URLEvent e)
    {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == URLEventListener.class)
            {
                ((URLEventListener) listeners[i + 1]).leaveURL(e);
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
    private static final String SCROLL_TO_URL = "scrolling to: {0}"; //$NON-NLS-1$
    private static final String RELATIVE_URL_PROTOCOL = ""; //$NON-NLS-1$

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
     * The listeners for handling urls
     */
    private EventListenerList listenerList = new EventListenerList();
}
