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

import org.crosswire.common.icu.NumberShaper;
import org.crosswire.common.util.MsgBase;

/**
 * BibleDesktop API for all the messages in the BibleDesktop jar.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
 public final class BDMsg extends MsgBase {

    /**
     * get the title of the application
     * @return the title of the application
     */
    public static String getApplicationTitle() {
        // TRANSLATOR: The name of the program.
        return BDMsg.gettext("Bible Desktop");
    }

    /**
     * get a version string of the form "Version: 1.0"
     * 
     * @return the version string
     */
    public static String getVersionInfo() {
        // TRANSLATOR: Gets a version string in the form "Version 1.0"
        // {0} is a placeholder for the version
        return BDMsg.gettext("Version {0}", getVersion());
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
        return BDMsg.gettext("{0} v{1}", getApplicationTitle(), getVersion());
    }

    /**
     * get an About string of the form "About App Name"
     * 
     * @return Info for "About"
     */
    public static String getAboutInfo() {
        // TRANSLATOR: An "About" string in the form "About Bible Desktop"
        return BDMsg.gettext("About {0}", getApplicationTitle());
    }

    private static String getVersion() {
        return VERSION;
    }

    /**
     * Get the internationalized text, but return key if key is unknown.
     * The text requires one or more parameters to be passed.
     * 
     * @param key
     * @param params
     * @return the formatted, internationalized text
     */
    public static String gettext(String key, Object... params) {
        return msg.lookup(key, params);
    }

    // The shaper for the version number
    private static NumberShaper shaper = new NumberShaper();

    /**
     * The current version of Bible Desktop. Adjust for each release.
     * And increment after each release and append alpha, beta, ... to it.
     */
    private static final String VERSION = shaper.shape("1.6.1beta");

    private static MsgBase msg = new BDMsg();
}
