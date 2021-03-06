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
 * ID: $Id$
 */
package org.crosswire.jsword.book.basic;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;

/**
 * A simple method of finding a directory in which Books are stored.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookRoot {
    /**
     * Search for versions directories
     */
    public static URI findBibleRoot(String subdir) throws MalformedURLException {
        URI root = null;

        // First see if there is a System property that can help us out
        String sysprop = System.getProperty(PROP_HOMEDIR);
        log.debug("Testing system property " + PROP_HOMEDIR + "=" + sysprop);

        if (sysprop != null) {
            try {
                URI found = NetUtil.lengthenURI(new URI(NetUtil.PROTOCOL_FILE, null, sysprop, null), DIR_VERSIONS);
                URI test = NetUtil.lengthenURI(found, FILE_LOCATOR);

                if (NetUtil.isFile(test)) {
                    log.debug("Found BibleRoot using system property " + PROP_HOMEDIR + " at " + test);
                    root = found;
                } else {
                    log.warn("Missing " + PROP_HOMEDIR + " under: " + test);
                }
            } catch (URISyntaxException e) {
                // root is null
            }

        }

        // If not then try a wild guess
        if (root == null) {
            URL found = ResourceUtil.getResource(DIR_VERSIONS + File.separator + FILE_LOCATOR);
            URI test = NetUtil.shortenURI(NetUtil.toURI(found), FILE_LOCATOR);
            if (NetUtil.isFile(test)) {
                log.debug("Found BibleRoot from current directory: " + test);
                root = test;
            } else {
                log.warn("Missing BibleRoot from current directory: " + test);
            }
        }

        if (root == null) {
            return null;
        }
        return NetUtil.lengthenURI(root, subdir);
    }

    /**
     * System property to let people re-direct where the project directory is
     * stored
     */
    private static final String PROP_HOMEDIR = "jsword.bible.dir";

    /**
     * A file so we know if we have the right versions directory
     */
    public static final String FILE_LOCATOR = "locator.properties";

    /**
     * Versions subdirectory of the project directory
     */
    public static final String DIR_VERSIONS = "versions";

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(BookRoot.class);
}
