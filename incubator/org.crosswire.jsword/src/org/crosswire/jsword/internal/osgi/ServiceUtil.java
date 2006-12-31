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

import org.crosswire.common.util.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class runs service operations in a contained environment, 
 * removing the caller's need to handle exceptions or to listen for service 
 * changes.
 * @see ServiceOperation 
 * @author Phillip [phillip at paristano dot org]
 */
final class ServiceUtil
{

	/**
	 * This method runs the given operation using the given context.
	 * The class string is used to construct an <code>OperationContext</code> to pass
	 * to the operation.
	 * 
	 * @param context The operation will be performed within this bundle context.
	 * @param clazz The operation will be performed on one or more of the services
	 * associated with this class name, via an <code>OperationContext</code>.
	 * @param operation The operation itself.
	 * @return The result of the operation. 
	 */
    public static Object runOperation(BundleContext context, String clazz, ServiceOperation operation)
    {
        return run(new ServiceTracker(context, clazz, null), operation);
    }

	/**
	 * This method runs the given operation using the given context.
	 * The OSGi filter is used to construct an <code>OperationContext</code> to pass
	 * to the operation.
	 * 
	 * @param context The operation will be performed within this bundle context.
	 * @param filter The operation will be performed on one or more of the services
	 * associated with this OSGi filter, via an <code>OperationContext</code>.
	 * @param operation The operation itself.
	 * @return The result of the operation. 
	 */
    public static Object runOperation(BundleContext context, Filter filter, ServiceOperation operation)
    {
        return run(new ServiceTracker(context, filter, null), operation);
    }

    /**
     * This method runs the operation. The given tracker is converted to a 
     * <code>ServiceContext</code> object and passed to the operation.
     * @param tracker A service tracker, built by one of this class's methods. 
     * @param operation The operation, provided by the original caller.
     * @return The operation's return value is returned.
     */
    private static Object run(ServiceTracker tracker, ServiceOperation operation) {
        tracker.open();
        Object returnValue = null;
        try
        {
            returnValue = operation.run(new ServiceContextImpl(tracker));
        }
        catch (Exception e)
        {
        	Logger.getLogger(ServiceUtil.class).error("Error encountered running service operation:", e);
        }
        finally
        {
            tracker.close();
        }
        return returnValue;
    }
    
    /**
     * An instance of this class adapts a <code>ServiceTracker</code>
     * instance to the more focused <code>OperationContext</code> interface.
     * This prevents the operation from altering the state of the tracker
     * and from using the tracker to register services. 
     */
    public static class ServiceContextImpl implements OperationContext
    {
        private ServiceTracker tracker;

        public ServiceContextImpl(ServiceTracker tracker)
        {
            this.tracker = tracker;
        }

        public Object[] getServices()
        {
            return tracker.getServices();
        }

        public Object getService()
        {
            return tracker.getService();
        }

    }

}
