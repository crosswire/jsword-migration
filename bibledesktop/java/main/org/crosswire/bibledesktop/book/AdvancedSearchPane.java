package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.StringUtil;
import org.crosswire.jsword.book.search.parse.IndexSearcher;
import org.crosswire.jsword.book.search.parse.PassageLeftParamWord;
import org.crosswire.jsword.book.search.parse.PassageRightParamWord;
import org.crosswire.jsword.book.search.parse.PhraseParamWord;
import org.crosswire.jsword.book.search.parse.RemoveCommandWord;
import org.crosswire.jsword.book.search.parse.RetainCommandWord;

/**
 * An advanced search dialog.
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
public class AdvancedSearchPane extends JPanel implements DocumentListener
{
    /**
     * This is the default constructor
     */
    public AdvancedSearchPane()
    {
        presets = Msg.PRESETS.toString().split("\\|"); //$NON-NLS-1$

        initialize();
    }

    /**
     * This method initializes this GUI
     */
    private void initialize()
    {
        actions = new ActionFactory(AdvancedSearchPane.class, this);

        // SystemColor.controlShadow
        JLabel temp = new JLabel();
        Color headBG = SystemColor.control.darker();
        Color headFG = Color.BLACK;
        Font headFont = temp.getFont().deriveFont(Font.BOLD);

        lblHeading = actions.createJLabel(HEAD_BASE);
        lblHeading.setBorder(BorderFactory.createLineBorder(headBG, 3));
        lblHeading.setBackground(headBG);
        lblHeading.setForeground(headFG);
        lblHeading.setFont(headFont);
        lblHeading.setOpaque(true);

        lblPhrase = actions.createJLabel(PHRASE);
        txtPhrase = new JTextField();
        txtPhrase.getDocument().addDocumentListener(this);

        txtIncludes = new JTextField();
        txtIncludes.getDocument().addDocumentListener(this);
        lblIncludes = actions.createJLabel(INCLUDES);
        lblIncludes.setLabelFor(txtIncludes);

        txtExcludes = new JTextField();
        txtExcludes.getDocument().addDocumentListener(this);
        lblExcludes = actions.createJLabel(EXCLUDES);
        lblExcludes.setLabelFor(txtExcludes);

        chkRestrict = new JCheckBox(actions.getAction(HEAD_RESTRICT));
        chkRestrict.setBackground(headBG);
        chkRestrict.setForeground(headFG);
        chkRestrict.setFont(headFont);
        lblPresets = actions.createJLabel(PRESETS);
        lblPresets.setVisible(false);
        cboPresets = new JComboBox(presets);
        cboPresets.setVisible(false);
        cboPresets.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                updatePreset();
            }
        });
        lblRestrict = actions.createJLabel(RESTRICT);
        lblRestrict.setVisible(false);
        txtRestrict = new JTextField();
        txtRestrict.setVisible(false);
        txtRestrict.getDocument().addDocumentListener(this);
        btnRestrict = new JButton(actions.getAction(RESTRICT_SELECT));
        btnRestrict.setVisible(false);

        chkSummary = new JCheckBox(actions.getAction(HEAD_SUMMARY));
        chkSummary.setBackground(headBG);
        chkSummary.setForeground(headFG);
        chkSummary.setFont(headFont);
        lblSummary = actions.createJLabel(SUMMARY);
        lblSummary.setVisible(false);
        txtSummary = new JTextArea();
        txtSummary.setBackground(SystemColor.control);
        txtSummary.setLineWrap(true);
        txtSummary.setEditable(false);
        txtSummary.setRows(2);
        scrSummary = new JScrollPane(txtSummary);
        scrSummary.setVisible(false);

