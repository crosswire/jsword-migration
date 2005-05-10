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
 * ID: $Id$
 */
package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilter;

/**
 * BookChooser is like JFileChooser except that it allows the user to
 * select one of the available Bibles.
 * 
 * @see gnu.gpl.Licence for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookChooser extends JPanel
{
	/**
     * Basic constructor
     */
    public BookChooser()
    {
        this(null);
    }

    /**
     * Basic constructor
     */
    public BookChooser(BookFilter filter)
    {
        bmod = new BooksListModel(filter);
        init();
    }

    /**
     * Initialize all the GUI components
     */
    private void init()
    {
        pnlBibles.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlBibles.setLayout(new BorderLayout());
        pnlBibles.add(scrBibles, BorderLayout.CENTER);
        scrBibles.setViewportView(lstBibles);
        lstBibles.setModel(bmod);
        lstBibles.setCellRenderer(new BookListCellRenderer());
        lstBibles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstBibles.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                selection();
            }
        });

        // I18N: migrate this to an ActionFactory
        btnOk.setText(Msg.CHOOSER_OK.toString());
        btnOk.setMnemonic(Msg.CHOOSER_OK.toString().charAt(0));
        btnOk.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                okPressed();
            }
        });
        btnOk.setEnabled(selected != null);
        btnOk.setDefaultCapable(true);

        // I18N: migrate this to an ActionFactory
        btnCancel.setText(Msg.CHOOSER_CANCEL.toString());
        btnCancel.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                cancelPressed();
            }
        });

        pnlButtons.setLayout(new FlowLayout());
        pnlButtons.add(btnOk);
        pnlButtons.add(btnCancel);

        this.setLayout(new BorderLayout());
        this.add(pnlBibles, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
    }

    /**
     * Display the BookChooser in a modal dialog
     */
    public int showDialog(Component parent)
    {
        Frame frame = GuiUtil.getFrame(parent);

        dialog = new JDialog(frame, title, true);

        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(this, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                cancelPressed();
            }
        });

        dialog.setVisible(true);

        return reply;
    }

    /**
     * Sets the string that goes in the FileChooser window's title bar.
     * @see #getDialogTitle()
     */
    public void setDialogTitle(String title)
    {
        this.title = title;
    }

    /**
     * Gets the string that goes in the FileChooser's titlebar.
     * @see #setDialogTitle(String)
     */
    public String getDialogTitle()
    {
        return title;
    }

    /**
     * Returns the selected Book.
     * @return the selected Book
     */
    public Book getSelected()
    {
        return (Book) lstBibles.getSelectedValue();
    }

    /**
     * When the list selection changes
     */
    public void selection()
    {
        selected = (String) lstBibles.getSelectedValue();
        btnOk.setEnabled(selected != null);
    }

    /**
     * OK is selected
     */
    public void okPressed()
    {
        reply = APPROVE_OPTION;
        dialog.setVisible(false);
    }

    /**
     * Cancel is selected
     */
    public void cancelPressed()
    {
        reply = CANCEL_OPTION;
        dialog.setVisible(false);
    }

    /**
     * Return value if cancel is chosen
     */
    public static final int CANCEL_OPTION = 1;

    /**
     * Return value if approve (yes, ok) is chosen
     */
    public static final int APPROVE_OPTION = 0;

    /**
     * Return value if an error occured
     */
    public static final int ERROR_OPTION = -1;

    /**
     * The name of the selected Bible
     */
    private String selected = null;

    /**
     * The way the dialog was closed
     */
    private int reply = CANCEL_OPTION;

    /**
     * The title of the dialog
     */
    private String title = Msg.CHOOSER_TITLE.toString();

    /**
     * The Bible list model
     */
    private BooksListModel bmod = null;

    /* GUI Componenets */
    private JDialog dialog;
    private JPanel pnlBibles = new JPanel();
    private JScrollPane scrBibles = new JScrollPane();
    private JList lstBibles = new JList();

    private JPanel pnlButtons = new JPanel();
    private JButton btnOk = new JButton();
    private JButton btnCancel = new JButton();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258689918547998773L;
}
