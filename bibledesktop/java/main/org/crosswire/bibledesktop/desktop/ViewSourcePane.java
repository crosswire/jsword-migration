package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
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
     * @param html The HTML contents of the text area
     * @param osis The OSIS contents of the text area
     */
    public ViewSourcePane(String html, String osis)
    {
        init();

        txtHtml.setText(html);
        txtHtml.setCaretPosition(0);

        txtOsis.setText(osis);
        txtOsis.setCaretPosition(0);
    }

    /**
     * Actually create the GUI
     */
    private void init()
    {
        actions = new ActionFactory(ViewSourcePane.class, this);

        txtHtml = new JTextArea();
        txtHtml.setEditable(false);
        txtHtml.setColumns(80);
        txtHtml.setRows(24);
        JPanel pnlHtml = new JPanel(new BorderLayout());
        pnlHtml.add(new JScrollPane(txtHtml), BorderLayout.CENTER);
        pnlHtml.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        txtOsis = new JTextArea();
        txtOsis.setEditable(false);
        txtOsis.setColumns(80);
        txtOsis.setRows(24);
        JPanel pnlOsis = new JPanel(new BorderLayout());
        pnlOsis.add(new JScrollPane(txtOsis), BorderLayout.CENTER);
        pnlOsis.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(new JButton(actions.getAction("SourceClip")), null); //$NON-NLS-1$

        JTabbedPane tabMain = new JTabbedPane();
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
        // TODO: make this copy the current tab's text
        StringSelection ss = new StringSelection(txtHtml.getText());
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
    private JTextArea txtHtml;
    private JTextArea txtOsis;
    private JPanel pnlButtons;
    private JDialog frame;
    private ActionFactory actions;
}