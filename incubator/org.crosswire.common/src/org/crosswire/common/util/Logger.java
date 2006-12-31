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
 * ID: $Id: Logger.java 1083 2006-04-18 18:13:36Z dmsmith $
 */
package org.crosswire.common.util;

import java.util.logging.Level;

import org.crosswire.common.internal.osgi.Activator;


/**
 * This class is very similar to Commons-Logging except it should be even
 * smaller and have an API closer to the Log4J API (and even J2SE 1.4 logging)
 * to help us to move over.
 * Having our own class will also help with re-factoring.
 *
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @author Phillip [phillip at paristano dot org]
 */
public final class Logger
{
    /**
     * Same as calling <code>getLogger(clazz.getName())</code>.
     */
    public static Logger getLogger(Class clazz)
    {
        return new Logger(clazz);
    }

    /**
     * Simple ctor
     */
    private Logger(Class id)
    {
        //TODO save off the id and integrate it into the logger's messages.
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message)
    {
        doLogging(Level.SEVERE, message, null);
    }

    /**
     * Log a message object with the FATAL level.
     * @param message the message object to log.
     */
    public void fatal(String message, Throwable th)
    {
        doLogging(Level.SEVERE, message, th);
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message)
    {
        doLogging(Level.WARNING, message, null);
    }

    /**
     * Log a message object with the ERROR level.
     * @param message the message object to log.
     */
    public void error(String message, Throwable th)
    {
        doLogging(Level.WARNING, message, th);
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message)
    {
        doLogging(Level.CONFIG, message, null);
    }

    /**
     * Log a message object with the INFO level.
     * @param message the message object to log.
     */
    public void info(String message, Throwable th)
    {
        doLogging(Level.CONFIG, message, th);
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message)
    {
        doLogging(Level.INFO, message, null);
    }

    /**
     * Log a message object with the WARN level.
     * @param message the message object to log.
     */
    public void warn(String message, Throwable th)
    {
        doLogging(Level.INFO, message, th);
    }

    /**
     * Log a message object with the DEBUG level.
     * @param message the message object to log.
     */
    public void debug(String message)
    {
        doLogging(Level.FINE, message, null);
    }

    // Private method to forward the event onto the registered services.
    private void doLogging(Level level, String message, Throwable th)
    {
        Activator.getCommonLogger().log(level, message, th);
    }

}
