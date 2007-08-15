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
import java.io.Writer;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
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
abstract class AbstractHtmlPassageEditor extends EditorPart
{
    private Browser browser;
    
    public final void doSave(IProgressMonitor monitor)
    {
        throw new UnsupportedOperationException();
    }

    public final void doSaveAs()
    {
        throw new UnsupportedOperationException();
    }

    public void init(IEditorSite site, IEditorInput input) throws PartInitException
    {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
    }

    public final boolean isDirty()
    {
        return false;
    }

    public final boolean isSaveAsAllowed()
    {
        return false;
    }

    
    public final void createPartControl(Composite parent)
    {
        browser = new Browser(parent, SWT.BORDER | SWT.WRAP);
        fill(browser);
    }    
    /**
     * @param browser
     */
    protected abstract void fill(Browser browser);

    public void setFocus()
    {
        if (browser != null && !browser.isDisposed())
        {
            browser.setFocus();
        }
    }


    protected static void transformBookData(BookData data, InputStream xsl, Writer writer) throws TransformerFactoryConfigurationError, TransformerException
    {
        Source xslSource = new StreamSource(xsl);
        Transformer transformer = TransformerFactory.newInstance().newTransformer(xslSource);
        StreamResult result = new StreamResult(writer);
        try
        {
            Source source = new JDOMSource(data.getOsis());
            transformer.transform(source, result);
        }
        catch (BookException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
