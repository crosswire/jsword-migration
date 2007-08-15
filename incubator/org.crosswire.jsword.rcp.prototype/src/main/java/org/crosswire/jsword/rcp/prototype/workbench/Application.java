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
 * Copyright: 2007
 *
 */
package org.crosswire.jsword.rcp.prototype.workbench;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 * @author Phillip [phillip at paristano dot org]
 * @author DM [dmsmith555 at yahoo dot com]
 */
public class Application implements IApplication
{
    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
     */
    public Object start(IApplicationContext context) throws Exception
    {
        Display display = PlatformUI.createDisplay();
        try
        {
            int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
            if (returnCode == PlatformUI.RETURN_RESTART)
            {
                return IApplication.EXIT_RESTART;
            }
            return IApplication.EXIT_OK;
        }
        finally
        {
            display.dispose();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.equinox.app.IApplication#stop()
     */
    public void stop()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();

        if (workbench == null)
        {
            return;
        }

        final Display display = workbench.getDisplay();

        display.syncExec(new Runnable()
        {
            public void run()
            {
                if (!display.isDisposed())
                {
                    workbench.close();
                }
            }
        });
    }
}
