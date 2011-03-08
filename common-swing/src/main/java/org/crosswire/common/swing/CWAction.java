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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.event.EventListenerList;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;

/**
 * A CrossWire Action is a generic extension of AbstractAction, that adds
 * LARGE_ICON to Action and also forwards the Action to its listeners after
 * modifying the ActionEvent to include the ACTION_COMMAND_KEY.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CWAction extends AbstractAction {
    /**
     * The icon to display when a large one is needed. This is still not part of
     * Java as of 1.5. Now it is with Java 1.6!
     */
    public static final String LARGE_ICON = "LargeIcon";

    /**
     * The tooltip to display. This is an alias for SHORT_DESCRIPTION. The
     * creator and user of a CWAction is to store and retrieve
     * SHORT_DESCRIPTION.
     */
    public static final String TOOL_TIP = "ToolTip";

    /**
     * Set or clear, using null, the icon on this action.
     * @param icon the small icon to set
     * @return this action
     */
    public CWAction setLargeIcon(Icon icon) {
        putValue(LARGE_ICON, icon);
        return this;
    }

    public CWAction setLargeIcon(String iconPath) {
        return setLargeIcon(GuiUtil.getIcon(iconPath));
    }

    public CWAction setTooltip(String tooltip) {
        putValue(Action.SHORT_DESCRIPTION, tooltip);
        return this;
    }

    /**
     * Set or clear, using null, the icon on this action.
     * @param icon the small icon to set
     * @return this action
     */
    public CWAction setSmallIcon(Icon icon) {
        putValue(SMALL_ICON, icon);
        return this;
    }

    public CWAction setSmallIcon(String iconPath) {
        return setSmallIcon(GuiUtil.getIcon(iconPath));
    }

    /**
     * Set the accelerator key from spec. If the spec is invalid it is logged and ignored.
     * @param acceleratorSpec
     * @return this action
     */
    public CWAction setAccelerator(String acceleratorSpec) {
        putValue(Action.ACCELERATOR_KEY, getAccelerator(acceleratorSpec));
        return this;
    }

    /**
     * Set enabled either true or false on this action.
     * 
     * @param newEnabled the desired state
     * @return this action
     */
    public CWAction enable(boolean newEnabled) {
        setEnabled(newEnabled);
        return this;
    }

    /**
     * Create a clone of this action and attache the listener. If
     * no listener is supplied, the action is not cloned.
     * 
     * @param listener the listener for the action
     * @return a cloned action with the listener attached or the current action
     */
    public CWAction setListener(ActionListener listener) {
        CWAction action = this;
        if (listener != null) {
            action = action.clone();
            action.addActionListener(listener);
        }
        return action;
    }

    /**
     * Forwards the ActionEvent to the registered listener.
     * 
     * @param evt
     *            ActionEvent
     */
    public void actionPerformed(ActionEvent evt) {
        if (listeners != null) {
            Object[] listenerList = listeners.getListenerList();

            // Recreate the ActionEvent and stuff the value of the
            // ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(), (String) getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length - 2; i += 2) {
                ((ActionListener) listenerList[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * Adds a listener for Action events.
     * 
     * @param listener
     *            <code>ActionListener</code> to add
     */
    public void addActionListener(ActionListener listener) {
        if (listeners == null) {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, listener);
    }

    /**
     * Remove an ActionListener
     * 
     * @param listener
     *            <code>ActionListener</code> to remove
     */
    public void removeActionListener(ActionListener listener) {
        if (listeners == null) {
            return;
        }
        listeners.remove(ActionListener.class, listener);
    }

    /**
     * String representation of this object suitable for debugging
     * 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name:");
        sb.append((String) getValue(Action.NAME));
        sb.append("\n Desc:");
        sb.append((String) getValue(Action.SHORT_DESCRIPTION));
        sb.append("\n    ActionCommandKey:");
        sb.append((String) getValue(Action.ACTION_COMMAND_KEY));
        sb.append("\n    Enabled:");
        sb.append(isEnabled());
        sb.append("\n    ObjectID:");
        sb.append(System.identityHashCode(this));
        sb.append('\n');

        return sb.toString();
    }

    /**
     * Create a clone that does not copy the listeners. These CWActions need to
     * have listeners added to be meaningful.
     * 
     * @see javax.swing.AbstractAction#clone()
     */
    @Override
    public CWAction clone() {
        CWAction action = null;
        try {
            action = (CWAction) super.clone();
            action.listeners = null;
        } catch (CloneNotSupportedException e) {
            assert false : e;
        }
        return action;
    }

    /**
     * Convert the string to a valid Accelerator (that is a KeyStroke)
     */
    private KeyStroke getAccelerator(String acceleratorSpec) {
        KeyStroke accelerator = null;
        if (acceleratorSpec != null && acceleratorSpec.length() > 0) {
            try {
                accelerator = getKeyStroke(acceleratorSpec);
            } catch (NumberFormatException nfe) {
                log.warn("Could not parse integer for accelerator of action", nfe);
            }
        }
        return accelerator;
    }

   /**
    *
    */
    private KeyStroke getKeyStroke(String acceleratorSpec) throws NumberFormatException {
        int keyModifier = 0;
        int key = 0;
        String[] parts = StringUtil.split(acceleratorSpec, ',');
        for (int j = 0; j < parts.length; j++) {
            String part = parts[j].trim();
            if ("ctrl".equalsIgnoreCase(part)) {
                // use this so MacOS users are happy
                // It will map to the CMD key on Mac; CTRL otherwise.
                keyModifier |= Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
            } else if ("shift".equalsIgnoreCase(part)) {
                keyModifier |= InputEvent.SHIFT_MASK;
            } else if ("alt".equalsIgnoreCase(part)) {
                keyModifier |= InputEvent.ALT_MASK;
            } else if (part.startsWith("0x")) {
                key = Integer.parseInt(part.substring(2), 16);
            } else if (part.length() == 1) {
                key = part.charAt(0);
            }
        }
        return KeyStroke.getKeyStroke(key, keyModifier);
    }

    private EventListenerList listeners;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(CWAction.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258416148742484276L;
}
