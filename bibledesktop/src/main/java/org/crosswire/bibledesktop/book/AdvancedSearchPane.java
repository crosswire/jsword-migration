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
package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Dictionary;
import java.util.Hashtable;

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
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.crosswire.bibledesktop.BibleDesktopMsg;
import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWLabel;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.index.search.SearchType;

/**
 * An advanced search dialog.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class AdvancedSearchPane extends JPanel implements DocumentListener {
    /**
     * This is the default constructor
     */
    public AdvancedSearchPane() {
        // TRANSLATOR: This pattern allows one to specify different, potentially overlapping divisions of the Bible.
        // Each division is separated by a '|', which will not be shown to the user.
        // If the division represents a part of the Bible, that part is shown after the division name.
        // If it is not appropriate for the part to be shown in ( ), but rather some other pair of characters,
        //    even the same character (other than '|') this can be done.
        // The book names should be given as something that JSword can understand. Chapter and verse numbers should be 1-9 and not localized.
        // The last choice should not have a division marker and is used when the user types something other than what is in the list.
        presets = BibleDesktopMsg.gettext("The Whole Bible|All Prophecy (Deu 28, Isa-Mal, Rev)|Old Testament (Gen-Mal)|New Testament (Mat-Rev)|The Pentateuch (Gen-Deu)|History (Josh-Est)|Poetry (Job-Song)|Major Prophets (Isa-Dan)|Minor Prophets (Hos-Mal)|Gospels and Acts (Mat-Act)|Letters to People (Rom-Heb)|Letters from People (Jam-Jude)|Custom").split("\\|");

        initialize();
    }

    /**
     * This method initializes this GUI
     */
    private void initialize() {
        shaper = new NumberShaper();
        // TRANSLATOR: The start of the passage list for the division of the Bible
        presetStart = BibleDesktopMsg.gettext("(");
        // TRANSLATOR: The end of the passage list for the division of the Bible
        presetEnd = BibleDesktopMsg.gettext(")");

        actions = new ActionFactory(this);

        // SystemColor.controlShadow
        JLabel temp = new JLabel();
        Color headBG = SystemColor.control.darker();
        Color headFG = Color.BLACK;
        Font headFont = temp.getFont().deriveFont(Font.BOLD);

        // TRANSLATOR: Heading for the first, most useful several search options.
        lblHeading = CWLabel.createJLabel(BibleDesktopMsg.gettext("Search for verses with the following details"));
        lblHeading.setBorder(BorderFactory.createLineBorder(headBG, 3));
        lblHeading.setBackground(headBG);
        lblHeading.setForeground(headFG);
        lblHeading.setFont(headFont);
        lblHeading.setOpaque(true);

        // TRANSLATOR: Label for an input box for searching of phrases.
        lblPhrase = CWLabel.createJLabel(BibleDesktopMsg.gettext("Includes this phrase:"));
        txtPhrase = new JTextField();
        txtPhrase.getDocument().addDocumentListener(this);

        txtIncludes = new JTextField();
        txtIncludes.getDocument().addDocumentListener(this);
        // TRANSLATOR: Label for an input box for searching of all of the given words.
        lblIncludes = CWLabel.createJLabel(BibleDesktopMsg.gettext("Includes these words:"));
        lblIncludes.setLabelFor(txtIncludes);

        txtExcludes = new JTextField();
        txtExcludes.getDocument().addDocumentListener(this);
        // TRANSLATOR: Label for an input box for searching of verses not containing the given words.
        lblExcludes = CWLabel.createJLabel(BibleDesktopMsg.gettext("Excludes all these words:"));
        lblExcludes.setLabelFor(txtExcludes);

        txtSpell = new JTextField();
        txtSpell.getDocument().addDocumentListener(this);
        // TRANSLATOR: Label for an input box for searching of words whose spelling is unknown or varies.
        lblSpell = CWLabel.createJLabel(BibleDesktopMsg.gettext("Something like this spelling:"));
        lblSpell.setLabelFor(txtSpell);

        txtStartsWith = new JTextField();
        txtStartsWith.getDocument().addDocumentListener(this);
        // TRANSLATOR: Label for an input box for searching of words by their prefix
        lblStartsWith = CWLabel.createJLabel(BibleDesktopMsg.gettext("Includes words starting with:"));
        lblStartsWith.setLabelFor(txtStartsWith);

        // TRANSLATOR: Heading for section to perform a search for the best verse match
        chkRank = new JCheckBox(actions.addAction("HeadRank", BibleDesktopMsg.gettext("Prioritize the found verses")));
        chkRank.setBackground(headBG);
        chkRank.setForeground(headFG);
        chkRank.setFont(headFont);
        // TRANSLATOR: Label for a slider how many of the best verses to show.
        lblRank = CWLabel.createJLabel(BibleDesktopMsg.gettext("Show"));
        setLabelRank(DisplaySelectPane.getNumRankedVerses());
        lblRank.setVisible(false);
        sliderRank = new JSlider(SwingConstants.HORIZONTAL, 0, DisplaySelectPane.getMaxNumRankedVerses(), DisplaySelectPane.getNumRankedVerses());
        sliderRank.setMajorTickSpacing(DisplaySelectPane.getMaxNumRankedVerses() / 5);
        sliderRank.setMinorTickSpacing(DisplaySelectPane.getMaxNumRankedVerses() / 20);
        sliderRank.setLabelTable(createSliderLabels());
        sliderRank.setPaintTicks(true);
        sliderRank.setPaintLabels(true);
        sliderRank.setVisible(false);
        sliderRank.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = source.getValue();
                if (source.getValueIsAdjusting()) {
                    setLabelRank(val);
                } else {
                    DisplaySelectPane.setNumRankedVerses(val);
                }
            }
        });

        // TRANSLATOR: Heading for section allowing user to restrict search to parts of the Bible.
        chkRestrict = new JCheckBox(actions.addAction("HeadRestrict", BibleDesktopMsg.gettext("Restrict search to parts of the Bible")));
        chkRestrict.setBackground(headBG);
        chkRestrict.setForeground(headFG);
        chkRestrict.setFont(headFont);

        // TRANSLATOR: Label for a dropdown with preset verse ranges for searching.
        lblPresets = CWLabel.createJLabel(BibleDesktopMsg.gettext("Preset Ranges:"));
        lblPresets.setVisible(false);
        cboPresets = new JComboBox(presets);
        cboPresets.setVisible(false);
        cboPresets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                updatePreset();
            }
        });

        // TRANSLATOR: Label for an input box for searching only in the specified verses
        // This is filled in when the user enters input or picks an entry from the Preset Ranges dropdown.
        // When the user enters input the dropdown is adjusted to show the last entry.
        lblRestrict = CWLabel.createJLabel(BibleDesktopMsg.gettext("Include these verses:"));
        lblRestrict.setVisible(false);
        txtRestrict = new JTextField();
        txtRestrict.setVisible(false);
        txtRestrict.getDocument().addDocumentListener(this);
        // TRANSLATOR: Button to bring up the verse selection dialog
        btnRestrict = new JButton(actions.addAction("RestrictSelect", BibleDesktopMsg.gettext("Select")));
        btnRestrict.setVisible(false);

        // TRANSLATOR: Heading for section allowing user to specify Hebrew and Greek Strong's Numbers to include or exclude from search
        // Not currently implemented.
        chkHebGrk = new JCheckBox(actions.addAction("HeadOriginal", BibleDesktopMsg.gettext("Contains Strong's Hebrew and Greek")));
        chkHebGrk.setBackground(headBG);
        chkHebGrk.setForeground(headFG);
        chkHebGrk.setFont(headFont);

        // TRANSLATOR: Label for an input box for searching for verses containing Hebrew Strong's Numbers.
        // Not currently implemented.
        lblHebInc = CWLabel.createJLabel(BibleDesktopMsg.gettext("Includes Hebrew Numbers:"));
        lblHebInc.setVisible(false);
        txtHebInc = new JTextField();
        txtHebInc.setVisible(false);
        txtHebInc.getDocument().addDocumentListener(this);

        // TRANSLATOR: Label for an input box for searching for verses not containing Hebrew Strong's Numbers.
        // Not currently implemented.
        lblHebExc = CWLabel.createJLabel(BibleDesktopMsg.gettext("Excludes Hebrew Numbers:"));
        lblHebExc.setVisible(false);
        txtHebExc = new JTextField();
        txtHebExc.setVisible(false);
        txtHebExc.getDocument().addDocumentListener(this);

        // TRANSLATOR: Label for an input box for searching for verses containing Greek Strong's Numbers.
        // Not currently implemented.
        lblGrkInc = CWLabel.createJLabel(BibleDesktopMsg.gettext("Includes Greek Numbers:"));
        lblGrkInc.setVisible(false);
        txtGrkInc = new JTextField();
        txtGrkInc.setVisible(false);
        txtGrkInc.getDocument().addDocumentListener(this);

        // TRANSLATOR: Label for an input box for searching for verses not containing Greek Strong's Numbers.
        // Not currently implemented.
        lblGrkExc = CWLabel.createJLabel(BibleDesktopMsg.gettext("Excludes Greek Numbers:"));
        lblGrkExc.setVisible(false);
        txtGrkExc = new JTextField();
        txtGrkExc.setVisible(false);
        txtGrkExc.getDocument().addDocumentListener(this);

        // TRANSLATOR: Heading for section allowing user to specify time boundaries on search.
        // Not currently implemented. Not sure it ever will be.
        chkTime = new JCheckBox(actions.addAction("HeadTime", BibleDesktopMsg.gettext("Narrow search by time period")));
        chkTime.setBackground(headBG);
        chkTime.setForeground(headFG);
        chkTime.setFont(headFont);

        // TRANSLATOR: Label for an input box for a timeline search for verses written after the ones given. 
        // Not currently implemented. Not sure it ever will be.
        lblAfter = CWLabel.createJLabel(BibleDesktopMsg.gettext("Restrict to verses written after:"));
        lblAfter.setVisible(false);
        txtAfter = new JTextField();
        txtAfter.setVisible(false);
        txtAfter.getDocument().addDocumentListener(this);

        // TRANSLATOR: Label for an input box for a timeline search for verses written before the ones given.
        // Not currently implemented. Not sure it ever will be.
        lblBefore = CWLabel.createJLabel(BibleDesktopMsg.gettext("Restrict to verses written before:"));
        lblBefore.setVisible(false);
        txtBefore = new JTextField();
        txtBefore.setVisible(false);
        txtBefore.getDocument().addDocumentListener(this);

        // TRANSLATOR: Label for section showing user's search.
        chkSummary = new JCheckBox(actions.addAction("HeadSummary", BibleDesktopMsg.gettext("Show quick search syntax")));
        chkSummary.setBackground(headBG);
        chkSummary.setForeground(headFG);
        chkSummary.setFont(headFont);
        // TRANSLATOR: Label for a text box that shows, dynamically, the search syntax as the other boxes are filled in.
        lblSummary = CWLabel.createJLabel(BibleDesktopMsg.gettext("Quick search syntax:"));
        lblSummary.setVisible(false);
        txtSummary = new JTextArea();
        txtSummary.setBackground(SystemColor.control);
        txtSummary.setLineWrap(true);
        txtSummary.setEditable(false);
        txtSummary.setRows(2);
        scrSummary = new CWScrollPane(txtSummary);
        scrSummary.setVisible(false);

        // TRANSLATOR: Button to initiate the close the window and initiate search.
        btnGo = new JButton(actions.addAction("Done", BibleDesktopMsg.gettext("Search")));

        this.setBorder(BorderFactory.createLineBorder(SystemColor.control, 5));
        this.setLayout(new GridBagLayout());
        int gridy = 0;
        this.add(lblHeading,  new GridBagConstraints(0, ++gridy, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblPhrase,   new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtPhrase,   new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblIncludes, new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtIncludes, new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblExcludes, new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtExcludes, new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblSpell,    new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtSpell,    new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblStartsWith, new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtStartsWith, new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(chkRank,     new GridBagConstraints(0, ++gridy, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblRank,     new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(sliderRank,  new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(chkRestrict, new GridBagConstraints(0, ++gridy, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblPresets,  new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(cboPresets,  new GridBagConstraints(1,   gridy, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(lblRestrict, new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtRestrict, new GridBagConstraints(1,   gridy, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 5, 2, 5), 0, 0));
        this.add(btnRestrict, new GridBagConstraints(2,   gridy, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,       new Insets(0, 0, 0, 5), 0, 0));
//        this.add(chkHebGrk,   new GridBagConstraints(0, ++gridy, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        this.add(lblHebInc,   new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtHebInc,   new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblHebExc,   new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtHebExc,   new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblGrkInc,   new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtGrkInc,   new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblGrkExc,   new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtGrkExc,   new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
//        this.add(chkTime,     new GridBagConstraints(0, ++gridy, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
//        this.add(lblAfter,    new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtAfter,    new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
//        this.add(lblBefore,   new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
//        this.add(txtBefore,   new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
        this.add(chkSummary,  new GridBagConstraints(0, ++gridy, 3, 1, 0.0, 0.0, GridBagConstraints.LINE_START,   GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.add(lblSummary,  new GridBagConstraints(0, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END,   GridBagConstraints.NONE,       new Insets(0, 0, 0, 0), 0, 0));
        this.add(scrSummary,  new GridBagConstraints(1,   gridy, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,       new Insets(2, 5, 2, 5), 0, 0));
        this.add(btnGo,       new GridBagConstraints(2, ++gridy, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,  GridBagConstraints.NONE,       new Insets(10, 0, 5, 5), 0, 0));
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Open us in a new (optionally modal) dialog window
     * 
     * @param parent
     *            The component to which to attach the new dialog
     * @param title
     *            The title for the new dialog
     */
    public String showInDialog(Component parent, String title, boolean modal, String search) {
        txtSummary.setText(search);

        Frame root = JOptionPane.getFrameForComponent(parent);
        dlgMain = new JDialog(root);

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        bailout = true;

        ActionListener closer = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
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
        GuiUtil.centerOnScreen(dlgMain);
        GuiUtil.applyDefaultOrientation(dlgMain);
        dlgMain.setVisible(true);

        if (bailout) {
            return null;
        }

        return txtSummary.getText();
    }

    public boolean isRanked() {
        return chkRank.isSelected();
    }

    public final void setLabelRank(int val) {
        if (val == 0) {
            // TRANSLATOR: Dynamic label for prioritization slider on Advanced Search.
            // The user has chosen 0, which means to show all verses.
            // This used to be "Show {0} verses:" and {0} was a placeholder for the English word "All".
            lblRank.setText(shaper.shape(BibleDesktopMsg.gettext("Show all verses:", "All")));
        } else if (val == 1) {
            // TRANSLATOR: Dynamic label for prioritization slider on Advanced Search.
            // The user has chosen 1, which means to show one verse, presumably the one that best satisfies the search.
            lblRank.setText(shaper.shape(BibleDesktopMsg.gettext("Show best verse:")));
        } else {
            // TRANSLATOR: Dynamic label for prioritization slider on Advanced Search.
            // The user has chosen a number other than 0 or 1. 
            lblRank.setText(shaper.shape(BibleDesktopMsg.gettext("Show {0} verses:", Integer.valueOf(val))));
        }
    }

    /**
     * Someone clicked the rank check button
     */
    public void doHeadRank() {
        boolean visible = chkRank.isSelected();

        lblRank.setVisible(visible);
        sliderRank.setVisible(visible);

        if (dlgMain != null) {
            dlgMain.pack();
        }
    }

    /**
     * Someone clicked the restrict toggle button
     */
    public void doHeadRestrict() {
        boolean visible = chkRestrict.isSelected();

        lblPresets.setVisible(visible);
        cboPresets.setVisible(visible);
        lblRestrict.setVisible(visible);
        txtRestrict.setVisible(visible);
        btnRestrict.setVisible(visible);

        if (dlgMain != null) {
            dlgMain.pack();
        }
    }

    /**
     * Someone clicked the restrict toggle button
     */
    public void doHeadSummary() {
        boolean visible = chkSummary.isSelected();

        lblSummary.setVisible(visible);
        scrSummary.setVisible(visible);

        if (dlgMain != null) {
            dlgMain.pack();
        }
    }

    /**
     * Someone clicked the original strongs toggle button
     */
    public void doHeadOriginal() {
        boolean visible = chkHebGrk.isSelected();

        lblHebInc.setVisible(visible);
        txtHebInc.setVisible(visible);
        lblHebExc.setVisible(visible);
        txtHebExc.setVisible(visible);
        lblGrkInc.setVisible(visible);
        txtGrkInc.setVisible(visible);
        lblGrkExc.setVisible(visible);
        txtGrkExc.setVisible(visible);

        if (dlgMain != null) {
            dlgMain.pack();
        }
    }

    /**
     * Someone clicked the original strongs toggle button
     */
    public void doHeadTime() {
        boolean visible = chkTime.isSelected();

        lblBefore.setVisible(visible);
        txtBefore.setVisible(visible);
        lblAfter.setVisible(visible);
        txtAfter.setVisible(visible);

        if (dlgMain != null) {
            dlgMain.pack();
        }
    }

    /**
     *
     */
    public void doRestrictSelect() {
        if (dlgSelect == null) {
            dlgSelect = new PassageSelectionPane();
        }

        // TRANSLATOR: This is the title to the dialog allowing a user to select passages for a restricted search.
        String passg = dlgSelect.showInDialog(this, BibleDesktopMsg.gettext("Select Passages to Restrict Search to"), true, txtRestrict.getText());
        if (passg != null) {
            cboPresets.setSelectedItem(presets[presets.length - 1]);
            txtRestrict.setText(passg);
        }
    }

    /**
     * Someone clicked on OK
     */
    public void doDone() {
        bailout = false;
        dlgMain.dispose();
    }

    /**
     *
     */
    public final void updatePreset() {
        if (editingRestrict) {
            return;
        }

        String include = "";
        String preset = (String) cboPresets.getSelectedItem();
        if (preset != null) {
            int open = preset.indexOf(presetStart);
            int close = preset.indexOf(presetEnd, open + 1);

            if (open != -1 && close != -1) {
                include = preset.substring(open + 1, close);
            }
        }

        txtRestrict.setText(include);
    }

    /**
     * Regenerate the search string from the input boxes
     */
    private void updateSearchString() {
        StringBuilder search = new StringBuilder();

        String restrict = txtRestrict.getText();
        if (restrict != null && restrict.trim().length() > 0) {
            if (search.length() != 0) {
                search.append(SPACE);
            }

            search.append(SearchType.RANGE.decorate(restrict));
        }

        String phrase = txtPhrase.getText();
        if (phrase != null && phrase.trim().length() > 0) {
            if (search.length() != 0) {
                search.append(SPACE);
            }

            search.append(SearchType.PHRASE.decorate(phrase));
        }

        String includes = txtIncludes.getText();
        if (includes != null && includes.trim().length() > 0) {
            if (search.length() != 0) {
                search.append(SPACE);
            }

            search.append(SearchType.ALL_WORDS.decorate(includes));
        }

        String excludes = txtExcludes.getText();
        if (excludes != null && excludes.trim().length() > 0) {
            if (search.length() != 0) {
                search.append(SPACE);
            }

            search.append(SearchType.NOT_WORDS.decorate(excludes));
        }

        String spell = txtSpell.getText();
        if (spell != null && spell.trim().length() > 0) {
            if (search.length() != 0) {
                search.append(SPACE);
            }

            search.append(SearchType.SPELL_WORDS.decorate(spell));
        }

        String startsWith = txtStartsWith.getText();
        if (startsWith != null && startsWith.trim().length() > 0) {
            if (search.length() != 0) {
                search.append(SPACE);
            }

            search.append(SearchType.START_WORDS.decorate(startsWith));
        }

        txtSummary.setText(search.toString());

        // Check that the presets match the combo
        editingRestrict = true;
        boolean match = false;
        ComboBoxModel model = cboPresets.getModel();
        String find = presetStart + restrict + presetEnd;
        for (int i = 0; !match && i < model.getSize(); i++) {
            String element = (String) model.getElementAt(i);
            if (element.indexOf(find) != -1) {
                cboPresets.setSelectedIndex(i);
                match = true;
            }
        }

        if (!match) {
            cboPresets.setSelectedItem(presets[presets.length - 1]);
        }

        editingRestrict = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @seejavax.swing.event.DocumentListener#changedUpdate(javax.swing.event.
     * DocumentEvent)
     */
    public void changedUpdate(DocumentEvent ev) {
        updateSearchString();
    }

    /*
     * (non-Javadoc)
     * 
     * @seejavax.swing.event.DocumentListener#insertUpdate(javax.swing.event.
     * DocumentEvent)
     */
    public void insertUpdate(DocumentEvent ev) {
        updateSearchString();
    }

    /*
     * (non-Javadoc)
     * 
     * @seejavax.swing.event.DocumentListener#removeUpdate(javax.swing.event.
     * DocumentEvent)
     */
    public void removeUpdate(DocumentEvent ev) {
        updateSearchString();
    }

    // /**
    // * Temporary driver.
    // * @param args The command line arguments
    // */
    // public static void main(String[] args)
    // {
    // LookAndFeelUtil.initialize();
    // AdvancedSearchPane adv = new AdvancedSearchPane();
    //        String reply = adv.showInDialog(null, "Advanced Search", true, "test");
    // log.debug(reply);
    // System.exit(0);
    // }

    /**
     * Create the internationalized labels for the slider.
     * 
     * @return the labels
     */
    private Dictionary<Integer,JLabel> createSliderLabels() {
        Dictionary<Integer,JLabel> labels = new Hashtable<Integer,JLabel>();
        int max = DisplaySelectPane.getMaxNumRankedVerses();
        for (int i = 0; i <= max; i += 20) {
            Integer label = Integer.valueOf(i);
            labels.put(label, new JLabel(shaper.shape(label.toString()), SwingConstants.CENTER));
        }
        return labels;
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        actions = new ActionFactory(this);
        is.defaultReadObject();
    }

    /**
     * In our parsing we use space quite a lot and this ensures there is only one.
     */
    private static final String SPACE = " ";

    private String presetEnd;

    private String presetStart;

    /**
     * If escape was pressed we don't want to update the parent
     */
    protected boolean bailout;

    /**
     * The ActionFactory holding the actions used by this Component.
     */
    private transient ActionFactory actions;

    /**
     * The transformer of numeric representation.
     */
    private NumberShaper shaper;

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
    private JLabel lblSpell;
    private JTextField txtSpell;
    private JLabel lblStartsWith;
    private JTextField txtStartsWith;
    private JLabel lblHeading;
    private JCheckBox chkRank;
    private JLabel lblRank;
    private JSlider sliderRank;
    private JCheckBox chkRestrict;
    private JLabel lblRestrict;
    private JTextField txtRestrict;
    private JButton btnRestrict;
    private JButton btnGo;
    private JLabel lblPresets;
    private JComboBox cboPresets;
    protected JDialog dlgMain;
    private JCheckBox chkHebGrk;
    private JLabel lblHebInc;
    private JTextField txtHebInc;
    private JLabel lblHebExc;
    private JTextField txtHebExc;
    private JLabel lblGrkInc;
    private JTextField txtGrkInc;
    private JLabel lblGrkExc;
    private JTextField txtGrkExc;
    private JCheckBox chkTime;
    private JLabel lblBefore;
    private JTextField txtBefore;
    private JLabel lblAfter;
    private JTextField txtAfter;
    private JLabel lblSummary;
    private JCheckBox chkSummary;
    private JTextArea txtSummary;
    private JScrollPane scrSummary;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3977303234983245108L;
}
