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

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.crosswire.jsword.rcp.prototype.workbench.PrototypePlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public abstract class TransformJob extends Job
{
    /**
     * @param name
     */
    public TransformJob(String name)
    {
        super(name);
    }

    protected abstract void handleResult(String html);
    protected abstract Source getXslSource() throws Exception;
    protected abstract Source getDocumentSource() throws Exception;
    
    protected IStatus run(IProgressMonitor monitor)
    {
        StringWriter writer = new StringWriter();
        monitor.beginTask("Transforming Input", 2);
        try
        {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(getXslSource());
            StreamResult result = new StreamResult(writer);
            Source documentSource = getDocumentSource();
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            monitor.worked(1);
            transformer.transform(documentSource, result);
            if (monitor.isCanceled()) {
                return Status.CANCEL_STATUS;
            }
            handleResult(writer.toString());
            monitor.worked(1);
        }
        catch (Exception e)
        {
            return new Status(IStatus.ERROR, PrototypePlugin.PLUGIN_ID, 0, "Error", e);
        }
        finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
}
