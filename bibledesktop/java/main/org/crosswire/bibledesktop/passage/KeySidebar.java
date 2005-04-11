package org.crosswire.bibledesktop.passage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.crosswire.bibledesktop.book.DisplaySelectEvent;
import org.crosswire.bibledesktop.book.DisplaySelectListener;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageListType;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * A list view of a key range list.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public class KeySidebar extends JPanel implements DisplaySelectListener, KeyChangeListener
{
    /**
     * Initialize the SplitBookDataDisplay
     */
    public KeySidebar(Book book)
    {
        this.book = book;
        init();
        setActive();
    }

    /**
     * Create the GUI
     */
    private void init()
    {
        setLayout(new BorderLayout());

        model = new PassageListModel();
        model.setMode(PassageListType.RANGES);
        model.setRestriction(RestrictionType.CHAPTER);

        list = new JList(model);
        list.addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent ev)
            {
                if (ev.getValueIsAdjusting())
                {
                    return;
                }

                selection();
            }
        });

        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(list);

        ActionFactory actions = new ActionFactory(KeySidebar.class, this);

        actDelete = actions.getAction(DELETE_SELECTED);
        actBlur1 = actions.getAction(BLUR1);
        actBlur5 = actions.getAction(BLUR5);

        JButton delete = new JButton(actDelete);
        delete.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
        delete.setText(null);
        JButton blur1 = new JButton(actBlur1);
        blur1.setText(null);
        blur1.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));
        JButton blur5 = new JButton(actBlur5);
        blur5.setText(null);
        blur5.setBorder(BorderFactory.createEmptyBorder(2,5,2,5));

        JPanel mutate = new JPanel(new FlowLayout());
        mutate.add(delete);
        mutate.add(blur1);
        mutate.add(blur5);

        add(mutate, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Blur (expand) the current passage action by one verse on each side.
     * This bound by the boundaries of the Chapter.
     */
    public void doBlur1()
    {
        doBlur(1);
    }

    /**
     * Blur (expand) the current key by five verses on each side.
     * This bound by the boundaries of the Chapter.
     */
    public void doBlur5()
    {
        doBlur(5);
    }

    /**
     * Blur (expand) the current key action by amount verses on each side.
     * This bound by the boundaries of the Chapter.
     * @param amount The amount of blurring
     */
    private void doBlur(int amount)
    {
        key.blur(amount, RestrictionType.CHAPTER);
        fireKeyChanged(new KeyChangeEvent(this, key));
    }

    /**
     * Remove the selected verses out of this KeySidebar.
     */
    public void doDeleteSelected()
    {
        PassageGuiUtil.deleteSelectedVersesFromList(list);
        fireKeyChanged(new KeyChangeEvent(this, key));
    }

    public Key getKey()
    {
        return key;
    }

    private void setKey(Key newKey)
    {
        if (partial != null && partial.equals(newKey))
        {
            return;
        }

        if (key != null && key.equals(newKey))
        {
            return;
        }

        // Have to have a copy of the key
        // since we allow it to be blurred and
        // that would cause the shared location
        // to get the change w/o seeing it.
        if (newKey == null)
        {
            key = null;
        }
        else
        {
            key = (Key) newKey.clone();
        }
        partial = null;
        model.setPassage((Passage) key);
        fireKeyChanged(new KeyChangeEvent(this, key));
    }

    /**
     * Someone clicked on a value in the list
     */
    protected void selection()
    {
        try
        {
            Object[] selected = list.getSelectedValues();

            if (selected.length > 0)
            {
               partial = book.createEmptyKeyList();

                for (int i = 0; i < selected.length; i++)
                {
                    partial.addAll((Key) selected[i]);
                }

                fireKeyChanged(new KeyChangeEvent(this, partial));
            }
            else
            {
                // Nothing selected so use the whole passage
                fireKeyChanged(new KeyChangeEvent(this, key));
            }

            setActive();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Make sure the correct buttons are made active
     */
    private void setActive()
    {
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
    public void passageSelected(DisplaySelectEvent ev)
    {
        setKey(ev.getKey());
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.DisplaySelectListener#bookChosen(org.crosswire.bibledesktop.book.DisplaySelectEvent)
     */
    public void bookChosen(DisplaySelectEvent ev)
    {
        book = ev.getBook();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.KeyChangeListener#keyChanged(org.crosswire.bibledesktop.book.KeyChangeEvent)
     */
    public void keyChanged(KeyChangeEvent ev)
    {
        setKey(ev.getKey());
    }

    /**
     * Add a command listener
     */
    public synchronized void addKeyChangeListener(KeyChangeListener listener)
    {
        List temp = new ArrayList(2);

        if (keyChangeListeners != null)
        {
            temp.addAll(keyChangeListeners);
        }

        if (!temp.contains(listener))
        {
            temp.add(listener);
            keyChangeListeners = temp;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void removeKeyChangeListener(KeyChangeListener listener)
    {
        if (keyChangeListeners != null && keyChangeListeners.contains(listener))
        {
            List temp = new ArrayList();
            temp.addAll(keyChangeListeners);

            temp.remove(listener);
            keyChangeListeners = temp;
        }
    }

    /**
     * Inform the command keyChangeListeners
     */
    protected void fireKeyChanged(KeyChangeEvent ev)
    {
        if (keyChangeListeners != null)
        {
            for (int i = 0; i < keyChangeListeners.size(); i++)
            {
                KeyChangeListener li = (KeyChangeListener) keyChangeListeners.get(i);
                li.keyChanged(ev);
            }
        }
    }

    private static final String BLUR1 = "Blur1"; //$NON-NLS-1$
    private static final String BLUR5 = "Blur5"; //$NON-NLS-1$
    private static final String DELETE_SELECTED = "DeleteSelected"; //$NON-NLS-1$

    /**
     * The whole key that we are viewing
     */
    private Key key;

    /**
     * The key that is selected
     */
    private Key partial;

    /**
     * The book who's keys we are looking at
     */
    private Book book;

    /**
     * The listener for KeyChangeEvents
     */
    private transient List keyChangeListeners;

    /*
     * GUI Components
     */
    private JList list;
    private PassageListModel model;
    private Action actDelete;
    private Action actBlur1;
    private Action actBlur5;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3905241217179466036L;
}
