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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.EventListenerList;

/**
 * A CrossWire Action is a generic extension of AbstractAction,
 * that adds LARGE_ICON to Action and also forwards the Action
 * to its listeners after modifying the ActionEvent to include
 * the ACTION_COMMAND_KEY.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CWAction extends AbstractAction implements Cloneable
{
    /**
     * The icon to display when a large one is needed.
     * This is still not part of Java as of 1.5
     */
    public static final String LARGE_ICON = "LargeIcon"; //$NON-NLS-1$

    /**
     * The tooltip to display. This is an alias for SHORT_DESCRIPTION.
     * The creator and user of a CWAction is to store and retrieve SHORT_DESCRIPTION.
     */
    public static final String TOOL_TIP = "ToolTip"; //$NON-NLS-1$

    /**
     * Forwards the ActionEvent to the registered listener.
     * @param evt ActionEvent
     */
    public void actionPerformed(ActionEvent evt)
    {
        if (listeners != null)
        {
            Object[] listenerList = listeners.getListenerList();

            // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
            ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(), (String) getValue(Action.ACTION_COMMAND_KEY));
            for (int i = 0; i <= listenerList.length - 2; i += 2)
            {
                ((ActionListener) listenerList[i + 1]).actionPerformed(e);
            }
        }
    }

    /**
     * Adds a listener for Action events.
     * @param listener <code>ActionListener</code> to add
     */
    public void addActionListener(ActionListener listener)
    {
        if (listeners == null)
        {
            listeners = new EventListenerList();
        }
        listeners.add(ActionListener.class, listener);
    }

    /**
     * Remove an ActionListener
     * @param listener <code>ActionListener</code> to remove
     */
    public void removeActionListener(ActionListener listener)
    {
        if (listeners == null)
        {
            return;
        }
        listeners.remove(ActionListener.class, listener);
    }

    /**
     * String representation of this object suitable for debugging
     *
     */
    /* @Override */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Name:"); //$NON-NLS-1$
        sb.append((String) getValue(Action.NAME));
        sb.append("\n Desc:"); //$NON-NLS-1$
        sb.append((String) getValue(Action.SHORT_DESCRIPTION));
        sb.append("\n    ActionCommandKey:"); //$NON-NLS-1$
        sb.append((String) getValue(Action.ACTION_COMMAND_KEY));
        sb.append("\n    Enabled:"); //$NON-NLS-1$
        sb.append(isEnabled());
        sb.append("\n    ObjectID:"); //$NON-NLS-1$
        sb.append(System.identityHashCode(this));
        sb.append('\n');

        return sb.toString();
    }

    /**
     * Create a clone that does not copy the listeners.
     * These CWActions need to have listeners added to be
     * meaningful.
     */
    public Object clone()
    {
        CWAction action = null;
        try
        {
            action = (CWAction) super.clone();
            action.listeners = null;
        }
        catch (CloneNotSupportedException e)
        {
            assert false: e;
        } 
        return action;
    }

    private EventListenerList listeners;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258416148742484276L;
}
