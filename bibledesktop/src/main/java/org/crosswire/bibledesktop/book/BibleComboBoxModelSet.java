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
package org.crosswire.bibledesktop.book;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javax.swing.JComboBox;
import javax.swing.event.EventListenerList;

import org.crosswire.common.swing.NumberCellRenderer;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.versification.BibleBook;
import org.crosswire.jsword.versification.BookName;

/**
 * A set of correctly constructed and linked BibleComboBoxModels.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BibleComboBoxModelSet implements Serializable {
    public BibleComboBoxModelSet(JComboBox books, JComboBox chapters, JComboBox verses) {
        listeners = new EventListenerList();
        cil = new CustomItemListener();

        mdlBook = new BibleComboBoxModel(this, BibleComboBoxModel.Level.BOOK);
        setBookComboBox(books);

        mdlChapter = new BibleComboBoxModel(this, BibleComboBoxModel.Level.CHAPTER);
        setChapterComboBox(chapters);

        if (verses != null) {
            mdlVerse = new BibleComboBoxModel(this, BibleComboBoxModel.Level.VERSE);
            setVerseComboBox(verses);
        }
    }

    /**
     * The book combo box
     */
    public final void setBookComboBox(JComboBox cboBook) {
        this.cboBook = cboBook;

        cboBook.setModel(mdlBook);
        cboBook.addItemListener(cil);
        cboBook.setRenderer(new BibleNameCellRenderer(true));

        try {
            cboBook.setToolTipText(verse.getBook().getLongName());
        } catch (NoSuchVerseException ex) {
            assert false : ex;
        }
    }

    /**
     * The chapter combo box
     */
    public final void setChapterComboBox(JComboBox cboChapter) {
        this.cboChapter = cboChapter;

        cboChapter.setModel(mdlChapter);
        // There are over 100 chapters in some books
        cboChapter.setPrototypeDisplayValue(Integer.valueOf(999));
        cboChapter.addItemListener(cil);
        cboChapter.setRenderer(new NumberCellRenderer());
    }

    /**
     * The verse combo box
     */
    public final void setVerseComboBox(JComboBox cboVerse) {
        this.cboVerse = cboVerse;

        cboVerse.setModel(mdlVerse);
        // There are over 100 verses in some chapters
        cboVerse.setPrototypeDisplayValue(Integer.valueOf(999));
        cboVerse.addItemListener(cil);
        cboVerse.setRenderer(new NumberCellRenderer());
    }

    /**
     * @return Verse
     */
    public Verse getVerse() {
        return verse;
    }

    /**
     * Set the combo-boxes to a new verse
     */
    public void setVerse(Verse newverse) {
        if (verse.equals(newverse)) {
            return;
        }

        try {
            Verse oldverse = verse;
            verse = newverse;
            BibleBook bookval = newverse.getBook();
            BookName bookName = bookval.getBookName();
            if (oldverse.getBook() != bookval || !cboBook.getSelectedItem().equals(bookName)) {
                cboBook.setSelectedItem(bookName);
                cboBook.setToolTipText(bookName.getLongName());
            }

            int chapterval = newverse.getChapter();
            Integer chapternum = Integer.valueOf(chapterval);
            if (oldverse.getChapter() != chapterval || !cboChapter.getSelectedItem().equals(chapternum)) {
                cboChapter.setSelectedItem(chapternum);
            }

            if (cboVerse != null) {
                int verseval = newverse.getVerse();
                Integer versenum = Integer.valueOf(verseval);
                if (oldverse.getVerse() != verseval || !cboVerse.getSelectedItem().equals(versenum)) {
                    cboVerse.setSelectedItem(versenum);
                }
            }

            fireContentsChanged();
        } catch (NoSuchVerseException ex) {
            assert false : ex;
        }
    }

    /**
     * Add a listener to the list that's notified each time a change to the data
     * model occurs.
     * 
     * @param li
     *            the ListDataListener
     */
    public void addActionListener(ActionListener li) {
        listeners.add(ActionListener.class, li);
    }

    /**
     * Remove a listener from the list that's notified each time a change to the
     * data model occurs.
     * 
     * @param li
     *            the ListDataListener
     */
    public void removeActionListener(ActionListener li) {
        listeners.remove(ActionListener.class, li);
    }

    /**
     * Called after the verse changes.
     * 
     * @see EventListenerList
     * @see javax.swing.DefaultListModel
     */
    protected void fireContentsChanged() {
        Object[] liarray = listeners.getListenerList();
        ActionEvent ev = null;

        for (int i = liarray.length - 2; i >= 0; i -= 2) {
            if (liarray[i] == ActionListener.class) {
                if (ev == null) {
                    ev = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, verse.getName());
                }

                ((ActionListener) liarray[i + 1]).actionPerformed(ev);
            }
        }
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        cil = new CustomItemListener();
        is.defaultReadObject();
    }

    /**
     * For when a selection is made
     */
    final class CustomItemListener implements ItemListener {

        public void itemStateChanged(ItemEvent ev) {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                // If the book changes we need to change both the chapter and
                // verse list
                // If the chapter changes we need to change the verse list
                Object source = ev.getSource();
                if (source.equals(cboBook)) {
                    mdlChapter.fireContentsChanged(this, 0, mdlChapter.getSize());
                }

                if (mdlVerse != null && (source.equals(cboBook) || source.equals(cboChapter))) {
                    mdlVerse.fireContentsChanged(this, 0, mdlVerse.getSize());
                }
            }
        }
    }

    private Verse verse = Verse.DEFAULT;

    protected JComboBox cboBook;
    protected JComboBox cboChapter;
    private JComboBox cboVerse;

    protected BibleComboBoxModel mdlBook;
    protected BibleComboBoxModel mdlChapter;
    protected BibleComboBoxModel mdlVerse;

    protected EventListenerList listeners;
    private transient ItemListener cil;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 5365220628525297473L;
}
