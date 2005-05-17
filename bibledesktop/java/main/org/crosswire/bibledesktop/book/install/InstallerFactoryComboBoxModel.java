/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
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
package org.crosswire.bibledesktop.book.install;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.crosswire.jsword.book.install.InstallManager;

/**
 * A ComboBoxModel that displays all the known InstallerFactory names.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class InstallerFactoryComboBoxModel extends AbstractListModel implements ComboBoxModel
{
    /**
     * Simple ctor
     */
    public InstallerFactoryComboBoxModel(InstallManager imanager)
    {
        Set nameset = imanager.getInstallerFactoryNames();
        names = new ArrayList();
        names.addAll(nameset);
        Collections.sort(names);
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#getSelectedItem()
     */
    public Object getSelectedItem()
    {
        return selection;
    }

    /* (non-Javadoc)
     * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
     */
    public void setSelectedItem(Object selection)
    {
        this.selection = selection;
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize()
    {
        return names.size();
    }

    /* (non-Javadoc)
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index)
    {
        return names.get(index);
    }

    /**
     * The list of installer names
     */
    private List names;

    /**
     * The currently selected object
     */
    private Object selection;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258134643734885174L;
}
