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

/**
 * Window layout persistence mechanism. Intended to be flexible enough to allow
 * persisting size, position, layout of multiple windows.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Adam Thomas [adam-thomas at cox dot net]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class LayoutPersistence
{

    /**
     * Creates the singleton persistence object capable of storing and retrieving layout
     * information on behalf windows.
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
     * been persisted, otherwise returns false
     */
    public synchronized boolean isLayoutPersisted(Window window)
    {
        String winName = window.getName();
        String stateKey = winName + LayoutPersistence.STATE_KEY_SUFFIX;
        return settings.containsKey(stateKey);
    }

    /**
     * Stores the current window's layout information.
     * 
     * @param window the window to persist
     */
    public synchronized void saveLayout(Window window)
    {
        String winName = window.getName();
        String stateKey = winName + LayoutPersistence.STATE_KEY_SUFFIX;
        String widthKey = winName + LayoutPersistence.WIDTH_KEY_SUFFIX;
        String heightKey = winName + LayoutPersistence.HEIGHT_KEY_SUFFIX;
        String locationXKey = winName + LayoutPersistence.LOCATION_X_KEY_SUFFIX;
        String locationYKey = winName + LayoutPersistence.LOCATION_Y_KEY_SUFFIX;

        Frame frame = null;
        int state = Frame.NORMAL;
        if (window instanceof Frame)
        {
            frame = (Frame) window;
            state = frame.getExtendedState();
        }
        settings.setProperty(stateKey, String.valueOf(state));

        int width = window.getWidth();
        settings.setProperty(widthKey, String.valueOf(width));

        int height = window.getHeight();
        settings.setProperty(heightKey, String.valueOf(height));

        int locationX = window.getX();
        settings.setProperty(locationXKey, String.valueOf(locationX));

        int locationY = window.getY();
        settings.setProperty(locationYKey, String.valueOf(locationY));

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
     * Loads and restores the layout to the window that was passed to the constructor.
     * 
     * @param window the window to persist
     */
    public synchronized void restoreLayout(Window window)
    {
        String winName = window.getName();
        String stateKey = winName + LayoutPersistence.STATE_KEY_SUFFIX;
        String widthKey = winName + LayoutPersistence.WIDTH_KEY_SUFFIX;
        String heightKey = winName + LayoutPersistence.HEIGHT_KEY_SUFFIX;
        String locationXKey = winName + LayoutPersistence.LOCATION_X_KEY_SUFFIX;
        String locationYKey = winName + LayoutPersistence.LOCATION_Y_KEY_SUFFIX;

        if (window instanceof Frame)
        {
            Frame frame = (Frame) window;
            int state = getState(stateKey);
            frame.setExtendedState(state);
        }

        Dimension sizeDimension = getSize(widthKey, heightKey);
        window.setSize(sizeDimension);

        Point locationPoint = getLocation(locationXKey, locationYKey);
        window.setLocation(locationPoint);
    }

    /**
     * Reads persisted window state data
     * @param stateKey the properties key for state data
     * @return Window state data
     */
    private int getState(String stateKey)
    {
        return Integer.parseInt(settings.getProperty(stateKey));
    }

    /**
     * Reads persisted window size data
     * @param widthKey the properties key for width
     * @param heightKey the properties key for height
     * @return Window size data
     */
    private Dimension getSize(String widthKey, String heightKey)
    {
        int width = Integer.parseInt(settings.getProperty(widthKey));
        int height = Integer.parseInt(settings.getProperty(heightKey));
        return new Dimension(width, height);
    }

    /**
     * Reads persisted window location data
     * @param locationXKey the properties key for x
     * @param locationYKey the properties key for y
     * @return Window location data
     */
    private Point getLocation(String locationXKey, String locationYKey)
    {
        int x = Integer.parseInt(settings.getProperty(locationXKey));
        int y = Integer.parseInt(settings.getProperty(locationYKey));
        return new Point(x, y);
    }

    /**
     * Provide class logging capabilities
     */
    private static final Logger log                   = Logger.getLogger(LayoutPersistence.class);

    /**
     * The persistence storage and retrieval object
     */
    private Properties   settings;

    /**
     * Suffix for window state key
     */
    private static final String STATE_KEY_SUFFIX      = ".WindowState";                           //$NON-NLS-1$

    /**
     * Suffix for window width key
     */
    private static final String WIDTH_KEY_SUFFIX      = ".Width";                                 //$NON-NLS-1$

    /**
     * Suffix for window height key
     */
    private static final String HEIGHT_KEY_SUFFIX     = ".Height";                                //$NON-NLS-1$

    /**
     * Suffix for window location x key
     */
    private static final String LOCATION_X_KEY_SUFFIX = ".LocationX";                             //$NON-NLS-1$

    /**
     * Suffix for window location y key
     */
    private static final String LOCATION_Y_KEY_SUFFIX = ".LocationY";                             //$NON-NLS-1$

    /**
     * The singleton instance of this class.
     */
    private static LayoutPersistence instance = new LayoutPersistence();

}
