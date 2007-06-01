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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.bibledesktop.desktop;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class Msg extends MsgBase
{
    // Strings used by Desktop
    // String for the title and version
    static final Msg APP_TITLE = new Msg("Desktop.Title"); //$NON-NLS-1$
    static final Msg SPLASH_TITLE = new Msg("Desktop.Splash"); //$NON-NLS-1$
    static final Msg APP_VERSION = new Msg("Desktop.Version"); //$NON-NLS-1$
    static final Msg VERSION_TITLE = new Msg("Desktop.VersionTitle"); //$NON-NLS-1$
    static final Msg VERSION_APP_TITLE = new Msg("Desktop.VersionAppTitle"); //$NON-NLS-1$

    // Auto save of config failed
    static final Msg CONFIG_SAVE_FAILED = new Msg("Desktop.ConfigSaveFailed"); //$NON-NLS-1$

    // Strings for hyperlink URIs
    static final Msg UNKNOWN_PROTOCOL = new Msg("Desktop.UnknownProtocol"); //$NON-NLS-1$

    // Strings for the startup job
    static final Msg STARTUP_TITLE = new Msg("Desktop.StartupTitle"); //$NON-NLS-1$
    static final Msg STARTUP_GENERATE = new Msg("Desktop.StartupGenerate"); //$NON-NLS-1$
    static final Msg STARTUP_GENERAL_CONFIG = new Msg("Desktop.StartupGeneral"); //$NON-NLS-1$

    // No Bibles "wizard"
    static final Msg NO_BIBLES_MESSAGE = new Msg("Desktop.NoBiblesMessage"); //$NON-NLS-1$
    static final Msg NO_BIBLES_TITLE = new Msg("Desktop.NoBiblesTitle"); //$NON-NLS-1$

    // Strings for DesktopAction
    static final Msg NO_HELP = new Msg("DesktopActions.NoHelp"); //$NON-NLS-1$
    static final Msg NO_PASSAGE = new Msg("DesktopActions.NoPassage"); //$NON-NLS-1$
    static final Msg SOURCE_MISSING = new Msg("DesktopActions.SourceMissing"); //$NON-NLS-1$

    // Strings for AboutPane and Splash
    // The splash image is of an English version of the application
    static final Msg SPLASH_IMAGE = new Msg("Splash.SplashImage"); //$NON-NLS-1$
    static final Msg ABOUT_TITLE = new Msg("AboutPane.AboutTitle"); //$NON-NLS-1$
    static final Msg WARRANTY_TAB_TITLE = new Msg("AboutPane.Warranty"); //$NON-NLS-1$
    static final Msg DETAILS_TAB_TITLE = new Msg("AboutPane.Details"); //$NON-NLS-1$
    static final Msg SYSTEM_PROPS_TAB_TITLE = new Msg("AboutPane.SystemPropsTabTitle"); //$NON-NLS-1$

    // Strings for StatusBar
    static final Msg STATUS_DEFAULT = new Msg("StatusBar.StatusDefault"); //$NON-NLS-1$

    // Strings for OptionsAction
    static final Msg CONFIG_TITLE = new Msg("OptionsAction.ConfigTitle"); //$NON-NLS-1$

    // Strings for ViewSourcePane
    static final Msg TEXT_VIEWER = new Msg("ViewSourcePane.TextViewer"); //$NON-NLS-1$
    static final Msg ORIG = new Msg("ViewSourcePane.ORIG"); //$NON-NLS-1$
    static final Msg OSIS = new Msg("ViewSourcePane.OSIS"); //$NON-NLS-1$
    static final Msg HTML = new Msg("ViewSourcePane.HTML"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }

    /*
     * get the title of the application
     * @return the title of the application
     */
    public static String getApplicationTitle()
    {
        return Msg.APP_TITLE.toString();
    }

    /**
     * get a version string of the form "Version: 1.0"
     * @return the version string
     */
    public static String getVersionInfo()
    {
        String version = Msg.APP_VERSION.toString();
        return Msg.VERSION_TITLE.toString(version);
    }

    /**
     * get a title of the form "App Name v1.0"
     * @return a versioned title
     */
    public static String getVersionedApplicationTitle()
    {
        String title = Msg.APP_TITLE.toString();
        String version = Msg.APP_VERSION.toString();
        return Msg.VERSION_APP_TITLE.toString(new Object[] { title, version });
    }

    /**
     * get an About string of the form "About App Name"
     * @return Info for "About"
     */
    public static String getAboutInfo()
    {
        return Msg.ABOUT_TITLE.toString(getApplicationTitle());
    }
}
