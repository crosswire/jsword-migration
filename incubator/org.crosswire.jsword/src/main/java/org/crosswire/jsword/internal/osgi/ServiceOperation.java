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


/**
 * An Implementor of this interface performs basic operations on a service
 * or services, available through the given context. The implementor should
 * not handle unexpected exceptions; this is the caller's responsibility.
 *
 * @see ServiceUtil
 * @author Phillip [phillip at paristano dot org]
 *
 */
interface ServiceOperation
{
    /**
     * This method performs any number of tasks using the given context,
     * such as retrieving a specific service (generally limited to finding 
     * a service or performing an operation on the service). 
     * @param context The operation retrieves information through this context.
     * @return The results of the operation. 
     * @throws Exception The method allows any unexpected exception to 
     * pass to the caller.
     */
    public Object run(OperationContext context) throws Exception;
}
