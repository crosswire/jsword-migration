
package org.crosswire.jsword.passage;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.crosswire.common.util.LogicError;

/**
 * Similar to a Passage, but that stores a ranking for each of the
 * Verses that it contains.
 *
 * <p>NOTE(joe): Specify how passage ranks work. Currently there is no well
 * defined spec for what the rank of a verse means - it is just an int. Since
 * this number is expoed in 2 places (getNameAndTally() and getTallyFor()) we
 * should specify what the numbers mean. Trouble is most tallies come from
 * searches where the numbers only have relative meaning.</p>
 *
 * <p>This class exactly implements the Passage interface when the
 * ordering is set to ORDER_BIBLICAL, however an additional setting of
 * ORDER_TALLY sorts the verses by the rank in this tally.
 *
 * <p>Calling <code>tally.add(Gen 1:1); tally.add(Gen 1:1);</code> is
 * redundant for a Passage however a PassageTally will increase the rank
 * of Gen 1:1, there are additional methods <code>unAdd()</code> and
 * <code>unAddAll()</code> that do the reverse, of decreasing the rank of
 * the specified verse(s).</p>
 *
 * <p>The point is to allow a search for "God loves us, and gave Jesus to
 * die to save us" to correctly identify John 3:16. So we are using fuzzy
 * matching big style, but I think this will be very useful.</p>
 *
 * <p>How should we rank VerseRanges? We could use a sum of the ranks of
 * the verses in a range or the maximum value of a range. The former would
 * seem to be more mathematically correct, but I think that the latter is
 * better because: the concept of max value is preserved, because a wide
 * blurred match is generally not as good as a sharply defined one.</p>
 *
 * <p>Should we be going for a PassageTallyFactory type approach? Of the
 * 3 implentations of Passage, The RangedPassage does not make sense
 * here, and a PassageTally will not have the range of uses that a
 * Passage has, so I think there is more likely to be a correct answer.
 * So right now the answer is no.</p>
 *
 * <p>Memory considerations: The BitSet approach will always use a
 * <code>int[31000]</code> = 128k of memory.<br />
 * The Distinct approach will be n * int[4] where n is the number of
 * verses stored. I expect most searches to have at least n=1000. Also
 * 128k<br />
 * Given this, (A Distinct style PassageTally will usually use more
 * memory than a BitSet sytle PassageTally) And the intuative result
 * that the BitSet will be faster, I'm going to start by implementing
 * the latter only.</p>
 * 
 * <p>To think about - I've upped the MAX_TALLY to 20000 to help the new
 * mapper program. I'm not sure why it was originally 100?
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
 * @see docs.Licence
 * @author Joe Walker [joe at eireneh dot com]
 * @version $Id$
 */
public class PassageTally extends AbstractPassage
{
    /**
     * Create an empty PassageTally
     */
    public PassageTally()
    {
    }

    /**
     * Create a Verse from a human readable string. The opposite
     * of toString()
     * @param refs The text to interpret
     * @throws NoSuchVerseException If refs is invalid
     */
    public PassageTally(String refs) throws NoSuchVerseException
    {
        super(refs);
        addVerses(refs);
    }

    /**
     * Set how we sort the verses we output. The options are:<ul>
     * <li>ORDER_BIBLICAL: Natural Biblical order</li>
     * <li>ORDER_TALLY: In an order specified by this class</li>
     * </ul>
     * @param order the sort order
     */
    public void setOrdering(int order)
    {
        if (order != ORDER_BIBLICAL && order != ORDER_TALLY)
            throw new IllegalArgumentException(PassageUtil.getResource("tally_error_order"));

        this.order = order;
    }

    /**
     * Get how we sort the verses we output.
     * @return the sort order
     */
    public int getOrdering()
    {
        return order;
    }

    /**
     * Get a copy of ourselves.
     * @return A complete copy of ourselves
     * @exception java.lang.CloneNotSupportedException We don't do this but our kids might
     */
    public Object clone() throws CloneNotSupportedException
    {
        // This gets us a shallow copy
        PassageTally copy = (PassageTally) super.clone();

        copy.board = (int[]) board.clone();

        return copy;
    }

