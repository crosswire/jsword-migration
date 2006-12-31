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
package org.crosswire.common.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class DefaultCommonLogger implements CommonLogger
{

    /**
     * Stop all logging output
     */
    public static void outputNothing()
    {
        java.util.logging.Logger.getLogger(ROOT_LOGGER).setLevel(Level.OFF);
    }

    /**
     * Output a minimum of stuff
     */
    public static void outputInfoMinimum()
    {
        java.util.logging.Logger.getLogger(ROOT_LOGGER).setLevel(Level.WARNING);
    }

    /**
     * Output everything
     */
    public static void outputEverything()
    {
        java.util.logging.Logger.getLogger(ROOT_LOGGER).setLevel(Level.FINEST);
    }
    
    /* (non-Javadoc)
     * @see org.crosswire.common.util.CommonLogger#log(java.util.logging.Level, java.lang.String, java.lang.Throwable)
     */
    public void log(Level level, String message, Throwable throwable)
    {
        String className = null;
        String methodName = null;
        int lineNumber = -1;
        // Get the stack trace.
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        // First, search back to a method in the Logger class.
        int ix = 0;
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals(CLASS_NAME))
            {
                break;
            }
            ix++;
        }
        // Now search for the first frame before the "Logger" class.
        while (ix < stack.length)
        {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (!cname.equals(CLASS_NAME))
            {
                // We've found the relevant frame.
                className = cname;
                methodName = frame.getMethodName();
                lineNumber = frame.getLineNumber();
                break;
            }
            ix++;
        }
        LogRecord logRecord = new LogRecord(level, message);
        logRecord.setLoggerName(logger.getName());
        logRecord.setSourceClassName(className);
        logRecord.setSourceMethodName(methodName);
        logRecord.setThrown(throwable);
        // This is a non-standard use of sequence number.
        // We could just subclass LogRecord and add line number.
        logRecord.setSequenceNumber(lineNumber);
        logger.log(logRecord);
    }

    static
    {
        // Establish a class that will load logging properties into java.util.logging.LogManager
        System.setProperty("java.util.logging.config.class", LogConfig.class.getName()); //$NON-NLS-1$
    }

    private static final String ROOT_LOGGER = ""; //$NON-NLS-1$
    private static final String CLASS_NAME = Logger.class.getName();
    
    private java.util.logging.Logger logger;
}
