package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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

import org.crosswire.bibledesktop.passage.PassageListModel;
import org.crosswire.bibledesktop.passage.WholeBibleTreeModel;
import org.crosswire.bibledesktop.passage.WholeBibleTreeNode;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.passage.KeyFactory;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageKeyFactory;
import org.crosswire.jsword.passage.PassageListType;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A JPanel (or dialog) that presents a interactive GUI way to select passages.
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
public class PassageSelectionPane extends JPanel
{
    /**
     * Constructor for PassageSelectionPane.
     */
    public PassageSelectionPane()
    {
        icoGood = GuiUtil.getIcon("toolbarButtonGraphics/general/About24.gif"); //$NON-NLS-1$
        icoBad = GuiUtil.getIcon("toolbarButtonGraphics/general/Stop24.gif"); //$NON-NLS-1$

        init();
    }

    /**
     * GUI init
     */
    private void init()
    {
        actions = new ActionFactory(PassageSelectionPane.class, this);

        JLabel lblAll = actions.createJLabel(BIBLE_TREE);
        JLabel lblSel = actions.createJLabel(SELECTED_VERSES);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridBagLayout());
        this.add(lblAll, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 10, 5, 5), 0, 0));
        this.add(createScrolledTree(lblAll), new GridBagConstraints(0, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 10, 10, 2), 0, 0));
        this.add(new JPanel(), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(new JButton(actions.getAction(DELETE)), new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(new JButton(actions.getAction(ADD)), new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        this.add(new JPanel(), new GridBagConstraints(1, 4, 1, 1, 0.0, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lblSel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5, 10), 0, 0));
        this.add(createScrolledList(lblSel), new GridBagConstraints(2, 1, 1, 4, 0.5, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 2, 10, 10), 0, 0));
        this.add(createMessageLabel(), new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 5, 10), 0, 0));
        this.add(createDisplayPanel(), new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
    }

    /**
     *
     */
    private Component createScrolledTree(JLabel label)
    {
        treAll = new JTree();
        treAll.setModel(new WholeBibleTreeModel());
        treAll.setShowsRootHandles(true);
        treAll.setRootVisible(false);
        treAll.putClientProperty("JTree.lineStyle", "Angled");  //$NON-NLS-1$//$NON-NLS-2$
        treAll.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                treeSelected();
            }
        });

        label.setLabelFor(treAll);

        return new JScrollPane(treAll);
    }

    /**
     *
     */
    private Component createScrolledList(JLabel label)
    {
        lstSel = new JList();
        lstSel.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                listSelected();
            }
        });

        label.setLabelFor(lstSel);

        return new JScrollPane(lstSel);
    }

    /**
     *
     */
    private Component createDisplayPanel()
    {
        txtDisplay = new JTextField();
        txtDisplay.getDocument().addDocumentListener(new CustomDocumentEvent());

        JLabel lblDisplay = actions.createJLabel(VERSES);
        lblDisplay.setLabelFor(txtDisplay);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(txtDisplay, BorderLayout.CENTER);
        panel.add(lblDisplay, BorderLayout.WEST);
        return panel;
    }

    /**
     *
     */
    private Component createMessageLabel()
    {
        lblMessage = new JLabel();

        return lblMessage;
    }

    /**
     * Called whenever the passage changes to update the text box.
     */
    protected void copyListToText()
    {
        if (changing)
        {
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
    protected void copyTextToList()
    {
        if (changing)
        {
            return;
        }

        changing = true;
        String refstr = txtDisplay.getText();

        try
        {
            Passage temp = (Passage) keyf.getKey(refstr);
            ref.clear();
            ref.addAll(temp);

            setValidPassage(true);
            updateMessageSummary();
        }
        catch (NoSuchKeyException ex)
        {
            setValidPassage(false);
            updateMessage(ex);
        }
        changing = false;
    }

    /**
     * Update the UI when the validity of the passage changes
     * @param valid
     */
    private void setValidPassage(boolean valid)
    {
        lstSel.setEnabled(valid);
        treAll.setEnabled(valid);
        actions.getAction(ADD).setEnabled(valid);
        actions.getAction(DELETE).setEnabled(valid);
    }

    /**
     * Write out an error message to the message label
     * @param ex
     */
    private void updateMessage(NoSuchKeyException ex)
    {
        lblMessage.setText(Msg.ERROR.toString(ex.getMessage()));
        lblMessage.setIcon(icoBad);
    }

    /**
     * Write out an summary message to the message label
     */
    private void updateMessageSummary()
    {
        lblMessage.setText(Msg.SUMMARY.toString(ref.getOverview()));
        lblMessage.setIcon(icoGood);
    }

    /**
     * Open us in a new (optionally modal) dialog window
     * @param parent The component to which to attach the new dialog
     * @param title The title for the new dialog
     * @param modal
     */
    public String showInDialog(Component parent, String title, boolean modal, String refstr)
    {
        try
        {
            ref = (Passage) keyf.getKey(refstr);

            txtDisplay.setText(refstr);
            lstSel.setModel(new PassageListModel(ref, PassageListType.RANGES, RestrictionType.CHAPTER));

            ref.addPassageListener(new CustomPassageListener());
            updateMessageSummary();
        }
        catch (NoSuchKeyException ex)
        {
            setValidPassage(false);
            updateMessage(ex);
        }

        // Make sure the add/delete buttons start right
        treeSelected();
        listSelected();

        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));
        JPanel pnlAction = new JPanel();
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        bailout = true;

        JButton btnGo = new JButton(actions.getAction(DONE));

        pnlAction.setLayout(new BorderLayout());
        pnlAction.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 20));
        pnlAction.add(btnGo, BorderLayout.EAST);

        ActionListener closer = new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
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

        GuiUtil.restrainedPack(dlgMain, 0.5f, 0.75f);
        GuiUtil.centerWindow(dlgMain);
        dlgMain.setVisible(true);

        if (bailout)
        {
            return null;
        }
        return txtDisplay.getText();
    }

    /**
     * Add from the tree to the list
     */
    public void doAddVerse()
    {
        TreePath[] selected = treAll.getSelectionPaths();
        if (selected != null)
        {
            for (int i = 0; i < selected.length; i++)
            {
                WholeBibleTreeNode node = (WholeBibleTreeNode) selected[i].getLastPathComponent();
                VerseRange range = node.getVerseRange();
                ref.add(range);
            }
        }
    }

    /**
     * Remove the selected items from the list
     */
    public void doDeleteVerse()
    {
        Object[] selected = lstSel.getSelectedValues();
        if (selected != null)
        {
            for (int i = 0; i < selected.length; i++)
            {
                VerseRange range = (VerseRange) selected[i];
                ref.remove(range);
            }
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
     * The tree selection has changed
     */
    protected void treeSelected()
    {
        TreePath[] selected = treAll.getSelectionPaths();
        actions.getAction(ADD).setEnabled(selected != null && selected.length > 0);
    }

    /**
     * List selection has changed
     */
    protected void listSelected()
    {
        Object[] selected = lstSel.getSelectedValues();
        actions.getAction(DELETE).setEnabled(selected != null && selected.length > 0);
    }

    private static final String BIBLE_TREE = "BibleTree"; //$NON-NLS-1$
    private static final String ADD = "AddVerse"; //$NON-NLS-1$
    private static final String DELETE = "DeleteVerse"; //$NON-NLS-1$
    private static final String SELECTED_VERSES = "SelectedVerses"; //$NON-NLS-1$
    private static final String VERSES = "Verses"; //$NON-NLS-1$
    private static final String DONE = "Done"; //$NON-NLS-1$

    /**
     * To convert strings into Biblical keys
     */
    protected KeyFactory keyf = PassageKeyFactory.instance();

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
    private ActionFactory actions;

    /*
     * GUI Components
     */
    private Icon icoGood;
    private Icon icoBad;
    private JTree treAll;
    private JList lstSel;
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
    private class CustomDocumentEvent implements DocumentListener
    {
        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
         */
        public void insertUpdate(DocumentEvent ev)
        {
            copyTextToList();
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
         */
        public void removeUpdate(DocumentEvent ev)
        {
            copyTextToList();
        }

        /* (non-Javadoc)
         * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
         */
        public void changedUpdate(DocumentEvent ev)
        {
            copyTextToList();
        }
    }

    /**
     * To update the textbox when the passage changes
     */
    private class CustomPassageListener implements PassageListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesAdded(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesAdded(PassageEvent ev)
        {
            copyListToText();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesRemoved(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesRemoved(PassageEvent ev)
        {
            copyListToText();
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.passage.PassageListener#versesChanged(org.crosswire.jsword.passage.PassageEvent)
         */
        public void versesChanged(PassageEvent ev)
        {
            copyListToText();
        }
    }
}
