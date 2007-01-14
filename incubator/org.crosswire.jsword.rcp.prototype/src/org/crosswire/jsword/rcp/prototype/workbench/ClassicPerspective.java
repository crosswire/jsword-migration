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

import org.crosswire.jsword.rcp.prototype.views.BooksView;
import org.crosswire.jsword.rcp.prototype.views.ClassicSearchView;
import org.crosswire.jsword.rcp.prototype.views.ContentView;
import org.crosswire.jsword.rcp.prototype.views.KeysView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class ClassicPerspective implements IPerspectiveFactory
{

    public static final String PERSPECTIVE_ID = "org.crosswire.jsword.rcp.prototype.classicperspective";
    
    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);
        layout.addView(BooksView.PART_ID, IPageLayout.RIGHT, 0.70f, layout.getEditorArea());
        layout.addView(KeysView.PART_ID, IPageLayout.BOTTOM, 0.20f, BooksView.PART_ID);
        layout.addView(ContentView.PART_ID, IPageLayout.BOTTOM, 0.40f, KeysView.PART_ID);
        layout.addView(ClassicSearchView.PART_ID, IPageLayout.TOP, 0.20f, layout.getEditorArea());
    }
}