    /**
     * Simply bounce to getName() to help String concatenation.
     * @return a String containing a description of the verses
     */
    public String toString()
    {
        return getName(0);
    }

    /**
     * A Human readable version of the PassageTally.
     * Uses short books names, and the shortest possible rendering eg "Mat 3:1-4"
     * @return a String containing a description of the verses
     */
    public String getName()
    {
        return getName(0);
    }

    /**
     * A Human readable version of the verse list. Uses short books names,
     * and the shortest possible rendering eg "Mat 3:1-4, 6"
     * @param max_count The number of matches to return, 0 gives all matches
     * @return a String containing a description of the verses
     */
    public String getName(int max_count)
    {
        if (PassageUtil.isPersistentNaming() && original_name != null)
        {
            return original_name;
        }

        StringBuffer retcode = new StringBuffer();

        try
        {
            if (order == ORDER_BIBLICAL)
            {
                Iterator it = rangeIterator();
                Verse current = null;
                while (it.hasNext())
                {
                    VerseRange range = (VerseRange) it.next();
                    retcode.append(range.getName(current));

                    if (it.hasNext())
                        retcode.append(REF_PREF_DELIM);

                    current = range.getStart();
                }
            }
            else
            {
                if (max_count == 0)
                    max_count = Integer.MAX_VALUE;

                Iterator it = new OrderedVerseIterator();
                Verse current = null;
                int count = 0;

                while (it.hasNext() && count < max_count)
                {
                    Verse verse = (Verse) it.next();
                    retcode.append(verse.getName(current));

                    current = verse;
                    count++;

                    if (it.hasNext() && count < max_count)
                        retcode.append(Passage.REF_PREF_DELIM);
                }
            }
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }

        return retcode.toString();
    }

    /**
     * A Human readable version of the PassageTally.
     * Uses short books names, and the shortest possible rendering eg "Mat 3:1-4"
     * @return a String containing a description of the verses
     */
    public String getNameAndTally()
    {
        return getNameAndTally(0);
    }

    /**
     * A Human readable version of the PassageTally.
     * Uses short books names, and the shortest possible rendering eg "Mat 3:1-4"
     * @param max_count The number of matches to return, 0 gives all matches
     * @return a String containing a description of the verses
     */
    public String getNameAndTally(int max_count)
    {
        StringBuffer retcode = new StringBuffer();
        if (max_count == 0) max_count = Integer.MAX_VALUE;

        try
        {
            OrderedVerseIterator it = new OrderedVerseIterator();
            int count = 0;

            while (it.hasNext() && count < max_count)
            {
                Verse verse = (Verse) it.next();
                retcode.append(verse.getName()+" ("+(100*it.lastRank()/max)+"%)");

                count++;

                if (it.hasNext() && count < max_count)
                    retcode.append(Passage.REF_PREF_DELIM);
            }
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }

        return retcode.toString();
    }

    /**
     * Iterate through the verse elements in the current sort order
     * @return A verse Iterator
     */
    public Iterator verseIterator()
    {
        if (order == ORDER_BIBLICAL)
            return new VerseIterator();
        else
            return new OrderedVerseIterator();
    }

    /**
     * Iterate through the range elements in the current sort order
     * @return A range Iterator
     */
    public Iterator rangeIterator()
    {
        if (order == ORDER_BIBLICAL)
            return new VerseRangeIterator();
        else
            return new OrderedVerseRangeIterator();
    }

    /**
     * Does this tally contain all the specified verses?
     * @param that The verses to test for
     * @return true if all the verses exist in this tally
     */
    public boolean contains(VerseBase that)
    {
        Verse[] verses = toVerseArray(that);

        for (int i=0; i<verses.length; i++)
        {
            if (board[verses[i].getOrdinal()-1] == 0)
                return false;
        }

        return true;
    }

    /**
     * The ranking given to a specific verse
     * @param verse The verse to get the ranking of
     * @return The rank of the verse in question
     */
    public int getTallyOf(Verse verse)
    {
        return board[verse.getOrdinal()-1];
    }

