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

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.rcp.prototype.workbench.PrototypePlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.jdom.transform.JDOMSource;
import org.osgi.framework.Bundle;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class HtmlPassageEditor extends AbstractHtmlPassageEditor
{
    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.htmlpassageeditor";

    private Browser browser;

    private OsisElementSelectionProvider osisProvider = new OsisElementSelectionProvider();

    public HtmlPassageEditor()
    {
        super();
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        super.init(site, input);
        IActionBars bars = site.getActionBars();
        bars.setGlobalActionHandler(ActionFactory.COPY.getId(), new CopyAction());
        bars.updateActionBars();
    }

    protected void fill(final Browser browser)
    {
        browser.setText("<html><head></head><body oncontextmenu='return false;'><h1>Loading ...</h1></body></html>");

        browser.addLocationListener(new LocationAdapter()
        {
            public void changing(LocationEvent event)
            {
                //Any time a note "protocol" link is clicked, 
                //fire off a selection event.
                if (event.location.startsWith("note://"))
                {
                    String osisId = event.location.substring(7).replaceAll("/", "");
                    osisProvider.fireSelection(this, "note", osisId);
                    event.doit = false;
                }
            }
        });

        //This job performs the transform on the book data associated
        //with our editor input.
        Job loadHtmlJob = new TransformJob("Loading HTML Text")
        {
            protected void handleResult(final String html)
            {
                //load the html in the UI thread since we don't 
                //know which thread the job runs in.
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        browser.setText(html);
                    }
                });
            }

            protected Source getXslSource() throws Exception
            {
                Bundle bundle = PrototypePlugin.getDefault().getBundle();
                return new StreamSource(FileLocator.openStream(bundle, new Path("xsl/simple.xsl"), false));
            }

            protected Source getDocumentSource() throws Exception
            {
                return new JDOMSource(((BookData) getEditorInput().getAdapter(BookData.class)).getOsis());
            }
        };
        loadHtmlJob.setSystem(true);
        loadHtmlJob.setPriority(Job.LONG);
        loadHtmlJob.schedule();

    }

    public void dispose()
    {
        osisProvider.dispose();
        super.dispose();
    }

    public void setFocus()
    {
        if (browser != null && !browser.isDisposed())
        {
            browser.setFocus();
        }
    }

    public Object getAdapter(Class adapter)
    {
        if (adapter.equals(IOsisElementSelectionProvider.class))
        {
            return osisProvider;
        }
        return super.getAdapter(adapter);
    }

    protected class CopyAction extends Action
    {
        //        public boolean isEnabled()
        //        {
        //            //TODO this code has not been tested outside of win xp
        //            //HACK inspired by http://dev.eclipse.org/newslists/news.eclipse.platform.swt/msg18238.html
        //            browser.execute("window.status = 'selection:' + document.selection.createRange().text");
        //            return selection != null && selection.length() > 0;
        //        }

        public void run()
        {
            //TODO copy content to clipboard
            //TODO consider copying HTML content, as explained here: http://www.faqts.com/knowledge_base/view.phtml/aid/32427/fid/126
            super.run();
        }
    }

}
