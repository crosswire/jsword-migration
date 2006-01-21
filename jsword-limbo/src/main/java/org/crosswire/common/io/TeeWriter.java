package org.crosswire.common.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Vector;

import org.crosswire.common.util.Logger;

/**
 * TeeWriter allows you to have one stream act as a proxy
 * to a number of other streams, so that output to one goes to all
 * of the streams.
 * @author Joe Walker
 */
public class TeeWriter extends Writer
{
    /**
     * Add the specified Writer to the list of streams.
     * @param out
     * @return "this". So we can do <pre>tee.add(a).add(b).add(c);</pre>
     */
    public TeeWriter add(Writer out)
    {
        if (!list.contains(out))
        {
            list.addElement(out);
        }

        return this;
    }

    /**
     * Remove the specified Writer from the list of streams
     * used in all outputs.
     * @param out The Stream to be removed
     */
    public boolean remove(Writer out)
    {
        return list.removeElement(out);
    }

    /**
     * Override write to ask the listed Streams.
     */
    public void write(char[] cbuf, int off, int len) throws IOException
    {
        for (int i=0; i<list.size(); i++)
        {
            Writer out = (Writer) list.elementAt(i);
            out.write(cbuf, off, len);
        }
    }

    /**
     * Override write to ask the listed Streams.
     * @param b The byte to be written, as normal.
     */
    public void write(int b) throws IOException
    {
        for (int i=0; i<list.size(); i++)
        {
            Writer out = (Writer) list.elementAt(i);
            out.write(b);
        }
    }

    /**
     * Override flush to flush the listed Streams.
     */
    public void flush() throws IOException
    {
        for (int i=0; i<list.size(); i++)
        {
            Writer out = (Writer) list.elementAt(i);
            out.flush();
        }
    }

    /**
     * If someone closes the TeeWriter then we go round
     * and close all the Streams on the stack.
     */
    public void close() throws IOException
    {
        // Close each Writer catching and noting IOExceptions
        // Then rethrow at end if any failed.
        boolean failed = false;

        for (int i=0; i<list.size(); i++)
        {
            try
            {
                Writer out = (Writer) list.elementAt(i);
                out.close();
            }
            catch (Exception ex)
            {
                log.warn("Error in closing loop", ex); //$NON-NLS-1$
                failed = true;
            }
        }

        list.removeAllElements();

        if (failed) throw new IOException();
    }

    /**
     * @return The number of items on the stack
     */
    public int size()
    {
        return list.size();
    }

    /**
     * Primarily for debugging. Reports on th state of the Stream.
     * @return A String containing the report.
     */
    public String toString()
    {
        String retcode = ""; //$NON-NLS-1$
        String NEWLINE = System.getProperty("line.separator", "\r\n"); //$NON-NLS-1$ //$NON-NLS-2$

        retcode += "There are " + list.size() + " output(s)" + NEWLINE; //$NON-NLS-1$ //$NON-NLS-2$

        for (int i=list.size()-1; i>=0; i--)
        {
            Writer out = (Writer) list.elementAt(i);
            retcode += "Stream" + i + ": " + out.toString() + NEWLINE; //$NON-NLS-1$ //$NON-NLS-2$
        }

        return retcode;
    }

    private Vector list = new Vector();

    /** The log stream */
    protected static Logger log = Logger.getLogger(TeeWriter.class);
}
