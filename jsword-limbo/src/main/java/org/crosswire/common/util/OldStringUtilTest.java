package org.crosswire.common.util;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class OldStringUtilTest extends TestCase {
    public OldStringUtilTest(String s) {
        super(s);
    }

    /* @Override */
    protected void setUp() throws Exception {
    }

    /* @Override */
    protected void tearDown() throws Exception {
    }

    public void testGetCapitals() {
        assertEquals(OldStringUtil.getCapitals("Church of England"), "CE");
        assertEquals(OldStringUtil.getCapitals("Java DataBase Connectivity"), "JDBC");
        assertEquals(OldStringUtil.getCapitals(""), "");
    }

    public void testCreateJavaName() {
        assertEquals(OldStringUtil.createJavaName("one  _Two"), "OneTwo");
        assertEquals(OldStringUtil.createJavaName("one_two"), "OneTwo");
        assertEquals(OldStringUtil.createJavaName("onetwo"), "Onetwo");
        assertEquals(OldStringUtil.createJavaName("ONetwo"), "ONetwo");
    }
}
