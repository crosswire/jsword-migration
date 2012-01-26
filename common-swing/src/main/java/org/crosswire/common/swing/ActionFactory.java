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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.OSType;
import org.crosswire.common.util.StringUtil;

/**
 * The ActionFactory is being radically updated. Take the following with a grain of salt.
 * 
 * The ActionFactory is responsible for creating CWActions and making them
 * available to the program. Each Action is constructed from resources of the
 * form: ActionName.field=value where ActionName is the ACTION_COMMAND_KEY value
 * and field is one of the CWAction constants, e.g. LargeIcon. <br/>
 * Field is one of:
 * <ul>
 * <li>Name - This is required. The value is used for the text of the Action.<br/>
 * A mnemonic can be specified by preceding the letter with _. Using this letter in
 * a case insensitive search, the earliest position of that letter will
 * cause the it to be underlined. In a platform dependent
 * way it provides a keyboard mechanism to fire the action. For example, on
 * Windows, alt + mnemonic will cause a visible, active element with that
 * mnemonic to fire. For this reason, it is important to ensure that two
 * visible, active elements do not have the same mnemonic.<br/>
 * Note: Mnemonics are suppressed on MacOSX.</li>
 * 
 * <li>ToolTip - A tip to show when the mouse is over an element. If not
 * present, Name is used. This is likely to change. It is redundant to show a
 * tooltip that is identical to the shown text.</li>
 * 
 * <li>SmallIcon - A 16x16 pixel image to be shown for the item. The value for
 * this is a path which can be found as a resource.<br/>
 * Note: the small icon will be used when actions are tied to menu items and
 * buttons.</li>
 * 
 * <li>LargeIcon - A 24x24 pixel image to be shown for the item when large items
 * are shown. Currently, these are only used for the ToolBar, when a large
 * toolbar is requested. The value is a resource path to the image.</li>
 * 
 * <li>AcceleratorKey - A key on the keyboard, which may be specified with 0x25
 * kind of notation.<br/>
 * <br/>
 * Accelerators are global key combinations that work within an application to
 * fire the action. When the action is shown as a menu item the accelerator will
 * be listed with the name. Note: The accelerator key and it's modifiers are
 * converted into a <code>KeyStroke</code> with
 * <code>KeyStroke.getKeyStroke(key, modifierMask);</code></li>
 * 
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
     * Creates an ActionFactory that merely holds actions.
     * It does not lookup properties to construct an action. Constructing an action is the
     * responsibility of the calling class. It does not arrange for actions to perform actions.
     * 
     */
    public ActionFactory() {
        actions = new HashMap<String, CWAction>();
    }

    /**
     * Creates an ActionFactory that merely arranges for actions to be called against a bean.
     * It does not lookup properties to construct an action. Constructing an action is the
     * responsibility of the calling class.
     * 
     * @param bean
     */
    public ActionFactory(Object bean) {
        this();
        this.bean = bean;
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.Actionable#actionPerformed(java.lang.String)
     */
    public void actionPerformed(String action) {
        Action act = findAction(action);
        act.actionPerformed(new ActionEvent(this, 0, action));
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev) {
        String action = ev.getActionCommand();

        if (action == null || action.length() == 0) {
            // There's nothing to do.
            log.error("No action available for: " + bean.getClass().getName());
            return;
        }

        // Instead of cascading if/then/else
        // use reflection to do a direct lookup and call
        String methodName = METHOD_PREFIX + action;
        Exception ex = null;
        try {
            try {
                Method doMethod = bean.getClass().getDeclaredMethod(methodName, ActionEvent.class);
                doMethod.invoke(bean, ev);
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
            log.error("Could not execute method " + bean.getClass().getName() + "." + methodName + "()", ex);
        }
    }

    /**
     * Get the Action for the given actionName.
     * 
     * @param key
     *            the internal name of the CWAction
     * @return CWAction null if it does not exist
     */
    public Action findAction(String key) {
        CWAction action = actions.get(key);

        if (action == null) {
            log.info("Missing key: '" + key + "'. Known keys are: " + StringUtil.join(actions.keySet().toArray(), ", "));
            action = new CWAction();
            action.putValue(Action.NAME, key);
            action.putValue(Action.SHORT_DESCRIPTION, MISSING_RESOURCE);
            action.setEnabled(true);
            action.addActionListener(this);
        }
        return action;
    }

    /**
     * Get the Action for the given actionName.
     * 
     * @param key
     *            the internal name of the CWAction
     * @return CWAction null if it does not exist
     * @deprecated use {@link #findAction(String)} instead
     */
    @Deprecated
    public Action getAction(String key) {
        return findAction(key);
    }

    /**
     * Build a button from an action.
     * 
     * @param action
     *            the action to use
     * @return the button
     */
    public JButton createJButton(CWAction action, ActionListener listener) {
        CWAction act = action.clone();
        act.addActionListener(listener);
        return new JButton(act);
    }

    /**
     * Lookup an existing action for actionName. Otherwise construct, store and return an action.
     * 
     * @param key
     *            The short name by which this action is known. It is used to
     *            lookup the action for reuse.
     * @param name
     *            This is required. The value is used for the text of the
     *            Action.<br/>
     *            A mnemonic can be specified by preceding the letter with _.
     *            Using this letter in a case insensitive search, the earliest
     *            position of that letter will cause the it to be underlined. In
     *            a platform dependent way it provides a keyboard mechanism to
     *            fire the action. For example, on Windows, alt + mnemonic will
     *            cause a visible, active element with that mnemonic to fire.
     *            For this reason, it is important to ensure that two visible,
     *            active elements do not have the same mnemonic.<br/>
     *            Note: Mnemonics are suppressed on MacOSX.
     * @return the stored or newly constructed action
     */
    public CWAction addAction(String key, String name) {
        CWAction cwAction = actions.get(key);

        if (cwAction == null) {
            cwAction = buildAction(key, name);
            cwAction.addActionListener(this);
            actions.put(key, cwAction);
        }

        return cwAction;
    }

    public CWAction addAction(String key) {
        return addAction(key, null);
    }

    private CWAction buildAction(String key, String name) {
        if (key == null || key.length() == 0) {
            log.warn("Key is missing for CWAction");
        }

        CWAction cwAction = actions.get(key);

        if (cwAction != null) {
            return cwAction;
        }

        cwAction = new CWAction();
        cwAction.putValue(Action.ACTION_COMMAND_KEY, key);

        // For buttons that are just icons, there may not be a "name" field.
        if (name != null) {
            JLabel cwLabel = CWLabel.createJLabel(name);
            cwAction.putValue(Action.NAME, cwLabel.getText());

            // Mac's don't have mnemonics.
            // Otherwise, dig out the mnemonic.
            if (!OSType.MAC.equals(OSType.getOSType())) {
                cwAction.putValue(Action.MNEMONIC_KEY, Integer.valueOf(cwLabel.getDisplayedMnemonic()));
            }
        }

        return cwAction;
    }

    /**
     * The tooltip for actions that we generate to paper around missing
     * resources Normally we would assert, but in live we might want to limp on.
     */
    private static final String MISSING_RESOURCE = "Missing Resource";

    /**
     * The prefix to methods that we call
     */
    private static final String METHOD_PREFIX = "do";

    /**
     * The object to which we forward events
     */
    private Object bean;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(ActionFactory.class);

    /**
     * The map of known CWActions
     */
    private Map<String, CWAction> actions;
}
