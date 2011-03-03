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
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.crosswire.bibledesktop.BibleDesktopMsg;
import org.crosswire.bibledesktop.passage.RangeListModel;
import org.crosswire.bibledesktop.passage.WholeBibleTreeModel;
import org.crosswire.bibledesktop.passage.WholeBibleTreeNode;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.CWLabel;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A JPanel (or dialog) that presents a interactive GUI way to select passages.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class PassageSelectionPane extends JPanel {
    /**
     * Constructor for PassageSelectionPane.
     */
    public PassageSelectionPane() {
        icoGood = GuiUtil.getIcon(GOOD_ICON);
        icoBad = GuiUtil.getIcon(BAD_ICON);

        init();
    }

    /**
     * GUI init
     */
    private void init() {
        actions = new ActionFactory(this);
        CWAction action;

        // I18N(DMS)
        JLabel lblAll = CWLabel.createJLabel(BibleDesktopMsg.gettext("All Verses"));
        // I18N(DMS)
        JLabel lblSel = CWLabel.createJLabel(BibleDesktopMsg.gettext("Selected Verses"));
        action = actions.addAction("DeleteVerse", BibleDesktopMsg.gettext("Remove <"));
        action.setTooltip(BibleDesktopMsg.gettext("Delete verses from the list selected."));
        JButton deleteButton = new JButton(action);
        action = actions.addAction("AddVerse", BibleDesktopMsg.gettext("Add >"));
        action.setTooltip(BibleDesktopMsg.gettext("Add verses to list selected."));
        JButton addButton = new JButton(action);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        this.add(lblAll, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 10, 5, 5), 0, 0));
        this.add(createScrolledTree(lblAll), new GridBagConstraints(0, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 10, 2), 0, 0));
        this.add(new JPanel(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(deleteButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(addButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(new JPanel(), new GridBagConstraints(1, 4, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lblSel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));
        this.add(createScrolledList(lblSel), new GridBagConstraints(2, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 10, 10), 0, 0));
        this.add(createMessageLabel(), new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
        this.add(createDisplayPanel(), new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     *
     */
    private Component createScrolledTree(JLabel label) {
        treAll = new JTree();
        treAll.setModel(new WholeBibleTreeModel());
        treAll.setShowsRootHandles(true);
        treAll.setRootVisible(false);
        treAll.putClientProperty("JTree.lineStyle", "Angled");
        treAll.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent ev) {
                treeSelected();
            }
        });

        label.setLabelFor(treAll);

        return new CWScrollPane(treAll);
    }

    /**
     *
     */
    private Component createScrolledList(JLabel label) {
        model = new RangeListModel(RestrictionType.CHAPTER);
        lstSel = new JList(model);
        lstSel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent ev) {
                listSelected();
            }
        });

        label.setLabelFor(lstSel);

        return new CWScrollPane(lstSel);
    }

    /**
     *
     */
    private Component createDisplayPanel() {
        txtDisplay = new JTextField();
        txtDisplay.getDocument().addDocumentListener(new CustomDocumentEvent());

        // I18N(DMS)
        JLabel lblDisplay = CWLabel.createJLabel(BibleDesktopMsg.gettext("Verses"));
        lblDisplay.setLabelFor(txtDisplay);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(txtDisplay, BorderLayout.CENTER);
        panel.add(lblDisplay, BorderLayout.LINE_START);
        return panel;
    }

    /**
     *
     */
    private Component createMessageLabel() {
        lblMessage = new JLabel();

        return lblMessage;
    }

    /**
     * Called whenever the passage changes to update the text box.
     */
    protected void copyListToText() {
        if (changing) {
            return;
        }

        changing = true;
        txtDisplay.setText(ref.getName());
        updateMessageSummary();
        changing = false;
    }

    /**
     * Called whenever the text box changes to update the list
     */
    protected void copyTextToList() {
        if (changing) {
            return;
        }

        changing = true;
        String refstr = txtDisplay.getText();

        try {
            Passage temp = (Passage) keyf.getKey(refstr);
            ref.clear();
            ref.addAll(temp);
            model.setPassage(ref);

            setValidPassage(true);
            updateMessageSummary();
        } catch (NoSuchKeyException ex) {
            setValidPassage(false);
            updateMessage(ex);
        }
        changing = false;
    }

    /**
     * Update the UI when the validity of the passage changes
     * 
     * @param valid
     */
    private void setValidPassage(boolean valid) {
        lstSel.setEnabled(valid);
        treAll.setEnabled(valid);
        actions.findAction("AddVerse").setEnabled(valid);
        actions.findAction("DeleteVerse").setEnabled(valid);
    }

    /**
     * Write out an error message to the message label
     * 
     * @param ex
     */
    private void updateMessage(NoSuchKeyException ex) {
        // TRANSLATOR: Error condition: An unexpected unknown error occurred.
        // Tell the user about it. {0} is a placeholder for the error that occurred.
        lblMessage.setText(BibleDesktopMsg.gettext("Error: {0}", ex.getMessage()));
        lblMessage.setIcon(icoBad);
    }

    /**
     * Write out an summary message to the message label
     */
    private void updateMessageSummary() {
        // TRANSLATOR: Output the Summary label followed by the passage
        // that the user has built using the Select Passage Wizard.
        // {0} is the placeholder for the passage reference.
        lblMessage.setText(BibleDesktopMsg.gettext("Summary: {0}", ref.getOverview()));
        lblMessage.setIcon(icoGood);
    }

    /**
     * Open us in a new (optionally modal) dialog window
     * 
     * @param parent
     *            The component to which to attach the new dialog
     * @param title
     *            The title for the new dialog
     * @param modal
     */
    public String showInDialog(Component parent, String title, boolean modal, String refstr) {
        try {
            ref = (Passage) keyf.getKey(refstr);

            txtDisplay.setText(refstr);

            ref.addPassageListener(new CustomPassageListener());
            updateMessageSummary();
        } catch (NoSuchKeyException ex) {
            setValidPassage(false);
            updateMessage(ex);
        }

        // Make sure the add/delete buttons start right
        treeSelected();
        listSelected();

        Frame root = JOptionPane.getFrameForComponent(parent);
        dlgMain = new JDialog(root);

        JPanel pnlAction = new JPanel();
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        bailout = true;

        CWAction action = actions.addAction("Done", BibleDesktopMsg.gettext("OK"));
        action.setTooltip(BibleDesktopMsg.gettext("Close this window."));
        JButton btnGo = new JButton(action);

        pnlAction.setLayout(new BorderLayout());
        pnlAction.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 20));
        pnlAction.add(btnGo, BorderLayout.LINE_END);

        ActionListener closer = new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                dlgMain.dispose();
            }
        };

        dlgMain.getContentPane().setLayout(new BorderLayout());
        dlgMain.getContentPane().add(this, BorderLayout.CENTER);
        dlgMain.getContentPane().add(pnlAction, BorderLayout.SOUTH);
        dlgMain.getRootPane().setDefaultButton(btnGo);
        dlgMain.getRootPane().registerKeyboardAction(closer, esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlgMain.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlgMain.setTitle(title);
        dlgMain.setModal(modal);

        GuiUtil.applyDefaultOrientation(dlgMain);
        GuiUtil.restrainedPack(dlgMain, 0.5f, 0.75f);
        GuiUtil.centerOnScreen(dlgMain);
        dlgMain.setVisible(true);

        if (bailout) {
            return null;
        }
        return txtDisplay.getText();
    }

    /**
     * Add from the tree to the list
     */
    public void doAddVerse() {
        TreePath[] selected = treAll.getSelectionPaths();
        if (selected != null) {
            for (int i = 0; i < selected.length; i++) {
                WholeBibleTreeNode node = (WholeBibleTreeNode) selected[i].getLastPathComponent();
                VerseRange range = node.getVerseRange();
                ref.add(range);
            }
            model.setPassage(ref);
        }
    }

    /**
     * Remove the selected items from the list
     */
    public void doDeleteVerse() {
        Object[] selected = lstSel.getSelectedValues();
        if (selected != null) {
            for (int i = 0; i < selected.length; i++) {
                VerseRange range = (VerseRange) selected[i];
                ref.remove(range);
            }
            model.setPassage(ref);
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
     * The tree selection has changed
     */
    /*private*/final void treeSelected() {
        TreePath[] selected = treAll.getSelectionPaths();
        actions.findAction("AddVerse").setEnabled(selected != null && selected.length > 0);
    }

    /**
     * List selection has changed
     */
    /*private*/final void listSelected() {
        Object[] selected = lstSel.getSelectedValues();
        actions.findAction("DeleteVerse").setEnabled(selected != null && selected.length > 0);
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        // We don't serialize views
        icoGood = GuiUtil.getIcon(GOOD_ICON);
        icoBad = GuiUtil.getIcon(BAD_ICON);
        keyf = PassageKeyFactory.instance();
        actions = new ActionFactory(this);
        is.defaultReadObject();
    }

    private static final String GOOD_ICON = "toolbarButtonGraphics/general/About24.gif";
    private static final String BAD_ICON = "toolbarButtonGraphics/general/Stop24.gif";

    /**
     * To convert strings into Biblical keys
     */
    protected transient KeyFactory keyf = PassageKeyFactory.instance();

    /**
     * If escape was pressed we don't want to update the parent
     */
    protected boolean bailout;

    /**
     * Prevent us getting in an event cascade loop
     */
    private boolean changing;

    /**
     * The passage we are editing
     */
    private Passage ref;

    /**
     * The ActionFactory holding the actions used by this Component.
     */
    private transient ActionFactory actions;

    /*
     * GUI Components
     */
    private transient Icon icoGood;
    private transient Icon icoBad;
    private JTree treAll;
    private JList lstSel;
    private RangeListModel model;
    private JTextField txtDisplay;
    private JLabel lblMessage;
    protected JDialog dlgMain;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3546920298944673072L;

    /**
     * Update the list whenever the textbox changes
     */
    class CustomDocumentEvent implements DocumentListener {
        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent ev) {
            copyTextToList();
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent ev) {
            copyTextToList();
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent ev) {
            copyTextToList();
        }
    }

    /**
     * To update the textbox when the passage changes
     */
    class CustomPassageListener implements PassageListener {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesAdded(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesAdded(PassageEvent ev) {
            copyListToText();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesRemoved(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesRemoved(PassageEvent ev) {
            copyListToText();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesChanged(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesChanged(PassageEvent ev) {
            copyListToText();
        }
    }
}
