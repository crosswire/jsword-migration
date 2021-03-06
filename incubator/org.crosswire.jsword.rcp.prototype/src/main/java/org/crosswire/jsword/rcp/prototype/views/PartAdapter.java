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
 * Copyright: 2006
 *
 */
package org.crosswire.jsword.rcp.prototype.views;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

class PartAdapter implements IPartListener
{
    public void partActivated(IWorkbenchPart part)
    {
        //do nothing
    }

    public void partBroughtToTop(IWorkbenchPart part)
    {
        //do nothing
    }

    public void partClosed(IWorkbenchPart part)
    {
        //do nothing
    }

    public void partDeactivated(IWorkbenchPart part)
    {
        //do nothing
    }

    public void partOpened(IWorkbenchPart part)
    {
        //do nothing
    }
}