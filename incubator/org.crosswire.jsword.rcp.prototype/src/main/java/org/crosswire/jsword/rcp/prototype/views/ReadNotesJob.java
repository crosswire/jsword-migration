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
package org.crosswire.jsword.rcp.prototype.views;

import java.util.ArrayList;
import java.util.List;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.rcp.prototype.workbench.PrototypePlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public abstract class ReadNotesJob extends Job
{
    public ReadNotesJob(String name)
    {
        super(name);
    }

    protected IStatus run(IProgressMonitor monitor)
    {
        BookData data = getBookData();
        monitor.beginTask("Loading Notes", data.getKey().getChildCount() * 2);
        NoteContentHandler handler = new NoteContentHandler(monitor);
        try
        {
            data.getSAXEventProvider().provideSAXEvents(handler);
            if (handler.anyNotesRead())
            {
                endNotes();
            }
            else
            {
                handleNoNotes();
            }
        }
        catch (SAXException e)
        {
            return new Status(IStatus.ERROR, PrototypePlugin.PLUGIN_ID, 1, "Error", e);
        }
        catch (BookException e)
        {
            return new Status(IStatus.ERROR, PrototypePlugin.PLUGIN_ID, 1, "Error", e);
        }
        finally
        {
            monitor.done();
        }
        return Status.OK_STATUS;
    }

    /**
     * This method is called to retrieve the book data.
     * @return
     */
    protected abstract BookData getBookData();

    /**
     * This method is called for each key (typically an OSIS id). 
     * @param key
     * @param notes
     */
    protected abstract void addNotes(String key, List notes);

    /**
     * This method is called after the last note has been provided.
     * If no notes were found, handleNoNotes will be called, not this.
     *
     */
    protected abstract void endNotes();

    /**
     * This method is called when no notes were found in the BookData.
     *
     */
    protected abstract void handleNoNotes();


    private final class NoteContentHandler extends DefaultHandler
    {
        private boolean inVerse;
        private boolean inNote;
        private StringBuffer buffer;
        private String currentOsisId;
        private IProgressMonitor monitor;
        private List currentNotes = new ArrayList();
        private boolean anyRead;;

        /**
         * @param monitor
         */
        public NoteContentHandler(IProgressMonitor monitor)
        {
            this.monitor = monitor;
        }

        /**
         * @return
         */
        public boolean anyNotesRead()
        {
            return anyRead;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if ("verse".equals(localName))
            {
                monitor.worked(1);
                currentOsisId = attributes.getValue("osisID");
                if (currentOsisId != null)
                {
                    //only look for notes if there's an osis id.
                    inVerse = true;
                }
            }
            else if (inVerse && "note".equals(localName))
            {
                String type = attributes.getValue("type");
                if (!"x-strongsMarkup".equals(type))
                {
                    buffer = new StringBuffer();
                    inNote = true;
                }
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if (inVerse && "verse".equals(localName))
            {
                inVerse = false;
                if (!currentNotes.isEmpty())
                {
                    addNotes(currentOsisId, currentNotes);
                    currentNotes.clear();
                    anyRead = true;
                }
            }
            else if (inNote && "note".equals(localName))
            {
                currentNotes.add(buffer.toString());
                inNote = false;
            }
        }

        /* (non-Javadoc)
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (inNote)
            {
                buffer.append(ch, start, length);
            }
        }
    }
}
