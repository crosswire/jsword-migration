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
 * Copyright: 2008
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */
package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.UIManager;

/**
 * CWOptionPane is just like JOptionPane, but internationalize the button text
 * for some languages that Java does not handle, for which JSword has translations.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CWOptionPane extends JOptionPane
{

    /**
     * Creates a <code>CWOptionPane</code> with a test message.
     */
    public CWOptionPane()
    {
        this("CWOptionPane message", PLAIN_MESSAGE, DEFAULT_OPTION, null, null, null); //$NON-NLS-1$
    }

    /**
     * Creates a instance of <code>CWOptionPane</code> to display a
     * message using the 
     * plain-message message type and the default options delivered by
     * the UI.
     *
     * @param message the <code>Object</code> to display
     */
    public CWOptionPane(Object message)
    {
        this(message, PLAIN_MESSAGE, DEFAULT_OPTION, null, null, null);
    }

    /**
     * Creates an instance of <code>CWOptionPane</code> to display a message
     * with the specified message type and the default options,
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     */
    public CWOptionPane(Object message, int messageType)
    {
        this(message, messageType, DEFAULT_OPTION, null, null, null);
    }

    /**
     * Creates an instance of <code>CWOptionPane</code> to display a message
     * with the specified message type and options.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param optionType the options to display in the pane:
     *                  <code>DEFAULT_OPTION</code>, <code>YES_NO_OPTION</code>,
     *          <code>YES_NO_CANCEL_OPTION</code>,
     *                  <code>OK_CANCEL_OPTION</code>
     */
    public CWOptionPane(Object message, int messageType, int optionType)
    {
        this(message, messageType, optionType, null, null, null);
    }

    /**
     * Creates an instance of <code>CWOptionPane</code> to display a message
     * with the specified message type, options, and icon.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param optionType the options to display in the pane:
     *                  <code>DEFAULT_OPTION</code>, <code>YES_NO_OPTION</code>,
     *          <code>YES_NO_CANCEL_OPTION</code>,
     *                  <code>OK_CANCEL_OPTION</code>
     * @param icon the <code>Icon</code> image to display
     */
    public CWOptionPane(Object message, int messageType, int optionType, Icon icon)
    {
        this(message, messageType, optionType, icon, null, null);
    }

    /**
     * Creates an instance of <code>CWOptionPane</code> to display a message
     * with the specified message type, icon, and options.
     * None of the options is initially selected.
     * <p>
     * The options objects should contain either instances of
     * <code>Component</code>s, (which are added directly) or
     * <code>Strings</code> (which are wrapped in a <code>JButton</code>).
     * If you provide <code>Component</code>s, you must ensure that when the
     * <code>Component</code> is clicked it messages <code>setValue</code>
     * in the created <code>CWOptionPane</code>.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>, 
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param optionType the options to display in the pane:
     *                  <code>DEFAULT_OPTION</code>,
     *          <code>YES_NO_OPTION</code>,
     *          <code>YES_NO_CANCEL_OPTION</code>,
     *                  <code>OK_CANCEL_OPTION</code>
     * @param icon the <code>Icon</code> image to display
     * @param options  the choices the user can select
     */
    public CWOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options)
    {
        this(message, messageType, optionType, icon, options, null);

        GuiUtil.applyDefaultOrientation(this);
    }

    /**
     * Creates an instance of <code>CWOptionPane</code> to display a message
     * with the specified message type, icon, and options, with the 
     * initially-selected option specified.
     *
     * @param message the <code>Object</code> to display
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param optionType the options to display in the pane:
     *                  <code>DEFAULT_OPTION</code>,
     *          <code>YES_NO_OPTION</code>,
     *          <code>YES_NO_CANCEL_OPTION</code>,
     *                  <code>OK_CANCEL_OPTION</code>
     * @param icon the Icon image to display
     * @param options  the choices the user can select
     * @param initialValue the choice that is initially selected; if
     *          <code>null</code>, then nothing will be initially selected;
     *          only meaningful if <code>options</code> is used
     */
    public CWOptionPane(Object message, int messageType, int optionType, Icon icon, Object[] options, Object initialValue)
    {
        super(message, messageType, optionType, icon, CWOptionPane.fixOptions(options, optionType, messageType), initialValue);
    }

    /**
     * Shows a question-message dialog requesting input from the user. The 
     * dialog uses the default frame, which usually means it is centered on 
     * the screen. 
     *
     * @param message the <code>Object</code> to display
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showInputDialog(Object message) throws HeadlessException
    {
        return showInputDialog(null, message);
    }

    /**
     * Shows a question-message dialog requesting input from the user, with
     * the input value initialized to <code>initialSelectionValue</code>. The 
     * dialog uses the default frame, which usually means it is centered on 
     * the screen. 
     *
     * @param message the <code>Object</code> to display
     * @param initialSelectionValue the value used to initialize the input
     *                 field
     */
    public static String showInputDialog(Object message, Object initialSelectionValue)
    {
        return showInputDialog(null, message, initialSelectionValue);
    }

    /**
     * Shows a question-message dialog requesting input from the user
     * parented to <code>parentComponent</code>.
     * The dialog is displayed on top of the <code>Component</code>'s
     * frame, and is usually positioned below the <code>Component</code>. 
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *      dialog
     * @param message  the <code>Object</code> to display
     * @exception HeadlessException if
     *    <code>GraphicsEnvironment.isHeadless</code> returns
     *    <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showInputDialog(Component parentComponent, Object message) throws HeadlessException
    {
        return showInputDialog(parentComponent, message, "?", QUESTION_MESSAGE); //$NON-NLS-1$
    }

    /**
     * Shows a question-message dialog requesting input from the user and
     * parented to <code>parentComponent</code>. The input value will be
     * initialized to <code>initialSelectionValue</code>.
     * The dialog is displayed on top of the <code>Component</code>'s
     * frame, and is usually positioned below the <code>Component</code>.  
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *      dialog
     * @param message the <code>Object</code> to display
     * @param initialSelectionValue the value used to initialize the input
     *                 field
     */
    public static String showInputDialog(Component parentComponent, Object message, Object initialSelectionValue)
    {
        return (String) showInputDialog(parentComponent, message, "?", QUESTION_MESSAGE, null, null, initialSelectionValue); //$NON-NLS-1$
    }

    /**
     * Shows a dialog requesting input from the user parented to
     * <code>parentComponent</code> with the dialog having the title
     * <code>title</code> and message type <code>messageType</code>.
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *          dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the dialog
     *          title bar
     * @param messageType the type of message that is to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static String showInputDialog(Component parentComponent, Object message, String title, int messageType) throws HeadlessException
    {
        return (String) showInputDialog(parentComponent, message, title, messageType, null, null, null);
    }

    /**
     * Prompts the user for input in a blocking dialog where the
     * initial selection, possible selections, and all other options can
     * be specified. The user will able to choose from
     * <code>selectionValues</code>, where <code>null</code> implies the
     * user can input
     * whatever they wish, usually by means of a <code>JTextField</code>. 
     * <code>initialSelectionValue</code> is the initial value to prompt
     * the user with. It is up to the UI to decide how best to represent
     * the <code>selectionValues</code>, but usually a
     * <code>JComboBox</code>, <code>JList</code>, or
     * <code>JTextField</code> will be used.
     *
     * @param parentComponent  the parent <code>Component</code> for the
     *          dialog
     * @param message  the <code>Object</code> to display
     * @param title    the <code>String</code> to display in the
     *          dialog title bar
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon     the <code>Icon</code> image to display
     * @param selectionValues an array of <code>Object</code>s that
     *          gives the possible selections
     * @param initialSelectionValue the value used to initialize the input
     *                 field
     * @return user's input, or <code>null</code> meaning the user
     *          canceled the input
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue) throws HeadlessException
    {
        CWOptionPane pane = new CWOptionPane(message, messageType, OK_CANCEL_OPTION, icon, null, null);

        pane.setWantsInput(true);
        pane.setSelectionValues(selectionValues);
        pane.setInitialSelectionValue(initialSelectionValue);
        GuiUtil.applyDefaultOrientation(pane);

        int style = styleFromMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title, style);

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object value = pane.getInputValue();

        if (value == UNINITIALIZED_VALUE)
        {
            return null;
        }

        return value;
    }

    /**
     * Brings up an information-message dialog titled "Message".
     *
     * @param parentComponent determines the <code>Frame</code> in
     *      which the dialog is displayed; if <code>null</code>,
     *      or if the <code>parentComponent</code> has no
     *      <code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent, Object message) throws HeadlessException
    {
        showOptionDialog(parentComponent, message, "?", DEFAULT_OPTION, INFORMATION_MESSAGE, null, null, null); //$NON-NLS-1$
   }

    /**
     * Brings up a dialog that displays a message using a default
     * icon determined by the <code>messageType</code> parameter.
     *
     * @param parentComponent determines the <code>Frame</code>
     *      in which the dialog is displayed; if <code>null</code>,
     *      or if the <code>parentComponent</code> has no
     *      <code>Frame</code>, a default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType) throws HeadlessException
    {
        showOptionDialog(parentComponent, message, title, DEFAULT_OPTION, messageType, null, null, null);
    }

    /**
     * Brings up a dialog displaying a message, specifying all parameters.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *          dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param messageType the type of message to be displayed:
     *                  <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>,
     *          <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon      an icon to display in the dialog that helps the user
     *                  identify the kind of message that is being displayed
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon) throws HeadlessException
    {
        showOptionDialog(parentComponent, message, title, DEFAULT_OPTION, messageType, icon, null, null);
    }

    /**
     * Brings up a dialog with the options <i>Yes</i>,
     * <i>No</i> and <i>Cancel</i>; with the
     * title, <b>Select an Option</b>.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *          dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @return an integer indicating the option selected by the user
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent, Object message) throws HeadlessException
    {
        return showOptionDialog(parentComponent, message, "?", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, null, null); //$NON-NLS-1$
    }

    /**
     * Brings up a dialog where the number of choices is determined
     * by the <code>optionType</code> parameter.
     * 
     * @param parentComponent determines the <code>Frame</code> in which the
     *          dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                  <code>YES_NO_OPTION</code>, or
     *          <code>YES_NO_CANCEL_OPTION</code>
     * @return an int indicating the option selected by the user
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) throws HeadlessException
    {
        return showOptionDialog(parentComponent, message, title, optionType, QUESTION_MESSAGE, null, null, null);
    }

    /**
     * Brings up a dialog where the number of choices is determined
     * by the <code>optionType</code> parameter, where the
     * <code>messageType</code>
     * parameter determines the icon to display.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the Look and Feel.
     *
     * @param parentComponent determines the <code>Frame</code> in
     *          which the dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *                  default <code>Frame</code> is used.
     * @param message   the <code>Object</code> to display
     * @param title     the title string for the dialog
     * @param optionType an integer designating the options available
     *          on the dialog: <code>YES_NO_OPTION</code>,
     *          or <code>YES_NO_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is; 
     *                  primarily used to determine the icon from the pluggable
     *                  Look and Feel: <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>, 
     *                  <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @return an integer indicating the option selected by the user
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType) throws HeadlessException
    {
        return showOptionDialog(parentComponent, message, title, optionType, messageType, null, null, null);
    }

    /**
     * Brings up a dialog with a specified icon, where the number of 
     * choices is determined by the <code>optionType</code> parameter.
     * The <code>messageType</code> parameter is primarily used to supply
     * a default icon from the look and feel.
     *
     * @param parentComponent determines the <code>Frame</code> in which the
     *          dialog is displayed; if <code>null</code>,
     *          or if the <code>parentComponent</code> has no
     *          <code>Frame</code>, a 
     *          default <code>Frame</code> is used
     * @param message   the Object to display
     * @param title     the title string for the dialog
     * @param optionType an int designating the options available on the dialog:
     *                  <code>YES_NO_OPTION</code>,
     *          or <code>YES_NO_CANCEL_OPTION</code>
     * @param messageType an int designating the kind of message this is, 
     *                  primarily used to determine the icon from the pluggable
     *                  Look and Feel: <code>ERROR_MESSAGE</code>,
     *          <code>INFORMATION_MESSAGE</code>, 
     *                  <code>WARNING_MESSAGE</code>,
     *                  <code>QUESTION_MESSAGE</code>,
     *          or <code>PLAIN_MESSAGE</code>
     * @param icon      the icon to display in the dialog
     * @return an int indicating the option selected by the user
     * @exception HeadlessException if
     *   <code>GraphicsEnvironment.isHeadless</code> returns
     *   <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon) throws HeadlessException
    {
        return showOptionDialog(parentComponent, message, title, optionType, messageType, icon, null, null);
    }

    /**
     * Brings up a dialog with a specified icon, where the initial choice is
     * determined by the <code>initialValue</code> parameter and the number of
     * choices is determined by the <code>optionType</code> parameter.
     * <p>
     * If <code>optionType</code> is <code>YES_NO_OPTION</code>, or
     * <code>YES_NO_CANCEL_OPTION</code> and the <code>options</code>
     * parameter is <code>null</code>, then the options are supplied by the
     * look and feel.
     * <p>
     * The <code>messageType</code> parameter is primarily used to supply a
     * default icon from the look and feel.
     * 
     * @param parentComponent determines the <code>Frame</code> in which the
     *            dialog is displayed; if <code>null</code>, or if the
     *            <code>parentComponent</code> has no <code>Frame</code>, a
     *            default <code>Frame</code> is used
     * @param message the <code>Object</code> to display
     * @param title the title string for the dialog
     * @param optionType an integer designating the options available on the
     *            dialog: <code>YES_NO_OPTION</code>, or
     *            <code>YES_NO_CANCEL_OPTION</code>
     * @param messageType an integer designating the kind of message this is,
     *            primarily used to determine the icon from the pluggable Look
     *            and Feel: <code>ERROR_MESSAGE</code>,
     *            <code>INFORMATION_MESSAGE</code>,
     *            <code>WARNING_MESSAGE</code>, <code>QUESTION_MESSAGE</code>,
     *            or <code>PLAIN_MESSAGE</code>
     * @param icon the icon to display in the dialog
     * @param options an array of objects indicating the possible choices the
     *            user can make; if the objects are components, they are
     *            rendered properly; non-<code>String</code> objects are
     *            rendered using their <code>toString</code> methods; if this
     *            parameter is <code>null</code>, the options are determined
     *            by the Look and Feel
     * @param initialValue the object that represents the default selection for
     *            the dialog; only meaningful if <code>options</code> is used;
     *            can be <code>null</code>
     * @return an integer indicating the option chosen by the user, or
     *         <code>CLOSED_OPTION</code> if the user closed the dialog
     * @exception HeadlessException if
     *                <code>GraphicsEnvironment.isHeadless</code> returns
     *                <code>true</code>
     * @see java.awt.GraphicsEnvironment#isHeadless
     */
    public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) throws HeadlessException
    {
        CWOptionPane pane = new CWOptionPane(message, messageType, optionType, icon, options, initialValue);

        pane.setInitialValue(initialValue);
        GuiUtil.applyDefaultOrientation(pane);

        int style = styleFromMessageType(messageType);
        JDialog dialog = pane.createDialog(parentComponent, title, style);

        pane.selectInitialValue();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null)
        {
            return CLOSED_OPTION;
        }

        Object [] opts = pane.getOptions();
        if (opts == null)
        {
            if (selectedValue instanceof Integer)
            {
                return ((Integer) selectedValue).intValue();
            }
            return CLOSED_OPTION;
        }

        if (getActionName("Yes").equals(selectedValue)) //$NON-NLS-1$
        {
            return YES_OPTION;
        }

        if (getActionName("No").equals(selectedValue)) //$NON-NLS-1$
        {
            return NO_OPTION;
        }

            if (getActionName("OK").equals(selectedValue)) //$NON-NLS-1$
        {
            return OK_OPTION;
        }

        if (getActionName("CANCEL").equals(selectedValue)) //$NON-NLS-1$
        {
            return CANCEL_OPTION;
        }

        int maxCounter = opts.length;
        for (int counter = 0; counter < maxCounter; counter++)
        {
            if (opts[counter].equals(selectedValue))
            {
                return counter;
            }
        }

        return CLOSED_OPTION;
    }

    private JDialog createDialog(Component parentComponent, String title, int style) throws HeadlessException
    {

        final JDialog dialog;

        Window window = GuiUtil.getWindow(parentComponent);
        if (window instanceof Frame)
        {
            dialog = new JDialog((Frame) window, title, true);
        }
        else
        {
            dialog = new JDialog((Dialog) window, title, true);
        }

        Container contentPane = dialog.getContentPane();

        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);
        dialog.setResizable(false);

        if (JDialog.isDefaultLookAndFeelDecorated())
        {
            boolean supportsWindowDecorations = UIManager.getLookAndFeel().getSupportsWindowDecorations();
            if (supportsWindowDecorations)
            {
                dialog.setUndecorated(true);
                getRootPane().setWindowDecorationStyle(style);
            }
        }

        dialog.pack();
        dialog.setLocationRelativeTo(parentComponent);
        dialog.addWindowListener(new WindowAdapter()
        {
            private boolean gotFocus;

            public void windowClosing(WindowEvent we)
            {
                setValue(null);
            }

            public void windowGainedFocus(WindowEvent we)
            {
                // Once window gets focus, set initial focus
                if (!gotFocus)
                {
                    selectInitialValue();
                    gotFocus = true;
                }
            }
        });

        dialog.addComponentListener(new ComponentAdapter()
        {
            public void componentShown(ComponentEvent ce)
            {
                // reset value to ensure closing works properly
                setValue(JOptionPane.UNINITIALIZED_VALUE);
            }
        });

        addPropertyChangeListener(new PropertyChangeListener()
        {
            public void propertyChange(PropertyChangeEvent event)
            {
                // Let the defaultCloseOperation handle the closing
                // if the user closed the window without selecting a button
                // (newValue = null in that case).  Otherwise, close the dialog.
                if (dialog.isVisible()
                    && event.getSource() == CWOptionPane.this
                    && event.getPropertyName().equals(VALUE_PROPERTY)
                    && event.getNewValue() != null
                    && event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)
                {
                    dialog.setVisible(false);
                }
            }
        });

        GuiUtil.applyDefaultOrientation(dialog);
        return dialog;
    }

    private static int styleFromMessageType(int messageType)
    {
        switch (messageType)
        {
            case ERROR_MESSAGE:
                return JRootPane.ERROR_DIALOG;
            case QUESTION_MESSAGE:
                return JRootPane.QUESTION_DIALOG;
            case WARNING_MESSAGE:
                return JRootPane.WARNING_DIALOG;
            case INFORMATION_MESSAGE:
                return JRootPane.INFORMATION_DIALOG;
            case PLAIN_MESSAGE:
            default:
                return JRootPane.PLAIN_DIALOG;
        }
    }

    private static String getActionName(String key)
    {
        return actions.getAction(key).getValue(Action.NAME).toString();
    }

    private static Object[] fixOptions(Object[] options, int optionType, int messageType)
    {
        Object[] opts = options;
        if (options == null)
        {
            if (optionType == YES_NO_OPTION)
            {
                opts = new Object[]
                {
                                getActionName("Yes"), //$NON-NLS-1$
                                getActionName("No") //$NON-NLS-1$
                };
            }
            else if (optionType == OK_CANCEL_OPTION)
            {
                opts = new Object[]
                {
                                getActionName("OK"), //$NON-NLS-1$
                                getActionName("Cancel") //$NON-NLS-1$
                };
            }
            else if (optionType == YES_NO_CANCEL_OPTION && messageType != INFORMATION_MESSAGE)
            {
                opts = new Object[]
                {
                                getActionName("Yes"), //$NON-NLS-1$
                                getActionName("No"), //$NON-NLS-1$
                                getActionName("Cancel") //$NON-NLS-1$
                };
            }
            else
            {
                opts = new Object[]
                {
                                getActionName("OK"), //$NON-NLS-1$
                };
            }
        }
        return opts;
    }

    /**
     * The actions for this dialog.
     */
    /* protected */ static ActionFactory actions = new ActionFactory(CWOptionPane.class, null);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -1870422750863765033L;

}
