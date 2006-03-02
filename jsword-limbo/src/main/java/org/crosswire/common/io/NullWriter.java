package org.crosswire.common.io;

import java.io.IOException;
import java.io.Writer;

/**
 * NullWriter allows you to write to /dev/null
 * @author Joe Walker
 */
public class NullWriter extends Writer
{
    /**
     * Override write to ask the listed Streams.
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException
    {
    }

    /**
     * Override write to ask the listed Streams.
     * @param b The byte to be written, as normal.
     */
    @Override
    public void write(int b) throws IOException
    {
    }

    /**
     * Override flush to flush the listed Streams.
     */
    @Override
    public void flush() throws IOException
    {
    }

    /**
     * If someone closes the TeeWriter then we go round
     * and close all the Streams on the stack.
     */
    @Override
    public void close() throws IOException
    {
    }
}
