package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.crosswire.bibledesktop.book.BookListCellRenderer;
import org.crosswire.bibledesktop.book.BooksComboBoxModel;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.basic.Verifier;
import org.crosswire.jsword.passage.Key;

/**
 * A ComparePane allows you to compare 2 differing version of the Bible
 * verse, by verse.
 * <p>so start one of these call:
 * <pre>
 * ComparePane comp = new ComparePane();
 * comp.showInDialog(getComponent());
 * </pre>
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
public class ComparePane extends EirPanel
{
    /**
     * Basic Constructor
     */
    public ComparePane()
    {
        init();
    }

    /**
     * Generate the GUI
     */
    private void init()
    {
        cboBible1.setModel(mdlBibles1);
        cboBible1.setRenderer(new BookListCellRenderer());
        cboBible2.setModel(mdlBibles2);
        cboBible2.setRenderer(new BookListCellRenderer());
        pnlBibles.setLayout(new BoxLayout(pnlBibles, BoxLayout.Y_AXIS));
        pnlBibles.setAlignmentX((float) 0.5);
        pnlBibles.setBorder(new TitledBorder(Msg.COMPARE_TITLE.toString()));
        btnGo.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                compare();
            }
        });
        pnlBibles.add(cboBible1, null);
        pnlBibles.add(Box.createVerticalStrut(5), null);
        pnlBibles.add(cboBible2, null);

        // TODO(joe): don't hard code this, read it from a Book
        txtVerses.setText("Gen-Rev"); //$NON-NLS-1$
        lblVerses.setText(Msg.COMPARE_VERSES.toString());
        lblVerses.setLabelFor(txtVerses);
        pnlVerses.setLayout(new BorderLayout());
        pnlVerses.add(lblVerses, BorderLayout.WEST);
        pnlVerses.add(txtVerses, BorderLayout.CENTER);
        txtWords.setToolTipText(Msg.COMPARE_WORDS_TIP.toString());
        lblWords.setText(Msg.COMPARE_WORDS.toString());
        lblWords.setLabelFor(txtWords);
        pnlWords.setLayout(new BorderLayout());
        pnlWords.add(lblWords, BorderLayout.WEST);
        pnlWords.add(txtWords, BorderLayout.CENTER);
        pnlUsing.setBorder(new TitledBorder(Msg.COMPARE_USING.toString()));
        pnlUsing.setLayout(new BoxLayout(pnlUsing, BoxLayout.Y_AXIS));
        pnlUsing.add(pnlVerses, null);
        pnlUsing.add(pnlWords, null);

        // I18N: Migrate this to an ActionFactory
        btnGo.setText(Msg.COMPARE_GO.toString());
        pnlButtons.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.add(btnGo, null);

        boxTop = Box.createVerticalBox();
        boxTop.add(pnlBibles, null);
        boxTop.add(pnlUsing, null);
        boxTop.add(pnlButtons, null);

        this.setLayout(new BorderLayout());
        this.add(boxTop, BorderLayout.NORTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, Msg.COMPARE_DIALOG.toString(), false);
    }

    /**
     * Actually preform the comparison.
     */
    protected void compare()
    {
        BookMetaData bmd1 = mdlBibles1.getSelectedBookMetaData();
        BookMetaData bmd2 = mdlBibles2.getSelectedBookMetaData();

        if (bmd1.equals(bmd2))
        {
            if (JOptionPane.showConfirmDialog(this, Msg.COMPARE_IDENT_QUESTION.toString(), Msg.COMPARE_IDENT_TITLE.toString(), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            {
                return;
            }
        }

        try
        {
            Book book1 = bmd1.getBook();
            Book book2 = bmd2.getBook();

            String words = txtWords.getText();
            String refText = txtVerses.getText();

            // Is this right?
            Key key = book1.getKey(refText);

            words = words.trim();
            if (words.equals("*")) //$NON-NLS-1$
            {
                words = ""; //$NON-NLS-1$
            }
            if (words.equals("")) //$NON-NLS-1$
            {
                words = null;
            }

            Verifier ver = new Verifier(book1, book2);

            CompareResultsPane results = new CompareResultsPane(ver);
            results.setCheckText(words);
            results.setCheckPassages(key);
            results.showInFrame(GuiUtil.getFrame(this));
            results.startStop();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * The first Bible selection combo.
     * We cast to Bible in compare() so we need to filter
     */
    private BooksComboBoxModel mdlBibles1 = new BooksComboBoxModel(BookFilters.getBibles());

    /**
     * The second Bible selection combo
     * We cast to Bible in compare() so we need to filter
     */
    private BooksComboBoxModel mdlBibles2 = new BooksComboBoxModel(BookFilters.getBibles());

    /* GUI Components */
    private Box boxTop;
    private JPanel pnlBibles = new JPanel();
    private JPanel pnlUsing = new JPanel();
    private JPanel pnlVerses = new JPanel();
    private JLabel lblVerses = new JLabel();
    private JTextField txtVerses = new JTextField();
    private JPanel pnlWords = new JPanel();
    private JLabel lblWords = new JLabel();
    private JTextField txtWords = new JTextField();
    private JComboBox cboBible1 = new JComboBox();
    private JComboBox cboBible2 = new JComboBox();
    private JPanel pnlButtons = new JPanel();
    private JButton btnGo = new JButton();
}