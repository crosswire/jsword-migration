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
package org.crosswire.biblemapper.swing;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.search.parse.IndexSearcher;
import org.crosswire.jsword.book.search.parse.PhraseParamWord;
import org.crosswire.jsword.passage.BibleInfo;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Command line mapping tool.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class CliMapper
{
    /**
     * Start point
     */
    public static void main(String[] args)
    {
        new CliMapper().run();
    }

    /**
     * Create a new Map
     */
    public void run()
    {
        try
        {
            PrintWriter dbout = new PrintWriter(new FileOutputStream("c:\\database.csv"));
            PrintWriter xlout = new PrintWriter(new FileOutputStream("c:\\sheet.csv"));

            List dicts = Books.installed().getBooks(BookFilters.getBibles());
            Book book = (Book) dicts.get(0);
            //Matcher engine = new Matcher(bible);

            Element links = new Element("links");

            for (int b=1; b<=BibleInfo.booksInBible(); b++)
            {
                Element eb = new Element("book");
                eb.setAttribute("num", ""+b);
                eb.setAttribute("name", BibleInfo.getBookName(b));
                links.addContent(eb);

                int chff = BibleInfo.chaptersInBook(b);
                int vsff = BibleInfo.versesInChapter(b, chff);
                Verse start = new Verse(b, 1, 1);
                Verse end = new Verse(b, chff, vsff);
                VerseRange range = new VerseRange(start, end);

                for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
                {
                    Element ec = new Element("chapter");
                    ec.setAttribute("num", ""+c);
                    eb.addContent(ec);

                    PassageTally total = new PassageTally();
                    total.setOrdering(PassageTally.ORDER_TALLY);
                    
                    for (int v=1; v<=BibleInfo.versesInChapter(b, c); v++)
                    {
                        Verse find = new Verse(b, c, v);
                        BookData bdata = book.getData(find);
                        String text = bdata.getPlainText();
                        String quote = IndexSearcher.getPreferredSyntax(PhraseParamWord.class);
                        text = quote + text + quote;
                        PassageTally temp = (PassageTally) book.find(text);
                        temp.setOrdering(PassageTally.ORDER_TALLY);
                        total.addAll(temp);
                    }

                    int ff = BibleInfo.versesInChapter(b, c);
                    VerseRange base = new VerseRange(new Verse(b, c, 1), new Verse(b, c, ff));

                    total.remove(range);
                    total.trimVerses(LINKS_PER_CHAPTER);
                    scrunchTally(total);

                    for (Key key : total)
                    {
                        Verse link = (Verse) key;
                        VerseRange chap = new VerseRange(link, new Verse(link.getBook(), link.getChapter(), BibleInfo.versesInChapter(link.getBook(), link.getChapter())));
                        Element el = new Element("link");
                        el.setAttribute("book", ""+link.getBook());
                        el.setAttribute("chapter", ""+link.getChapter());
                        el.setAttribute("name", chap.getName());
                        el.setAttribute("rating", ""+total.getIndexOf(link));
                        ec.addContent(el);

                        dbout.println(base.getName()+","+base.getStart().getBook()+","+base.getStart().getChapter()+","
                            +chap.getName()+","+link.getBook()+","+link.getChapter()+","
                            +total.getIndexOf(link));

                        for (int tb=1; tb<=BibleInfo.booksInBible(); tb++)
                        {
                            for (int tc=0; tc<BibleInfo.chaptersInBook(tb); tc++)
                            {
                                Verse t = new Verse(tb, tc, 1);
                                total.getIndexOf(t);
                            }
                        }
                    }
                }
            }

            xlout.close();
            dbout.close();

            PrintWriter xmlout = new PrintWriter(new FileOutputStream("c:\\links.xml"));
            Document doc = new Document(links);
            XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
            output.output(doc, xmlout);
            xmlout.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void scrunchTally(PassageTally tally) throws NoSuchVerseException
    {
        for (int b=1; b<=BibleInfo.booksInBible(); b++)
        {
            for (int c=1; c<=BibleInfo.chaptersInBook(b); c++)
            {
                Verse start = new Verse(b, c, 1);
                Verse end = new Verse(b, c, BibleInfo.versesInChapter(b, c));
                VerseRange chapter = new VerseRange(start, end);
                
                int chaptotal = 0;

                for (int v=1; v<=BibleInfo.versesInChapter(b, c); v++)
                {
                    chaptotal += tally.getTallyOf(new Verse(b, c, v));
                }
                
                tally.remove(chapter);
                tally.add(start, chaptotal);

                if (chaptotal > PassageTally.MAX_TALLY)
                {
                    System.out.println("truncated chaptotal: "+chaptotal);
                }
            }
        }        
    }

    public static final int LINKS_PER_CHAPTER = 200;

    /*
    // Remove the original wherever it was
    tally.remove(verse);

    // Create the links for the tally
    links[index] = new Link[LINKS_PER_VERSE];
    for (int i=0; i<LINKS_PER_VERSE; i++)
    {
        try
        {
            Verse loop = tally.getVerseAt(i);
            int strength = tally.getTallyOf(loop);

            links[index][i] = new Link(loop.getOrdinal(), strength);
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            links[index][i] = new Link(verse.getOrdinal(), 0);
        }
    }

    return links[index];
    */
}
