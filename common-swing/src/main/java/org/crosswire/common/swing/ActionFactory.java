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

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

import org.crosswire.common.util.CWClassLoader;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.OSType;
import org.crosswire.common.util.StringUtil;

/**
 * The ActionFactory is responsible for creating CWActions and making them
 * available to the program. Each Action is constructed from resources of the
 * form: ActionName.field=value where ActionName is the ACTION_COMMAND_KEY value
 * and field is one of the CWAction constants, e.g. LargeIcon. <br/>
 * Field is one of:
 * <ul>
 * <li>Name - This is required. The value is used for the text of the Action.</li>
 * <li>Mnemonic - An upper case letter or other character in the value of the
 * Name field. If found, using a case insensitive search, the mnemonic will
 * cause the corresponding character to be underlined. In a platform dependent
 * way it provides a keyboard mechanism to fire the action. For example, on
 * Windows, alt + mnemonic will cause a visible, active element with that
 * mnemonic to fire. For this reason, it is important to ensure that two
 * visible, active elements do not have the same mnemonic.<br/>
 * Note: Mnemonics are suppressed on MacOSX.</li>
 * <li>ToolTip - A tip to show when the mouse is over an element. If not
 * present, Name is used. This is likely to change. It is redundant to show a
 * tooltip that is identical to the shown text.</li>
 * <li>SmallIcon - A 16x16 pixel image to be shown for the item. The value for
 * this is a path which can be found as a resource.<br/>
 * Note: the small icon will be used when actions are tied to menu items and
 * buttons.</li>
 * <li>LargeIcon - A 24x24 pixel image to be shown for the item when large items
 * are shown. Currently, these are only used for the ToolBar, when a large
 * toolbar is requested. The value is a resource path to the image.</li>
 * <li>AcceleratorKey - A key on the keyboard, which may be specified with 0x25
 * kind of notation.<br/>
 * <br/>
 * Accelerators are global key combinations that work within an application to
 * fire the action. When the action is shown as a menu item the accelerator will
 * be listed with the name. Note: The accelerator key and it's modifiers are
 * converted into a <code>KeyStroke</code> with
 * <code>KeyStroke.getKeyStroke(key, modifierMask);</code></li>
 * <li>AcceleratorKey.Modifier - A comma separated list of ctrl, alt, and shift,
 * indicating what modifiers are necessary for the accelerator.<br/>
 * Note: ctrl will use a platform's command key. On MacOSX this is the
 * Apple/Command key. Other platforms use Ctrl.</li>
 * <li>Enabled - Defaults to true when not present. It is disabled when the
 * value does not match "true" regardless of case. This is used to initialize
 * widgets tied to actions to disabled. Once the action is created, it's state
 * can be changed and the tied widgets will behave appropriately.</li>
 * <li>Shared - Defaults to true when not present. It is unshared when the value
 * does not match "true" regardless of case. When false, each copy of the action
 * is independent of other copies.</li>
 * </ul>
 * 
 * <p>
 * In order to facilitate easier translation, Enabled, SmallIcon and LargeIcon
 * can be specified in a parallel resource, whose name is suffixed with
 * "_control" as in Desktop_control. This is meant to extrapolate the constant
 * behavior of an action into a file that probably does not need to be
 * internationalized. If it does, for example, to suppress the display of icons,
 * then one would create a resource further suffixed with the language and
 * perhaps country, as in Desktop_control_fa.
 * </p>
 * 
 * <p>
 * To add another twist, several actions may have the same name and mnemonic,
 * differing perhaps by tooltip. To facilitate the sharing of these definitions,
 * an Aliases resource is defined to contain common values. If the value of a
 * ActionName.Name is prefixed with "Alias.", as in Go.Name=Alias.Go, then Go
 * will be used as the ActionName to look up values in the Aliases resource.
 * </p>
 * 
 * <p>
 * Aliases defines defaults that can be overridden by the referring resource
 * file. The only value that cannot be overridden is Name.
 * </p>
 * 
 * <p>
 * When an action is fired, this class, as a listener, reflects the action on
 * the class providing the resource. For example, DesktopActions creates an
 * ActionFactory from the Desktop ResourceBundle. When the Exit action is fired,
 * ActionFactory calls DesktopActions.doExit(ActionEvent event) or
 * DesktopActions.doExit(), if the first did not exist.
 * </p>
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ActionFactory implements ActionListener, Actionable {
    /**
     * Constructor that distinguishes between the object to call and the type to
     * look up resources against. This is useful for when you are writing a
     * class with subclasses but wish to keep the resources registered in the
     * name of the superclass.
     */
    public ActionFactory(Class type, Object bean) {
        actions = new HashMap();

        buildActionMap(type);

        this.bean = bean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.crosswire.common.swing.Actionable#performAction(java.lang.String)
     */
    public void actionPerformed(String action) {
        Action act = getAction(action);
        act.actionPerformed(new ActionEvent(this, 0, action));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();

        assert action != null;
        assert action.length() != 0;

        // Instead of cascading if/then/else
        // use reflection to do a direct lookup and call
        String methodName = METHOD_PREFIX + action;
        Exception ex = null;
        try {
            try {
                Method doMethod = bean.getClass().getDeclaredMethod(methodName, new Class[] {
                    ActionEvent.class
                });
                doMethod.invoke(bean, new Object[] {
                    ev
                });
            } catch (NoSuchMethodException e) {
                Method doMethod = bean.getClass().getDeclaredMethod(methodName, new Class[0]);
                doMethod.invoke(bean, new Object[0]);
            }
        } catch (NoSuchMethodException e) {
            ex = e;
        } catch (IllegalArgumentException e) {
            ex = e;
        } catch (IllegalAccessException e) {
            ex = e;
        } catch (InvocationTargetException e) {
            ex = e;
        }

        if (ex != null) {
            log.error("Could not execute method " + bean.getClass().getName() + "." + methodName + "()", ex); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * Get the Action for the given actionName.
     * 
     * @param key
     *            the internal name of the CWAction
     * @return CWAction null if it does not exist
     */
    public Action getAction(String key) {
        return getAction(key, null);
    }

    /**
     * Get the Action for the given actionName.
     * 
     * @param key
     *            the internal name of the CWAction
     * @return CWAction null if it does not exist
     */
    public Action getAction(String key, ActionListener listener) {
        CWAction action = (CWAction) actions.get(key);

        if (action != null) {
            if (listener != null) {
                action = (CWAction) action.clone();
                action.addActionListener(listener);
            }
            return action;
        }
        log.info("Missing key: '" + key + "'. Known keys are: " + StringUtil.join(actions.keySet().toArray(), ", ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assert false;

        CWAction getOutOfJailFreeAction = new CWAction();

        getOutOfJailFreeAction.putValue(Action.NAME, key);
        getOutOfJailFreeAction.putValue(Action.SHORT_DESCRIPTION, MISSING_RESOURCE);
        getOutOfJailFreeAction.setEnabled(true);
        getOutOfJailFreeAction.addActionListener(this);

        return getOutOfJailFreeAction;
    }

    /**
     * Construct a JLabel from the Action. Only Action.NAME and
     * Action.MNEMONIC_KEY are used.
     * 
     * @param key
     *            the internal name of the CWAction
     * @return A label, asserting if missing resources or with default values
     *         otherwise
     */
    public JLabel createJLabel(String key) {
        Action action = getAction(key);

        JLabel label = new JLabel();
        label.setText(action.getValue(Action.NAME).toString());

        Integer mnemonic = (Integer) action.getValue(Action.MNEMONIC_KEY);
        if (mnemonic != null) {
            label.setDisplayedMnemonic(mnemonic.intValue());
        }

        return label;
    }

    /**
     * Build a button from an action that consist solely of the icon.
     * 
     * @param key
     *            the action to use
     * @return the button
     */
    public JButton createActionIcon(String key) {
        return createActionIcon(key, null);
    }

    /**
     * Build a button from an action that consist solely of the icon.
     * 
     * @param key
     *            the action to use
     * @return the button
     */
    public JButton createActionIcon(String key, ActionListener listener) {
        Action action = getAction(key, listener);

        JButton button = new JButton(action);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setText(null);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    /**
     * Build a button from an action.
     * 
     * @param key
     *            the action to use
     * @return the button
     */
    public JButton createJButton(String key) {
        return createJButton(key, null);
    }

    /**
     * Build a button from an action.
     * 
     * @param key
     *            the action to use
     * @return the button
     */
    public JButton createJButton(String key, ActionListener listener) {
        return new JButton(getAction(key, listener));
    }

    /**
     * Build the map of actions from resources
     */
    private void buildActionMap(Class basis) {
        try {
            StringBuffer basisName = new StringBuffer(basis.getName());
            ResourceBundle resources = ResourceBundle.getBundle(basisName.toString(), Locale.getDefault(), CWClassLoader.instance(basis));
            ResourceBundle controls = null;
            try {
                basisName.append("_control"); //$NON-NLS-1$
                controls = ResourceBundle.getBundle(basisName.toString(), Locale.getDefault(), CWClassLoader.instance(basis));
            } catch (MissingResourceException ex) {
                // It is OK for this to not exist. This just means that the
                // defaults are used
            }

            // Get all the keys but we only need those that end with .Name
            Enumeration en = resources.getKeys();
            while (en.hasMoreElements()) {
                String key = (String) en.nextElement();
                if (key.endsWith(TEST)) {
                    String actionName = key.substring(0, key.length() - TEST.length());

                    ResourceBundle nickname = null;
                    String nameValue = getActionString(resources, null, actionName, Action.NAME);

                    // We know this should never happen because we are merely rebuilding the key.
                    if (nameValue == null) {
                        log.warn("Missing original key for " + actionName + '.' + Action.NAME); //$NON-NLS-1$
                        continue;
                    }

                    // If the value starts with alias, we have to dig the actual name out of the aliases
                    if (nameValue.startsWith(ActionFactory.ALIAS)) {
                        String newActionName = nameValue.substring(ActionFactory.ALIAS.length());
                        String newNameValue = getActionString(aliases, null, newActionName, Action.NAME);
                        // We had a clear request for an Alias. So newNameValue should never be null here.
                        if (newNameValue == null) {
                            log.warn("Missing alias key for " + actionName + '.' + Action.NAME); //$NON-NLS-1$
                            continue;
                        }
                        nameValue = newNameValue;
                        nickname = aliases;
                    }

                    String tooltip = getActionString(resources, nickname, actionName, CWAction.TOOL_TIP);
                    if (tooltip == null) {
                        tooltip = nameValue;
                    }

                    Integer mnemonic = getMnemonic(nickname, resources, actionName);
                    KeyStroke accelerator = getAccelerator(nickname, resources, actionName);

                    Icon smallIcon = getIcon(controls, actionName, Action.SMALL_ICON);
                    Icon largeIcon = getIcon(controls, actionName, CWAction.LARGE_ICON);
                    String enabledStr = getActionString(controls, null, actionName, "Enabled"); //$NON-NLS-1$
                    boolean enabled = enabledStr == null ? true : Boolean.valueOf(enabledStr).booleanValue();

                    CWAction cwAction = new CWAction();

                    if (actionName == null || actionName.length() == 0) {
                        log.warn("Acronymn is missing for CWAction"); //$NON-NLS-1$
                    } else {
                        cwAction.putValue(Action.ACTION_COMMAND_KEY, actionName);
                    }

                    if (nameValue.length() == 0) {
                        log.warn("Name is missing for CWAction"); //$NON-NLS-1$
                        cwAction.putValue(Action.NAME, "?"); //$NON-NLS-1$
                    } else {
                        cwAction.putValue(Action.NAME, nameValue);
                    }

                    cwAction.putValue(CWAction.LARGE_ICON, largeIcon);
                    cwAction.putValue(Action.SMALL_ICON, smallIcon);
                    cwAction.putValue(Action.SHORT_DESCRIPTION, tooltip);
                    // Mac's don't have mnemonics
                    if (!OSType.MAC.equals(OSType.getOSType())) {
                        cwAction.putValue(Action.MNEMONIC_KEY, mnemonic);
                    }
                    cwAction.putValue(Action.ACCELERATOR_KEY, accelerator);
                    cwAction.setEnabled(enabled);

                    cwAction.addActionListener(this);

                    actions.put(actionName, cwAction);
                }
            }
        } catch (MissingResourceException ex) {
            log.error("Missing resource for class: " + basis.getName()); //$NON-NLS-1$
            throw ex;
        }
    }

    /**
     * Lookup an action/field combination, returning null for missing resources.
     * If aliases are present use them, with values in resources over-riding.
     */
    private String getActionString(ResourceBundle resources, ResourceBundle nicknames, String actionName, String field) {
        String result = null;
        try {
            // The normal case is not to have aliases so look for the resource as not aliased
            // The control resource file does not have to exist.
            if (resources != null) {
                result = resources.getString(actionName + '.' + field);
            }
        } catch (MissingResourceException ex) {
            // do something later, if not optional
        }

        try {
            // If there were no result and we are aliasing then look for the alias
            if (result == null && nicknames != null) {
                result = nicknames.getString(actionName + '.' + field);
            }
        } catch (MissingResourceException ex) {
            // do something later, if not optional
        }

        return result;
    }

    /**
     * Get an icon for the string
     */
    private Icon getIcon(ResourceBundle resources, String actionName, String iconName) {
        Icon icon = null;
        String iconStr = getActionString(resources, null, actionName, iconName);
        if (iconStr != null && iconStr.length() > 0) {
            icon = GuiUtil.getIcon(iconStr);
        }
        return icon;
    }

    /**
     * Convert the string to a mnemonic
     */
    private Integer getMnemonic(ResourceBundle nicknames, ResourceBundle resources, String actionName) {
        Integer mnemonic = null;
        String mnemonicStr = getActionString(resources, nicknames, actionName, Action.MNEMONIC_KEY);
        if (mnemonicStr != null && mnemonicStr.length() > 0) {
            try {
                mnemonic = new Integer(getInteger(mnemonicStr));
            } catch (NumberFormatException ex) {
                log.warn("Could not parse integer for mnemonic of action " + actionName, ex); //$NON-NLS-1$
            }
        }
        return mnemonic;
    }

    /**
     * Convert the string to a valid Accelerator (that is a KeyStroke)
     */
    private KeyStroke getAccelerator(ResourceBundle nicknames, ResourceBundle resources, String actionName) {
        // Create the KeyStroke for the action's shortcut/accelerator
        KeyStroke accelerator = null;
        String acceleratorStr = getActionString(resources, nicknames, actionName, Action.ACCELERATOR_KEY);
        if (acceleratorStr != null && acceleratorStr.length() > 0) {
            String modifierName = Action.ACCELERATOR_KEY + ".Modifiers";  //$NON-NLS-1$
            // Not every accelerator needs a modifier
            String modifierSpec = getActionString(resources, nicknames, actionName, modifierName);
            if (modifierSpec == null) {
                return accelerator;
            }

            String[] modifiers = StringUtil.split(modifierSpec, ',');

            try {
                int shortcut = getInteger(acceleratorStr);
                int keyModifier = getModifier(modifiers);

                // Now we can create it
                accelerator = KeyStroke.getKeyStroke(shortcut, keyModifier);
            } catch (NumberFormatException nfe) {
                log.warn("Could not parse integer for accelerator of action " + actionName, nfe); //$NON-NLS-1$
            }
        }
        return accelerator;
    }

    /**
     * Convert the string to an integer. The string is either a single character
     * or it is hex number.
     * 
     * @return the integer value of the accelerator
     */
    private int getInteger(String str) throws NumberFormatException {
        int val = 0;
        int length = str.length();
        if (str.startsWith("0x")) { //$NON-NLS-1$
            val = Integer.parseInt(str.substring(2), 16);
        } else if (length == 1) {
            val = str.charAt(0);
        } else {
            val = Integer.parseInt(str);
        }

        return val;
    }

    /**
     *
     */
    private int getModifier(String[] modifiers) {
        int keyModifier = 0;
        for (int j = 0; j < modifiers.length; j++) {
            String modifier = modifiers[j];
            if ("ctrl".equalsIgnoreCase(modifier)) { //$NON-NLS-1$
                // use this so MacOS users are happy
                // It will map to the CMD key on Mac; CTRL otherwise.
                keyModifier |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            } else if ("shift".equalsIgnoreCase(modifier)) { //$NON-NLS-1$
                keyModifier |= InputEvent.SHIFT_MASK;
            } else if ("alt".equalsIgnoreCase(modifier)) { //$NON-NLS-1$

                keyModifier |= InputEvent.ALT_MASK;
            }
        }

        return keyModifier;
    }

    /**
     * The tooltip for actions that we generate to paper around missing
     * resources Normally we would assert, but in live we might want to limp on.
     */
    private static final String MISSING_RESOURCE = "Missing Resource"; //$NON-NLS-1$

    /**
     * The prefix to methods that we call
     */
    private static final String METHOD_PREFIX = "do"; //$NON-NLS-1$

    /**
     * What we lookup
     */
    private static final String SEPARATOR = "."; //$NON-NLS-1$

    /**
     * The test string to find actions
     */
    private static final String TEST = SEPARATOR + Action.NAME;

    /**
     * The object to which we forward events
     */
    private Object bean;

    private static final String ALIASES = "Aliases"; //$NON-NLS-1$

    private static final String ALIAS = "Alias" + SEPARATOR; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ActionFactory.class);

    /**
     * The aliases known by this system.
     */
    private static ResourceBundle aliases;

    static {
        try {
            aliases = ResourceBundle.getBundle(ALIASES, Locale.getDefault(), CWClassLoader.instance(ActionFactory.class));
        } catch (MissingResourceException ex) {
            log.error("Tell me it isn't so. The Aliases.properties does exist!", ex); //$NON-NLS-1$
        }
    }

    /**
     * The map of known CWActions
     */
    private Map actions;
}
