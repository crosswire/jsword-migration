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

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.rcp.prototype.workbench.PrototypePlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.jdom.transform.JDOMSource;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class PassageEditor extends EditorPart
{

    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.passageeditor";
    private Browser browser;

    public PassageEditor()
    {
        super();
    }

    public void doSave(IProgressMonitor monitor)
    {
        throw new UnsupportedOperationException();
    }

    public void doSaveAs()
    {
        throw new UnsupportedOperationException();
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
    }

    public boolean isDirty()
    {
        return false;
    }

    public boolean isSaveAsAllowed()
    {
        return false;
    }

    public void createPartControl(Composite parent)
    {
        browser = new Browser(parent, SWT.BORDER | SWT.WRAP);
        IEditorInput input = getEditorInput();
        BookData data = (BookData) input.getAdapter(BookData.class);
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
            transformBookData(data, PrototypePlugin.getDefault().openStream(new Path("xsl/simple.xsl")), writer);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            writer.write(e.toString());
        }
        browser.setText(writer.toString());
        browser.addLocationListener(new LocationListener()
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

            public void changed(LocationEvent event)
            {
            }
        });
    }

    public void setFocus()
    {
        if (browser != null && !browser.isDisposed())
        {
            browser.setFocus();
        }
    }


    private static void transformBookData(BookData data, InputStream xsl, Writer writer) throws TransformerFactoryConfigurationError,
                    TransformerException
    {
        Source xslSource = new StreamSource(xsl);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
        StreamResult result = new StreamResult(writer);
        Source source = new JDOMSource(data.getOsis());
        transformer.transform(source, result);
    }
}
