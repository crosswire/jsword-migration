
package org.crosswire.common.util.event;

import java.util.*;

/**
 * An event indicating that some bit of data needs capturing.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 */
public class CaptureEvent extends EventObject
{
    /**
     * Constructs an CaptureEvent object.
     * @param source The event originator (typically <code>this</code>)
     * @param ev An exception
     */
    public CaptureEvent(Object source, Throwable ex, int level)
    {
        super(source);

        this.ex = ex;
        this.message = null;
        this.level = level;
    }

    /**
     * Constructs an CaptureEvent object.
     * @param source The event originator (typically <code>this</code>)
     * @param ev An exception
     */
    public CaptureEvent(Object source, String message, int level)
    {
        super(source);

        this.ex = null;
        this.message = message;
        this.level = level;
    }

    /**
    * Returns a string specifying the source of the message.
    * @return The Source as a String
    */
    public String getSourceName()
    {
        Class clazz;
        Object source = getSource();
        if (source instanceof Class)
            clazz = (Class) source;
        else
            clazz = source.getClass();

        String full = clazz.getName();
        int last_dot = full.lastIndexOf(".");

        if (last_dot == -1)
            return full;
        else
            return full.substring(last_dot+1);
    }

    /**
     * Returns the exception.
     * @return the Exception
     */
    public Throwable getException()
    {
        return ex;
    }

    /**
     * Returns the message.
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
    * Returns the level.
    * @return the level
    */
    public int getLevel()
    {
        return level;
    }

    /** The thing that went wrong */
    private Throwable ex;

    /** The message that is being passed around */
    private String message;

    /** The level */
    private int level;
}
