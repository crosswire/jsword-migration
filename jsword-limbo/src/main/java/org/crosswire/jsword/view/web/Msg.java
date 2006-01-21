package org.crosswire.jsword.view.web;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
class Msg extends MsgBase
{
    static final Msg INIT_FAILED = new Msg("Failed to initialize"); //$NON-NLS-1$
    static final Msg VERSION = new Msg("Version {0}"); //$NON-NLS-1$
    static final Msg NON_DIR = new Msg("{0} is not a directory"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
