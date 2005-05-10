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
 * ID: $ID$
 */
package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.book.BookListCellRenderer;
import org.crosswire.bibledesktop.book.BooksComboBoxModel;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;

/**
 * Allows various maintenance procedures to be done on Bibles like
 * deletion, renaming, and viewing the notes that are associated with the
 * generation of a version.
 *
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class MaintenancePane extends EirPanel
{
	// I18N: This class has not been internationalized, because it is not used.
    /**
     * Create a new BibleMaintenance Panel
     */
    public MaintenancePane()
    {
        init();
        updateButtons();
    }

    /**
     * Ensure that the buttons are enabled correctly
     */
    protected final void updateButtons()
    {
        boolean selected = (lst_versions.getSelectedIndex() != -1);
        btn_remove.setEnabled(selected);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Version Maintenance", false); //$NON-NLS-1$
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        scr_versions.getViewport().add(lst_versions, null);
        lst_versions.setModel(mdl_versions);
        lst_versions.setCellRenderer(new BookListCellRenderer());
        lst_versions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst_versions.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                updateButtons();
            }
        });

        btn_add.setText("Add ..."); //$NON-NLS-1$
        btn_add.setMnemonic('A');
        btn_add.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                add();
            }
        });

        btn_remove.setText("Remove"); //$NON-NLS-1$
        btn_remove.setMnemonic('R');
        btn_remove.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                delete();
            }
        });

        lay_buttons.setAlignment(FlowLayout.RIGHT);
        pnl_buttons.setLayout(lay_buttons);
        pnl_buttons.add(btn_add, null);
        pnl_buttons.add(btn_remove, null);

        this.setLayout(new BorderLayout());
        this.add(scr_versions, BorderLayout.CENTER);
        this.add(pnl_buttons, BorderLayout.SOUTH);
    }

    /**
     * Create a new Bible
     */
    public void add()
    {
        GeneratorPane vergen = new GeneratorPane();
        vergen.showInFrame(GuiUtil.getFrame(this));
    }

    /**
     * Delete a selected Bible
     */
    public void delete()
    {
        try
        {
            Book book = getSelected();
            if (book == null)
            {
                JOptionPane.showMessageDialog(this,
                    "Please select a Bible to delete.", //$NON-NLS-1$
                    "Delete Bible", //$NON-NLS-1$
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete "+book.getName()+"?\nDeleted Books can not be recovered", //$NON-NLS-1$ //$NON-NLS-2$
                "Delete Bible", //$NON-NLS-1$
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                book.getDriver().delete(book);
            }
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * What is the selected Bible name?
     * @return The version name or null if none is selected
     */
    private Book getSelected()
    {
        return (Book) lst_versions.getSelectedValue();
    }

    /**
     * The version list scroller
     */
    private JScrollPane scr_versions = new JScrollPane();

    /**
     * The version list
     */
    private JList lst_versions = new JList();

    /**
     * The BiblesModel for the list
     */
    private BooksComboBoxModel mdl_versions = new BooksComboBoxModel(BookFilters.getBibles());

    /**
     * The button bar
     */
    private JPanel pnl_buttons = new JPanel();

    /**
     * View Notes button
     */
    private JButton btn_add = new JButton();

    /**
     * Delete Bible button
     */
    private JButton btn_remove = new JButton();

    /**
     * Layout for the button bar
     */
    private FlowLayout lay_buttons = new FlowLayout();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257284721162663473L;
}
