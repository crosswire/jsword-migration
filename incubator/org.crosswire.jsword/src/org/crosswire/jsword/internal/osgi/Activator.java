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
 *     The copyright to this program is held by it's authors.
 *
 */
package org.crosswire.jsword.internal.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This is the bundle's activator class. The work
 * of managing the services is delegated to the 
 * various registry classes located in this package.
 *  
 * @author Phillip [phillip at paristano dot org]
 */
public class Activator implements BundleActivator
{
    //keep the bundle context around for the registry classes.
    static BundleContext currentContext;

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        Activator.currentContext = context;

        //The registries manage the services themselves. 
        FilterRegistry.register(context);
        BookRegistry.register(context);
        IndexRegistry.register(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception
    {
        Activator.currentContext = null;
        FilterRegistry.unregister(context);
        BookRegistry.unregister(context);
        IndexRegistry.unregister(context);
    }
}
