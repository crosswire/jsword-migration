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
 * ID: $Id$
 */
package org.crosswire.common.config.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.crosswire.common.config.Choice;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.FieldLayout;
import org.crosswire.common.swing.MapTableModel;
import org.crosswire.common.swing.CWMsg;
import org.crosswire.common.util.Convert;

/**
 * A MapField allows editing of a Map in a JTable. It allows the user to specify
 * additional classes that extend the functionality of the program.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class MapField extends JPanel implements Field {
    /**
     * Create a PropertyHashtableField for editing Hashtables.
     */
    public MapField() {
        tableModel = new NamedMapTableModel();
        table = new JTable(tableModel);

        actions = new ActionFactory(this);

        JPanel buttons = new JPanel(new FlowLayout());

        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setPreferredScrollableViewportSize(new Dimension(30, 100));
        table.setColumnSelectionAllowed(false);

        JScrollPane scroll = new JScrollPane();
        scroll.setViewportView(table);

        buttons.add(new JButton(actions.addAction("Add", CWMsg.gettext("Add"))));
        buttons.add(new JButton(actions.addAction("Remove", CWMsg.gettext("Remove"))));
        buttons.add(new JButton(actions.addAction("Update", CWMsg.gettext("Update"))));

        Border title = BorderFactory.createTitledBorder(LimboMsg.COMPONENT_EDITOR.toString());
        Border pad = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        setBorder(BorderFactory.createCompoundBorder(title, pad));

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(buttons, BorderLayout.PAGE_END);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.config.swing.Field#setChoice(org.crosswire.common
     * .config.Choice)
     */
    public void setChoice(Choice param) {
        // superclass = ((MapChoice) param).getSuperClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#getValue()
     */
    public String getValue() {
        return tableModel.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#setValue(java.lang.String)
     */
    public void setValue(String value) {
        setMap(Convert.string2Hashtable(value, superclass));
    }

    /**
     * Set the current value using a Map
     * 
     * @param value
     *            The new text
     */
    public void setMap(Map value) {
        tableModel.setMap(value);
        table.setModel(tableModel);
        table.getColumnModel().getColumn(0).setWidth(15);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.crosswire.common.config.swing.Field#getComponent()
     */
    public JComponent getComponent() {
        return this;
    }

    /**
     * Pop up a dialog to allow editing of a new value
     */
    public void doAddEntry() {
        InputPane input = new InputPane();

        if (JOptionPane.showConfirmDialog(this, input, LimboMsg.NEW_CLASS.toString(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String newClass = input.classField.getText();
            String newName = input.nameField.getText();

            if (isValid(newClass)) {
                tableModel.add(newName, newClass);
            }
        }
    }

    /**
     * Pop up a dialog to allow editing of a current value
     */
    public void doUpdateEntry() {
        InputPane input = new InputPane();
        input.nameField.setText(currentKey());
        input.classField.setText(currentValue());

        if (JOptionPane.showConfirmDialog(this, input, LimboMsg.EDIT_CLASS.toString(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String newClass = input.classField.getText();
            String newName = input.nameField.getText();

            if (isValid(newClass)) {
                tableModel.update(currentKey(), newName, newClass);
            }
        }
    }

    /**
     * Delete the current value in the Map
     */
    public void doRemoveEntry() {
        tableModel.remove(currentKey());
    }

    /**
     * Create an instance of a class for the Map
     * 
     * @param name
     *            The name of the class to create
     * @return The instantiated object or null if the name is not valid
     */
    public boolean isValid(String name) {
        try {
            Class clazz = Class.forName(name);

            if (!superclass.isAssignableFrom(clazz)) {
                throw new ClassCastException(LimboMsg.BAD_SUPERCLASS.toString(new Object[] {
                        name, superclass
                }));
            }

            return true;
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, LimboMsg.CLASS_NOT_FOUND.toString(name));
        } catch (Exception ex) {
            ExceptionPane.showExceptionDialog(this, ex);
        }

        return false;
    }

    /**
     * What is the currently selected key?
     * 
     * @return The currently selected key
     */
    private String currentKey() {
        return (String) tableModel.getValueAt(table.getSelectedRow(), 0);
    }

    /**
     * What is the currently selected value?
     * 
     * @return The currently selected value
     */
    private String currentValue() {
        return (String) tableModel.getValueAt(table.getSelectedRow(), 1);
    }

    /**
     * A MapTableModel with named columns that is not ediatble
     */
    static class NamedMapTableModel extends MapTableModel {
        /**
         * 
         */
        protected NamedMapTableModel() {
            super(new Hashtable());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int col) {
            return (col == 0) ? LimboMsg.NAME.toString() : LimboMsg.CLASS.toString();
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257566217748427059L;
    }

    /**
     * The panel for a JOptionPane that allows editing a name/class combination.
     */
    static class InputPane extends JPanel {
        /**
         * 
         */
        protected InputPane() {
            super(new FieldLayout(10, 10));

            nameField = new JTextField();
            classField = new JTextField(20);

            add(new JLabel(LimboMsg.NAME + ":"));
            add(nameField);
            add(new JLabel(LimboMsg.CLASS + ":"));
            add(classField);

            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        }

        /**
         * To edit a name (Map key)
         */
        protected JTextField nameField;

        /**
         * To edit a class (Map value)
         */
        protected JTextField classField;

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257849861683296313L;
    }

    private static final String ADD = "AddMapEntry";
    private static final String REMOVE = "RemoveMapEntry";
    private static final String UPDATE = "UpdateMapEntry";

    private transient ActionFactory actions;

    /**
     * The TableModel that points the JTable at the Map
     */
    private NamedMapTableModel tableModel;

    /**
     * The Table - displays the Hashtble
     */
    private JTable table;

    /**
     * The class that everything must inherit from
     */
    private Class superclass;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257849861683296313L;
}
