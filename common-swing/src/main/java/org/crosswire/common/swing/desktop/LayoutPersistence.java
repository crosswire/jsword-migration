/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2007
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: org.eclipse.jdt.ui.prefs 1178 2006-11-06 12:48:02Z dmsmith $
 */

package org.crosswire.common.swing.desktop;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.util.StringUtil;

/**
 * Window layout persistence mechanism. Intended to be flexible enough to allow
 * persisting size, position, layout of multiple windows.
 * 
 * @see gnu.gpl.License for license details. The copyright to this program is
 *      held by it's authors.
 * @author Adam Thomas [adam-thomas at cox dot net]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class LayoutPersistence
{

    /**
     * Creates the singleton persistence object capable of storing and
     * retrieving layout information on behalf windows.
     */
    private LayoutPersistence()
    {
        try
        {
            settings = ResourceUtil.getProperties(getClass());
        }
        catch (IOException e)
        {
            settings = new Properties();
        }
    }

    /**
     * All access to LayoutPersistence is through this single instance.
     * 
     * @return the singleton instance
     */
    public static LayoutPersistence instance()
    {
        return instance;
    }

    /**
     * Indicates whether the window passed to the constructor has had layout
     * information persisted.
     * 
     * @param window the window to persist
     * @return Returns true is layout information for the current window has
     *         been persisted, otherwise returns false
     */
    public synchronized boolean isLayoutPersisted(Window window)
    {
        return settings.containsKey(window.getName());
    }

    /**
     * Stores the current window's layout information.
     * 
     * @param window the window to persist
     */
    public synchronized void saveLayout(Window window)
    {
        int state = Frame.NORMAL;
        if (window instanceof Frame)
        {
            Frame frame = (Frame) window;
            state = frame.getExtendedState();
        }

        settings.setProperty(window.getName(),
                             StringUtil.join(new String[] {
                                             Integer.toString(state),
                                             Integer.toString(window.getWidth()),
                                             Integer.toString(window.getHeight()),
                                             Integer.toString(window.getX()),
                                             Integer.toString(window.getY())
                             }, "_") //$NON-NLS-1$
        );

        try
        {
            URI outputURI = CWProject.instance().getWritablePropertiesURI(getClass().getName());
            NetUtil.storeProperties(settings, outputURI, "Persistent Window properties"); //$NON-NLS-1$
        }
        catch (IOException ex)
        {
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    /**
     * Loads and restores the layout to the window that was passed to the
     * constructor.
     * 
     * @param window the window to persist
     */
    public synchronized void restoreLayout(Window window)
    {
        String[] parts = StringUtil.split(settings.getProperty(window.getName()), '_');

        // If our window did not have saved settings do nothing.
        if (parts == null || parts.length == 0)
        {
            return;
        }

        if (window instanceof Frame)
        {
            Frame frame = (Frame) window;
            frame.setExtendedState(Integer.parseInt(parts[STATE]));
        }

        window.setSize(new Dimension(Integer.parseInt(parts[WIDTH]), Integer.parseInt(parts[HEIGHT])));
        window.setLocation(new Point(Integer.parseInt(parts[LOCATION_X]), Integer.parseInt(parts[LOCATION_Y])));
    }

    /**
     * Provide class logging capabilities
     */
    private static final Logger      log        = Logger.getLogger(LayoutPersistence.class);

    /**
     * The persistence storage and retrieval object
     */
    private Properties               settings;

    /**
     * Suffix for window state key
     */
    private static final int         STATE      = 0;

    /**
     * Suffix for window width key
     */
    private static final int         WIDTH      = 1;

    /**
     * Suffix for window height key
     */
    private static final int         HEIGHT     = 2;

    /**
     * Suffix for window location x key
     */
    private static final int         LOCATION_X = 3;

    /**
     * Suffix for window location y key
     */
    private static final int         LOCATION_Y = 4;

    /**
     * The singleton instance of this class.
     */
    private static LayoutPersistence instance   = new LayoutPersistence();

}
