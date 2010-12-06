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
package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;
import javax.xml.transform.TransformerException;

import org.crosswire.bibledesktop.book.install.BookFont;
import org.crosswire.bibledesktop.util.ConfigurableSwingConverter;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.GuiConvert;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.FormatType;
import org.crosswire.common.xml.PrettySerializingContentHandler;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * ViewSourcePane allow viewing of some text in its own standalone frame. The
 * text to be viewed can be grabbed from a String, a URI, or a file.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class ViewSourcePane extends JPanel {
    public ViewSourcePane(Book[] books, Key key) {
        try {
            StringBuilder buf = new StringBuilder();

            Iterator iter = key.iterator();
            while (iter.hasNext()) {
                Key currentKey = (Key) iter.next();
                String osisID = currentKey.getOsisID();
                for (int i = 0; i < books.length; i++) {
                    Book book = books[i];
                    if (buf.length() > 0) {
                        buf.append('\n');
                    }
                    buf.append(book.getInitials());
                    buf.append(':');
                    buf.append(osisID);
                    buf.append(" - ");
                    buf.append(book.getRawText(currentKey));
                }
            }

            // TODO(DMS): handle comparison
            BookData bdata = new BookData(books, key, false);

            Book book = bdata.getFirstBook();
            BookMetaData bmd = book.getBookMetaData();

            String fontSpec = GuiConvert.font2String(BookFont.instance().getFont(book));

            SAXEventProvider osissep = bdata.getSAXEventProvider();

            // This really looks nice but its performance was terrible.
            // ContentHandler osis = new
            // HTMLSerializingContentHandler(FormatType.CLASSIC_INDENT);
            ContentHandler osis = new PrettySerializingContentHandler(FormatType.CLASSIC_INDENT);
            osissep.provideSAXEvents(osis);

            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);

            XSLTProperty.DIRECTION.setState(bmd.isLeftToRight() ? "ltr" : "rtl");

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

            // This really looks nice but its performance was terrible.
            // ContentHandler html = new
            // HTMLSerializingContentHandler(FormatType.CLASSIC_INDENT);
            ContentHandler html = new PrettySerializingContentHandler(FormatType.CLASSIC_INDENT);
            htmlsep.provideSAXEvents(html);

            init(buf.toString(), osis.toString(), html.toString());
        } catch (SAXException e) {
            Reporter.informUser(null, e);
        } catch (TransformerException e) {
            Reporter.informUser(null, e);
        } catch (BookException e) {
            Reporter.informUser(null, e);
        }
    }

    /**
     * Actually create the GUI
     */
    private void init(String orig, String osis, String html) {
        actions = new ActionFactory(Msg.class, this);

        Font userRequestedFont = ConfigurableSwingConverter.toFont();

        JTextArea txtOrig = new JTextArea(orig, 24, 80);
        txtOrig.setFont(userRequestedFont);
        txtOrig.setLineWrap(true);
        txtOrig.setWrapStyleWord(true);
        txtOrig.setTabSize(2);
        txtOrig.setEditable(false);
        JPanel pnlOrig = new JPanel(new BorderLayout());
        pnlOrig.add(new CWScrollPane(txtOrig), BorderLayout.CENTER);
        pnlOrig.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // This really looks nice but its performance was terrible.
        // JTextPane txtOsis = new AntiAliasedTextPane();
        // txtOsis.setFont(userRequestedFont);
        // txtOsis.setEditable(false);
        // txtOsis.setEditorKit(new HTMLEditorKit());
        // txtOsis.setText(osis);
        // txtOsis.setCaretPosition(0);
        JTextArea txtOsis = new JTextArea(osis, 24, 80);
        txtOsis.setFont(userRequestedFont);
        txtOsis.setLineWrap(true);
        txtOsis.setWrapStyleWord(true);
        txtOsis.setTabSize(2);
        txtOsis.setEditable(false);
        JPanel pnlOsis = new JPanel(new BorderLayout());
        pnlOsis.add(new CWScrollPane(txtOsis), BorderLayout.CENTER);
        pnlOsis.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // This really looks nice but its performance was terrible.
        // JTextPane txtHtml = new AntiAliasedTextPane();
        // txtHtml.setFont(userRequestedFont);
        // txtHtml.setEditable(false);
        // txtHtml.setEditorKit(new HTMLEditorKit());
        // txtHtml.setText(html);
        // txtHtml.setCaretPosition(0);
        JTextArea txtHtml = new JTextArea(html, 24, 80);
        txtHtml.setFont(userRequestedFont);
        txtHtml.setLineWrap(true);
        txtHtml.setWrapStyleWord(true);
        txtHtml.setTabSize(2);
        txtHtml.setEditable(false);
        JPanel pnlHtml = new JPanel(new BorderLayout());
        pnlHtml.add(new CWScrollPane(txtHtml), BorderLayout.CENTER);
        pnlHtml.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        textAreas = new JTextComponent[] {
                txtOrig, txtOsis, txtHtml
        };

        pnlButtons = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        pnlButtons.add(new JButton(actions.getAction("SourceClip")), null);

        tabMain = new JTabbedPane();
        // TRANSLATOR: Label for the View Source tab holding the raw content
        // of the passage in the active Bible View.
        tabMain.add(pnlOrig, Msg.gettext("Original"));
        // TRANSLATOR: Label for the View Source tab holding the OSIS transformation
        // of the raw content in the prior tab for the passage in the active Bible View.
        tabMain.add(pnlOsis, Msg.gettext("OSIS"));
        // TRANSLATOR: Label for the View Source tab holding the HTML transformation
        // of the OSIS in the prior tab for the passage in the active Bible View.
        tabMain.add(pnlHtml, Msg.gettext("HTML"));

        this.setLayout(new BorderLayout());
        this.add(tabMain, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Display this Panel in a new JFrame
     */
    public void showInFrame(Frame parent) {
        // TRANSLATOR: title for the Source View dialog
        frame = new JDialog(parent, Msg.gettext("Source Viewer"));

        pnlButtons.add(new JButton(actions.getAction("SourceOK")), null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        GuiUtil.setSize(frame, new Dimension(750, 500));
        GuiUtil.centerOnScreen(frame);
        frame.setVisible(true);
    }

    /**
     * Copy the current text into the system clipboard
     */
    public void doSourceClip() {
        int i = tabMain.getSelectedIndex();
        JTextComponent tc = textAreas[i];
        StringSelection ss = new StringSelection(tc.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     *
     */
    public void doSourceOK() {
        frame.setVisible(false);
        frame.dispose();
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        actions = new ActionFactory(ViewSourcePane.class, this);
        is.defaultReadObject();
    }

    /*
     * GUI Components
     */
    private JTabbedPane tabMain;
    private JTextComponent[] textAreas;
    private JPanel pnlButtons;
    private JDialog frame;
    private transient ActionFactory actions;

    private static Converter converter = ConverterFactory.getConverter();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257281435579985975L;
}
