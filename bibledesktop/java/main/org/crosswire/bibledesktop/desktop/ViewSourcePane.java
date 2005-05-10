/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import org.crosswire.bibledesktop.util.ConfigurableSwingConverter;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.FormatType;
import org.crosswire.common.xml.PrettySerializingContentHandler;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.util.ConverterFactory;
import org.xml.sax.ContentHandler;

/**
 * ViewSourcePane allow viewing of some text in its own standalone frame.
 * The text to be viewed can be grabbed from a String, a URL, or a file.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at gmail dot com]
 */
public class ViewSourcePane extends JPanel
{
    public ViewSourcePane(Book book, Key key)
    {
        try
        {
            String orig = book.getRawData(key);

            BookData bdata = book.getData(key);

            BookMetaData bmd = book.getBookMetaData();
            boolean direction = bmd.isLeftToRight();

            SAXEventProvider osissep = bdata.getSAXEventProvider();

            // This really looks nice but its performance was terrible.
//            ContentHandler osis = new HTMLSerializingContentHandler(FormatType.CLASSIC_INDENT);
            ContentHandler osis = new PrettySerializingContentHandler(FormatType.CLASSIC_INDENT);
            osissep.provideSAXEvents(osis);

            TransformingSAXEventProvider htmlsep = (TransformingSAXEventProvider) converter.convert(osissep);
            if (bmd.getType() == BookType.BIBLE)
            {
                htmlsep.setParameter(XSLTProperty.STRONGS_NUMBERS.getName(), Boolean.toString(XSLTProperty.STRONGS_NUMBERS.getState()));
                htmlsep.setParameter(XSLTProperty.MORPH.getName(), Boolean.toString(XSLTProperty.MORPH.getState()));
                htmlsep.setParameter(XSLTProperty.START_VERSE_ON_NEWLINE.getName(), Boolean.toString(XSLTProperty.START_VERSE_ON_NEWLINE.getState()));
                htmlsep.setParameter(XSLTProperty.VERSE_NUMBERS.getName(), Boolean.toString(XSLTProperty.VERSE_NUMBERS.getState()));
                htmlsep.setParameter(XSLTProperty.TINY_VERSE_NUMBERS.getName(), Boolean.toString(XSLTProperty.TINY_VERSE_NUMBERS.getState()));
                htmlsep.setParameter(XSLTProperty.NOTES.getName(), Boolean.toString(XSLTProperty.NOTES.getState()));
                htmlsep.setParameter(XSLTProperty.XREF.getName(), Boolean.toString(XSLTProperty.XREF.getState()));
                htmlsep.setParameter("direction", direction ? "ltr" : "rtl"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }

            // This really looks nice but its performance was terrible.
//            ContentHandler html = new HTMLSerializingContentHandler(FormatType.CLASSIC_INDENT);
            ContentHandler html = new PrettySerializingContentHandler(FormatType.CLASSIC_INDENT);
            htmlsep.provideSAXEvents(html);

            init(orig, osis.toString(), html.toString());
        }
        catch (Exception ex)
        {
            Reporter.informUser(null, ex);
        }
    }

    /**
     * Actually create the GUI
     */
    private void init(String orig, String osis, String html)
    {
        actions = new ActionFactory(ViewSourcePane.class, this);

        Font userRequestedFont = ConfigurableSwingConverter.toFont();

        JTextArea txtOrig = new JTextArea(orig, 24, 80);
        txtOrig.setFont(userRequestedFont);
        txtOrig.setLineWrap(true);
        txtOrig.setWrapStyleWord(true);
        txtOrig.setTabSize(2);
        txtOrig.setEditable(false);
        JPanel pnlOrig = new JPanel(new BorderLayout());
        pnlOrig.add(new JScrollPane(txtOrig), BorderLayout.CENTER);
        pnlOrig.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // This really looks nice but its performance was terrible.
//        JTextPane txtOsis = new AntiAliasedTextPane();
//        txtOsis.setFont(userRequestedFont);
//        txtOsis.setEditable(false);
//        txtOsis.setEditorKit(new HTMLEditorKit());
//        txtOsis.setText(osis);
//        txtOsis.setCaretPosition(0);
        JTextArea txtOsis = new JTextArea(html, 24, 80);
        txtOsis.setFont(userRequestedFont);
        txtOsis.setLineWrap(true);
        txtOsis.setWrapStyleWord(true);
        txtOsis.setTabSize(2);
        txtOsis.setEditable(false);
        JPanel pnlOsis = new JPanel(new BorderLayout());
        pnlOsis.add(new JScrollPane(txtOsis), BorderLayout.CENTER);
        pnlOsis.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // This really looks nice but its performance was terrible.
//        JTextPane txtHtml = new AntiAliasedTextPane();
//        txtHtml.setFont(userRequestedFont);
//        txtHtml.setEditable(false);
//        txtHtml.setEditorKit(new HTMLEditorKit());
//        txtHtml.setText(html);
//        txtHtml.setCaretPosition(0);
        JTextArea txtHtml = new JTextArea(osis, 24, 80);
        txtHtml.setFont(userRequestedFont);
        txtHtml.setLineWrap(true);
        txtHtml.setWrapStyleWord(true);
        txtHtml.setTabSize(2);
        txtHtml.setEditable(false);
        JPanel pnlHtml = new JPanel(new BorderLayout());
        pnlHtml.add(new JScrollPane(txtHtml), BorderLayout.CENTER);
        pnlHtml.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        textAreas = new JTextComponent[] { txtOrig, txtOsis, txtHtml };

        pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(new JButton(actions.getAction("SourceClip")), null); //$NON-NLS-1$

        tabMain = new JTabbedPane();
        tabMain.add(pnlOrig, Msg.ORIG.toString());
        tabMain.add(pnlOsis, Msg.OSIS.toString());
        tabMain.add(pnlHtml, Msg.HTML.toString());

        this.setLayout(new BorderLayout());
        this.add(tabMain, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
    }

    /**
     * Display this Panel in a new JFrame
     */
    public void showInFrame(Frame parent)
    {
        frame = new JDialog(parent, Msg.TEXT_VIEWER.toString());

        pnlButtons.add(new JButton(actions.getAction("SourceOK")), null); //$NON-NLS-1$

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        GuiUtil.setSize(frame, new Dimension(750, 500));
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Copy the current text into the system clipboard
     */
    public void doSourceClip()
    {
        int i = tabMain.getSelectedIndex();
        JTextComponent tc = textAreas[i];
        StringSelection ss = new StringSelection(tc.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     *
     */
    public void doSourceOK()
    {
        frame.setVisible(false);
        frame.dispose();
    }

    /*
     * GUI Components
     */
    private JTabbedPane tabMain;
    private JTextComponent [] textAreas;
    private JPanel pnlButtons;
    private JDialog frame;
    private transient ActionFactory actions;

    private static Converter converter = ConverterFactory.getConverter();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257281435579985975L;
}
