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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.BDMsg;
import org.crosswire.bibledesktop.book.DisplaySelectEvent;
import org.crosswire.bibledesktop.book.DisplaySelectListener;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.RestrictionType;
import org.crosswire.jsword.passage.VerseRange;

/**
 * A list view of a key range list.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class KeySidebar extends JPanel implements DisplaySelectListener, KeyChangeListener {
    /**
     * Initialize the SplitBookDataDisplay
     */
    public KeySidebar(Book[] books) {
        this.books = books == null ? null : (Book[]) books.clone();
        init();
        setActive();
    }

    /**
     * Create the GUI
     */
    private void init() {
        setLayout(new BorderLayout());

        model = new RangeListModel(RestrictionType.CHAPTER);

        list = new JList(model);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent ev) {
                if (ev.getValueIsAdjusting()) {
                    return;
                }

                selection();
            }
        });

        JScrollPane scroll = new CWScrollPane(list);

        ActionFactory actions = new ActionFactory(this);

        actDelete = actions.addAction("DeleteSelected");
        // TRANSLATOR: This is the tooltip for the delete selected icon button in the Passage Sidebar
        actDelete.setTooltip(BDMsg.gettext("Remove the selected passages in the current passage list."));
        actDelete.setSmallIcon("toolbarButtonGraphics/general/Remove16.gif");
        actDelete.setLargeIcon("toolbarButtonGraphics/general/Remove24.gif");

        actBlur1 = actions.addAction("Blur1");
        // TRANSLATOR: This is the tooltip for the blur by 1 icon button in the Passage Sidebar
        actBlur1.setTooltip(BDMsg.gettext("Expand all or the selected passage by 1 verse."));
        actBlur1.setSmallIcon("images/Blur1_16.gif");

        actBlur5 = actions.addAction("Blur5");
        // TRANSLATOR: This is the tooltip for the blur by 5 icon button in the Passage Sidebar
        actBlur5.setTooltip(BDMsg.gettext("Expand all or the selected passage by 5 verses."));
        actBlur5.setSmallIcon("images/Blur5_16.gif");

        JButton delete = new JButton(actDelete);
        // delete.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        delete.setText(null);
        JButton blur1 = new JButton(actBlur1);
        blur1.setText(null);
        // blur1.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        JButton blur5 = new JButton(actBlur5);
        blur5.setText(null);
        // blur5.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JPanel mutate = new JPanel(new FlowLayout());
        mutate.add(delete);
        mutate.add(blur1);
        mutate.add(blur5);

        add(mutate, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Blur (expand) the current passage action by one verse on each side. This
     * bound by the boundaries of the Chapter.
     */
    public void doBlur1() {
        doBlur(1);
    }

    /**
     * Blur (expand) the current key by five verses on each side. This bound by
     * the boundaries of the Chapter.
     */
    public void doBlur5() {
        doBlur(5);
    }

    /**
     * Blur (expand) the current key action by amount verses on each side. This
     * bound by the default Blur Restriction.
     * 
     * @param amount
     *            The amount of blurring
     */
    private void doBlur(int amount) {
        // Remember what was selected
        Object[] objs = list.getSelectedValues();

        // Make sure that key changes are not visible until blur is done.
        Key copy = key.clone();

        // Either blur the entire unselected list or just the selected elements.
        if (objs.length == 0) {
            copy.blur(amount, RestrictionType.getDefaultBlurRestriction());
        } else {
            for (Object obj : objs) {
                Key keyCopy = ((VerseRange) obj).clone();
                keyCopy.blur(amount, RestrictionType.getDefaultBlurRestriction());
                copy.addAll(keyCopy);
            }
        }
        fireKeyChanged(new KeyChangeEvent(this, copy));

        // Restore the selection
        int total = model.getSize();
        int count = objs.length;
        for (int i = 0; i < total; i++) {
            Key listedKey = (Key) model.getElementAt(i);

            // As keys are found, remove them
            for (Object obj : objs) {
                Key selectedKey = (VerseRange) obj;
                if (listedKey.contains(selectedKey)) {
                    list.addSelectionInterval(i, i);
                    --count;
                }
            }

            // If the list is empty then we are done.
            if (count <= 0) {
                break;
            }
        }

        GuiUtil.refresh(this);
    }

    /**
     * Remove the selected verses out of this KeySidebar.
     */
    public void doDeleteSelected() {
        RangeListModel rlm = (RangeListModel) list.getModel();

        Passage ref = rlm.getPassage();
        Object[] selected = list.getSelectedValues();
        for (int i = 0; i < selected.length; i++) {
            VerseRange range = (VerseRange) selected[i];
            ref.remove(range);
        }

        list.setSelectedIndices(new int[0]);

        partial = null;
        model.setPassage((Passage) key);
        fireKeyChanged(new KeyChangeEvent(this, key));
        setActive();
    }

    public Key getKey() {
        return key;
    }

    private void setKey(Key newKey) {
        if (partial != null && partial.equals(newKey)) {
            return;
        }

        if (key != null && key.equals(newKey)) {
            return;
        }

        // Have to have a copy of the key
        // since we allow it to be blurred and
        // that would cause the shared location
        // to get the change w/o seeing it.
        if (newKey == null) {
            key = null;
        } else {
            if (key != newKey) {
                key = newKey.clone();
            }
        }
        partial = null;
        model.setPassage((Passage) key);
        fireKeyChanged(new KeyChangeEvent(this, key));
        setActive();
    }

    /**
     * Someone clicked on a value in the list
     */
    /*private*/final void selection() {
        Object[] selected = list.getSelectedValues();

        if (selected.length > 0) {
            partial = books[0].createEmptyKeyList();

            for (int i = 0; i < selected.length; i++) {
                partial.addAll((Key) selected[i]);
            }

            fireKeyChanged(new KeyChangeEvent(this, partial));
        } else {
            // Nothing selected so use the whole passage
            fireKeyChanged(new KeyChangeEvent(this, key));
        }

        setActive();
    }

    /**
     * Make sure the correct buttons are made active
     */
    private void setActive() {
        Object[] selected = list.getSelectedValues();

        // make sure the mutator buttons are correctly active
        actDelete.setEnabled(selected.length != 0);
        boolean blurable = model.getSize() != 0;
        actBlur1.setEnabled(blurable);
        actBlur5.setEnabled(blurable);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#passageSelected(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void passageSelected(DisplaySelectEvent ev) {
        setKey(ev.getKey());
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#bookChosen(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void bookChosen(DisplaySelectEvent ev) {
        books = ev.getBookProvider().getBooks();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.KeyChangeListener#keyChanged(org.crosswire.bibledesktop.book.KeyChangeEvent)
     */
    public void keyChanged(KeyChangeEvent ev) {
        setKey(ev.getKey());
    }

    /**
     * Add a command listener
     */
    public synchronized void addKeyChangeListener(KeyChangeListener listener) {
        List<KeyChangeListener> temp = new ArrayList<KeyChangeListener>(2);

        if (keyChangeListeners != null) {
            temp.addAll(keyChangeListeners);
        }

        if (!temp.contains(listener)) {
            temp.add(listener);
            keyChangeListeners = temp;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void removeKeyChangeListener(KeyChangeListener listener) {
        if (keyChangeListeners != null && keyChangeListeners.contains(listener)) {
            List<KeyChangeListener> temp = new ArrayList<KeyChangeListener>();
            temp.addAll(keyChangeListeners);

            temp.remove(listener);
            keyChangeListeners = temp;
        }
    }

    /**
     * Inform the command keyChangeListeners
     */
    /*private*/final synchronized void fireKeyChanged(KeyChangeEvent ev) {
        if (keyChangeListeners != null) {
            for (KeyChangeListener li : keyChangeListeners) {
                li.keyChanged(ev);
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
        // Broken but we don't serialize views
        books = null;
        keyChangeListeners = null;
        is.defaultReadObject();
    }

    /**
     * The whole key that we are viewing
     */
    private Key key;

    /**
     * The key that is selected
     */
    private Key partial;

    /**
     * The books who's keys we are looking at
     */
    private transient Book[] books;

    /**
     * The listener for KeyChangeEvents
     */
    private transient List<KeyChangeListener> keyChangeListeners;

    /*
     * GUI Components
     */
    private JList list;
    private RangeListModel model;
    private CWAction actDelete;
    private CWAction actBlur1;
    private CWAction actBlur5;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3905241217179466036L;
}
