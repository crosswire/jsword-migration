package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
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

import org.crosswire.bibledesktop.util.ConfigurableSwingConverter;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;

/**
 * ViewSourcePane allow viewing of some text in its own standalone frame.
 * The text to be viewed can be grabbed from a String, a URL, or a file.
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
 * @version $Id$
 */
public class ViewSourcePane extends JPanel
{
    /**
     * Construct a ViewSourcePane with some string contents
     * @param orig The original contents of the text area
     * @param osis The OSIS contents of the text area
     * @param html The HTML contents of the text area
     */
    public ViewSourcePane(String orig, String osis, String html)
    {
        init(orig, osis, html);
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
        txtOrig.setEditable(false);
        txtOrig.setLineWrap(true);
        txtOrig.setWrapStyleWord(true);
        JPanel pnlOrig = new JPanel(new BorderLayout());
        pnlOrig.add(new JScrollPane(txtOrig), BorderLayout.CENTER);
        pnlOrig.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTextArea txtOsis = new JTextArea(osis, 24, 80);
        txtOsis.setFont(userRequestedFont);
        txtOsis.setEditable(false);
        JPanel pnlOsis = new JPanel(new BorderLayout());
        pnlOsis.add(new JScrollPane(txtOsis), BorderLayout.CENTER);
        pnlOsis.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTextArea txtHtml = new JTextArea(html, 24, 80);
        txtHtml.setFont(userRequestedFont);
        txtHtml.setEditable(false);
        JPanel pnlHtml = new JPanel(new BorderLayout());
        pnlHtml.add(new JScrollPane(txtHtml), BorderLayout.CENTER);
        pnlHtml.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        textAreas = new JTextArea[] { txtOrig, txtOsis, txtHtml };

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

        frame.pack();
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Copy the current text into the system clipboard
     */
    public void doSourceClip()
    {
        int i = tabMain.getSelectedIndex();
        JTextArea ta = textAreas[i];
        StringSelection ss = new StringSelection(ta.getText());
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
    private JTextArea [] textAreas;
    private JPanel pnlButtons;
    private JDialog frame;
    private ActionFactory actions;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257281435579985975L;
}
