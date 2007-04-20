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
 * ID: $Id: CWAction.java 1168 2006-10-19 21:47:42Z dmsmith $
 */
package org.crosswire.common.swing;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.crosswire.common.util.OSType;

/**
 * A ScrollPane that give appropriate cross platform behavior.
 * Specifically, on the Mac the vertical and horizontal scrollbars should always appear.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class CWScrollPane extends JScrollPane
{
    public CWScrollPane()
    {
        this(null);
    }

    public CWScrollPane(Component view)
    {
        super(view, verticalPolicy, horizontalPolicy);
    }

    private static int getXPlatformVerticalScrollBarPolicy()
    {
        if (OSType.MAC.equals(OSType.getOSType()))
        {
            return ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
        }
        return ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
    }

    private static int getXPlatformHorizontalScrollBarPolicy()
    {
        if (OSType.MAC.equals(OSType.getOSType()))
        {
            return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS;
        }
        return ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
    }

    private static int verticalPolicy = getXPlatformVerticalScrollBarPolicy();
    private static int horizontalPolicy = getXPlatformHorizontalScrollBarPolicy();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -7774104652833574820L;
}
