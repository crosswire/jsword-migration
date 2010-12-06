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
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * FontChooserBean allows the user to select a font in a similar way to a
 * FileSelectionDialog.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class FontChooser extends JPanel {
    /**
     * Create a FontChooser.
     */
    public FontChooser() {
        ItemListener changer = new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
                fireStateChange();
            }
        };

        font = DEFAULT_FONT.getFont();
        name = new FontNameComboBox();
        name.setModel(new CustomComboBoxModel());
        name.setRenderer(new CustomListCellRenderer());
        name.setSelectedItem(font.deriveFont(Font.PLAIN, RENDERED_FONT_SIZE));
        name.addItemListener(changer);

        size = new JComboBox();
        size.setRenderer(new NumberCellRenderer());
        for (int i = MIN_FONT_SIZE; i <= MAX_FONT_SIZE; i++) {
            size.addItem(Integer.valueOf(i));
        }

        size.setSelectedItem(Integer.valueOf(RENDERED_FONT_SIZE));
        size.addItemListener(changer);

        // TRANSLATOR: This is an option for a bold font.
        bold = new JCheckBox(UserMsg.gettext("Bold"));
        bold.setSelected(font.isBold());
        bold.addItemListener(changer);

        // TRANSLATOR: This is an option for an italic font.
        italic = new JCheckBox(UserMsg.gettext("Italic"));
        italic.setSelected(font.isItalic());
        italic.addItemListener(changer);

        setLayout(new GridLayout(2, 2, 5, 5));

        add(name);
        add(size);
        add(bold);
        add(italic);
        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Display a FontChooser in a dialog
     */
    public static Font showDialog(Component parent, String title, Font initial) {
        final FontChooser fontc = new FontChooser();

        Component root = SwingUtilities.getRoot(parent);

        fontc.dialog = (root instanceof JFrame) ? new JDialog((JFrame) root, title, true) : new JDialog((JDialog) root, title, true);

        Font font = (initial != null) ? initial : DEFAULT_FONT.getFont();
        fontc.name.setSelectedItem(font);
        fontc.bold.setSelected(font.isBold());
        fontc.italic.setSelected(font.isItalic());
        fontc.size.setSelectedItem(Integer.valueOf(font.getSize()));

        final ActionFactory actions = new ActionFactory(FontChooser.class, fontc);

        JButton ok = actions.createJButton("OK", new ActionListener()
                {
                    public void actionPerformed(ActionEvent ex) {
                        fontc.dialog.setVisible(false);
                    }
                });

        JButton cancel = actions.createJButton("Cancel", new ActionListener()
                {
                    public void actionPerformed(ActionEvent ex) {
                        fontc.dialog.setVisible(false);
                        fontc.font = null;
                    }
                });

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());
        buttons.add(ok);
        buttons.add(cancel);

        // TRANSLATOR: Label indicating that the user should select a font.
        fontc.setBorder(BorderFactory.createTitledBorder(UserMsg.gettext("Select Font")));

        fontc.dialog.getRootPane().setDefaultButton(ok);
        fontc.dialog.getContentPane().setLayout(new BorderLayout());
        fontc.dialog.getContentPane().add(fontc, BorderLayout.NORTH);
        fontc.dialog.getContentPane().add(buttons, BorderLayout.SOUTH);
        fontc.dialog.setSize(800, 500);
        fontc.dialog.pack();
        GuiUtil.centerOnScreen(fontc.dialog);
        GuiUtil.applyDefaultOrientation(fontc.dialog);
        fontc.dialog.setVisible(true);

        fontc.dialog.dispose();

        return fontc.font;
    }

    /**
     * Set the Font displayed
     * 
     * @param newFont
     *            The current Font
     */
    public void setStyle(Font newFont) {
        suppressEvents = true;

        if (newFont == null) {
            return;
        }

        CustomComboBoxModel model = (CustomComboBoxModel) name.getModel();
        model.setSelectedItem(newFont.deriveFont(Font.PLAIN, RENDERED_FONT_SIZE));

        bold.setSelected(newFont.isBold());
        italic.setSelected(newFont.isItalic());
        size.setSelectedItem(Integer.valueOf(newFont.getSize()));

        suppressEvents = false;
        fireStateChange();
    }

    /**
     * @return The currently selected font
     */
    public Font getStyle() {
        Font selected = (Font) name.getSelectedItem();

        if (selected == null) {
            return DEFAULT_FONT.getFont();
        }

        int font_style = (bold.isSelected() ? Font.BOLD : Font.PLAIN) | (italic.isSelected() ? Font.ITALIC : Font.PLAIN);
        int font_size = ((Integer) size.getSelectedItem()).intValue();

        return selected.deriveFont(font_style, font_size);
    }

    /**
     * When something changes we must inform out listeners.
     */
    protected void fireStateChange() {
        Font old = font;
        font = getStyle();

        if (!suppressEvents) {
            firePropertyChange(PROPERTY_STYLE, old, font);
        }
    }

    /**
     * Model for the font style drop down
     */
    static class CustomComboBoxModel extends AbstractListModel implements ComboBoxModel {
        /**
         * Create a custom data model for a JComboBox
         */
        protected CustomComboBoxModel() {
            String[] names = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

            fonts = new Font[names.length];

            for (int i = 0; i < fonts.length; i++) {
                // We need to exclude certain fonts that cause the JVM to crash.
                // BUG_PARADE(DMS): 6376296
                // It will be fixed in Java 1.6 (Mustang)
                if (names[i].equals("padmaa") || names[i].equals("Rekha") || names[i].indexOf("Lohit") > -1 || names[i].indexOf("aakar") > -1)
                {
                    continue;
                }

                // Add good fonts to total font listing
                fonts[fontCount++] = new Font(names[i], Font.PLAIN, RENDERED_FONT_SIZE);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
         */
        public void setSelectedItem(Object selection) {
            this.selection = selection;
            fireContentsChanged(this, -1, -1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ComboBoxModel#getSelectedItem()
         */
        public Object getSelectedItem() {
            return selection;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ListModel#getSize()
         */
        public int getSize() {
            return fontCount;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ListModel#getElementAt(int)
         */
        public Object getElementAt(int index) {
            return fonts[index];
        }

        /**
         * The total number of fonts. Note, this may be less than or equal to
         * fonts.length.
         */
        private int fontCount;

        /**
         * An array of the fonts themselves. Note the array is as big as the
         * total number of fonts in the system.
         */
        private Font[] fonts;

        /**
         * The currently selected item
         */
        private Object selection;

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258129150505071664L;
    }

    /**
     * An extension of JLabel that resets it's font so that it can be used to
     * render the items in a JComboBox
     */
    static class CustomListCellRenderer extends DefaultListCellRenderer {
        public CustomListCellRenderer() {
            GuiUtil.applyDefaultOrientation(this);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
         * .JList, java.lang.Object, int, boolean, boolean)
         */
        /* @Override */
        public Component getListCellRendererComponent(JList listbox, Object value, int index, boolean selected, boolean focus) {
            Font defaultFont = DEFAULT_FONT.getFont();
            if (value == null) {
                setText("<null>");
                setFont(defaultFont);
            } else {
                Font afont = (Font) value;
                setText(afont.getFamily());
                setFont(defaultFont); // afont); // Some fonts cannot display
                // their own name.
            }

            return this;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3256726195025358905L;
    }

    /**
     * An extension of JComboBox that selects a font in the combo based on it's
     * name, not object equivalence.
     */
    static class FontNameComboBox extends JComboBox {
        public void setSelectedItem(Object anObject) {
            if ((selectedItemReminder == null || !selectedItemReminder.equals(anObject)) && (anObject instanceof Font)) {
                String fontName = ((Font) anObject).getName();
                for (int i = 0; i < dataModel.getSize(); i++) {
                    Object element = dataModel.getElementAt(i);
                    if (element instanceof Font && (((Font) element).getName().equals(fontName))) {
                        super.setSelectedItem(element);
                        return;
                    }
                }
            }
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = -7394816349446551753L;
    }

    public static final String PROPERTY_STYLE = "style";

    /**
     * A label that we can use to get defaults
     */
    protected static final JLabel DEFAULT_FONT = new JLabel();

    /**
     * The dialog box
     */
    protected JDialog dialog;

    /**
     * The current font
     */
    protected Font font;

    /**
     * The minimum size of the font.
     */
    private static final int MIN_FONT_SIZE = 5;

    /**
     * The minimum size of the font.
     */
    private static final int MAX_FONT_SIZE = 72;

    /**
     * The default size of the rendered font
     */
    private static final int RENDERED_FONT_SIZE = 16;

    /**
     * The choice of font name
     */
    protected JComboBox name;

    /**
     * Bold font?
     */
    protected JCheckBox bold;

    /**
     * Italic font?
     */
    protected JCheckBox italic;

    /**
     * The font size
     */
    protected JComboBox size;

    /**
     * Are we doing some processing, that makes us not want to send events?
     */
    protected boolean suppressEvents;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3978992071925250097L;
}
