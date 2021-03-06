/**
 * Distribution License:
 * This is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published
 * by the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/llgpl.html
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.crosswire.common.util.ClassUtil;
import org.crosswire.common.util.Logger;

/**
 * Attempt to find parenting errors.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DebugContainerListener implements ContainerListener {
    /*
     * (non-Javadoc)
     * 
     * @see
     * java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent
     * )
     */
    public void componentAdded(ContainerEvent ev) {
        Component child = ev.getChild();
        Container cont = ev.getContainer();

        addChild(cont, child);
    }

    /**
     *
     */
    private void setAlert(Component comp, Color color) {
        comp.setBackground(color.brighter());
        if (comp instanceof JComponent) {
            JComponent jcomp = (JComponent) comp;
            jcomp.setBorder(BorderFactory.createLineBorder(color, 5));
        }
    }

    /**
     *
     */
    private void addChild(Container parent, Component child) {
        Container statedParent = child.getParent();
        if (statedParent == null) {
            log.warn("CL1: child:" + toString(child) + "(pink), claiming getParent()=null", new Exception());
            setAlert(child, Color.PINK);
        } else {
            if (statedParent != parent) {
                log
                        .warn(
                                "CL1: child:" + toString(child) + "(cyan), getParent()=" + toString(statedParent) + "(green) added under parent=" + toString(parent) + "(yellow)", new Exception());
                setAlert(child, Color.CYAN);
                setAlert(statedParent, Color.GREEN);
                setAlert(parent, Color.YELLOW);
            }
        }

        Container lastKnownParent = (Container) map.get(child);
        if (lastKnownParent != null) {
            if (lastKnownParent != parent) {
                log
                        .warn(
                                "CL1: child:" + toString(child) + "(blue), altered reparent, old parent=" + toString(lastKnownParent) + "(magenta), new parent=" + toString(parent) + "(orange)", new Exception());
                setAlert(child, Color.BLUE);
                setAlert(lastKnownParent, Color.MAGENTA);
                setAlert(parent, Color.ORANGE);
            }
        }

        map.put(child, parent);

        if (child instanceof Container) {
            Container cont = (Container) child;
            cont.addContainerListener(this);

            // if we have already added ourselves to this component
            // then we don't need to dig down
            Component[] children = cont.getComponents();
            for (int i = 0; i < children.length; i++) {
                addChild(cont, children[i]);
            }
        }
    }

    /**
     *
     */
    private String toString(Component parent) {
        return ClassUtil.getShortClassName(parent, "Null") + '(' + parent.hashCode() + ')';
    }

    /*
     * (non-Javadoc)
     * 
     * @seejava.awt.event.ContainerListener#componentRemoved(java.awt.event.
     * ContainerEvent)
     */
    public void componentRemoved(ContainerEvent ev) {
        Component child = ev.getComponent();
        map.remove(child);
    }

    private Map map = new HashMap();

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DebugContainerListener.class);
}
