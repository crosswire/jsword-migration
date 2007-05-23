package org.crosswire.common.diff;

/* Test harness for diff_match_patch.java
 *
 * Version 2.2, May 2007
 * If debugging errors, start with the first reported error, 
 * subsequent tests often rely on earlier ones.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;


public class DifferenceEngineTest extends TestCase
{
    protected void setUp()
    {
    }

    public void testDiffPath()
    {
        // Single letters
        // Trace a path from back to front.
        DifferenceEngine generator = new DifferenceEngine();
        List v_map = new ArrayList();
        Set row_set = new HashSet();
        row_set.add("0,0"); //$NON-NLS-1$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,1"); row_set.add("1,0"); //$NON-NLS-1$ //$NON-NLS-2$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,2"); row_set.add("2,0"); row_set.add("2,2"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,3"); row_set.add("2,3"); row_set.add("3,0"); row_set.add("4,3"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,4"); row_set.add("2,4"); row_set.add("4,0"); row_set.add("4,4"); row_set.add("5,3"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,5"); row_set.add("2,5"); row_set.add("4,5"); row_set.add("5,0"); row_set.add("6,3"); row_set.add("6,5"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,6"); row_set.add("2,6"); row_set.add("4,6"); row_set.add("6,6"); row_set.add("7,5"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        v_map.add(row_set);

        List diffs = diffList(new Object[] { new Difference(EditType.INSERT, "W"), new Difference(EditType.DELETE, "A"), new Difference(EditType.EQUAL, "1"), new Difference(EditType.DELETE, "B"), new Difference(EditType.EQUAL, "2"), new Difference(EditType.INSERT, "X"), new Difference(EditType.DELETE, "C"), new Difference(EditType.EQUAL, "3"), new Difference(EditType.DELETE, "D") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
        assertEquals("diff_path1: Single letters.", diffs, generator.path1(v_map, "A1B2C3D", "W12X3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Trace a path from front to back.
        v_map.remove(v_map.size() - 1);
        diffs = diffList(new Object[] { new Difference(EditType.EQUAL, "4"), new Difference(EditType.DELETE, "E"), new Difference(EditType.INSERT, "Y"), new Difference(EditType.EQUAL, "5"), new Difference(EditType.DELETE, "F"), new Difference(EditType.EQUAL, "6"), new Difference(EditType.DELETE, "G"), new Difference(EditType.INSERT, "Z") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
        assertEquals("diff_path2: Single letters.", diffs, generator.path2(v_map, "4E5F6G", "4Y56Z")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Double letters
        // Trace a path from back to front.
        v_map = new ArrayList();
        row_set = new HashSet();
        row_set.add("0,0"); //$NON-NLS-1$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,1"); row_set.add("1,0"); //$NON-NLS-1$ //$NON-NLS-2$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,2"); row_set.add("1,1"); row_set.add("2,0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,3"); row_set.add("1,2"); row_set.add("2,1"); row_set.add("3,0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,4"); row_set.add("1,3"); row_set.add("3,1"); row_set.add("4,0"); row_set.add("4,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        v_map.add(row_set);

        diffs = diffList(new Object[] { new Difference(EditType.INSERT, "WX"), new Difference(EditType.DELETE, "AB"), new Difference(EditType.EQUAL, "12") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("diff_path1: Double letters.", diffs, generator.path1(v_map, "AB12", "WX12")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Trace a path from front to back.
        v_map = new ArrayList();
        row_set = new HashSet();
        row_set.add("0,0"); //$NON-NLS-1$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("0,1"); row_set.add("1,0"); //$NON-NLS-1$ //$NON-NLS-2$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("1,1"); row_set.add("2,0"); row_set.add("2,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("2,1"); row_set.add("2,5"); row_set.add("3,0"); row_set.add("3,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        v_map.add(row_set);
        row_set = new HashSet();
        row_set.add("2,6"); row_set.add("3,5"); row_set.add("4,4"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        v_map.add(row_set);

        diffs = diffList(new Object[] { new Difference(EditType.DELETE, "CD"), new Difference(EditType.EQUAL, "34"), new Difference(EditType.INSERT, "YZ") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        assertEquals("diff_path2: Double letters.", diffs, generator.path2(v_map, "CD34", "34YZ")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testTimeout()
    {
        DifferenceEngine.setTimeout(0.001f); // 1ms
        // This test may fail on extremely fast computers.  If so, just increase the text lengths.
        assertNull("diff_main: Timeout.", new DifferenceEngine("`Twas brillig, and the slithy toves\nDid gyre and gimble in the wabe:\nAll mimsy were the borogoves,\nAnd the mome raths outgrabe.", "I am the very model of a modern major general,\nI've information vegetable, animal, and mineral,\nI know the kings of England, and I quote the fights historical,\nFrom Marathon to Waterloo, in order categorical.").generate()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        DifferenceEngine.setTimeout(0);
    }
    // Private function for quickly building lists of diffs.
    private static List diffList(Object[] diffs)
    {
        List myDiffList = new ArrayList();
        myDiffList.addAll(Arrays.asList(diffs));
        return myDiffList;
    }
}
