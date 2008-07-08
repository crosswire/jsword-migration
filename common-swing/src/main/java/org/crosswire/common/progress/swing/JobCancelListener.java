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
 * ID: $Id: CancelListener.java 1523 2007-07-24 20:51:33Z dmsmith $
 */
package org.crosswire.common.progress.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.crosswire.common.progress.Progress;

/**
 * Listen for cancel events and do the cancel.
 * 
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
final class JobCancelListener implements ActionListener
{
    /**
     * @param theJob
     */
    public JobCancelListener(Progress theJob)
    {
        job = theJob;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ev)
    {
        job.cancel();
    }

    private Progress job;
}
