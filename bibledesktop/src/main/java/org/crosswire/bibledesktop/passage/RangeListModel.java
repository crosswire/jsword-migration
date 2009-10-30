/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
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
package org.crosswire.bibledesktop.passage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractListModel;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * The RangeListModel class gives access to a Passage as a list of ranges via a
 * ListModel.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class RangeListModel extends AbstractListModel {
    /**
     * Create a RangeListModel specifying whether to list the ranges bounded by
     * Chapter or not at all.
     * 
     * @param theRestriction
     *            Do we chop at chapter boundaries
     */
    public RangeListModel(RestrictionType theRestriction) {
        restrict = theRestriction;
        ranges = new ArrayList();
    }

    /**
     * Change the restrictions we are using. Must be one of:
     * <code>RestrictType.NONE</code>, or <code>RestrictType.CHAPTER</code>
     * 
     * @param restrict
     *            The new restrictions
     */
    public void setRestriction(RestrictionType restrict) {
        this.restrict = restrict;

        refresh();
    }

    /**
     * Return the current Range Restriction
     */
    public RestrictionType getRestriction() {
        return restrict;
    }

    /**
     * Returns the length of the list.
     * 
     * @return The number of verses/ranges in the list
     */
    public int getSize() {
        return ranges.size();
    }

    /**
     * Returns the value at the specified index.
     * 
     * @param index
     *            The index (based at 0) of the element to fetch
     * @return The required verse/range
     */
    public Object getElementAt(int index) {
        return ranges.get(index);
    }

    /**
     * Accessor for the current passage
     */
    public final void setPassage(Passage newRef) {
        fireIntervalRemoved(this, 0, getSize());

        ref = newRef;

        if (ref != null) {
            ref.optimizeReads();
        }

        refresh();

        fireIntervalAdded(this, 0, getSize());
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage() {
        return ref;
    }

    private void refresh() {
        ranges.clear();
        if (ref != null) {
            Iterator iter = ref.rangeIterator(restrict);
            while (iter.hasNext()) {
                ranges.add(iter.next());
            }
        }
    }

    /**
     * The Passage that we are modeling
     */
    private Passage ref;

    /**
     * The list of ranges in the passage.
     */
    private List ranges;

    /**
     * If we are modeling in groups, do we break at chapter/book boundaries
     */
    private RestrictionType restrict;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3761692273179964725L;
}
