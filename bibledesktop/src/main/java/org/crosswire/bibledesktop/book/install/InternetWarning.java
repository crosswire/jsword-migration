/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: FontChooser.java 1605 2007-08-03 21:34:46Z dmsmith $
 */
package org.crosswire.bibledesktop.book.install;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.util.WebWarning;

/**
 * InternetWarning is used to request permission of the user to access
 * the Internet. An option allows them to request that they are not asked
 * again. The default for the option is to be asked every time.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class InternetWarning extends JPanel
{
    /**
     * Create a WebWarningDialog.
     */
    public InternetWarning()
    {
        actions = new ActionFactory(InternetWarning.class, this);

        ItemListener changer = new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                fireStateChange();
            }
        };

        setLayout(new GridLayout(2, 1, 5, 5));

        add(new JLabel(WebWarning.instance().getWarning()));

        showWarning = new JCheckBox(WebWarning.instance().getShownWarningLabel());
        showWarning.setSelected(true);
        showWarning.addItemListener(changer);

        add(showWarning);
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Display a InternetWarning as a dialog
     */
    public static int showDialog(Component parent, String title)
    {
        final InternetWarning webWarning = new InternetWarning();

        JPanel buttons = new JPanel();
        JButton yesButton = new JButton(webWarning.actions.getAction("Yes")); //$NON-NLS-1$
        JButton noButton = new JButton(webWarning.actions.getAction("No")); //$NON-NLS-1$
        buttons.add(yesButton);
        buttons.add(noButton);

        Component root = SwingUtilities.getRoot(parent);

        JDialog dialog = (root instanceof JFrame)
                      ? new JDialog((JFrame) root, title, true)
                      : new JDialog((JDialog) root, title, true);

        webWarning.dialog = dialog;
        webWarning.choice = InternetWarning.DENIED;

        dialog.getRootPane().setDefaultButton(yesButton);

        Container content = dialog.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(webWarning, BorderLayout.NORTH);
        content.add(buttons, BorderLayout.SOUTH);
        dialog.pack();
        GuiUtil.centerOnScreen(dialog);
        GuiUtil.applyDefaultOrientation(dialog);
        dialog.setVisible(true);

        dialog.dispose();

        return webWarning.choice;
    }

    public void doYes()
    {
        dialog.setVisible(false);
        choice = InternetWarning.GRANTED;
    }

    public void doNo()
    {
        dialog.setVisible(false);
        choice = InternetWarning.DENIED;
    }

    /**
     * When something changes we must inform out listeners.
     */
    protected void fireStateChange()
    {
        WebWarning.instance().setShown(showWarning.isSelected());
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        actions = new ActionFactory(InternetWarning.class, this);
        is.defaultReadObject();
    }

    /**
     * Access to the Internet is granted.
     */
    public static final int    GRANTED = 0;

    /**
     * Access to the Internet is denied.
     */
    public static final int    DENIED  = 1;

    /*
     * The ActionFactory holding the actions used by this
     * EditSite.
     */
    private transient ActionFactory actions;

    /**
     * The user's choice.
     */
    protected int choice;

    /**
     * The dialog box
     */
    protected JDialog dialog;

    /**
     * Bold font?
     */
    protected JCheckBox showWarning;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978992071925250097L;
}
