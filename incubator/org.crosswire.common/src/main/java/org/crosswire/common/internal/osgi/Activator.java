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
package org.crosswire.common.internal.osgi;

import java.util.logging.Level;

import org.crosswire.common.util.CommonLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This activator loads the bundle's default services
 * and provides an entry point to the <code>CommonLogger</code> service
 * implementations for the <code>Logger</code> class. 
 * 
 * @author Phillip [phillip at paristano dot org]
 */
public final class Activator implements BundleActivator
{

    /**
     * This method is called upon bundle activation
     * @param context
     * @throws Exception
     */
    public void start(BundleContext context) throws Exception
    {
        //The service's log tracker monitors all registered logger services. 
        Activator.logTracker = new ServiceTracker(context, CommonLogger.class.getName(), null);
        Activator.logTracker.open();
    }

    /**
     * This method is called when the bundle is stopped
     * @param context
     * @throws Exception
     */
    public void stop(BundleContext context) throws Exception
    {
        //Terminate tracking. Registered logger services are untouched,
        //but they will not be available through the Logger class because
        //the bundle as a whole is stopped and ready to be removed.
        Activator.logTracker.close();
        Activator.logTracker = null;
    }

    /**
     * This method returns a <code>CommonLogger</code> implementation
     * that serves as an aggregate of all registered <code>CommonLogger</code> services.
     * Clients may call this instance regardless of the bundle's current
     * state, although no logging will not be performed through the returned
     * instance unless the bundle is active.
     * 
     * @return A non-<code>null</code> <code>CommonLogger</code>
     */
    public static CommonLogger getCommonLogger()
    {
        return Activator.logger;
    }

    /**
     * This class acts as an aggregate logger, encapsulating
     * all registered <code>CommonLogger</code> services. The
     * registered services are polled for each logged event.
     */
    /*private*/ static final class CommonLoggerImpl implements CommonLogger
    {
        public void log(Level level, String message, Throwable throwable)
        {
            if (Activator.logTracker == null)
            {
                //The bundle isn't activated. Logging is disabled. 
                return;
            }
            
            ServiceReference[] references = Activator.logTracker.getServiceReferences();
            if (references == null) {
                //No loggers were found.
                return;
            }
            
            for (int i = 0; i < references.length; i++)
            {
                ServiceReference reference = references[i];
                Object service = Activator.logTracker.getService(reference);
                if (service != null)
                {
                    CommonLogger loggerService = (CommonLogger) service; 
                    try
                    {
                        //It's possible that the service was unregistered between
                        //our call to getService and now, so the service call 
                        //should be considered volatile. 
                        loggerService.log(level, message, throwable);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    protected static final CommonLoggerImpl logger = new CommonLoggerImpl();
    protected static ServiceTracker logTracker;

}