    /**
     * What is the index of the give verse in the current ordering scheme
     * @param verse The verse to get the index of
     * @return The index of the verse or -1 if the verse was not found
     */
    public int getIndexOf(Verse verse)
    {
        int reply = 0;

        Iterator it = verseIterator();
        while (it.hasNext())
        {
            if (verse.equals(it.next()))
                return reply;

            reply++;
        }

        return -1;
    }

    /**
     * Add/Increment this verses in the rankings
     * @param that The verses to add/increment
     */
    public void add(VerseBase that)
    {
        optimizeWrites();

        alterVerseBase(that, 1);
        fireIntervalAdded(this, null, null);
    }

    /**
     * DONT USE THIS. It makes public something of the ratings scheme which
     * is not generally recommended. This method is likely to be removed at
     * a moments notice, and it only here to keep Mapper happy.
     * Add/Increment this verses in the rankings
     * @param that The verses to add/increment
     * @param count The amount to increment by
     */
    public void add(VerseBase that, int count)
    {
        optimizeWrites();

        alterVerseBase(that, count);
        fireIntervalAdded(this, null, null);
    }

    /**
     * Remove/Decrement this verses in the rankings
     * @param that The verses to remove/decrement
     */
    public void unAdd(VerseBase that)
    {
        optimizeWrites();

        alterVerseBase(that, -1);
        fireIntervalRemoved(this, null, null);
    }

    /**
     * Remove these verses from the rankings, ie, set
     * their rank to zero.
     * @param that The verses to remove/decrement
     */
    public void remove(VerseBase that)
    {
        optimizeWrites();

        Verse[] verses = toVerseArray(that);

        for (int i=0; i<verses.length; i++)
            kill(verses[i].getOrdinal());

        fireIntervalRemoved(this, null, null);
    }

    /**
     * Add/Increment these verses in the rankings
     * @param that The verses to add/increment
     */
    public void addAll(Passage that)
    {
        optimizeWrites();

        if (that instanceof PassageTally)
        {
            PassageTally that_rt = (PassageTally) that;

            int vib = BibleInfo.versesInBible();
            for (int i=0; i<vib; i++)
            {
                increment(i+1, that_rt.board[i]);
            }

            incrementMax(that_rt.max);
        }
        else
        {
            Iterator it = that.verseIterator();

            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                increment(verse.getOrdinal(), 1);
            }

            incrementMax(1);
        }

