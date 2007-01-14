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
package org.crosswire.jsword.rcp.prototype.actions;

import org.crosswire.jsword.rcp.prototype.editors.PassageEditor;
import org.crosswire.jsword.rcp.prototype.editors.BookDataInput;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class ViewPassageAction extends Action
{

    private String passage;
    private String bibleId;
    private IWorkbenchWindow window;

    /**
     * @param bible
     * @param passage
     */
    public ViewPassageAction(IWorkbenchWindow window, String bibleId, String passage)
    {
        this.window = window;
        this.bibleId = bibleId;
        this.passage = passage;
    }

    public void run()
    {
        try
        {
            window.getActivePage().openEditor(new BookDataInput(bibleId, passage), PassageEditor.PART_ID);
        }
        catch (PartInitException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
