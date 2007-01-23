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
 * Copyright: 2006
 *
 */
package org.crosswire.jsword.rcp.prototype.workbench;

import org.crosswire.jsword.book.BookCategory;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;

/**
 * @author Phillip [phillip at paristano dot org]
 */
public class PrototypePlugin extends AbstractUIPlugin
{

    public static final String PLUGIN_ID = "org.crosswire.jsword.rcp.prototype";
    //The shared instance.
    private static PrototypePlugin plugin;

    /**
     * The constructor.
     */
    public PrototypePlugin()
    {
        plugin = this;
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
        plugin = null;
    }

    /**
     * Returns the shared instance.
     */
    public static PrototypePlugin getDefault()
    {
        return plugin;
    }

    /**
     * Returns an image descriptor for the image file at the given
     * plug-in relative path.
     *
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path)
    {
        return AbstractUIPlugin.imageDescriptorFromPlugin("org.crosswire.jsword.rcp.prototype", path);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#createImageRegistry()
     */
    protected ImageRegistry createImageRegistry()
    {
        ImageRegistry registry = super.createImageRegistry();
        registry.put("image.category." + BookCategory.BIBLE, getImageDescriptor("icons/book-b16.gif"));
        registry.put("image.category." + BookCategory.COMMENTARY, getImageDescriptor("icons/book-c16.gif"));
        registry.put("image.category." + BookCategory.DAILY_DEVOTIONS, getImageDescriptor("icons/book-r16.gif"));
        registry.put("image.category." + BookCategory.DICTIONARY, getImageDescriptor("icons/book-d16.gif"));
        registry.put("image.category." + BookCategory.GLOSSARY, getImageDescriptor("icons/book-g16.gif"));
        registry.put("image.category." + BookCategory.OTHER, getImageDescriptor("icons/book-o16.gif"));
        registry.put("image.category." + BookCategory.QUESTIONABLE, getImageDescriptor("icons/book-o16.gif"));
        registry.put("image.general.bd16", getImageDescriptor("icons/bd-icon16.gif"));
        return registry;
    }
    /**
     * @param category
     * @return
     */
    public Image getBookCategoryImage(BookCategory category)
    {
        String key = "image.category." + category.toString();
        ImageRegistry registry = getImageRegistry();
        Image image = registry.get(key);
        if (image == null) {
            return ImageDescriptor.getMissingImageDescriptor().createImage();
        }
        return image;
    }
}
