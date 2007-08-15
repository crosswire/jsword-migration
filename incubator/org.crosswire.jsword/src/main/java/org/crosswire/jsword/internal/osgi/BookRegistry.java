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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;

import org.crosswire.common.util.EventListenerList;
import org.crosswire.common.util.Filter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.readings.ReadingsBookDriver;
import org.crosswire.jsword.book.sword.SwordBookDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;


/**
 * This class provides Book-related data to the classes
 * within this bundle.
 * 
 * @author Phillip [phillip at paristano dot org]
 * 
 */
public final class BookRegistry
{

    /**
     * This method returns an array of the available
     * book drivers. No drivers are available unless
     * the bundle is active.
     *  
     * Neither this array nor its contents
     * should be cached or otherwise stored off due to 
     * the driver service's volatile nature. 
     * 
     * @return An array of available book drivers. This
     * will not be <code>null</code>, but may be empty.
     */
    public static BookDriver[] getBookDrivers()
    {
        return getBookDrivers(new Filter()
        {
            public boolean test(Object obj)
            {
                return true;
            }
        });
    }

    /**
     * This method returns an array of the available, writable
     * book drivers. No drivers are available unless the bundle
     * is active.
     * Neither this array nor its contents
     * should be cached or otherwise stored off due to 
     * the driver service's volatile nature. 
     * 
     * @return An array of available, writable book drivers. This
     * will not be <code>null</code>, but may be empty.
     */
    public static BookDriver[] getWritableDrivers()
    {
        return BookRegistry.getBookDrivers(new Filter()
        {
            public boolean test(Object obj)
            {
                return ((BookDriver) obj).isWritable();
            }
        });
    }

    /**
     * This method is the activator's entry point. This is only
     * called once per start of the bundle. 
     *  
     * @param context Our bundle's context.
     */
    static void register(BundleContext context)
    {
        String bookDriverClassName = BookDriver.class.getName();
        context.registerService(bookDriverClassName, new SwordBookDriver(), ServiceUtil.createIdDictionary(ID_BOOKDRIVER, "sword"));
        context.registerService(bookDriverClassName, new ReadingsBookDriver(), ServiceUtil.createIdDictionary(ID_BOOKDRIVER, "sword"));

        driverTracker = new ServiceTracker(context, bookDriverClassName, null)
        {
            public Object addingService(ServiceReference reference)
            {
                Object service = super.addingService(reference);
                addBooks((BookDriver) service);
                return service;
            }

            /* (non-Javadoc)
             * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
             */
            public void removedService(ServiceReference reference, Object service)
            {
                removeBooks((BookDriver) service);
                super.removedService(reference, service);
            }
        };

        driverTracker.open();
    }

    /**
     * This method is called when the bundle stops.
     * 
     * @param context
     */
    static void unregister(BundleContext context)
    {
        books.clear();
        driverTracker.close();
        driverTracker = null;
    }

    /**
     * This method adds the given driver's books
     * to the internal cache. This method is only
     * called when a new driver is registered.
     * @param driver Books will be pulled from this driver service. 
     */
    protected static void addBooks(BookDriver driver)
    {
        Book[] newBooks = driver.getBooks();
        books.addAll(Arrays.asList(newBooks));
        for (int i = 0; i < newBooks.length; i++)
        {
            Book book = newBooks[i];
            EventListener[] listenerArray = listeners.getListeners(BookRegistryEventListener.class);
            for (int j = 0; j < listenerArray.length; j++)
            {
                BookRegistryEventListener listener = (BookRegistryEventListener) listenerArray[j];
                listener.bookAdded(book);
            }
        }
    }

    /**
     * This method removes the given driver's books
     * from the internal cache. This method is only
     * called when a driver is unregistered.
     * @param driver This driver's books will be removed.
     */
    protected static void removeBooks(BookDriver driver)
    {
        Book[] obsoleteBooks = driver.getBooks();
        books.removeAll(Arrays.asList(obsoleteBooks));
        for (int i = 0; i < obsoleteBooks.length; i++)
        {
            Book book = obsoleteBooks[i];
            EventListener[] listenerArray = listeners.getListeners(BookRegistryEventListener.class);
            for (int j = 0; j < listenerArray.length; j++)
            {
                BookRegistryEventListener listener = (BookRegistryEventListener) listenerArray[j];
                listener.bookRemoved(book);
            }
        }
    }

    /**
     * This method returns the available books. Neither the array
     * nor its contents should be stored due to the volatile
     * nature of the services that provide the books.
     * 
     * @return An array of available books.
     */
    public static Book[] getBooks()
    {
        return (Book[]) books.toArray(new Book[books.size()]);
    }


    /**
     * This method attempts to find a book driver from the given
     * book driver id. If it cannot find corresponding book driver, it will
     * return <code>null</code>.
     * @param bookDriverId The id of the book driver to find.
     * @return Returns the requested driver if it's registered, <code>null</code> otherwise.
     */
    public static BookDriver getBookDriverById(String bookDriverId)
    {
        return (BookDriver) ServiceUtil.getServiceById(BookDriver.class, ID_BOOKDRIVER, bookDriverId);
    }

    private static BookDriver[] getBookDrivers(Filter filter)
    {
        BundleContext context = Activator.currentContext;
        if (context == null)
        {
            // The bundle isn't active. No drivers are available.
            return new BookDriver[0];
        }


        FindBookDriversOperation operation = new FindBookDriversOperation(filter);
        Object retval = ServiceUtil.runOperation(context, BookDriver.class.getName(), operation);

        return operation.translate(retval);
    }

    /* private */static final class FindBookDriversOperation implements ServiceOperation
    {
        private final Filter filter;

        /* private */ FindBookDriversOperation(Filter filter)
        {
            this.filter = filter;
        }

        public BookDriver[] translate(Object retval)
        {
            return retval == null ? new BookDriver[0] : (BookDriver[]) retval;
        }

        public Object run(OperationContext context) throws Exception
        {
            Object[] services = context.getServices();
            ArrayList drivers = new ArrayList();
            for (int i = 0; i < services.length; i++)
            {
                Object service = services[i];
                if (service != null && filter.test(service))
                {
                    drivers.add(service);
                }
            }

            return drivers.toArray(new BookDriver[drivers.size()]);
        }
    }

    public static void addChangeListener(BookRegistryEventListener listener)
    {
        listeners.add(BookRegistryEventListener.class, listener);
    }

    public static void removeChangeListener(BookRegistryEventListener listener)
    {
        listeners.remove(BookRegistryEventListener.class, listener);
    }

    private static final EventListenerList listeners = new EventListenerList();

    /**
     * @param bookDriverId
     */
    public static void refreshBookDriver(String bookDriverId)
    {
        BookDriver driver = getBookDriverById(bookDriverId);
        if (driver != null)
        {
            removeBooks(driver);
            addBooks(driver);
        }
    }

    private static final String ID_BOOKDRIVER = "bookdriver.id";
    private static ArrayList books = new ArrayList();
    private static ServiceTracker driverTracker;

}