//        chkHebGrk = new JCheckBox(actions.getAction(HEAD_ORIGINAL));
//        chkHebGrk.setBackground(headBG);
//        chkHebGrk.setForeground(headFG);
//        chkHebGrk.setFont(headFont);
//        lblHebInc = actions.createJLabel(HEBREW_INCLUDE);
//        lblHebInc.setVisible(false);
//        txtHebInc = new JTextField();
//        txtHebInc.setVisible(false);
//        txtHebInc.getDocument().addDocumentListener(this);
//        lblHebExc = actions.createJLabel(HEBREW_EXCLUDE);
//        lblHebExc.setVisible(false);
//        txtHebExc = new JTextField();
//        txtHebExc.setVisible(false);
//        txtHebExc.getDocument().addDocumentListener(this);
//        lblGrkInc = actions.createJLabel(GREEK_INCLUDE);
//        lblGrkInc.setVisible(false);
//        txtGrkInc = new JTextField();
//        txtGrkInc.setVisible(false);
//        txtGrkInc.getDocument().addDocumentListener(this);
//        lblGrkExc = actions.createJLabel(GREEK_EXCLUDE);
//        lblGrkExc.setVisible(false);
//        txtGrkExc = new JTextField();
//        txtGrkExc.setVisible(false);
//        txtGrkExc.getDocument().addDocumentListener(this);
//
//        chkTime = new JCheckBox(actions.getAction(HEAD_TIME));
//        chkTime.setBackground(headBG);
//        chkTime.setForeground(headFG);
//        chkTime.setFont(headFont);
//        lblAfter = actions.createJLabel(AFTER);
//        lblAfter.setVisible(false);
//        txtAfter = new JTextField();
//        txtAfter.setVisible(false);
//        txtAfter.getDocument().addDocumentListener(this);
//        lblBefore = actions.createJLabel(BEFORE);
//        lblBefore.setVisible(false);
//        txtBefore = new JTextField();
//        txtBefore.setVisible(false);
//        txtBefore.getDocument().addDocumentListener(this);

        btnGo = new JButton(actions.getAction(DONE));

        this.setBorder(BorderFactory.createLineBorder(SystemColor.control, 5));
        this.setLayout(new GridBagLayout());
        this.add(lblHeading, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblPhrase, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtPhrase, new GridBagConstraints(1, 2, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblIncludes, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtIncludes, new GridBagConstraints(1, 3, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblExcludes, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtExcludes, new GridBagConstraints(1, 4, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(chkRestrict, new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblPresets, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(cboPresets, new GridBagConstraints(1, 6, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblRestrict, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtRestrict, new GridBagConstraints(1, 7, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(btnRestrict, new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
//        this.add(chkHebGrk, new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        this.add(lblHebInc, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtHebInc, new GridBagConstraints(1, 9, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblHebExc, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtHebExc, new GridBagConstraints(1, 10, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblGrkInc, new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtGrkInc, new GridBagConstraints(1, 11, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblGrkExc, new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtGrkExc, new GridBagConstraints(1, 12, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
//        this.add(chkTime, new GridBagConstraints(0, 13, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        this.add(lblAfter, new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtAfter, new GridBagConstraints(1, 14, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblBefore, new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtBefore, new GridBagConstraints(1, 15, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        this.add(chkSummary, new GridBagConstraints(0, 16, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblSummary, new GridBagConstraints(0, 17, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(scrSummary, new GridBagConstraints(1, 17, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 5, 2, 5), 0, 0));
        this.add(btnGo, new GridBagConstraints(2, 18, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(10, 0, 5, 5), 0, 0));
    }

    /**
     * Open us in a new (optionally modal) dialog window
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     */
    public String showInDialog(Component parent, String title, boolean modal, String search)
    {
        txtSummary.setText(search);

        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        bailout = true;

        ActionListener closer = new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                dlgMain.dispose();
            }
        };

        dlgMain.getContentPane().setLayout(new BorderLayout());
        dlgMain.getContentPane().add(this, BorderLayout.CENTER);

        dlgMain.getRootPane().setDefaultButton(btnGo);
        dlgMain.getRootPane().registerKeyboardAction(closer, esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlgMain.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlgMain.setTitle(title);
        dlgMain.setModal(modal);

        GuiUtil.restrainedPack(dlgMain, 0.5f, 0.75f);
        GuiUtil.centerWindow(dlgMain);
        dlgMain.setVisible(true);

        if (bailout)
        {
            return null;
        }
        return txtSummary.getText();
    }

    /**
     * Someone clicked the restrict toggle button
     */
    public void doHeadRestrict()
    {
        boolean visible = chkRestrict.isSelected();

        lblPresets.setVisible(visible);
        cboPresets.setVisible(visible);
        lblRestrict.setVisible(visible);
        txtRestrict.setVisible(visible);
        btnRestrict.setVisible(visible);

        if (dlgMain != null)
        {
            dlgMain.pack();
        }
    }

    /**
     * Someone clicked the restrict toggle button
     */
    public void doHeadSummary()
    {
        boolean visible = chkSummary.isSelected();

        lblSummary.setVisible(visible);
        scrSummary.setVisible(visible);

        if (dlgMain != null)
        {
            dlgMain.pack();
        }
    }

    /**
     * Someone clicked the original strongs toggle button
     */
    public void doHeadOriginal()
    {
//        boolean visible = chkHebGrk.isSelected();
//
//        lblHebInc.setVisible(visible);
//        txtHebInc.setVisible(visible);
//        lblHebExc.setVisible(visible);
//        txtHebExc.setVisible(visible);
//        lblGrkInc.setVisible(visible);
//        txtGrkInc.setVisible(visible);
//        lblGrkExc.setVisible(visible);
//        txtGrkExc.setVisible(visible);
//
//        if (dlgMain != null)
//        {
//            dlgMain.pack();
//        }
    }

    /**
     * Someone clicked the original strongs toggle button
     */
    public void doHeadTime()
    {
//        boolean visible = chkTime.isSelected();
//
//        lblBefore.setVisible(visible);
//        txtBefore.setVisible(visible);
//        lblAfter.setVisible(visible);
//        txtAfter.setVisible(visible);
//
//        if (dlgMain != null)
//        {
//            dlgMain.pack();
//        }
    }

    /**
     *
     */
    public void doRestrictSelect()
    {
        if (dlgSelect == null)
        {
            dlgSelect = new PassageSelectionPane();
        }

        String passg = dlgSelect.showInDialog(this, Msg.ADVANCED_SELECT_TITLE.toString(), true, txtRestrict.getText());
        if (passg != null)
        {
            cboPresets.setSelectedItem(presets[presets.length - 1]);
            txtRestrict.setText(passg);
        }
    }

    /**
     * Someone clicked on OK
     */
    public void doDone()
    {
        bailout = false;
        dlgMain.dispose();
    }

    /**
     *
     */
    public void updatePreset()
    {
        if (editingRestrict)
        {
            return;
        }

        String include = ""; //$NON-NLS-1$
        String preset = (String) cboPresets.getSelectedItem();
        if (preset != null)
        {
            int open = preset.indexOf(PRESET_START);
            int close = preset.indexOf(PRESET_END);

            if (open != -1 && close != -1)
            {
                include = preset.substring(open + 1, close);
            }
        }

        txtRestrict.setText(include);
    }

    /**
     * Regenerate the search string from the input boxes
     */
    private void updateSearchString()
    {
        String quote = IndexSearcher.getPreferredSyntax(PhraseParamWord.class);
        String plus = IndexSearcher.getPreferredSyntax(RetainCommandWord.class);
        String minus = IndexSearcher.getPreferredSyntax(RemoveCommandWord.class);
        String open = IndexSearcher.getPreferredSyntax(PassageLeftParamWord.class);
        String close = IndexSearcher.getPreferredSyntax(PassageRightParamWord.class);

        StringBuffer search = new StringBuffer();

        String phrase = txtPhrase.getText();
        if (phrase != null && phrase.trim().length() > 0)
        {

            search.append(quote);
            search.append(phrase);
            search.append(quote);
        }

        String includes = txtIncludes.getText();
        if (includes != null && includes.trim().length() > 0)
        {
            if (search.length() != 0)
            {
                search.append(SPACE);
            }

            String[] words = includes.split(SPACE);

            search.append(plus);
            search.append(StringUtil.join(words, SPACE + plus));
        }

        String excludes = txtExcludes.getText();
        if (excludes != null && excludes.trim().length() > 0)
        {
            if (search.length() != 0)
            {
                search.append(SPACE);
            }

            String[] words = excludes.split(SPACE);

            search.append(minus);
            search.append(StringUtil.join(words, SPACE + minus));
        }

        String restrict = txtRestrict.getText();
        if (restrict != null && restrict.trim().length() > 0)
        {
            if (search.length() != 0)
            {
                search.append(SPACE);
            }

            search.append(plus);
            search.append(open);
            search.append(restrict);
            search.append(close);
        }

        txtSummary.setText(search.toString());

        // Check that the presets match the combo
        editingRestrict = true;
        boolean match = false;
        ComboBoxModel model = cboPresets.getModel();
        String find = PRESET_START + restrict + PRESET_END;
        for (int i = 0; !match && i < model.getSize(); i++)
        {
            String element = (String) model.getElementAt(i);
            if (element.indexOf(find) != -1)
            {
                cboPresets.setSelectedIndex(i);
                match = true;
            }
        }

        if (!match)
        {
            cboPresets.setSelectedItem(presets[presets.length - 1]);
        }

        editingRestrict = false;
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent ev)
    {
        updateSearchString();
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent ev)
    {
        updateSearchString();
    }

    /* (non-Javadoc)
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent ev)
    {
        updateSearchString();
    }

//    /**
//     * Temporary driver.
//     * @param args The command line arguments
//     */
//    public static void main(String[] args)
//    {
//        LookAndFeelUtil.initialize();
//        AdvancedSearchPane adv = new AdvancedSearchPane();
//        String reply = adv.showInDialog(null, "Advanced Search", true, "test"); //$NON-NLS-1$ //$NON-NLS-2$
//        log.debug(reply);
//        System.exit(0);
//    }

    /*
     * Action constants
     */
    private static final String DONE = "Done"; //$NON-NLS-1$
    private static final String PHRASE = "Phrase"; //$NON-NLS-1$
    private static final String RESTRICT = "Restrict"; //$NON-NLS-1$
    private static final String HEAD_RESTRICT = "HeadRestrict"; //$NON-NLS-1$
    private static final String HEAD_BASE = "HeadBase"; //$NON-NLS-1$
    private static final String INCLUDES = "Includes"; //$NON-NLS-1$
    private static final String EXCLUDES = "Excludes"; //$NON-NLS-1$
    private static final String PRESETS = "Presets"; //$NON-NLS-1$
    private static final String RESTRICT_SELECT = "RestrictSelect"; //$NON-NLS-1$
    private static final String HEAD_SUMMARY = "HeadSummary"; //$NON-NLS-1$
    private static final String SUMMARY = "Summary"; //$NON-NLS-1$
//    private static final String HEAD_ORIGINAL = "HeadOriginal"; //$NON-NLS-1$
//    private static final String HEBREW_INCLUDE = "HebrewInclude"; //$NON-NLS-1$
//    private static final String HEBREW_EXCLUDE = "HebrewExclude"; //$NON-NLS-1$
//    private static final String GREEK_INCLUDE = "GreekInclude"; //$NON-NLS-1$
//    private static final String GREEK_EXCLUDE = "GreekExclude"; //$NON-NLS-1$
//    private static final String HEAD_TIME = "HeadTime"; //$NON-NLS-1$
//    private static final String AFTER = "After"; //$NON-NLS-1$
//    private static final String BEFORE = "Before"; //$NON-NLS-1$

    /**
     * In our parsing we use space quite a lot and this ensures there is only
     * one and that we don't have lots of NON-NLS comments everywhere
     */
    private static final String SPACE = " "; //$NON-NLS-1$

    private static final String PRESET_END = ")"; //$NON-NLS-1$

    private static final String PRESET_START = "("; //$NON-NLS-1$

    /**
     * If escape was pressed we don't want to update the parent
     */
    protected boolean bailout;

    /**
     * The ActionFactory holding the actions used by this Component.
     */
    private ActionFactory actions;

    /**
     * The entries in the restrictions preset
     */
    private String[] presets;

    /**
     * If we are editing the restrict text box, ignore preset updates
     */
    private boolean editingRestrict;

    /*
     * GUI Components
     */
    private PassageSelectionPane dlgSelect;
    private JLabel lblPhrase;
    private JLabel lblIncludes;
    private JTextField txtIncludes;
    private JTextField txtPhrase;
    private JLabel lblExcludes;
    private JTextField txtExcludes;
    private JLabel lblHeading;
    private JCheckBox chkRestrict;
    private JLabel lblRestrict;
    private JTextField txtRestrict;
    private JButton btnRestrict;
    private JButton btnGo;
    private JLabel lblPresets;
    private JComboBox cboPresets;
    protected JDialog dlgMain;
//    private JCheckBox chkHebGrk;
//    private JLabel lblHebInc;
//    private JTextField txtHebInc;
//    private JLabel lblHebExc;
//    private JTextField txtHebExc;
//    private JLabel lblGrkInc;
//    private JTextField txtGrkInc;
//    private JLabel lblGrkExc;
//    private JTextField txtGrkExc;
//    private JCheckBox chkTime;
//    private JLabel lblBefore;
//    private JTextField txtBefore;
//    private JLabel lblAfter;
//    private JTextField txtAfter;
    private JLabel lblSummary;
    private JCheckBox chkSummary;
    private JTextArea txtSummary;
    private JScrollPane scrSummary;

//    /**
//     * The log stream
//     */
//    private static final Logger log = Logger.getLogger(AdvancedSearchPane.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977303234983245108L;
}
