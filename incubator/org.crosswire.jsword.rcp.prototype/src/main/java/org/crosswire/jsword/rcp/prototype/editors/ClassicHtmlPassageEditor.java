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
package org.crosswire.jsword.rcp.prototype.editors;

import java.io.StringWriter;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.rcp.prototype.workbench.PrototypePlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.osgi.framework.Bundle;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class ClassicHtmlPassageEditor extends AbstractHtmlPassageEditor
{
    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.classichtmlpassageeditor";

    public ClassicHtmlPassageEditor()
    {
        super();
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
    }


    protected void fill(final Browser browser)
    {
        BookData data = (BookData) getEditorInput().getAdapter(BookData.class);
        if (data == null)
        {
            //The editor assumes the input data is valid -- this
            //scenario should have been accounted for by the caller.
            //TODO display an error message.
            return;
        }
        
        StringWriter writer = new StringWriter();
        try
        {
            Bundle bundle = PrototypePlugin.getDefault().getBundle();
            transformBookData(data, FileLocator.openStream(bundle, new Path("xsl/simple.xsl"), false), writer);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            writer.write(e.toString());
        }
        browser.setText(writer.toString());

        browser.addLocationListener(new LocationAdapter()
        {
            public void changing(LocationEvent event)
            {
                //HACK This is a workaround for eclipse bug 117108.
                String[] anchorParts = event.location.split("about:blank#");
                if (anchorParts != null && anchorParts.length == 2)
                {
                    String anchorName = anchorParts[1];
                    browser.execute("document.all['" + anchorName + "'].scrollIntoView()");
                    event.doit = false;
                }
            }
        });
    }
}
