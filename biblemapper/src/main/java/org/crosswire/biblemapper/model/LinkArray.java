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

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.crosswire.common.util.Logger;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.OSISUtil;
import org.crosswire.jsword.index.search.DefaultSearchModifier;
import org.crosswire.jsword.index.search.DefaultSearchRequest;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseRange;
import org.crosswire.jsword.versification.BibleInfo;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * LinkArray contains a set of link chapters for each chapter in the Bible. It
 * is similar to a central margin reference data set, except that it works with
 * chapters and not verses and every chapter is linked to a constant number of
 * others, and the links have strengths.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class LinkArray implements Serializable {
    /**
     * Basic constructor
     * 
     * @param book
     *            The source of Bible data
     */
    public LinkArray(Book book) throws NoSuchVerseException {
        this.book = book;

        links = new Link[BibleInfo.booksInBible() + 1][][];

        for (int b = 1; b <= BibleInfo.booksInBible(); b++) {
            links[b] = new Link[BibleInfo.chaptersInBook(b) + 1][];
        }
    }

    /**
     * Save link data to XML as a stream.
     */
    public void load(Reader out) throws IOException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(out);
            Element root = doc.getRootElement();
            fromXML(root);
        } catch (JDOMException ex) {
            throw new IOException(ex.getMessage());
        }
    }

    /**
     * Save link data to XML as a stream.
     */
    public void save(Writer out) throws IOException {
        Element root = toXML();
        Document doc = new Document(root);
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        outputter.output(doc, out);
    }

    /**
     * Generate links from an XML representation.
     */
    public void fromXML(Element elinks) throws JDOMException {
        if (!elinks.getName().equals("links")) {
            throw new JDOMException("root element is not called 'links'");
        }

        List ebs = elinks.getChildren("book");
        Iterator bit = ebs.iterator();
        while (bit.hasNext()) {
            Element eb = (Element) bit.next();
            int b = Integer.parseInt(eb.getAttributeValue("num"));

            List ecs = eb.getChildren("chapter");
            Iterator cit = ecs.iterator();
            while (cit.hasNext()) {
                Element ec = (Element) cit.next();
                int c = Integer.parseInt(ec.getAttributeValue("num"));

                List ls = new ArrayList();

                List els = ec.getChildren("link");
                Iterator lit = els.iterator();
                while (lit.hasNext()) {
                    Element el = (Element) lit.next();
                    int db = Integer.parseInt(el.getAttributeValue("book"));
                    int dc = Integer.parseInt(el.getAttributeValue("chapter"));
                    int dr = Integer.parseInt(el.getAttributeValue("rating"));
                    Link l = new Link(db, dc, dr);
                    ls.add(l);
                }

                links[b][c] = (Link[]) ls.toArray(new Link[ls.size()]);
            }
        }
    }

    /**
     * Save link data to XML as a JDOM tree.
     */
    public Element toXML() {
        Element elinks = new Element("links");

        try {
            for (int b = 1; b <= BibleInfo.booksInBible(); b++) {
                Element eb = new Element("book");
                eb.setAttribute("num", "" + b);
                eb.setAttribute("name", BibleInfo.getPreferredBookName(b));
                elinks.addContent(eb);

                for (int c = 1; c <= BibleInfo.chaptersInBook(b); c++) {
                    Element ec = new Element("chapter");
                    ec.setAttribute("num", "" + c);
                    eb.addContent(ec);
                    Link[] export = links[b][c];
                    for (int i = 0; export != null && i < export.length; i++) {
                        Link l = export[i];
                        int dbook = l.getDestinationBook();
                        int dchap = l.getDestinationChapter();

                        Verse start = new Verse(dbook, dchap, 1);
                        Verse end = new Verse(dbook, dchap, BibleInfo.versesInChapter(dbook, dchap));
                        VerseRange chap = new VerseRange(start, end);

                        Element el = new Element("link");
                        el.setAttribute("book", "" + dbook);
                        el.setAttribute("chapter", "" + dchap);
                        el.setAttribute("name", chap.getName());
                        el.setAttribute("rating", "" + l.getStrength());
                        ec.addContent(el);
                    }
                }
            }
        } catch (NoSuchVerseException ex) {
            assert false : ex;
        }

        return elinks;
    }

    /**
     * Fill up the link cache
     */
    public void cacheAll() throws NoSuchVerseException {
        // Create the array of Nodes
        for (int b = 1; b <= BibleInfo.booksInBible(); b++) {
            for (int c = 1; c <= BibleInfo.chaptersInBook(b); c++) {
                getLinks(b, c);
            }
        }
    }

    /**
     * Turn a PassageTally into an array of links.
     * 
     * @return The array of links for the specified verse
     */
    public Link[] getLinks(int b, int c) {
        if (links[b][c] != null) {
            return links[b][c];
        }

        try {
            PassageTally total = new PassageTally();
            total.setOrdering(PassageTally.ORDER_TALLY);

            for (int v = 1; v <= BibleInfo.versesInChapter(b, c); v++) {
                Verse find = new Verse(b, c, v);
                BookData bdata = new BookData(book, find);
                String text = OSISUtil.getCanonicalText(bdata.getOsisFragment());
                DefaultSearchModifier modifier = new DefaultSearchModifier();
                modifier.setRanked(true);

                PassageTally temp = (PassageTally) book.find(new DefaultSearchRequest(text, modifier));
                temp.setOrdering(PassageTally.ORDER_TALLY);
                total.addAll(temp);
            }

            int chff = BibleInfo.chaptersInBook(b);
            int vsff = BibleInfo.versesInChapter(b, chff);
            Verse start = new Verse(b, 1, 1);
            Verse end = new Verse(b, chff, vsff);
            VerseRange range = new VerseRange(start, end);

            total.remove(range);
            total.trimVerses(LINKS_PER_CHAPTER);
            scrunchTally(total);

            // Create the links for the tally
            links[b][c] = new Link[total.countVerses()];
            for (int i = 0; i < links[b][c].length; i++) {
                Verse loop = total.getVerseAt(i);
                int strength = total.getTallyOf(loop);
                links[b][c][i] = new Link(loop.getBook(), loop.getChapter(), strength);
            }

            log.debug("Generated links for: book=" + b + " chapter=" + c + " #links=" + links[b][c].length);
        } catch (Exception ex) {
            assert false : ex;
        }

        return links[b][c];
    }

    /**
     * What is the average index for a given match. This is a measure of how
     * good the nest match algorithm is. The closer to zero the better
     * 
     * @return The average match index
     */
    public float getMatchScore() {
        if (linked == 0) {
            return -1;
        }

        return ((float) (100 * miss_total)) / linked;
    }

    /**
     * Take a tally and move all the link strengths in and chapter to the first
     * verse in the chapter.
     */
    public void scrunchTally(PassageTally tally) throws NoSuchVerseException {
        for (int b = 1; b <= BibleInfo.booksInBible(); b++) {
            for (int c = 1; c <= BibleInfo.chaptersInBook(b); c++) {
                Verse start = new Verse(b, c, 1);
                Verse end = new Verse(b, c, BibleInfo.versesInChapter(b, c));
                VerseRange chapter = new VerseRange(start, end);

                int chaptotal = 0;

                for (int v = 1; v <= BibleInfo.versesInChapter(b, c); v++) {
                    chaptotal += tally.getTallyOf(new Verse(b, c, v));
                }

                tally.remove(chapter);
                tally.add(start, chaptotal);

                if (chaptotal > PassageTally.MAX_TALLY) {
                    System.out.println("truncated chaptotal: " + chaptotal);
                }
            }
        }
    }

    /**
     * Debug for an array of Links
     */
    public static String debug(Link[] set) {
        StringBuilder buff = new StringBuilder();

        for (int i = 0; i < set.length; i++) {
            if (i != 0) {
                buff.append(", ");
            }

            buff.append(set[i].toString());
        }

        return buff.toString();
    }

    /**
     * To make serialization work across new versions
     */
    static final long serialVersionUID = -2354670272946948354L;

    /**
     * The total miss mark
     */
    private transient int miss_total = 0;

    /**
     * The number of verses checked
     */
    private transient int linked = 0;

    /**
     * The Bible that we search in
     */
    private transient Book book;

    /**
     * The link data
     */
    private Link[][][] links;

    /**
     * The number of links we record for each chapter
     */
    public static final int LINKS_PER_CHAPTER = 200;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(LinkArray.class);
}
