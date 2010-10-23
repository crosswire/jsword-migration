package org.crosswire.jsword.view.web;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase {
    static final Msg INIT_FAILED = new Msg("Failed to initialize");
    static final Msg VERSION = new Msg("Version {0}");
    static final Msg NON_DIR = new Msg("{0} is not a directory");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
