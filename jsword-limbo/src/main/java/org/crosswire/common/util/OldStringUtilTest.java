package org.crosswire.common.util;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OldStringUtilTest extends TestCase
{
    public OldStringUtilTest(String s)
    {
        super(s);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public void testGetCapitals() throws Exception
    {
        assertEquals(OldStringUtil.getCapitals("Church of England"), "CE"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OldStringUtil.getCapitals("Java DataBase Connectivity"), "JDBC"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OldStringUtil.getCapitals(""), ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void testCreateJavaName() throws Exception
    {
        assertEquals(OldStringUtil.createJavaName("one  _Two"), "OneTwo"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OldStringUtil.createJavaName("one_two"), "OneTwo"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OldStringUtil.createJavaName("onetwo"), "Onetwo"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(OldStringUtil.createJavaName("ONetwo"), "ONetwo"); //$NON-NLS-1$ //$NON-NLS-2$
    }
}