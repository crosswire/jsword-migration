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

import org.crosswire.biblemapper.model.AntiGravityRule;

import junit.framework.TestCase;

/**
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class AntiGravityRuleTest extends TestCase
{

    /**
     * Constructor for AntiGravityRuleTest.
     * @param arg0
     */
    public AntiGravityRuleTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(AntiGravityRuleTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testAddDistanceToTotals() {
        float[] totals = new float[] { 0.0F, 0.0F };
        float[] that = null;
        float[] us = null;

        that = new float[] { 0.1F, 0.1F };
        us = new float[] { 0.2F, 0.2F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.067666754F, 0.000001F);
        assertEquals(totals[1], -0.067666754F, 0.000001F);

        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.135333508F, 0.00001F);
        assertEquals(totals[1], -0.135333508F, 0.00001F);

        that = new float[] { 0.3F, 0.3F };
        us = new float[] { 0.2F, 0.2F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], 0.067666754F, 0.000001F);
        assertEquals(totals[1], 0.067666754F, 0.000001F);

        that = new float[] { 0.1F, 0.3F };
        us = new float[] { 0.2F, 0.2F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.067666754F, 0.0001F);
        assertEquals(totals[1], 0.067666754F, 0.0001F);

        that = new float[] { 0.1F, 0.1F };
        us = new float[] { 0.3F, 0.3F };
        totals = new float[] { 0.0F, 0.0F };
        AntiGravityRule.addDistanceToTotals(that, us, totals);
        assertEquals(totals[0], -0.007497886F, 0.005F);
        assertEquals(totals[1], -0.007497886F, 0.005F);
    }

    public void testGetNewDist() {
        //*
        for (float i=-1f; i<1f; i=i+0.01f)
        {
            System.out.println("f("+i+")="+AntiGravityRule.getNewDistance(i));
        }
        // */

        assertTrue(AntiGravityRule.getNewDistance(-1F) < 0.0001F);
        assertTrue(AntiGravityRule.getNewDistance(-1F) > 0F);

        assertTrue(AntiGravityRule.getNewDistance(0F) > 0.49999F);
        assertTrue(AntiGravityRule.getNewDistance(0F) < 0.50001F);

        assertTrue(AntiGravityRule.getNewDistance(1F) < 0.0001F);
        assertTrue(AntiGravityRule.getNewDistance(1F) < 0F);
    }
}
