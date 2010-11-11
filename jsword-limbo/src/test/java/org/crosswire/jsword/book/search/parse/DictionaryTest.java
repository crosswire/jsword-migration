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
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id: DictionaryTest.java 763 2005-07-27 23:26:43Z dmsmith $
 */
package org.crosswire.jsword.book.search.parse;

import junit.framework.TestCase;

import org.crosswire.jsword.book.search.Grammar;

/**
 * JUnit Test.
 * 
 * @see gnu.lgpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DictionaryTest extends TestCase {
    public DictionaryTest(String s) {
        super(s);
    }

    /* @Override */
    protected void setUp() throws Exception {
    }

    /* @Override */
    protected void tearDown() throws Exception {
    }

    public void testGetRoot() {
        assertEquals(Grammar.getRoot("joseph"), "joseph");
        assertEquals(Grammar.getRoot("joseph's"), "joseph");
        assertEquals(Grammar.getRoot("walker"), "walk");
        assertEquals(Grammar.getRoot("walked"), "walk");
        assertEquals(Grammar.getRoot("walks"), "walk");
        assertEquals(Grammar.getRoot("boxes"), "box");
    }

    public void testIsSmallWord() {
        assertTrue(Grammar.isSmallWord("the"));
        assertTrue(Grammar.isSmallWord("and"));
        assertTrue(!Grammar.isSmallWord("lord"));
        assertTrue(!Grammar.isSmallWord("god"));
        assertTrue(Grammar.isSmallWord("o"));
        assertTrue(!Grammar.isSmallWord("nothing"));
        assertTrue(Grammar.isSmallWord(" the "));
        assertTrue(Grammar.isSmallWord(" and "));
        assertTrue(!Grammar.isSmallWord(" lord "));
        assertTrue(!Grammar.isSmallWord(" god "));
        assertTrue(Grammar.isSmallWord(" o "));
        assertTrue(!Grammar.isSmallWord(" nothing "));
        assertTrue(Grammar.isSmallWord(""));
        assertTrue(Grammar.isSmallWord(" "));
        assertTrue(Grammar.isSmallWord("  "));
    }

    public void testStripSmallWords() {
        String[] temp = Grammar.stripSmallWords(new String[] {
                "i", "am", "but", "nothing", "o", "the", "lord", "god", "and", "",});
        assertEquals(temp[0], "nothing");
        assertEquals(temp[1], "lord");
        assertEquals(temp[2], "god");
        assertEquals(temp.length, 3);
    }

    public void testTokenizeWithoutSmallWords() {
        String[] temp = Grammar.tokenizeWithoutSmallWords("i am but nothing o the lord god and ", " ");
        assertEquals(temp[0], "nothing");
        assertEquals(temp[1], "lord");
        assertEquals(temp[2], "god");
        assertEquals(temp.length, 3);
    }
}
