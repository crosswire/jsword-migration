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

import javax.swing.AbstractListModel;

import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageEvent;
import org.crosswire.jsword.passage.PassageListType;
import org.crosswire.jsword.passage.PassageListener;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * The PassageListModel class gives access to a Passage via a ListModel.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @see javax.swing.JList
 * @see javax.swing.AbstractListModel
 */
public class PassageListModel extends AbstractListModel implements PassageListener {
    /**
     * Create a PassageListModel (in verse mode) from a Passage.
     * 
     * @exception IllegalArgumentException
     *                If the mode is illegal
     */
    public PassageListModel() {
        this(null, PassageListType.VERSES, RestrictionType.NONE);
    }

    /**
     * Create a PassageListModel (in verse mode) from a Passage.
     * 
     * @param ref
     *            The reference that we are modeling
     * @exception IllegalArgumentException
     *                If the mode is illegal
     */
    public PassageListModel(Passage ref) {
        this(ref, PassageListType.VERSES, RestrictionType.NONE);
    }

    /**
     * Create a PassageListModel from a Passage. We also specify whether to list
     * the individual verses.
     * 
     * @param ref
     *            The reference that we are modeling
     * @param mode
     *            The verse/range mode
     * @param restrict
     *            When we are in range mode, do we chop at chapter boundries
     * @exception IllegalArgumentException
     *                If the mode is illegal
     */
    public PassageListModel(Passage ref, PassageListType mode, RestrictionType restrict) {
        this.ref = ref;
        this.restrict = restrict;

        setMode(mode);
        setPassage(ref);
    }

    /**
     * Change the mode we are operating in.
     * 
     * @param mode
     *            The new operation mode
     * @exception IllegalArgumentException
     *                If the mode is illegal
     */
    public final void setMode(PassageListType mode) {
        this.mode = mode;
    }

    /**
     * Return the mode we are operating in.
     * 
     * @return The operation mode
     */
    public PassageListType getMode() {
        return mode;
    }

    /**
     * Change the restrictions we are using when the mode is LIST_RANGES. Must
     * be one of: <code>RESTRICT_NONE</code>, <code>RESTRICT_BOOK</code> or
     * <code>RESTRICT_CHAPTER</code>
     * 
     * @param restrict
     *            The new restrictions
     */
    public void setRestriction(RestrictionType restrict) {
        this.restrict = restrict;
    }

    /**
     * Return the current txtRestrict
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
        return size;
    }

    /**
     * Recompute the size from the reference.
     */
    private void recomputeSize() {
        size = mode.count(ref, restrict);
    }

    /**
     * Returns the value at the specified index.
     * 
     * @param index
     *            The index (based at 0) of the element to fetch
     * @return The required verse/range
     */
    public Object getElementAt(int index) {
        return mode.getElementAt(ref, index, restrict);
    }

    /**
     * Sent after stuff has been added to the Passage. More info about what and
     * where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapuslating the event information
     */
    public void versesAdded(PassageEvent ev) {
        recomputeSize();

        fireIntervalRemoved(this, 0, size);
        fireIntervalAdded(this, 0, getSize());

        // it would be good to be able to do something like:
        // fireIntervalAdded(ev.getSource(), ev.getLowerIndex(),
        // ev.getUpperIndex());
    }

    /**
     * Sent after stuff has been removed from the Passage. More info about what
     * and where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapuslating the event information
     */
    public void versesRemoved(PassageEvent ev) {
        recomputeSize();

        fireIntervalRemoved(this, 0, size);
        fireIntervalAdded(this, 0, getSize());

        // it would be good to be able to do something like:
        // fireIntervalRemoved(ev.getSource(), ev.getLowerIndex(),
        // ev.getUpperIndex());
    }

    /**
     * Sent after verses have been simultaneously added and removed from the
     * Passage. More info about what and where can be had from the Event
     * 
     * @param ev
     *            a PassageEvent encapuslating the event information
     */
    public void versesChanged(PassageEvent ev) {
        recomputeSize();

        fireIntervalRemoved(this, 0, size);
        fireIntervalAdded(this, 0, getSize());

        // it would be good to be able to do something like:
        // fireContentsChanged(ev.getSource(), ev.getLowerIndex(),
        // ev.getUpperIndex());
    }

    /**
     * Accessor for the current passage
     */
    public final void setPassage(Passage newRef) {
        fireIntervalRemoved(this, 0, size);

        if (this.ref != null) {
            this.ref.removePassageListener(this);
        }

        ref = newRef;

        if (ref != null) {
            ref.optimizeReads();
            ref.addPassageListener(this);
        }

        recomputeSize();

        fireIntervalAdded(this, 0, getSize());
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage() {
        return ref;
    }

    /**
     * The Passage that we are modelling
     */
    private Passage ref;

    /**
     * We need to cache the passage size because we need to report on changed
     * ranges, when informed by the passage that has already changed
     */
    private int size;

    /**
     * Are we modelling in groups or individually
     */
    private PassageListType mode;

    /**
     * If we are modelling in groups, do we break at chapter/book boundries
     */
    private RestrictionType restrict;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3761692273179964725L;
}
