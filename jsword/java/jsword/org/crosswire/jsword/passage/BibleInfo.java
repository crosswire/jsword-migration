
package org.crosswire.jsword.passage;

import java.util.Locale;
import java.util.MissingResourceException;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.LogicError;
import org.crosswire.common.util.Reporter;

/**
 * BibleInfo is a static class that deals with Book number conversions and similar.
 * We start counting at 1 for books, chapters and verses (so Genesis=1, Revelation=66).
 * However internally books start counting at 0 and go up to 65.
 * <p>I've considered merging BibleInfo and PassageUtil since they are both supporting
 * static only classes. However they are both non-trivial, so together they would
 * be large, and there is a good dividing line between the 2.
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
public class BibleInfo
{
    /**
     * Ensure that we can not be instansiated
     */
    private BibleInfo()
    {
    }

    /**
     * How do we report the names of the books?.
     * These are static. This is on the assumption that we will not want to have
     * different sections of the app using a different format. I expect this to
     * be a good assumption, and it saves passing a Book class around everywhere.
     * BibleInfo.MIXED is not allowed
     * @param book_case The new case to use for reporting book names
     * @exception IllegalArgumentException If the case is not between 0 and 2
     * @see Passage
     * @see #getCase()
     */
    public static final void setCase(int book_case)
    {
        if (book_case < 0 || book_case > 2)
        {
            Object[] params = new Object[] { new Integer(book_case) };
            throw new IllegalArgumentException(Msg.ERROR_CASE.toString(params));
        }

        BibleInfo.book_case = book_case;
    }

    /**
     * How do we report the names of the books?.
     * @return The current case setting
     * @see Passage
     * @see BibleInfo#setCase(int)
     */
    public static final int getCase()
    {
        return BibleInfo.book_case;
    }

    /**
     * Get the full name of a book (e.g. "Genesis").
     * Altered by the case setting (see setBookCase())
     * @param book The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public static final String getLongBookName(int book) throws NoSuchVerseException
    {
        try
        {
            switch (book_case)
            {
            case PassageConstants.CASE_LOWER:
                return full_books_lower[book-1];
            case PassageConstants.CASE_UPPER:
                return full_books_upper[book-1];
            default:
                return full_books[book-1];
            }
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Get the short name of a book (e.g. "Gen").
     * Altered by the case setting (see setBookCase())
     * @param book The book number (1-66)
     * @return The short name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public static final String getShortBookName(int book) throws NoSuchVerseException
    {
        try
        {
            switch (book_case)
            {
            case PassageConstants.CASE_LOWER:
                return short_books_lower[book-1];
            case PassageConstants.CASE_UPPER:
                return short_books_upper[book-1];
            default:
                return short_books[book-1];
            }
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Get the OSIS name for a book.
     * @param book The book number (1-66)
     * @return the OSIS defined short name for a book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public static final String getOSISName(int book) throws NoSuchVerseException
    {
        try
        {
            return osis_books[book-1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Get number of a book from its name.
     * @param find The string to identify
     * @return The book number (1 to 66)
     * @exception NoSuchVerseException If the text can not be matched
     */
    public static final int getBookNumber(String find) throws NoSuchVerseException
    {
        String match = find.toLowerCase();

        // Check this isn't a number.
        // And that it does not start with any of the numeric book markers
        if (!PassageUtil.containsLetter(find))
        {
            boolean numeric_book = false;
            for (int i=0; i<PassageConstants.VERSE_NUMERIC_BOOK.length; i++)
            {
                if (find.startsWith(PassageConstants.VERSE_NUMERIC_BOOK[i]))
                {
                    numeric_book = true;
                }
            }

            if (!numeric_book)
            {
                throw new NoSuchVerseException(Msg.BOOKS_NUMBER, new Object[] { find });
            }
        }

        // Does it match a long version of the book or a short version
        for (int i=0; i<full_books.length; i++)
        {
            if (full_books_lower[i].startsWith(match))
            {
                return i + 1;
            }
            if (match.startsWith(short_books_lower[i]))
            {
                return i + 1;
            }
        }

        // Or does it match one of the alternative versions
        for (int i=0; i<alt_books.length; i++)
        {
            for (int j=0; j<alt_books[i].length; j++)
            {
                if (match.startsWith(alt_books_lower[i][j]))
                {
                    return i + 1;
                }
            }
        }

        // if we start with a book number id mark
        for (int i=0; i<PassageConstants.VERSE_NUMERIC_BOOK.length; i++)
        {
            if (find.startsWith(PassageConstants.VERSE_NUMERIC_BOOK[i]))
            {
                int book = Integer.parseInt(find.substring(PassageConstants.VERSE_NUMERIC_BOOK[i].length()));
                if (book < 1 || book > 66)
                {
                    throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
                }
                return book;
            }
        }

        throw new NoSuchVerseException(Msg.BOOKS_FIND, new Object[] { find });
    }

    /**
     * Is the given string a valid book name. If this method returns true then
     * getBookNumber() will return a number and not throw an exception.
     * @param find The string to identify
     * @return The book number (1 to 66)
     */
    public static final boolean isBookName(String find)
    {
        String match = find.toLowerCase();

        if (!PassageUtil.containsLetter(find)) return false;

        // This could be sped up with less of the toLowerCase()
        for (int i=0; i<full_books.length; i++)
        {
            if (full_books_lower[i].startsWith(match))
            {
                return true;
            }
            if (match.startsWith(short_books_lower[i]))
            {
                return true;
            }

            for (int j=0; j<alt_books[i].length; j++)
            {
                if (match.startsWith(alt_books_lower[i][j])) return true;
            }
        }

        return false;
    }

    /**
     * Count the books in the Bible.
     * @return 66 always - the number of books in the Bible
     */
    public static final int booksInBible()
    {
        return BOOKS_IN_BIBLE;
    }

    /**
     * Count the chapters in the Bible.
     * @return 1189 always - the number of chapters in the Bible
     */
    public static final int chaptersInBible()
    {
        return CHAPTERS_IN_BIBLE;
    }

    /**
     * Count the verses in the Bible.
     * This counts possible verses, so this number is not affected
     * by some versions missing out some verses as 'there in error'
     * @return 31102 always - the number of verses in the Bible
     */
    public static final int versesInBible()
    {
        return VERSES_IN_BIBLE;
    }

    /**
     * Count the chapters in this book.
     * @param book The book part of the reference.
     * @return The number of chapters
     * @exception NoSuchVerseException If the book number is not valid
     */
    public static final int chaptersInBook(int book) throws NoSuchVerseException
    {
        try
        {
            return CHAPTERS_IN_BOOK[book-1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Count the verses in a chapter.
     * @param book The book part of the reference.
     * @param chapter The current chapter
     * @return The number of verses
     * @exception NoSuchVerseException If the book or chapter number is not valid
     */
    public static final int versesInChapter(int book, int chapter) throws NoSuchVerseException
    {
        try
        {
            return VERSES_IN_CHAPTER[book-1][chapter-1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right

            Object[] params = new Object[] { new Integer(book), new Integer(chapter) };
            throw new NoSuchVerseException(Msg.BOOKS_BOOKCHAP, params);
        }
    }

    /**
     * Count the verses in a book.
     * @param book The book part of the reference.
     * @return The number of verses
     * @exception NoSuchVerseException If the book number is not valid
     */
    public static final int versesInBook(int book) throws NoSuchVerseException
    {
        try
        {
            return VERSES_IN_BOOK[book-1];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }
    }

    /**
     * Where does this verse come in the Bible. Starting with Gen 1:1 as
     * number 1 counting up one per verse and not resetting at each new
     * chapter.
     * @param book The book part of the reference.
     * @param chapter The current chapter
     * @param verse The current verse
     * @return The ordinal number of verses
     * @exception NoSuchVerseException If the reference is illegal
     */
    public static final int verseOrdinal(int book, int chapter, int verse) throws NoSuchVerseException
    {
        validate(book, chapter, verse);
        return ORDINAL_AT_START_OF_CHAPTER[book-1][chapter-1] + verse - 1;
    }

    /**
     * Where does this verse come in the Bible. Starting with Gen 1:1 as
     * number 1 counting up one per verse and not resetting at each new
     * chapter.
     * @param ref An array of 3 ints, book, chapter, verse
     * @return The ordinal number of the verse
     * @exception NoSuchVerseException If the reference is illegal
     */
    public static final int verseOrdinal(int[] ref) throws NoSuchVerseException
    {
        if (ref.length != 3)
        {
            throw new NoSuchVerseException(Msg.BOOKS_ORDINAL);
        }

        return verseOrdinal(ref[0], ref[1], ref[2]);
    }

    /**
     * Where does this verse come in the Bible. Starting with Gen 1:1 as
     * number 1 counting up one per verse and not resetting at each new
     * chapter.
     * @param ordinal The ordinal number of the verse
     * @return An array of 3 ints, book, chapter, verse
     * @exception NoSuchVerseException If the reference is illegal
     */
    public static final int[] decodeOrdinal(int ordinal) throws NoSuchVerseException
    {
        if (ordinal < 1 || ordinal > BibleInfo.versesInBible())
        {
            Object[] params = new Object[] { new Integer(BibleInfo.versesInBible()), new Integer(ordinal) };
            throw new NoSuchVerseException(Msg.BOOKS_DECODE, params);
        }

        for (int b=BOOKS_IN_BIBLE; b>0; b--)
        {
            if (ordinal >= ORDINAL_AT_START_OF_BOOK[b-1])
            {
                int cib = BibleInfo.chaptersInBook(b);
                for (int c=cib; c>0; c--)
                {
                    if (ordinal >= ORDINAL_AT_START_OF_CHAPTER[b-1][c-1])
                    {
                        return new int[] { b, c, ordinal - ORDINAL_AT_START_OF_CHAPTER[b-1][c-1] + 1 };
                    }
                }
            }
        }

        throw new LogicError();
    }

    /**
     * Does the following represent a real verse?.
     * It is code like this that makes me wonder if I18 is done well/worth
     * doing. All this code does is check if the numbers are valid, but the
     * exception handling code is huge :(
     * @param book The book part of the reference.
     * @param chapter The chapter part of the reference.
     * @param verse The verse part of the reference.
     * @exception NoSuchVerseException If the reference is illegal
     */
    public static final void validate(int book, int chapter, int verse) throws NoSuchVerseException
    {
        // Check the book
        if (book < 1 || book > BOOKS_IN_BIBLE)
        {
            throw new NoSuchVerseException(Msg.BOOKS_BOOK, new Object[] { new Integer(book) });
        }

        // Check the chapter
        if (chapter < 1 || chapter > chaptersInBook(book))
        {
            Object[] params = new Object[]
            {
                new Integer(chaptersInBook(book)),
                full_books[book-1], new Integer(chapter),
            };
            throw new NoSuchVerseException(Msg.BOOKS_CHAPTER, params);
        }

        // Check the verse
        if (verse < 1 || verse > versesInChapter(book, chapter))
        {
            Object[] params = new Object[]
            {
                new Integer(versesInChapter(book, chapter)),
                full_books[book-1],
                new Integer(chapter),
                new Integer(verse),
            };
            throw new NoSuchVerseException(Msg.BOOKS_VERSE, params);
        }
    }

    /**
     * Does the following represent a real verse?
     * @param ref An array of 3 ints, book, chapter, verse
     * @exception NoSuchVerseException If the reference is illegal
     */
    public static final void validate(int[] ref) throws NoSuchVerseException
    {
        if (ref.length != 3)
        {
            throw new NoSuchVerseException(Msg.BOOKS_ORDINAL);
        }

        validate(ref[BOOK], ref[CHAPTER], ref[VERSE]);
    }

    /**
     * Fix up these verses so that they are as valid a possible. This is currently
     * done so that we can say "Gen 1:1" + 31 = "Gen 1:32" and "Gen 1:32".patch()
     * is "Gen 2:1".
     * <p>There is another patch system that allows us to use large numbers to
     * mean "the end of" so "Gen 1:32".otherPatch() gives "Gen 1:31". This could
     * be useful to allow the user to enter things like "Gen 1:99" meaning
     * the end of the chapter. Or  "Isa 99:1" to mean the last chapter in Isaiah
     * verse 1 or even "Rev 99:99" to mean the last verse in the Bible.
     * <p>However I have not implemented this because I've used a different convention:
     * "Gen 1:$" (OLB compatible) or "Gen 1:ff" (common comentary usage) to
     * mean the end of the chapter - So the functionality is there anyway.
     * <p>I think that getting into the habit of typing "Gen 1:99" is bad. It could
     * be the source of surprises "Psa 119:99" is not what you'd might expect,
     * and neither is "Psa 99:1" is you wanted the last chapter in Psalms - expecting
     * us to type "Psa 999:1" seems like we're getting silly.
     * <p>However dispite this maybe we should provide the functionality anyway.
     * @param ref An array of 3 ints, book, chapter, verse. This array will be changed.
     * @return The original array that has been patched.
     */
    public static final int[] patch(int[] ref)
    {
        try
        {
            // If they are too small
            if (ref[BOOK] <= 0)
            {
                ref[BOOK] = 1;
            }
            if (ref[CHAPTER] <= 0)
            {
                ref[CHAPTER] = 1;
            }
            if (ref[VERSE] <= 0)
            {
                ref[VERSE] = 1;
            }

            // If they are too big
            if (ref[BOOK] > BOOKS_IN_BIBLE)
            {
                ref[BOOK] = Names.REVELATION;
                ref[CHAPTER] = chaptersInBook(ref[BOOK]);
                ref[VERSE] = versesInChapter(ref[BOOK], ref[CHAPTER]);
                return ref;
            }

            while (ref[CHAPTER] > chaptersInBook(ref[BOOK]))
            {
                ref[CHAPTER] -= chaptersInBook(ref[BOOK]);
                ref[BOOK] += 1;

                if (ref[BOOK] > BOOKS_IN_BIBLE)
                {
                    ref[BOOK] = Names.REVELATION;
                    ref[CHAPTER] = chaptersInBook(ref[BOOK]);
                    ref[VERSE] = versesInChapter(ref[BOOK], ref[CHAPTER]);
                    return ref;
                }
            }

            while (ref[VERSE] > versesInChapter(ref[BOOK], ref[CHAPTER]))
            {
                ref[VERSE] -= versesInChapter(ref[BOOK], ref[CHAPTER]);
                ref[CHAPTER] += 1;

                if (ref[CHAPTER] > chaptersInBook(ref[BOOK]))
                {
                    ref[CHAPTER] -= chaptersInBook(ref[BOOK]);
                    ref[BOOK] += 1;

                    if (ref[BOOK] > BOOKS_IN_BIBLE)
                    {
                        ref[BOOK] = Names.REVELATION;
                        ref[CHAPTER] = chaptersInBook(ref[BOOK]);
                        ref[VERSE] = versesInChapter(ref[BOOK], ref[CHAPTER]);
                        return ref;
                    }
                }
            }

            return ref;
        }
        catch (Exception ex)
        {
            throw new LogicError(ex);
        }
    }

    /**
     * How many verses between ref1 and ref2 (inclusive).
     * @param book1 The book part of the first reference.
     * @param chapter1 The chapter part of the first reference.
     * @param verse1 The verse part of the first reference.
     * @param book2 The book part of the second reference.
     * @param chapter2 The chapter part of the second reference.
     * @param verse2 The verse part of the second reference.
     * @return the number of verses
     * @exception NoSuchVerseException If either reference is illegal
     */
    public static final int verseCount(int book1, int chapter1, int verse1, int book2, int chapter2, int verse2) throws NoSuchVerseException
    {
        int verse_ord1 = verseOrdinal(book1, chapter1, verse1);
        int verse_ord2 = verseOrdinal(book2, chapter2, verse2);

        return verse_ord2 - verse_ord1 + 1;
    }

    /**
     * How many verses between ref1 and ref2 (inclusive).
     * @param ref1 An array of 3 ints, book, chapter, verse for the first reference.
     * @param ref2 An array of 3 ints, book, chapter, verse for the second reference.
     * @return the number of verses
     * @exception NoSuchVerseException If either reference is illegal
     */
    public static final int verseCount(int[] ref1, int[] ref2) throws NoSuchVerseException
    {
        if (ref1.length != 3 || ref2.length != 3)
        {
            throw new IllegalArgumentException(PassageUtil.getResource(Msg.BOOKS_ORDINAL));
        }

        return verseCount(ref1[0], ref1[1], ref1[2], ref2[0], ref2[1], ref2[2]);
    }

    /**
     * Is this book part of the Pentateuch?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isPentateuch(int book)
    {
        return book >= Names.GENESIS && book <= Names.DEUTERONOMY;
    }

    /**
     * Is this book part of the OT History?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isHistory(int book)
    {
        return book >= Names.JOSHUA && book <= Names.ESTHER;
    }

    /**
     * Is this book part of the OT History?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isPoetry(int book)
    {
        return book >= Names.JOB && book <= Names.SONGOFSOLOMON;
    }

    /**
     * Is this book part of the major prophets?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isMajorProphet(int book)
    {
        return book >= Names.ISAIAH && book <= Names.DANIEL;
    }

    /**
     * Is this book part of the minor prophets?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isMinorProphet(int book)
    {
        return book >= Names.HOSEA && book <= Names.MALACHI;
    }

    /**
     * Is this book part of the Gospels?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isGospel(int book)
    {
        return book >= Names.MATTHEW && book <= Names.JOHN;
    }

    /**
     * Is this book part of the Gospels or Acts?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isGospelOrActs(int book)
    {
        return book >= Names.MATTHEW && book <= Names.ACTS;
    }

    /**
     * Is this book part of the letters?
     * @param book The book to test
     * @return True if this book is a part of this section
     */
    public static final boolean isLetter(int book)
    {
        return book >= Names.ROMANS && book <= Names.JUDE;
    }

    /**
     * What section is this book a part of?
     * @param book The book to test
     * @return True The section
     * @see BibleInfo.Section
     */
    public static final int getSection(int book)
    {
        // Ordered by section size for speed
        if (isLetter(book))       return Section.LETTERS;          // 21
        if (isHistory(book))      return Section.HISTORY;          // 12
        if (isMinorProphet(book)) return Section.MINOR_PROPHETS;   // 12
        if (isGospelOrActs(book)) return Section.GOSPELS_AND_ACTS; // 5
        if (isPentateuch(book))   return Section.PENTATEUCH;       // 5
        if (isPoetry(book))       return Section.POETRY;           // 5
        if (isMajorProphet(book)) return Section.MAJOR_PROPHETS;   // 5
        return Section.REVELATION;
    }

    /**
     * How many books are there in each of the above sections
     * @param section The section
     * @return The number of books in the given section
     * @see #getSection(int)
     */
    public static final int booksInSection(int section)
    {
        return BOOKS_IN_SECTION[section];
    }

    /**
     * Get the full name of a book (e.g. "Genesis").
     * Altered by the case setting (see setBookCase())
     * @param section The book number (1-66)
     * @return The full name of the book
     * @exception NoSuchVerseException If the book number is not valid
     */
    public static final String getSectionName(int section) throws NoSuchVerseException
    {
        if (section == 0)
        {
            throw new NoSuchVerseException(Msg.BOOKS_SECTION, new Object[] { new Integer(section)});
        }

        try
        {
            switch (book_case)
            {
            case PassageConstants.CASE_LOWER:
                return sections_lower[section-1];
            case PassageConstants.CASE_UPPER:
                return sections_upper[section-1];
            default:
                return sections[section-1];
            }
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            // This is faster than doing the check explicitly, unless
            // The exception is actually thrown, then it is a lot slower
            // I'd like to think that the norm is to get it right
            throw new NoSuchVerseException(Msg.BOOKS_SECTION, new Object[] { new Integer(section) });
        }
    }

    /**
     * How the book names are reported
     */
    private static int book_case = PassageConstants.CASE_SENTANCE;

    /**
     * Handy section finder. There is a bit of moderately bad programming
     * here because org.crosswire.jsword.control.map.swing.GroupVerseColor
     * uses these numbers as an index into an array, so we shouldn't
     * change these numbers without fixing that, however I don't imagine
     * that this section could ever change without breaking
     * GroupVerseColor anyway so I don't see it as a big problem.
     */
    public static class Section
    {
        public static final byte PENTATEUCH = 1;
        public static final byte HISTORY = 2;
        public static final byte POETRY = 3;
        public static final byte MAJOR_PROPHETS = 4;
        public static final byte MINOR_PROPHETS = 5;
        public static final byte GOSPELS_AND_ACTS = 6;
        public static final byte LETTERS = 7;
        public static final byte REVELATION = 8;
    }

    /**
     * Handy book finder
     */
    public static class Names
    {
        public static final byte GENESIS = 1;
        public static final byte EXODUS = 2;
        public static final byte LEVITICUS = 3;
        public static final byte NUMBERS = 4;
        public static final byte DEUTERONOMY = 5;
        public static final byte JOSHUA = 6;
        public static final byte JUDGES = 7;
        public static final byte RUTH = 8;
        public static final byte SAMUEL1 = 9;
        public static final byte SAMUEL2 = 10;
        public static final byte KINGS1 = 11;
        public static final byte KINGS2 = 12;
        public static final byte CHRONICLES1 = 13;
        public static final byte CHRONICLES2 = 14;
        public static final byte EZRA = 15;
        public static final byte NEHEMIAH = 16;
        public static final byte ESTHER = 17;
        public static final byte JOB = 18;
        public static final byte PSALMS = 19;
        public static final byte PROBERBS = 20;
        public static final byte ECCLESIASTES = 21;
        public static final byte SONGOFSOLOMON = 22;
        public static final byte ISAIAH = 23;
        public static final byte JEREMIAH = 24;
        public static final byte LAMENTATIONS = 25;
        public static final byte EZEKIEL = 26;
        public static final byte DANIEL = 27;
        public static final byte HOSEA = 28;
        public static final byte JOEL = 29;
        public static final byte AMOS = 30;
        public static final byte OBDIAH = 31;
        public static final byte JONAH = 32;
        public static final byte MICAH = 33;
        public static final byte NAHUM = 34;
        public static final byte HABAKUK = 35;
        public static final byte ZEPHANIAH = 36;
        public static final byte HAGGAI = 37;
        public static final byte ZECHARIAH = 38;
        public static final byte MALACHI = 39;
        public static final byte MATTHEW = 40;
        public static final byte MARK = 41;
        public static final byte LUKE = 42;
        public static final byte JOHN = 43;
        public static final byte ACTS = 44;
        public static final byte ROMANS = 45;
        public static final byte CORINTHIANS1 = 46;
        public static final byte CORINTHIANS2 = 47;
        public static final byte GALATIANS = 48;
        public static final byte EPHESIANS = 49;
        public static final byte PHILIPPIANS = 50;
        public static final byte COLOSSIANS = 51;
        public static final byte THESSALONIANS1 = 52;
        public static final byte THESSALONIANS2 = 53;
        public static final byte TIMOTHY1 = 54;
        public static final byte TIMOTHY2 = 55;
        public static final byte TITUS = 56;
        public static final byte PHILEMON = 57;
        public static final byte HEBREWS = 58;
        public static final byte JAMES = 59;
        public static final byte PETER1 = 60;
        public static final byte PETER2 = 61;
        public static final byte JOHN1 = 62;
        public static final byte JOHN2 = 63;
        public static final byte JOHN3 = 64;
        public static final byte JUDE = 65;
        public static final byte REVELATION = 66;
    }

    /** Used for methods with int[3] parameters, for the book */
    public static final int BOOK = 0;

    /** Used for methods with int[3] parameters, for the chapter */
    public static final int CHAPTER = 1;

    /** Used for methods with int[3] parameters, for the verse */
    public static final int VERSE = 2;

    /** The full names of the book of the Bible, in mixed case */
    private static String[] full_books =
    {
        "Genesis",
        "Exodus",
        "Leviticus",
        "Numbers",
        "Deuteronomy",
        "Joshua",
        "Judges",
        "Ruth",
        "1 Samuel",
        "2 Samuel",
        "1 Kings",
        "2 Kings",
        "1 Chronicles",
        "2 Chronicles",
        "Ezra",
        "Nehemiah",
        "Esther",
        "Job",
        "Psalms",
        "Proberbs",
        "Ecclesiastes",
        "Song of Solomon",
        "Isaiah",
        "Jeremiah",
        "Lamentations",
        "Ezekiel",
        "Daniel",
        "Hosea",
        "Joel",
        "Amos",
        "Obdiah",
        "Jonah",
        "Micah",
        "Nahum",
        "Habakuk",
        "Zephaniah",
        "Haggai",
        "Zechariah",
        "Malachi",
        "Matthew",
        "Mark",
        "Luke",
        "John",
        "Acts",
        "Romans",
        "1 Corinthians",
        "2 Corinthians",
        "Galatians",
        "Ephesians",
        "Philippians",
        "Colossians",
        "1 Thessalonians",
        "2 Thessalonians",
        "1 Timothy",
        "2 Timothy",
        "Titus",
        "Philemon",
        "Hebrews",
        "James",
        "1 Peter",
        "2 Peter",
        "1 John",
        "2 John",
        "3 John",
        "Jude",
        "Revelation",
    };

    /** Standard shortened names for the book of the Bible, in mixed case */
    private static String[] short_books =
    {
        "Gen",  "Exo",  "Lev",  "Num",  "Deu",  "Jos",  "Judg", "Rut",  "1Sa",  "2Sa",
        "1Ki",  "2Ki",  "1Ch",  "2Ch",  "Ezr",  "Neh",  "Est",  "Job",  "Psa",  "Pro",
        "Ecc",  "Son",  "Isa",  "Jer",  "Lam",  "Eze",  "Dan",  "Hos",  "Joe",  "Amo",
        "Obd",  "Jon",  "Mic",  "Nah",  "Hab",  "Zep",  "Hag",  "Zec",  "Mal",  "Mat",
        "Mar",  "Luk",  "Joh",  "Act",  "Rom",  "1Co",  "2Co",  "Gal",  "Eph",  "Phili",
        "Col",  "1Th",  "2Th",  "1Ti",  "2Ti",  "Tit",  "Phile", "Heb", "Jam",  "1Pe",
        "2Pe",  "1Jo",  "2Jo",  "3Jo",  "Jude", "Rev",
    };

    /** Standard OSIS names for the book of the Bible, in mixed case */
    private static String[] osis_books =
    {
        "Gen",    "Exod",   "Lev",    "Num",    "Deut",   "Josh",   "Judg",
        "Ruth",   "1Sam",   "2Sam",   "1Kgs",   "2Kgs",   "1Chr",   "2Chr",
        "Ezra",   "Neh",    "Esth",   "Job",    "Pss",    "Prov",   "Eccl",
        "Song",   "Isa",    "Jer",    "Lam",    "Ezek",   "Dan",    "Hos",
        "Joel",   "Amos",   "Obad",   "Jonah",  "Mic",    "Nah",    "Hab",
        "Zeph",   "Hag",    "Zech",   "Mal",    "Matt",   "Mark",   "Luke",
        "John",   "Acts",   "Rom",    "1Cor",   "2Cor",   "Gal",    "Eph",
        "Phil",   "Col",    "1Thess", "2Thess", "1Tim",   "2Tim",   "Titus",
        "Phlm",   "Heb",    "Jas",    "1Pet",   "2Pet",   "1John",  "2John",
        "3John",  "Jude",   "Rev",  
    };

    /** Alternative shortened names for the book of the Bible, in mixed case */
    private static String[][] alt_books =
    {
        /* Gen */   { },
        /* Exo */   { },
        /* Lev */   { },
        /* Num */   { },
        /* Deu */   { "Dt" },
        /* Jos */   { },
        /* Judg */  { "Jdg" },
        /* Rut */   { "Rth" },
        /* 1Sa */   { },
        /* 2Sa */   { },
        /* 1Ki */   { "1Kgs" },
        /* 2Ki */   { "2Kgs" },
        /* 1Ch */   { "1Chr" },
        /* 2Ch */   { "2Chr" },
        /* Ezr */   { },
        /* Neh */   { },
        /* Est */   { },
        /* Job */   { },
        /* Psa */   { "Pss", "Ps" },
        /* Pro */   { },
        /* Ecc */   { "Qoh", },
        /* Son */   { "SS", "Canticle", "Can" },
        /* Isa */   { },
        /* Jer */   { },
        /* Lam */   { },
        /* Eze */   { },
        /* Dan */   { },
        /* Hos */   { },
        /* Joe */   { },
        /* Amo */   { },
        /* Obd */   { },
        /* Jon */   { "Jnh" },
        /* Mic */   { },
        /* Nah */   { },
        /* Hab */   { },
        /* Zep */   { },
        /* Hag */   { },
        /* Zec */   { },
        /* Mal */   { },
        /* Mat */   { "Mt" },
        /* Mar */   { "Mk" },
        /* Luk */   { "Lk" },
        /* Joh */   { "Jn", "Jhn" },
        /* Act */   { },
        /* Rom */   { },
        /* 1Co */   { },
        /* 2Co */   { },
        /* Gal */   { },
        /* Eph */   { },
        /* Phili */ { "Php" },
        /* Col */   { },
        /* 1Th */   { },
        /* 2Th */   { },
        /* 1Ti */   { "1Tm" },
        /* 2Ti */   { "2Tm" },
        /* Tit */   { },
        /* Phile */ { "Phm", "Phlm" },
        /* Heb */   { },
        /* Jam */   { "Jas" },
        /* 1Pe */   { "1Pt" },
        /* 2Pe */   { "1Pt" },
        /* 1Jo */   { "1Jn" },
        /* 2Jo */   { "2Jn" },
        /* 3Jo */   { "3Jn" },
        /* Jude */  { },
        /* Rev */   { "Rv" },
    };

    /** Standard names for the sections */
    private static String[] sections =
    {
        "Pentateuch",
        "History",
        "Poetry",
        "MajorProphets",
        "MinorProphets",
        "GospelsAndActs",
        "Letters",
        "Revelation",
    };

    /** The full names of the book of the Bible, in lower case, generated at run time */
    private static String[] full_books_lower;

    /** Standard shortened names for the book of the Bible, in lower case, generated at run time */
    private static String[] short_books_lower;

    /** Alternative shortened names for the book of the Bible, in lower case, generated at run time */
    private static String[][] alt_books_lower;

    /** The full names of the book of the Bible, in upper case, generated at run time */
    private static String[] full_books_upper;

    /** Standard shortened names for the book of the Bible, in upper case, generated at run time */
    private static String[] short_books_upper;

    /** Standard Bible section names, in upper case, generated at run time */
    private static String[] sections_upper;

    /** Standard Bible section names, in lower case, generated at run time */
    private static String[] sections_lower;

    /* Alternative shortened names for the book of the Bible, in upper case, generated at run time */
    // Not needed as the lower version was only there to speed up searching.
    // private static String[][] alt_books_upper;

    /** Constant for the number of books in the Bible */
    private static final int BOOKS_IN_BIBLE = 66;

    /** Constant for the number of chapters in the Bible */
    private static final int CHAPTERS_IN_BIBLE = 1189;

    /** Constant for the number of chapters in each book */
    private static final int[] CHAPTERS_IN_BOOK =
    {
        50, 40, 27, 36, 34, 24, 21,  4, 31, 24,
        22, 25, 29, 36, 10, 13, 10, 42, 150, 31,
        12,  8, 66, 52,  5, 48, 12, 14,  3,  9,
        1,  4,  7,  3,  3,  3,  2, 14,  4, 28,
        16, 24, 21, 28, 16, 16, 13,  6,  6,  4,
        4,  5,  3,  6,  4,  3,  1, 13,  5,  5,
        3,  5,  1,  1,  1, 22,
    };

    /** Constant for the number of verses in the Bible */
    private static final int VERSES_IN_BIBLE = 31102;

    /** Constant for the number of verses in each book */
    private static final int[] VERSES_IN_BOOK =
    {
        1533, 1213,  859, 1288,  959,  658,  618,   85,  810,  695,
        816,  719,  942,  822,  280,  406,  167, 1070, 2461,  915,
        222,  117, 1292, 1364,  154, 1273,  357,  197,   73,  146,
        21,   48,  105,   47,   56,   53,   38,  211,   55, 1071,
        678, 1151,  879, 1007,  433,  437,  257,  149,  155,  104,
        95,   89,   47,  113,   83,   46,   25,  303,  108,  105,
        61,  105,   13,   14,   25,  404,
    };

    /** Constant for the number of verses in each chapter */
    private static final int[][] VERSES_IN_CHAPTER =
    {
        { 31, 25, 24, 26, 32, 22, 24, 22, 29, 32, 32, 20, 18, 24, 21, 16, 27, 33, 38, 18, 34, 24, 20, 67, 34, 35, 46, 22, 35, 43, 55, 32, 20, 31, 29, 43, 36, 30, 23, 23, 57, 38, 34, 34, 28, 34, 31, 22, 33, 26 },
        { 22, 25, 22, 31, 23, 30, 25, 32, 35, 29, 10, 51, 22, 31, 27, 36, 16, 27, 25, 26, 36, 31, 33, 18, 40, 37, 21, 43, 46, 38, 18, 35, 23, 35, 35, 38, 29, 31, 43, 38 },
        { 17, 16, 17, 35, 19, 30, 38, 36, 24, 20, 47,  8, 59, 57, 33, 34, 16, 30, 37, 27, 24, 33, 44, 23, 55, 46, 34 },
        { 54, 34, 51, 49, 31, 27, 89, 26, 23, 36, 35, 16, 33, 45, 41, 50, 13, 32, 22, 29, 35, 41, 30, 25, 18, 65, 23, 31, 40, 16, 54, 42, 56, 29, 34, 13 },
        { 46, 37, 29, 49, 33, 25, 26, 20, 29, 22, 32, 32, 18, 29, 23, 22, 20, 22, 21, 20, 23, 30, 25, 22, 19, 19, 26, 68, 29, 20, 30, 52, 29, 12 },
        { 18, 24, 17, 24, 15, 27, 26, 35, 27, 43, 23, 24, 33, 15, 63, 10, 18, 28, 51,  9, 45, 34, 16, 33 },
        { 36, 23, 31, 24, 31, 40, 25, 35, 57, 18, 40, 15, 25, 20, 20, 31, 13, 31, 30, 48, 25 },
        { 22, 23, 18, 22 },
        { 28, 36, 21, 22, 12, 21, 17, 22, 27, 27, 15, 25, 23, 52, 35, 23, 58, 30, 24, 42, 15, 23, 29, 22, 44, 25, 12, 25, 11, 31, 13 },
        { 27, 32, 39, 12, 25, 23, 29, 18, 13, 19, 27, 31, 39, 33, 37, 23, 29, 33, 43, 26, 22, 51, 39, 25 },
        { 53, 46, 28, 34, 18, 38, 51, 66, 28, 29, 43, 33, 34, 31, 34, 34, 24, 46, 21, 43, 29, 53 },
        { 18, 25, 27, 44, 27, 33, 20, 29, 37, 36, 21, 21, 25, 29, 38, 20, 41, 37, 37, 21, 26, 20, 37, 20, 30 },
        { 54, 55, 24, 43, 26, 81, 40, 40, 44, 14, 47, 40, 14, 17, 29, 43, 27, 17, 19,  8, 30, 19, 32, 31, 31, 32, 34, 21, 30 },
        { 17, 18, 17, 22, 14, 42, 22, 18, 31, 19, 23, 16, 22, 15, 19, 14, 19, 34, 11, 37, 20, 12, 21, 27, 28, 23,  9, 27, 36, 27, 21, 33, 25, 33, 27, 23 },
        { 11, 70, 13, 24, 17, 22, 28, 36, 15, 44 },
        { 11, 20, 32, 23, 19, 19, 73, 18, 38, 39, 36, 47, 31 },
        { 22, 23, 15, 17, 14, 14, 10, 17, 32, 03 },
        { 22, 13, 26, 21, 27, 30, 21, 22, 35, 22, 20, 25, 28, 22, 35, 22, 16, 21, 29, 29, 34, 30, 17, 25,  6, 14, 23, 28, 25, 31, 40, 22, 33, 37, 16, 33, 24, 41, 30, 24, 34, 17 },
        {  6, 12,  8,  8, 12, 10, 17,  9, 20, 18,  7,  8,  6,  7,  5, 11, 15, 50, 14,  9, 13, 31,  6, 10, 22, 12, 14,  9, 11, 12, 24, 11, 22, 22, 28, 12, 40, 22, 13, 17, 13, 11,  5, 26, 17, 11,  9, 14, 20, 23, 19,  9,  6,  7, 23, 13, 11, 11, 17, 12,  8, 12, 11, 10, 13, 20,  7, 35, 36,  5, 24, 20, 28, 23, 10, 12, 20, 72, 13, 19, 16,  8, 18, 12, 13, 17,  7, 18, 52, 17, 16, 15,  5, 23, 11, 13, 12,  9,  9,  5,  8, 28, 22, 35, 45, 48, 43, 13, 31,  7, 10, 10,  9,  8, 18, 19,  2, 29, 176,  7,  8,  9,  4,  8,  5,  6,  5,  6,  8,  8,  3, 18,  3,  3, 21, 26, 9,  8, 24, 13, 10,  7, 12, 15, 21, 10, 20, 14,  9,  6 },
        { 33, 22, 35, 27, 23, 35, 27, 36, 18, 32, 31, 28, 25, 35, 33, 33, 28, 24, 29, 30, 31, 29, 35, 34, 28, 28, 27, 28, 27, 33, 31 },
        { 18, 26, 22, 16, 20, 12, 29, 17, 18, 20, 10, 14 },
        { 17, 17, 11, 16, 16, 13, 13, 14 },
        { 31, 22, 26, 06, 30, 13, 25, 22, 21, 34, 16, 06, 22, 32,  9, 14, 14, 07, 25, 06, 17, 25, 18, 23, 12, 21, 13, 29, 24, 33,  9, 20, 24, 17, 10, 22, 38, 22,  8, 31, 29, 25, 28, 28, 25, 13, 15, 22, 26, 11, 23, 15, 12, 17, 13, 12, 21, 14, 21, 22, 11, 12, 19, 12, 25, 24 },
        { 19, 37, 25, 31, 31, 30, 34, 22, 26, 25, 23, 17, 27, 22, 21, 21, 27, 23, 15, 18, 14, 30, 40, 10, 38, 24, 22, 17, 32, 24, 40, 44, 26, 22, 19, 32, 21, 28, 18, 16, 18, 22, 13, 30,  5, 28,  7, 47, 39, 46, 64, 34 },
        { 22, 22, 66, 22, 22 },
        { 28, 10, 27, 17, 17, 14, 27, 18, 11, 22, 25, 28, 23, 23,  8, 63, 24, 32, 14, 49, 32, 31, 49, 27, 17, 21, 36, 26, 21, 26, 18, 32, 33, 31, 15, 38, 28, 23, 29, 49, 26, 20, 27, 31, 25, 24, 23, 35 },
        { 21, 49, 30, 37, 31, 28, 28, 27, 27, 21, 45, 13 },
        { 11, 23, 05, 19, 15, 11, 16, 14, 17, 15, 12, 14, 16,  9 },
        { 20, 32, 21 },
        { 15, 16, 15, 13, 27, 14, 17, 14, 15 },
        { 21 },
        { 17, 10, 10, 11 },
        { 16, 13, 12, 13, 15, 16, 20 },
        { 15, 13, 19 },
        { 17, 20, 19 },
        { 18, 15, 20 },
        { 15, 23 },
        { 21, 13, 10, 14, 11, 15, 14, 23, 17, 12, 17, 14,  9, 21 },
        { 14, 17, 18, 06 },
        { 25, 23, 17, 25, 48, 34, 29, 34, 38, 42, 30, 50, 58, 36, 39, 28, 27, 35, 30, 34, 46, 46, 39, 51, 46, 75, 66, 20 },
        { 45, 28, 35, 41, 43, 56, 37, 38, 50, 52, 33, 44, 37, 72, 47, 20 },
        { 80, 52, 38, 44, 39, 49, 50, 56, 62, 42, 54, 59, 35, 35, 32, 31, 37, 43, 48, 47, 38, 71, 56, 53 },
        { 51, 25, 36, 54, 47, 71, 53, 59, 41, 42, 57, 50, 38, 31, 27, 33, 26, 40, 42, 31, 25 },
        { 26, 47, 26, 37, 42, 15, 60, 40, 43, 48, 30, 25, 52, 28, 41, 40, 34, 28, 41, 38, 40, 30, 35, 27, 27, 32, 44, 31 },
        { 32, 29, 31, 25, 21, 23, 25, 39, 33, 21, 36, 21, 14, 23, 33, 27 },
        { 31, 16, 23, 21, 13, 20, 40, 13, 27, 33, 34, 31, 13, 40, 58, 24 },
        { 24, 17, 18, 18, 21, 18, 16, 24, 15, 18, 33, 21, 14 },
        { 24, 21, 29, 31, 26, 18 },
        { 23, 22, 21, 32, 33, 24 },
        { 30, 30, 21, 23 },
        { 29, 23, 25, 18 },
        { 10, 20, 13, 18, 28 },
        { 12, 17, 18 },
        { 20, 15, 16, 16, 25, 21 },
        { 18, 26, 17, 22 },
        { 16, 15, 15 },
        { 25 },
        { 14, 18, 19, 16, 14, 20, 28, 13, 28, 39, 40, 29, 25 },
        { 27, 26, 18, 17, 20 },
        { 25, 25, 22, 19, 14 },
        { 21, 22, 18 },
        { 10, 29, 24, 21, 21 },
        { 13 },
        { 14 },
        { 25 },
        { 20, 29, 22, 11, 14, 17, 17, 13, 21, 11, 19, 17, 18, 20,  8, 21, 18, 24, 21, 15, 27, 21 }
    };

    /** Constant for the ordinal number of the first verse in each book */
    private static final int[] ORDINAL_AT_START_OF_BOOK =
    {
        1,  1534,  2747,  3606,  4894,  5853,  6511,  7129,  7214,  8024,
        8719,  9535, 10254, 11196, 12018, 12298, 12704, 12871, 13941, 16402,
        17317, 17539, 17656, 18948, 20312, 20466, 21739, 22096, 22293, 22366,
        22512, 22533, 22581, 22686, 22733, 22789, 22842, 22880, 23091, 23146,
        24217, 24895, 26046, 26925, 27932, 28365, 28802, 29059, 29208, 29363,
        29467, 29562, 29651, 29698, 29811, 29894, 29940, 29965, 30268, 30376,
        30481, 30542, 30647, 30660, 30674, 30699,
    };

    /**
     * Constant for the ordinal number of the first verse in each chapter.
     * Warning if you regenerate this code (from the code at the bottom of
     * this module) then you will need to cut the psalms line in half to
     * get it to compile under JBuilder.
     */
    private static final int[][] ORDINAL_AT_START_OF_CHAPTER =
    {
        {     1,    32,    57,    81,   107,   139,   161,   185,   207,   236,   268,   300,   320,   338,   362,   383,   399,   426,   459,   497,   515,   549,   573,   593,   660,   694,   729,   775,   797,   832,   875,   930,   962,   982,  1013,  1042,  1085,  1121,  1151,  1174,  1197,  1254,  1292,  1326,  1360,  1388,  1422,  1453,  1475,  1508, },
        {  1534,  1556,  1581,  1603,  1634,  1657,  1687,  1712,  1744,  1779,  1808,  1818,  1869,  1891,  1922,  1949,  1985,  2001,  2028,  2053,  2079,  2115,  2146,  2179,  2197,  2237,  2274,  2295,  2338,  2384,  2422,  2440,  2475,  2498,  2533,  2568,  2606,  2635,  2666,  2709, },
        {  2747,  2764,  2780,  2797,  2832,  2851,  2881,  2919,  2955,  2979,  2999,  3046,  3054,  3113,  3170,  3203,  3237,  3253,  3283,  3320,  3347,  3371,  3404,  3448,  3471,  3526,  3572, },
        {  3606,  3660,  3694,  3745,  3794,  3825,  3852,  3941,  3967,  3990,  4026,  4061,  4077,  4110,  4155,  4196,  4246,  4259,  4291,  4313,  4342,  4377,  4418,  4448,  4473,  4491,  4556,  4579,  4610,  4650,  4666,  4720,  4762,  4818,  4847,  4881, },
        {  4894,  4940,  4977,  5006,  5055,  5088,  5113,  5139,  5159,  5188,  5210,  5242,  5274,  5292,  5321,  5344,  5366,  5386,  5408,  5429,  5449,  5472,  5502,  5527,  5549,  5568,  5587,  5613,  5681,  5710,  5730,  5760,  5812,  5841, },
        {  5853,  5871,  5895,  5912,  5936,  5951,  5978,  6004,  6039,  6066,  6109,  6132,  6156,  6189,  6204,  6267,  6277,  6295,  6323,  6374,  6383,  6428,  6462,  6478, },
        {  6511,  6547,  6570,  6601,  6625,  6656,  6696,  6721,  6756,  6813,  6831,  6871,  6886,  6911,  6931,  6951,  6982,  6995,  7026,  7056,  7104, },
        {  7129,  7151,  7174,  7192, },
        {  7214,  7242,  7278,  7299,  7321,  7333,  7354,  7371,  7393,  7420,  7447,  7462,  7487,  7510,  7562,  7597,  7620,  7678,  7708,  7732,  7774,  7789,  7812,  7841,  7863,  7907,  7932,  7944,  7969,  7980,  8011, },
        {  8024,  8051,  8083,  8122,  8134,  8159,  8182,  8211,  8229,  8242,  8261,  8288,  8319,  8358,  8391,  8428,  8451,  8480,  8513,  8556,  8582,  8604,  8655,  8694, },
        {  8719,  8772,  8818,  8846,  8880,  8898,  8936,  8987,  9053,  9081,  9110,  9153,  9186,  9220,  9251,  9285,  9319,  9343,  9389,  9410,  9453,  9482, },
        {  9535,  9553,  9578,  9605,  9649,  9676,  9709,  9729,  9758,  9795,  9831,  9852,  9873,  9898,  9927,  9965,  9985, 10026, 10063, 10100, 10121, 10147, 10167, 10204, 10224, },
        { 10254, 10308, 10363, 10387, 10430, 10456, 10537, 10577, 10617, 10661, 10675, 10722, 10762, 10776, 10793, 10822, 10865, 10892, 10909, 10928, 10936, 10966, 10985, 11017, 11048, 11079, 11111, 11145, 11166, },
        { 11196, 11213, 11231, 11248, 11270, 11284, 11326, 11348, 11366, 11397, 11416, 11439, 11455, 11477, 11492, 11511, 11525, 11544, 11578, 11589, 11626, 11646, 11658, 11679, 11706, 11734, 11757, 11766, 11793, 11829, 11856, 11877, 11910, 11935, 11968, 11995, },
        { 12018, 12029, 12099, 12112, 12136, 12153, 12175, 12203, 12239, 12254, },
        { 12298, 12309, 12329, 12361, 12384, 12403, 12422, 12495, 12513, 12551, 12590, 12626, 12673, },
        { 12704, 12726, 12749, 12764, 12781, 12795, 12809, 12819, 12836, 12868, },
        { 12871, 12893, 12906, 12932, 12953, 12980, 13010, 13031, 13053, 13088, 13110, 13130, 13155, 13183, 13205, 13240, 13262, 13278, 13299, 13328, 13357, 13391, 13421, 13438, 13463, 13469, 13483, 13506, 13534, 13559, 13590, 13630, 13652, 13685, 13722, 13738, 13771, 13795, 13836, 13866, 13890, 13924, },
        { 13941, 13947, 13959, 13967, 13975, 13987, 13997, 14014, 14023, 14043, 14061, 14068, 14076, 14082, 14089, 14094, 14105, 14120, 14170, 14184, 14193, 14206, 14237, 14243, 14253, 14275, 14287, 14301, 14310, 14321, 14333, 14357, 14368, 14390, 14412, 14440, 14452, 14492, 14514, 14527, 14544, 14557, 14568, 14573, 14599, 14616, 14627, 14636, 14650, 14670, 14693, 14712, 14721, 14727, 14734, 14757, 14770, 14781, 14792, 14809, 14821, 14829, 14841, 14852, 14862, 14875, 14895, 14902, 14937, 14973, 14978, 15002,
          15022, 15050, 15073, 15083, 15095, 15115, 15187, 15200, 15219, 15235, 15243, 15261, 15273, 15286, 15303, 15310, 15328, 15380, 15397, 15413, 15428, 15433, 15456, 15467, 15480, 15492, 15501, 15510, 15515, 15523, 15551, 15573, 15608, 15653, 15701, 15744, 15757, 15788, 15795, 15805, 15815, 15824, 15832, 15850, 15869, 15871, 15900, 16076, 16083, 16091, 16100, 16104, 16112, 16117, 16123, 16128, 16134, 16142, 16150, 16153, 16171, 16174, 16177, 16198, 16224, 16233, 16241, 16265, 16278, 16288, 16295, 16307, 16322, 16343, 16353, 16373, 16387, 16396, },
        { 16402, 16435, 16457, 16492, 16519, 16542, 16577, 16604, 16640, 16658, 16690, 16721, 16749, 16774, 16809, 16842, 16875, 16903, 16927, 16956, 16986, 17017, 17046, 17081, 17115, 17143, 17171, 17198, 17226, 17253, 17286, },
        { 17317, 17335, 17361, 17383, 17399, 17419, 17431, 17460, 17477, 17495, 17515, 17525, },
        { 17539, 17556, 17573, 17584, 17600, 17616, 17629, 17642, },
        { 17656, 17687, 17709, 17735, 17741, 17771, 17784, 17809, 17831, 17852, 17886, 17902, 17908, 17930, 17962, 17971, 17985, 17999, 18006, 18031, 18037, 18054, 18079, 18097, 18120, 18132, 18153, 18166, 18195, 18219, 18252, 18261, 18281, 18305, 18322, 18332, 18354, 18392, 18414, 18422, 18453, 18482, 18507, 18535, 18563, 18588, 18601, 18616, 18638, 18664, 18675, 18698, 18713, 18725, 18742, 18755, 18767, 18788, 18802, 18823, 18845, 18856, 18868, 18887, 18899, 18924, },
        { 18948, 18967, 19004, 19029, 19060, 19091, 19121, 19155, 19177, 19203, 19228, 19251, 19268, 19295, 19317, 19338, 19359, 19386, 19409, 19424, 19442, 19456, 19486, 19526, 19536, 19574, 19598, 19620, 19637, 19669, 19693, 19733, 19777, 19803, 19825, 19844, 19876, 19897, 19925, 19943, 19959, 19977, 19999, 20012, 20042, 20047, 20075, 20082, 20129, 20168, 20214, 20278, },
        { 20312, 20334, 20356, 20422, 20444, },
        { 20466, 20494, 20504, 20531, 20548, 20565, 20579, 20606, 20624, 20635, 20657, 20682, 20710, 20733, 20756, 20764, 20827, 20851, 20883, 20897, 20946, 20978, 21009, 21058, 21085, 21102, 21123, 21159, 21185, 21206, 21232, 21250, 21282, 21315, 21346, 21361, 21399, 21427, 21450, 21479, 21528, 21554, 21574, 21601, 21632, 21657, 21681, 21704, },
        { 21739, 21760, 21809, 21839, 21876, 21907, 21935, 21963, 21990, 22017, 22038, 22083, },
        { 22096, 22107, 22130, 22135, 22154, 22169, 22180, 22196, 22210, 22227, 22242, 22254, 22268, 22284, },
        { 22293, 22313, 22345, },
        { 22366, 22381, 22397, 22412, 22425, 22452, 22466, 22483, 22497, },
        { 22512, },
        { 22533, 22550, 22560, 22570, },
        { 22581, 22597, 22610, 22622, 22635, 22650, 22666, },
        { 22686, 22701, 22714, },
        { 22733, 22750, 22770, },
        { 22789, 22807, 22822, },
        { 22842, 22857, },
        { 22880, 22901, 22914, 22924, 22938, 22949, 22964, 22978, 23001, 23018, 23030, 23047, 23061, 23070, },
        { 23091, 23105, 23122, 23140, },
        { 23146, 23171, 23194, 23211, 23236, 23284, 23318, 23347, 23381, 23419, 23461, 23491, 23541, 23599, 23635, 23674, 23702, 23729, 23764, 23794, 23828, 23874, 23920, 23959, 24010, 24056, 24131, 24197, },
        { 24217, 24262, 24290, 24325, 24366, 24409, 24465, 24502, 24540, 24590, 24642, 24675, 24719, 24756, 24828, 24875, },
        { 24895, 24975, 25027, 25065, 25109, 25148, 25197, 25247, 25303, 25365, 25407, 25461, 25520, 25555, 25590, 25622, 25653, 25690, 25733, 25781, 25828, 25866, 25937, 25993, },
        { 26046, 26097, 26122, 26158, 26212, 26259, 26330, 26383, 26442, 26483, 26525, 26582, 26632, 26670, 26701, 26728, 26761, 26787, 26827, 26869, 26900, },
        { 26925, 26951, 26998, 27024, 27061, 27103, 27118, 27178, 27218, 27261, 27309, 27339, 27364, 27416, 27444, 27485, 27525, 27559, 27587, 27628, 27666, 27706, 27736, 27771, 27798, 27825, 27857, 27901, },
        { 27932, 27964, 27993, 28024, 28049, 28070, 28093, 28118, 28157, 28190, 28211, 28247, 28268, 28282, 28305, 28338, },
        { 28365, 28396, 28412, 28435, 28456, 28469, 28489, 28529, 28542, 28569, 28602, 28636, 28667, 28680, 28720, 28778, },
        { 28802, 28826, 28843, 28861, 28879, 28900, 28918, 28934, 28958, 28973, 28991, 29024, 29045, },
        { 29059, 29083, 29104, 29133, 29164, 29190, },
        { 29208, 29231, 29253, 29274, 29306, 29339, },
        { 29363, 29393, 29423, 29444, },
        { 29467, 29496, 29519, 29544, },
        { 29562, 29572, 29592, 29605, 29623, },
        { 29651, 29663, 29680, },
        { 29698, 29718, 29733, 29749, 29765, 29790, },
        { 29811, 29829, 29855, 29872, },
        { 29894, 29910, 29925, },
        { 29940, },
        { 29965, 29979, 29997, 30016, 30032, 30046, 30066, 30094, 30107, 30135, 30174, 30214, 30243, },
        { 30268, 30295, 30321, 30339, 30356, },
        { 30376, 30401, 30426, 30448, 30467, },
        { 30481, 30502, 30524, },
        { 30542, 30552, 30581, 30605, 30626, },
        { 30647, },
        { 30660, },
        { 30674, },
        { 30699, 30719, 30748, 30770, 30781, 30795, 30812, 30829, 30842, 30863, 30874, 30893, 30910, 30928, 30948, 30956, 30977, 30995, 31019, 31040, 31055, 31082, },
    };

    /**
     * The number of books in each section
     */
    private static final int[] BOOKS_IN_SECTION =
    {
        0, // Does not exist
        5, // Pentateuch = 1;
        12, // History = 2;
        5, // Poetry = 3;
        5, // MajorProphets = 4;
        12, // MinorProphets = 5;
        5, // GospelsAndActs = 6;
        21, // Letters = 7;
        1, // Revelation = 8;
    };

    /** The log stream */
    private static final Logger log = Logger.getLogger(BibleInfo.class);

    /**
     * Set up the Internationalization (Msg), and cache the upper and
     * lower versions of the book names. I have not used I18Ned strings
     * for this code, because it copes with Msg failures. The bad news
     * is that a non-english speaker may have trouble understanding the
     * error message.
     */
    static
    {
        try
        {
            // LATER(joe): Move these from PassageUtil lookups into this class
            // as a variant on the Msg/Enum theme.
            String key = "";
            boolean success = true;

            for (int i=0; i<BibleInfo.BOOKS_IN_BIBLE; i++)
            {
                // Read any customized versions of the Book names
                try
                {
                    key = "books_long_"+(i+1);
                    full_books[i] = PassageUtil.getResource(key);
                }
                catch (MissingResourceException ex)
                {
                    log.debug("Can't find resource for '"+key+"' in Locale '" + Locale.getDefault().toString() + "'");
                    success = false;
                }

                try
                {
                    key = "books_short_"+(i+1);
                    short_books[i] = PassageUtil.getResource(key);
                }
                catch (MissingResourceException ex)
                {
                    log.debug("Can't find resource for '"+key+"' in Locale '" + Locale.getDefault().toString() + "'");
                    success = false;
                }

                try
                {
                    key = "books_alt_"+(i+1);
                    alt_books[i] = PassageUtil.tokenize(PassageUtil.getResource(key), " ");
                }
                catch (MissingResourceException ex)
                {
                    log.debug("Can't find resource for '"+key+"' in Locale '" + Locale.getDefault().toString() + "'");
                    success = false;
                }
            }

            for (int i=0; i<sections.length; i++)
            {
                try
                {
                    key = "sections_"+(i+1);
                    sections[i] = PassageUtil.getResource(key);
                }
                catch (MissingResourceException ex)
                {
                    log.debug("Can't find resource for '"+key+"' in Locale '" + Locale.getDefault().toString() + "'");
                    success = false;
                }
            }

            if (!success)
                Reporter.informUser(BibleInfo.class, "Failed to load all resources correctly, using defaults.");
        }
        catch (Throwable ex)
        {
            Reporter.informUser(BibleInfo.class, "Can't find resources for Locale '" + Locale.getDefault().toString() + "'");
        }

        try
        {
            // Create the book name arrays
            full_books_lower = new String[BOOKS_IN_BIBLE];
            short_books_lower = new String[BOOKS_IN_BIBLE];
            full_books_upper = new String[BOOKS_IN_BIBLE];
            short_books_upper = new String[BOOKS_IN_BIBLE];
            alt_books_lower = new String[BOOKS_IN_BIBLE][];

            sections_lower = new String[sections.length];
            sections_upper = new String[sections.length];

            for (int i=0; i<BibleInfo.BOOKS_IN_BIBLE; i++)
            {
                // Cache the upper and lower case versions of the book names
                full_books_lower[i] = full_books[i].toLowerCase();
                short_books_lower[i] = short_books[i].toLowerCase();
                full_books_upper[i] = full_books[i].toUpperCase();
                short_books_upper[i] = short_books[i].toUpperCase();

                alt_books_lower[i] = new String[alt_books[i].length];
                for (int j=0; j<alt_books[i].length; j++)
                {
                    alt_books_lower[i][j] = alt_books[i][j].toLowerCase();
                }
            }

            for (int i=0; i<sections.length; i++)
            {
                sections_lower[i] = sections[i].toLowerCase();
                sections_upper[i] = sections[i].toUpperCase();
            }
        }
        catch (Throwable ex)
        {
            Reporter.informUser(BibleInfo.class, ex);
        }
    }

    /**
     * This is the code used to create ordinal_at_start_of_chapter and
     * ordinal_at_start_of_book. It is usually commented out because I
     * don't see any point in making .class files bigger for no reason
     * and this is needed only very rarely.
     *
    public static void main(String[] args) throws NoSuchVerseException
    {
        int verse_num;

        verse_num = 1;
        log.fine("    private static final int[] ordinal_at_start_of_book =");
        log.fine("    {");
        log.fine("        ");
        for (int b=1; b<=booksInBible(); b++)
        {
            String vstr1 = "     "+verse_num;
            String vstr2 = vstr1.substring(vstr1.length()-5);
            log.fine(vstr2+", ");
            verse_num += versesInBook(b);

            if (b % 10 == 0) log.fine();
        }
        log.fine();
        log.fine("    };");

        verse_num = 1;
        log.fine("    private static final int[][] ordinal_at_start_of_chapter =");
        log.fine("    {");
        for (int b=1; b<=BibleInfo.booksInBible(); b++)
        {
            log.fine("        { ");
            for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
            {
                String vstr1 = "     "+verse_num;
                String vstr2 = vstr1.substring(vstr1.length()-5);
                log.fine(vstr2+", ");
                verse_num += BibleInfo.versesInChapter(b, c);
            }
            log.fine("},");
        }
        log.fine("    };");
    }
    */
}
