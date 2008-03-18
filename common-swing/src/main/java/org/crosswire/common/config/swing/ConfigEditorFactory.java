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
package org.crosswire.common.config.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import org.crosswire.common.config.Config;
import org.crosswire.common.util.LucidRuntimeException;
import org.crosswire.common.util.PluginUtil;
import org.crosswire.common.util.Reporter;

/**
 * Allow a swing program to display a Dialog box displaying a set of
 * config options.
 *
 * @see gnu.lgpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public final class ConfigEditorFactory
{
    /**
     * Prevent instantiation
     */
    private ConfigEditorFactory()
    {
    }

    /**
     * Create a dialog to house a TreeConfig component
     * using the default set of Fields
     * @param config The set of Choices to display
     * @param parent A component to use to find a frame to use as a dialog parent
     * @param al The action when the user clicks on ok or apply
     */
    public static void showDialog(Config config, Component parent, ActionListener al)
    {
        Exception ex = null;
        try
        {
            ConfigEditor base = (ConfigEditor) PluginUtil.getImplementation(ConfigEditor.class);
            base.construct(config);
            base.showDialog(parent, al);
        }
        catch (ClassCastException e)
        {
            ex = e;
        }
        catch (IOException e)
        {
            ex = e;
        }
        catch (ClassNotFoundException e)
        {
            ex = e;
        }
        catch (InstantiationException e)
        {
            ex = e;
        }
        catch (IllegalAccessException e)
        {
            ex = e;
        }

        if (ex != null)
        {
            Reporter.informUser(parent, ex);
        }
    }

    /**
     * Create a dialog to house a TreeConfig component using the default set of
     * Fields, with the default accept action of config.localToApplication and
     * config,localToPermanent
     *
     * @param config The set of Choices to display
     * @param parent A component to use to find a frame to use as a dialog
     *            parent
     */
    public static void showDialog(Config config, Component parent, URI uri)
    {
        showDialog(config, parent, new URIActionListener(config, uri));
    }

    /**
     * A quick class to save a config to a uri
     */
    static class URIActionListener implements ActionListener
    {
        /**
         * To save to a URI
         */
        public URIActionListener(Config config, URI uri)
        {
            this.config = config;
            this.uri = uri;
        }

        /**
         * The save action
         */
        public void actionPerformed(ActionEvent ev)
        {
            try
            {
                config.localToApplication();
                config.localToPermanent(uri);
            }
            catch (IOException ex)
            {
                throw new LucidRuntimeException(UserMsg.CONFIG_SAVE_FAILED, ex, new Object[] { uri });
            }
        }

        /**
         * The config to save if needed
         */
        private Config config;

        /**
         * The URI to save to if needed
         */
        private URI uri;
    }
}
