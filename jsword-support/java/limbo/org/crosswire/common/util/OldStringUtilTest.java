package org.crosswire.common.util;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * <p><table border='1' cellPadding='3' cellSpacing='0'>
 * <tr><td bgColor='white' class='TableRowColor'><font size='-7'>
 *
 * Distribution Licence:<br />
 * JSword is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, or by writing to:
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see gnu.gpl.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
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