        fireIntervalAdded(this, null, null);
    }

    /**
     * Remove/Decrement these verses in the rankings
     * @param that The verses to remove/decrement
     */
    public void unAddAll(Passage that)
    {
        optimizeWrites();

        if (that instanceof PassageTally)
        {
            PassageTally that_rt = (PassageTally) that;

            int vib = BibleInfo.versesInBible();
            for (int i=0; i<vib; i++)
            {
                increment(i, -that_rt.board[i-1]);
            }
        }
        else
        {
            Iterator it = that.verseIterator();

            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                increment(verse.getOrdinal(), -1);
            }
        }

        fireIntervalRemoved(this, null, null);

        // Just because we've decremented some doesn't
        // change the max. So we don't need to do:
        // incrementMax(-1);
    }

    /**
     * Remove/Decrement these verses in the rankings
     * @param that The verses to remove/decrement
     */
    public void removeAll(Passage that)
    {
        optimizeWrites();

        if (that instanceof PassageTally)
        {
            PassageTally that_rt = (PassageTally) that;

            int vib = BibleInfo.versesInBible();
            for (int i=0; i<vib; i++)
            {
                if (that_rt.board[i] != 0)
                    kill(i+1);
            }
        }
        else
        {
            Iterator it = that.verseIterator();

            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();
                kill(verse.getOrdinal());
            }
        }

        fireIntervalRemoved(this, null, null);

        // Just because we've decremented some doesn't
        // change the max. So we don't need to do:
        // incrementMax(-1);
    }

    /**
     * Removes all of the Verses from this Passage.
     */
    public void clear()
    {
        optimizeWrites();

        int vib = BibleInfo.versesInBible();
        for (int i=0; i<vib; i++)
        {
            board[i] = 0;
        }

        fireIntervalRemoved(this, null, null);
    }

    /**
     * Ensures that there are a maximum of <code>count</code> Verses in
     * this Passage. If there were more than <code>count</code> Verses
     * then a new Passage is created containing the Verses from
     * <code>count</code>+1 onwards. If there was not greater than
     * <code>count</code> in the Passage, then the passage remains
     * unchanged, and null is returned.
     * @param count The maximum number of Verses to allow in this collection
     * @return A new Passage conatining the remaining verses or null
     * @see Verse
     */
    public Passage trimVerses(int count)
    {
        optimizeWrites();

        Passage remainder = null;
        int i = 0;
        boolean overflow = false;

        try
        {
            remainder = (Passage) this.clone();

            Iterator it = verseIterator();
            while (it.hasNext())
            {
                Verse verse = (Verse) it.next();

                if (i > count)
                {
                    remove(verse);
                    overflow = true;
                }
                else
                {
                    remainder.remove(verse);
                }

                i++;
            }
        }
        catch (CloneNotSupportedException ex)
        {
            throw new LogicError(ex);
        }

        if (overflow)   return remainder;
        else            return null;
    }

    /**
     * Take the verses in the tally and give them all and equal rank of 1.
     * After this method has executed then both sorting methods for a.
     */
    public void flatten()
    {
        optimizeWrites();

        int vib = BibleInfo.versesInBible();
        for (int i=0; i<vib; i++)
        {
            if (board[i] != 0)
                board[i] = 1;
        }

        max = 1;
    }

    /**
     * Widen the range of the verses in this list. This is primarily for
     * "find x within n verses of y" type applications.
     * @param verses The number of verses to widen by
     * @param restrict How should we restrict the blurring?
     * @see Passage
     */
    public void blur(int verses, int restrict)
    {
        optimizeWrites();

        raiseNormalizeProtection();

        if (verses < 0)
            throw new IllegalArgumentException(PassageUtil.getResource("tally_error_blur"));

        if (restrict != RESTRICT_NONE)
        {
            log.warn("Restrict="+restrict+" is not properly supported.");

            // This is a bit of a cheat, but there is no way I'm going
            // to do the maths to speed up the restricted version
            try
            {
                PassageTally temp = (PassageTally) this.clone();
                Iterator it = temp.rangeIterator();

                while (it.hasNext())
                {
                    VerseRange range = (VerseRange) it.next();
                    for (int i=0; i<=verses; i++)
                    {
                        add(new VerseRange(range, i, i, restrict));
                    }
                }
            }
            catch (CloneNotSupportedException ex)
            {
                throw new Error(PassageUtil.getResource("error_logic"));
            }
        }
        else
        {
            int[] new_board = new int[BibleInfo.versesInBible()];

            int vib = BibleInfo.versesInBible();
            for (int i=0; i<vib; i++)
            {
                if (board[i] != 0)
                {
                    // This could be re-written more simply:
                    //   for (int j=-verses; j<=verses; j++)
                    //   {
                    //       int k = i+j;
                    //       if (k >= 0 && k < BibleInfo.versesInBible())
                    //           new_board[k] += board[i]+verses-mod(j);
                    //   }
                    // However splitting the loop in 2 will speed it
                    // up quite a bit.

                    for (int j=-verses; j<0; j++)
                    {
                        int k = i+j;
                        if (k >= 0)
                            new_board[k] += board[i]+verses+j;
                    }

                    new_board[i] += board[i]+verses;

                    for (int j=1; j<=verses; j++)
                    {
                        int k = i+j;
                        if (k < vib)
                            new_board[k] += board[i]+verses-j;
                    }
                }
            }

            board = new_board;
        }

        resetMax();

        lowerNormalizeProtection();
        fireIntervalAdded(this, null, null);
    }

    /**
     * Sometimes we end up not knowing what the max is - this makes sure
     * we know accurately
     */
    private void resetMax()
    {
        optimizeWrites();

        int vib = BibleInfo.versesInBible();
        max = 0;
        for (int i=0; i<vib; i++)
        {
            if (board[i] > max)
                max = board[i];
        }
    }

    /**
     * Increment/Decrement this verses in the rankings
     * @param that The verses to add/increment
     * @param tally The amount to increment/decrement by
     */
    private void alterVerseBase(VerseBase that, int tally)
    {
        Verse[] verses = toVerseArray(that);

        for (int i=0; i<verses.length; i++)
            increment(verses[i].getOrdinal(), tally);

        if (tally > 0)
            incrementMax(tally);
    }

    /**
     * Increment a verse by an amount
     * @param ord The verse to increment
     * @param tally The amount to inrease by
     */
    private final void increment(int ord, int tally)
    {
        board[ord-1] += tally;
        if (board[ord-1] > MAX_TALLY) board[ord-1] = MAX_TALLY;
        if (board[ord-1] < 0) board[ord-1] = 0;
    }

    /**
     * Increment a verse by an amount
     * @param tally The amount to inrease by
     */
    private final void incrementMax(int tally)
    {
        max += tally;
        if (max > MAX_TALLY) max = MAX_TALLY;
        if (max < 0) max = 0;
    }

    /**
     * Wipe the rank of the given verse to zero
     * @param ord The verse to increment
     */
    private final void kill(int ord)
    {
        board[ord-1] = 0;
    }

    /** Sort in Biblical order */
    public static final int ORDER_BIBLICAL = 0;

    /** Sort in tally rank order */
    public static final int ORDER_TALLY = 1;

    /** The highest tally possible */
    public static final int MAX_TALLY = 20000;

    /** The tallyboard itself */
    private int[] board = new int[BibleInfo.versesInBible()];

    /** The maximum tally possible */
    private int max = 0;

    /** The maximum tally possible */
    private int order = ORDER_BIBLICAL;

    /** The log stream */
    protected static Logger log = Logger.getLogger(PassageTally.class);

    /**
     * Iterate over the Verses in normal verse order
     * @author Joe Walker
     */
    private final class VerseIterator implements Iterator
    {
        /**
         * Find the first unused verse
         */
        public VerseIterator()
        {
            calculateNext();
        }

        /**
         * @return true if the iteration has more Verses
         */
        public boolean hasNext()
        {
            return next <= BibleInfo.versesInBible();
        }

        /**
         * @return the next Verse in the interation
         * @throws NoSuchElementException if hasNext() == false
         */
        public Object next() throws NoSuchElementException
        {
            try
            {
                if (next > BibleInfo.versesInBible())
                    throw new NoSuchElementException();

                Object retcode = new Verse(next);
                calculateNext();

                return retcode;
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
        }

        /**
         * We don't do remove
         * @throws UnsupportedOperationException Every time
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * Find the next bit
         */
        private void calculateNext()
        {
            do
            {
                next++;
            }
            while (next <= BibleInfo.versesInBible() && board[next-1] == 0);
        }

        /** What is the next Verse to be considered */
        private int next = 0;
    }

    /**
     * Iterate over the Verses in order of their rank in the tally
     * @author Joe Walker
     */
    private final class OrderedVerseIterator implements Iterator
    {
        /**
         * Find the first unused verse
         */
        public OrderedVerseIterator()
        {
            TreeSet output = new TreeSet();

            int vib = BibleInfo.versesInBible();
            for (int i=0; i<vib; i++)
            {
                if (board[i] != 0)
                    output.add(new TalliedVerse(i+1, board[i]));
            }

            it = output.iterator();
        }

        /**
         * @return true if the iteration has more Verses
         */
        public boolean hasNext()
        {
            return it.hasNext();
        }

        /**
         * @return the next Verse in the interation
         * @throws NoSuchElementException if hasNext() == false
         */
        public Object next() throws NoSuchElementException
        {
            try
            {
                last = (TalliedVerse) it.next();
                return new Verse(last.ord);
            }
            catch (NoSuchVerseException ex)
            {
                throw new LogicError(ex);
            }
        }

        /**
         * We don't do remove
         * @throws UnsupportedOperationException Every time
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * @return the next Verse in the interation
         * @throws NoSuchElementException if hasNext() == false
         */
        public int lastRank() throws NoSuchElementException
        {
            try
            {
                return last.tally;
            }
            catch (NullPointerException ex)
            {
                throw new NoSuchElementException(PassageUtil.getResource("tally_error_enum"));
            }
        }

        /** So that we can get at the ranking of the given verse */
        private TalliedVerse last;

        /** The Iterator we are converting */
        private Iterator it = null;

        /**
         * Hack to make this work with JDK1.1 as well as JDK1.2
         * This compared 2 Integers
         */
        private class TalliedVerse implements Comparable
        {
            /**
             * Convenience ctor to set the public variables
             * @param ord the verse id
             * @param tally the rank of the verse
             */
            public TalliedVerse(int ord, int tally)
            {
                this.ord = ord;
                this.tally = tally;
            }

            /** The verse id */
            public int ord = 0;

            /** The rank of the verse */
            public int tally = 0;

            /**
             * @param obj The thing to compare against
             * @return 1 means he is earlier than me, -1 means he is later ...
             */
            public int compareTo(Object obj)
            {
                TalliedVerse that = (TalliedVerse) obj;

                if (that.tally == this.tally)
                    return this.ord - that.ord;

                return that.tally - this.tally;
            }
        }
    }

    /**
     * Iterate over the Ranges in order of their rank in the tally
     * @author Joe Walker
     */
    private final class OrderedVerseRangeIterator implements Iterator
    {
        /**
         * Find the first unused verse
         */
        public OrderedVerseRangeIterator()
        {
            TreeSet output = new TreeSet();

            Iterator rit = new VerseRangeIterator();
            while (rit.hasNext())
            {
                VerseRange range = (VerseRange) rit.next();

                // Calculate the maximum rank for a verse
                int rank = 0;
                Iterator vit = range.verseIterator();
                while (vit.hasNext())
                {
                    Verse verse =(Verse) vit.next();
                    int temp = board[verse.getOrdinal()-1];
                    if (temp > rank)
                        rank = temp;
                }

                output.add(new TalliedVerseRange(range, rank));
            }

            it = output.iterator();
        }

        /**
         * @return true if the iteration has more Verses
         */
        public boolean hasNext()
        {
            return it.hasNext();
        }

        /**
         * @return the next Verse in the interation
         * @throws NoSuchElementException if hasNext() == false
         */
        public Object next() throws NoSuchElementException
        {
            last = (TalliedVerseRange) it.next();
            return last.range;
        }

        /**
         * We don't do remove
         * @throws UnsupportedOperationException Every time
         */
        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }

        /**
         * @return the next Verse in the interation
         * @throws NoSuchElementException if hasNext() == false
         */
        public int lastRank() throws NoSuchElementException
        {
            try
            {
                return last.tally;
            }
            catch (NullPointerException ex)
            {
                throw new NoSuchElementException(PassageUtil.getResource("tally_error_enum"));
            }
        }

        /** So that we can get at the ranking of the given verse */
        private TalliedVerseRange last;

        /** The Iterator we are converting */
        private Iterator it = null;

        /**
         * Hack to make this work with JDK1.1 as well as JDK1.2
         * This compared 2 Integers
         */
        private class TalliedVerseRange implements Comparable
        {
            /**
             * Convenience ctor to set the public variables
             * @param range The verserange
             * @param tally The rank of the verse
             */
            public TalliedVerseRange(VerseRange range, int tally)
            {
                this.range = range;
                this.tally = tally;
            }

            /** The verse range */
            public VerseRange range;

            /** The rank of the verse */
            public int tally = 0;

            /**
             * @param obj The thing to compare against
             * @return 1 means he is earlier than me, -1 means he is later ...
             */
            public int compareTo(Object obj)
            {
                TalliedVerseRange that = (TalliedVerseRange) obj;

                if (that.tally == this.tally)
                    return this.range.compareTo(that.range);

                return that.tally - this.tally;
            }
        }
    }
}
