package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.IntrospectionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.BeanPanel;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.install.InstallerFactory;

/**
 * An editor for the list of available update sites.
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
public class EditSitePane extends JPanel
{
    /**
     * This is the default constructor
     */
    public EditSitePane(InstallManager imanager)
    {
        this.imanager = imanager;
        userInitiated = true;

        init();
        setState(STATE_DISPLAY, null);
        select();
    }

    /**
     * GUI init
     */
    private void init()
    {
        actions = new ActionFactory(EditSitePane.class, this);

        lstSite = new JList(new InstallManagerListModel(imanager));
        JScrollPane scrSite = new JScrollPane();
        scrSite.add(lstSite, null);
        scrSite.getViewport().add(lstSite, null);
        lstSite.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstSite.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                select();
            }
        });

        JButton btnAdd = new JButton(actions.getAction(ADD));
        JButton btnEdit = new JButton(actions.getAction(EDIT));
        JButton btnDelete = new JButton(actions.getAction(DELETE));

        JPanel pnlBtn1 = new JPanel();
        pnlBtn1.add(btnAdd, null);
        pnlBtn1.add(btnEdit, null);
        pnlBtn1.add(btnDelete, null);

        JPanel pnlSite = new JPanel();
        pnlSite.setLayout(new BorderLayout());
        pnlSite.add(scrSite, BorderLayout.CENTER);
        pnlSite.add(pnlBtn1, BorderLayout.SOUTH);

        txtName = new JTextField();
        txtName.setColumns(10);
        txtName.getDocument().addDocumentListener(new DocumentListener()
        {
            public void changedUpdate(DocumentEvent ev)
            {
                siteUpdate();
            }

            public void insertUpdate(DocumentEvent ev)
            {
                siteUpdate();
            }

            public void removeUpdate(DocumentEvent ev)
            {
                siteUpdate();
            }
        });

        JLabel lblName = actions.createJLabel(NAME);
        lblName.setLabelFor(txtName);

        cboType = new JComboBox(new InstallerFactoryComboBoxModel(imanager));
        cboType.setEditable(false);
        cboType.setSelectedIndex(0);
        cboType.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                newType();
            }
        });

        JLabel lblType = actions.createJLabel(TYPE);
        lblType.setLabelFor(cboType);

        lblMesg = new JLabel();
        lblMesg.setText(BLANK_STRING);

        JButton btnReset = new JButton(actions.getAction(RESET));

        JButton btnSave = new JButton(actions.getAction(SAVE));

        JPanel pnlBtn2 = new JPanel();
        pnlBtn2.add(btnSave, null);
        pnlBtn2.add(btnReset, null);

        pnlBean = new BeanPanel();
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new GridBagLayout());
        pnlMain.add(lblMesg, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        pnlMain.add(lblName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        pnlMain.add(txtName, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        pnlMain.add(lblType, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        pnlMain.add(cboType, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        pnlMain.add(new JSeparator(), new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        pnlMain.add(pnlBean, new GridBagConstraints(0, 4, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        pnlMain.add(pnlBtn2, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        JSplitPane sptMain = new JSplitPane();
        sptMain.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        sptMain.setResizeWeight(0.0);
        sptMain.add(pnlSite, JSplitPane.LEFT);
        sptMain.add(pnlMain, JSplitPane.RIGHT);

        this.setLayout(new BorderLayout());
        this.add(sptMain, BorderLayout.CENTER);

        btnClose = new JButton(actions.getAction(CLOSE));

        pnlAction = new JPanel();
        pnlAction.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlAction.setLayout(new FlowLayout(FlowLayout.RIGHT));
        pnlAction.add(btnClose, null);
    }

    /**
     * Open us in a new modal dialog window
     * @param parent The component to which to attach the new dialog
     */
    public void showInDialog(Component parent)
    {
        dlgMain = new JDialog(JOptionPane.getFrameForComponent(parent));

        ActionListener closer = new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                doClose();
            }
        };

        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

        dlgMain.getContentPane().setLayout(new BorderLayout());
        dlgMain.getContentPane().add(new JPanel(), BorderLayout.NORTH);
        dlgMain.getContentPane().add(pnlAction, BorderLayout.SOUTH);
        dlgMain.getContentPane().add(this, BorderLayout.CENTER);
        dlgMain.getContentPane().add(new JPanel(), BorderLayout.EAST);
        dlgMain.getContentPane().add(new JPanel(), BorderLayout.WEST);
        dlgMain.getRootPane().setDefaultButton(btnClose);
        dlgMain.getRootPane().registerKeyboardAction(closer, esc, JComponent.WHEN_IN_FOCUSED_WINDOW);
        dlgMain.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dlgMain.setTitle(Msg.EDIT_SITE_TITLE.toString());
        dlgMain.setModal(true);

        GuiUtil.restrainedPack(dlgMain, 0.5f, 0.75f);
        GuiUtil.centerWindow(dlgMain);
        dlgMain.setVisible(true);
    }

    /**
     * Close the window, and save the install manager state
     */
    public void doClose()
    {
        imanager.save();
        dlgMain.dispose();
    }

    /**
     * The name field has been updated, so we need to check the entry is valid
     */
    protected void siteUpdate()
    {
        if (txtName.isEditable())
        {
            String name = txtName.getText().trim();
    
            if (name.length() == 0)
            {
                setState(STATE_EDIT_ERROR, Msg.MISSING_SITE.toString());
                return;
            }
    
            if (imanager.getInstaller(name) != null)
            {
                setState(STATE_EDIT_ERROR, Msg.DUPLICATE_SITE.toString());
                return;
            }
    
            setState(STATE_EDIT_OK, EMPTY_STRING);
        }
    }

    /**
     * The installer type combo box has been changed
     */
    protected void newType()
    {
        if (userInitiated)
        {    
            String type = (String) cboType.getSelectedItem();
            InstallerFactory ifactory = imanager.getInstallerFactory(type);
            Installer installer = ifactory.createInstaller();

            setBean(installer);
        }
    }

    /**
     * Someone has picked a new installer
     */
    protected void select()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            actions.getAction(EDIT).setEnabled(false);
            clear();
        }
        else
        {
            actions.getAction(EDIT).setEnabled(true);

            Installer installer = imanager.getInstaller(name);
            display(name, installer);
        }

        // Since setting the display undoes any work done to set the edit state
        // of the bean panel we need to redo it here. Since we are always in
        // display mode at this point, this is fairly easy.
        pnlBean.setEditable(false);
    }

    /**
     * Add a new installer to the list
     */
    public void doAdd()
    {
        newType();

        editName = null;
        editInstaller = null;

        // We need to call setState() to enable the text boxes so that
        // siteUpdate() works properly
        setState(STATE_EDIT_OK, null);
        siteUpdate();

        Window window = GuiUtil.getWindow(this);
        GuiUtil.restrainedRePack(window);
    }

    /**
     * Move the selected installer to the installer edit panel
     */
    public void doEdit()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            JOptionPane.showMessageDialog(this, Msg.NO_SELECTED_SITE.toString(), Msg.NO_SITE.toString(), JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        editName = name;
        editInstaller = imanager.getInstaller(name);

        imanager.removeInstaller(name);

        setState(STATE_EDIT_OK, null);
        siteUpdate();

        txtName.grabFocus();
    }

    /**
     * Delete the selected installer from the list (on the left hand side)
     */
    public void doDelete()
    {
        String name = (String) lstSite.getSelectedValue();
        if (name == null)
        {
            return;
        }

        if (JOptionPane.showConfirmDialog(this, Msg.CONFIRM_DELETE_SITE.toString(name), Msg.DELETE_SITE.toString(), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
        {
            imanager.removeInstaller(name);
        }

        clear();
        setState(STATE_DISPLAY, null);
    }

    /**
     * End editing the current installer
     */
    public void doReset()
    {
        if (editName != null)
        {
            imanager.addInstaller(editName, editInstaller);
        }

        clear();
        editName = null;
        editInstaller = null;

        setState(STATE_DISPLAY, EMPTY_STRING);
        select();
    }

    /**
     * Save the current installer to the list of installers
     */
    public void doSave()
    {
        String name = txtName.getText();
        Installer installer = (Installer) pnlBean.getBean();
        imanager.addInstaller(name, installer);

        clear();
        editName = null;
        editInstaller = null;

        setState(STATE_DISPLAY, EMPTY_STRING);
        select();
    }

    /**
     * Set the various gui elements depending on the current edit mode
     */
    private void setState(int state, String message)
    {
        switch (state)
        {
        case STATE_DISPLAY:
            actions.getAction(ADD).setEnabled(true);
            actions.getAction(DELETE).setEnabled(true);
            actions.getAction(EDIT).setEnabled(true);
            lstSite.setEnabled(true);

            actions.getAction(RESET).setEnabled(false);
            actions.getAction(SAVE).setEnabled(false);
            
            actions.getAction(CLOSE).setEnabled(true);

            txtName.setEditable(false);
            cboType.setEnabled(false);
            pnlBean.setEditable(false);

            lblMesg.setIcon(null);
            break;

        case STATE_EDIT_OK:
        case STATE_EDIT_ERROR:
            actions.getAction(ADD).setEnabled(false);
            actions.getAction(DELETE).setEnabled(false);
            actions.getAction(EDIT).setEnabled(false);
            lstSite.setEnabled(false);

            actions.getAction(RESET).setEnabled(true);
            actions.getAction(SAVE).setEnabled(state == STATE_EDIT_OK);
            pnlBean.setEditable(true);

            actions.getAction(CLOSE).setEnabled(false);

            txtName.setEditable(true);
            cboType.setEnabled(true);
            pnlBean.setEditable(true);

            // PENDING: lblMesg.setIcon(null);
            break;

        default:
            assert false : state;
        }

        if (message == null || message.trim().length() == 0)
        {
            lblMesg.setText(BLANK_STRING);
        }
        else
        {
            lblMesg.setText(message);
        }
    }

    /**
     * Set the display in the RHS to the given installer
     */
    private void display(String name, Installer installer)
    {
        txtName.setText(name);

        String type = imanager.getFactoryNameForInstaller(installer);
        userInitiated = false;
        cboType.setSelectedItem(type);
        userInitiated = true;

        setBean(installer);
    }

    /**
     * Clear the display in the RHS of any installers
     */
    private void clear()
    {
        try
        {
            txtName.setText(EMPTY_STRING);
            pnlBean.setBean(null);
        }
        catch (IntrospectionException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Convenience method to allow us to change the type of the current
     * installer.
     * @param installer The new installer to introspect
     */
    private void setBean(Installer installer)
    {
        try
        {
            pnlBean.setBean(installer);
        }
        catch (IntrospectionException ex)
        {
            Reporter.informUser(this, ex);
        }

        Window window = GuiUtil.getWindow(this);
        GuiUtil.restrainedRePack(window);
    }

    private static final String ADD = "Add"; //$NON-NLS-1$
    private static final String EDIT = "Edit"; //$NON-NLS-1$
    private static final String DELETE = "Delete"; //$NON-NLS-1$
    private static final String NAME = "Name"; //$NON-NLS-1$
    private static final String TYPE = "Type"; //$NON-NLS-1$
    private static final String RESET = "Reset"; //$NON-NLS-1$
    private static final String SAVE = "Save"; //$NON-NLS-1$
    private static final String CLOSE = "Close"; //$NON-NLS-1$
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private static final String BLANK_STRING = " "; //$NON-NLS-1$

    /**
     * The state is viewing a site
     */
    private static final int STATE_DISPLAY = 0;

    /**
     * The state is editing a site (syntactically valid)
     */
    private static final int STATE_EDIT_OK = 1;

    /**
     * The state is editing a site (syntactically invalid)
     */
    private static final int STATE_EDIT_ERROR = 2;

    /**
     * The model that we are providing a view/controller for
     */
    private InstallManager imanager;

    /**
     * If we are editing an installer, we need to know it's original name
     * in case someone clicks cancel.
     */
    private String editName;

    /**
     * If we are editing an installer, we need to know it's original value
     * in case someone clicks cancel.
     */
    private Installer editInstaller;

    /**
     * Edits to the type combo box mean different things depending on
     * whether it was triggered by the user or the application.
     */
    private boolean userInitiated;

    /*
     * The ActionFactory holding the actions used by this
     * EditSite.
     */
    private ActionFactory actions;

    /*
     * GUI Components for the list of sites
     */
    private JList lstSite;

    /*
     * GUI Components for the site view/edit area
     */
    private JLabel lblMesg;
    private JTextField txtName;
    private JComboBox cboType;
    private BeanPanel pnlBean;

    /*
     * Components for the dialog box including the button bar at the bottom.
     * These are separaed in this way in case this component is reused in a
     * larger context.
     */
    protected JDialog dlgMain;
    private JButton btnClose;
    private JPanel pnlAction;
}