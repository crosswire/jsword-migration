/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.biblemapper.model;

import org.crosswire.biblemapper.model.Map;
import org.crosswire.biblemapper.model.Position;
import org.crosswire.biblemapper.model.PositionUtil;

import junit.framework.TestCase;

/**
 * JUnit Test.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class MapTest extends TestCase
{
    public MapTest(String s)
    {
        super(s);
    }

    Position[][] dar = new Position[][]
    {
        new Position[]
        {
            new Position(new float[] { 1, 1 }),
            new Position(new float[] { 2, 2 }),
            new Position(new float[] { 3, 3 }),
        },
        new Position[]
        {
            new Position(new float[] { 4, 4 }),
            new Position(new float[] { 5, 5 }),
            new Position(new float[] { 6, 6 }),
        },
        new Position[]
        {
            new Position(new float[] { 7, 7 }),
            new Position(new float[] { 8, 8 }),
            new Position(new float[] { 9, 9 }),
        },
    };
    Position[] ar = null;

    @Override
    protected void setUp() throws Exception
    {
        ar = Map.cat(dar);
    }

    @Override
    protected void tearDown() throws Exception
    {
    }

    public void testCat() throws Exception
    {
        assertEquals(ar[0].pos[0], 1F, 0F);
        assertEquals(ar[1].pos[0], 2F, 0F);
        assertEquals(ar[2].pos[0], 3F, 0F);
        assertEquals(ar[3].pos[0], 4F, 0F);
        assertEquals(ar[4].pos[0], 5F, 0F);
        assertEquals(ar[5].pos[0], 6F, 0F);
        assertEquals(ar[6].pos[0], 7F, 0F);
        assertEquals(ar[7].pos[0], 8F, 0F);
        assertEquals(ar[8].pos[0], 9F, 0F);
        assertEquals(ar.length, 9);
    }

    public void testAverage() throws Exception
    {
        Position ave = PositionUtil.average(ar, 2);
        assertEquals(ave.pos[0], 5F, 0F);
        assertEquals(ave.pos[1], 5F, 0F);
        assertEquals(ave.pos.length, 2);
    }
}
