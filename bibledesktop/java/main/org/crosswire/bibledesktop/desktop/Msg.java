package org.crosswire.bibledesktop.desktop;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 *
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class Msg extends MsgBase
{
    // Strings used by Desktop
    // String for the title and version
    static final Msg APP_TITLE = new Msg("Desktop.Title"); //$NON-NLS-1$
    static final Msg SPLASH_TITLE = new Msg("Desktop.Splash"); //$NON-NLS-1$
    static final Msg APP_VERSION = new Msg("Desktop.Version"); //$NON-NLS-1$
    static final Msg VERSION_TITLE = new Msg("Desktop.VersionTitle"); //$NON-NLS-1$
    static final Msg VERSION_APP_TITLE = new Msg("Desktop.VersionAppTitle"); //$NON-NLS-1$
    
    // Strings for hyperlink urls
    static final Msg UNKNOWN_PROTOCOL = new Msg("Desktop.UnknownProtocol"); //$NON-NLS-1$
    static final Msg BAD_PROTOCOL_URL = new Msg("Desktop.BadProtocolUrl"); //$NON-NLS-1$

    // Strings for the preloading job
    static final Msg PRELOAD_TITLE = new Msg("Desktop.PreloadTitle"); //$NON-NLS-1$
    static final Msg PRELOAD_SETUP = new Msg("Desktop.PreloadSetup"); //$NON-NLS-1$
    static final Msg PRELOAD_DATA = new Msg("Desktop.PreloadData"); //$NON-NLS-1$
    static final Msg PRELOAD_PROVIDER = new Msg("Desktop.PreloadProvider"); //$NON-NLS-1$
    static final Msg PRELOAD_STYLE = new Msg("Desktop.PreloadStyle"); //$NON-NLS-1$

    // Strings for the startup job
    static final Msg STARTUP_TITLE = new Msg("Desktop.StartupTitle"); //$NON-NLS-1$
    static final Msg STARTUP_CONFIG = new Msg("Desktop.StartupConfig"); //$NON-NLS-1$
    static final Msg STARTUP_LOAD_CONFIG = new Msg("Desktop.StartupLoadConfig"); //$NON-NLS-1$
    static final Msg STARTUP_LOAD_SETTINGS = new Msg("Desktop.StartupLoadSettings"); //$NON-NLS-1$
    static final Msg STARTUP_GENERATE = new Msg("Desktop.StartupGenerate"); //$NON-NLS-1$
    static final Msg STARTUP_GENERAL_CONFIG = new Msg("Desktop.StartupGeneral"); //$NON-NLS-1$

    // Strings for DesktopAction
    static final Msg NO_HELP = new Msg("DesktopActions.NoHelp"); //$NON-NLS-1$
    static final Msg NOT_IMPLEMENTED = new Msg("DesktopActions.NotImplemented"); //$NON-NLS-1$
    static final Msg NO_PASSAGE = new Msg("DesktopActions.NoPassage"); //$NON-NLS-1$
    static final Msg SOURCE_MISSING = new Msg("DesktopActions.SourceMissing"); //$NON-NLS-1$
    static final Msg OSIS = new Msg("DesktopActions.OSIS"); //$NON-NLS-1$
    static final Msg HTML = new Msg("DesktopActions.HTML"); //$NON-NLS-1$
    static final Msg NO_SOURCE = new Msg("DesktopActions.NoSourceFound"); //$NON-NLS-1$

    // Strings for AboutPane and Splash
    // The splash image is of an English version of the application
    static final Msg SPLASH_IMAGE = new Msg("Splash.SplashImage"); //$NON-NLS-1$
    static final Msg ABOUT_TITLE = new Msg("AboutPane.AboutTitle"); //$NON-NLS-1$
    static final Msg TASK_TAB_TITLE = new Msg("AboutPane.TaskTabTitle"); //$NON-NLS-1$
    static final Msg ERROR_TAB_TITLE = new Msg("AboutPane.ErrorTabTitle"); //$NON-NLS-1$
    static final Msg SYSTEM_PROPS_TAB_TITLE = new Msg("AboutPane.SystemPropsTabTitle"); //$NON-NLS-1$
    static final Msg DEBUG_TAB_TITLE = new Msg("AboutPane.DebugTabTitle"); //$NON-NLS-1$

    // Strings for StatusBar
    static final Msg STATUS_DEFAULT = new Msg("StatusBar.StatusDefault"); //$NON-NLS-1$

    // Strings for OptionsAction
    static final Msg CONFIG_TITLE = new Msg("OptionsAction.ConfigTitle"); //$NON-NLS-1$

    // Strings for ComparePane
    static final Msg COMPARE_DIALOG = new Msg("Compare.DIALOG"); //$NON-NLS-1$
    static final Msg COMPARE_IDENT_QUESTION = new Msg("Compare.IDENT_QUESTION"); //$NON-NLS-1$
    static final Msg COMPARE_IDENT_TITLE = new Msg("Compare.IDENT_TITLE"); //$NON-NLS-1$
    static final Msg COMPARE_WORDS = new Msg("Compare.WORDS"); //$NON-NLS-1$
    static final Msg COMPARE_GO = new Msg("Compare.GO"); //$NON-NLS-1$
    static final Msg COMPARE_USING = new Msg("Compare.USING"); //$NON-NLS-1$
    static final Msg COMPARE_WORDS_TIP = new Msg("Compare.WORDS_TIP"); //$NON-NLS-1$
    static final Msg COMPARE_TITLE = new Msg("Compare.TITLE"); //$NON-NLS-1$
    static final Msg COMPARE_VERSES = new Msg("Compare.VERSES"); //$NON-NLS-1$

    // Strings for CompareResultsPane
    static final Msg RESULTS_TITLE = new Msg("Results.TITLE"); //$NON-NLS-1$
    static final Msg RESULTS_START = new Msg("Results.START"); //$NON-NLS-1$
    static final Msg RESULTS_CLOSE = new Msg("Results.CLOSE"); //$NON-NLS-1$
    static final Msg RESULTS_DIALOG = new Msg("Results.DIALOG"); //$NON-NLS-1$
    static final Msg RESULTS_BOOKS = new Msg("Results.BOOKS"); //$NON-NLS-1$
    static final Msg RESULTS_COMPARING = new Msg("Results.COMPARING"); //$NON-NLS-1$
    static final Msg RESULTS_PASSAGE = new Msg("Results.PASSAGE"); //$NON-NLS-1$
    static final Msg RESULTS_WORDS = new Msg("Results.WORDS"); //$NON-NLS-1$
    static final Msg RESULTS_STOP = new Msg("RESULTS_STOP"); //$NON-NLS-1$

    // Strings for DebugPane
    static final Msg DEBUG_STEPS = new Msg("Debug.STEPS"); //$NON-NLS-1$
    static final Msg DEBUG_VIEWS = new Msg("Debug.VIEWS"); //$NON-NLS-1$
    static final Msg DEBUG_GO = new Msg("Debug.GO"); //$NON-NLS-1$
    static final Msg DEBUG_METHOD = new Msg("Debug.METHOD"); //$NON-NLS-1$

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
