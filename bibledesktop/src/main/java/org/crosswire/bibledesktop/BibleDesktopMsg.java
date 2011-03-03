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

package org.crosswire.bibledesktop;

import org.crosswire.common.util.MsgBase;

/**
 * BibleDesktop API for all the messages in the BibleDesktop jar.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
final public class BibleDesktopMsg extends MsgBase {
    /**
     * get the title of the application
     * @return the title of the application
     */
    public static String getApplicationTitle() {
        // TRANSLATOR: The name of the program.
        return BibleDesktopMsg.gettext("Bible Desktop");
    }

    /**
     * get a version string of the form "Version: 1.0"
     * 
     * @return the version string
     */
    public static String getVersionInfo() {
        // TRANSLATOR: Gets a version string in the form "Version 1.0"
        // {0} is a placeholder for the version
        return BibleDesktopMsg.gettext("Version {0}", getVersion());
    }

    /**
     * get a title of the form "App Name v1.0"
     * 
     * @return a versioned title
     */
    public static String getVersionedApplicationTitle() {
        // TRANSLATOR: Gets a version string in the form "Bible Desktop v1.0"
        // {0} is a placeholder for the application name
        // {1} is a placeholder for the version
        return BibleDesktopMsg.gettext("{0} v{1}", new Object[] {
                getApplicationTitle(), getVersion()
        });
    }

    /**
     * get an About string of the form "About App Name"
     * 
     * @return Info for "About"
     */
    public static String getAboutInfo() {
        // TRANSLATOR: An "About" string in the form "About Bible Desktop"
        return BibleDesktopMsg.gettext("About {0}", getApplicationTitle());
    }

    private static String getVersion() {
        // TRANSLATOR: the current version of the application.
        // When translating use digits 0-9. They will be shaped appropriately.
        return BibleDesktopMsg.gettext("1.6");
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     * 
     * @param key
     * @return the internationalized text
     */
    public static String gettext(String key) {
        return msg.lookup(key);
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     * The text requires one parameter to be passed.
     * 
     * @param key
     * @param param
     * @return the formatted, internationalized text
     */
    public static String gettext(String key, Object param) {
        return msg.toString(key, param);
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     * The text requires one parameter to be passed.
     * 
     * @param key
     * @param params
     * @return the formatted, internationalized text
     */
    public static String gettext(String key, Object[] params) {
        return msg.toString(key, params);
    }

    private static MsgBase msg = new BibleDesktopMsg();
}
