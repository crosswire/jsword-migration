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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import org.crosswire.bibledesktop.display.BookDataDisplay;
import org.crosswire.bibledesktop.display.splitlist.SplitBookDataDisplay;
import org.crosswire.bibledesktop.display.tab.TabbedBookDataDisplay;
import org.crosswire.bibledesktop.passage.KeySidebar;
import org.crosswire.common.swing.desktop.Clearable;
import org.crosswire.common.swing.desktop.TabbedPanePanel;
import org.crosswire.common.swing.desktop.Titleable;
import org.crosswire.common.swing.desktop.event.TitleChangedEvent;
import org.crosswire.common.swing.desktop.event.TitleChangedListener;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageKeyFactory;

/**
 * A BibleViewPane consists of three areas for looking up passages,
 * for navigating and minipulating parts of passage and for viewing a passage.
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BibleViewPane extends TabbedPanePanel implements Titleable, Clearable, TitleChangedListener
{
    /**
     * Simple ctor
     */
    public BibleViewPane(boolean showSidebar)
    {
        listeners = new EventListenerList();
        pnlSelect = new DisplaySelectPane();
        KeySidebar sidebar = new KeySidebar(pnlSelect.getBook());
        BookDataDisplay display = new TabbedBookDataDisplay();
        pnlPassg = new SplitBookDataDisplay(sidebar, display);
        pnlPassg.showSidebar(showSidebar);
        sidebar.addKeyChangeListener(pnlSelect);
        pnlSelect.addCommandListener(sidebar);
        pnlSelect.addTitleChangedListener(this);
        pnlPassg.addKeyChangeListener(sidebar);
        init();
    }

    /**
     * Setup the GUI
     */
    private void init()
    {
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new CustomFileFilter());
        chooser.setMultiSelectionEnabled(false);

        pnlSelect.addCommandListener(new DisplaySelectListener()
        {
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
                pnlPassg.setBookData(ev.getBook(), ev.getKey());
            }
        });

        pnlSelect.setBorder(UIManager.getBorder("SelectPanel.border")); //$NON-NLS-1$

        this.setLayout(new BorderLayout());
        this.setMinimumSize(new Dimension(0, 0));
        this.add(pnlSelect, BorderLayout.NORTH);
        this.add(pnlPassg, BorderLayout.CENTER);
    }

    /**
     * Make it as though no-one is using this view
     */
    public void clear()
    {
        saved = null;
        if (!pnlSelect.isClear())
        {
            pnlSelect.clear();
        }
    }

    /**
     * Has anyone started using this view
     */
    public boolean isClear()
    {
        saved = null;
        return pnlSelect.isClear();
    }

    /**
     * How has this view been saved
     */
    public String getTitle()
    {
        if (saved == null)
        {
            return pnlSelect.getTitle();
        }

        return saved.getName();
    }

    /**
     * Save the view to disk.
     */
    public void save() throws IOException
    {
        Key key = getKey();
        if (key == null)
        {
            return;
        }

        // We need a name to save against
        if (saved == null && !querySaveFile())
        {
            return;
        }

        saveKey(key);
    }

    /**
     * Save the view to disk, but ask the user where to save it first.
     * @throws IOException
     */
    public void saveAs() throws IOException
    {
        Key key = getKey();
        if (key == null)
        {
            return;
        }

        querySaveFile();

        saveKey(key);
    }

    /**
     * Do the real work of saving to a file
     * @param key The key to save
     * @throws IOException If a write error happens
     */
    private void saveKey(Key key) throws IOException
    {
        assert saved != null;

        Writer out = null;
        try
        {
            out = new FileWriter(saved);
            if (key instanceof Passage)
            {
                Passage ref = (Passage) key;
                ref.writeDescription(out);
            }
            else
            {
                out.write(key.getName());
                out.write("\n"); //$NON-NLS-1$
            }
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    /**
     * Returns true if there is something to save.
     */
    public boolean maySave()
    {
        return getKey() != null;
    }

    /**
     * Open a saved verse list form disk
     * @throws IOException 
     * @throws NoSuchVerseException
     */
    public void open() throws NoSuchVerseException, IOException
    {
        int reply = chooser.showOpenDialog(getRootPane());
        if (reply == JFileChooser.APPROVE_OPTION)
        {
            saved = chooser.getSelectedFile();
            if (saved.length() == 0)
            {
                Reporter.informUser(getRootPane(), Msg.EMPTY_FILE, saved.getName());
                return;
            }

            Reader in = null;
            try
            {
                in = new FileReader(saved);
                Passage ref = PassageKeyFactory.readPassage(in);
                setKey(ref);
            }
            finally
            {
                if (in != null)
                {
                    in.close();
                }
            }
        }
    }

    /**
     * Ask the user where to store the data
     */
    private boolean querySaveFile()
    {
        if (saved == null)
        {
            File guess = new File(getTitle() + EXTENSION);
            chooser.setSelectedFile(guess);
        }
        else
        {
            chooser.setSelectedFile(saved);
        }

        int reply = chooser.showSaveDialog(getRootPane());
        if (reply == JFileChooser.APPROVE_OPTION)
        {
            saved = chooser.getSelectedFile();
            return true;
        }
        return false;
    }

    /**
     * Accessor for the current passage
     */
    public Key getKey()
    {
        return pnlPassg.getKey();
    }

    /**
     * Accessor for the current passage
     */
    public void setKey(Key key)
    {
        pnlPassg.setBookData(pnlSelect.getBook(), key);
//        if (saved == null)
//        {
//            fireTitleChanged(new TitleChangedEvent(BibleViewPane.this, getTitle()));
//        }
    }

    /**
     * Accessor for the SplitBookDataDisplay
     */
    public SplitBookDataDisplay getPassagePane()
    {
        return pnlPassg;
    }

    /**
     * Accessor for the DisplaySelectPane
     */
    public DisplaySelectPane getSelectPane()
    {
        return pnlSelect;
    }

    /**
     * Add a TitleChangedEvent listener
     */
    public synchronized void addTitleChangedListener(TitleChangedListener li)
    {
        listeners.add(TitleChangedListener.class, li);
    }

    /**
     * Remove a TitleChangedEvent listener
     */
    public synchronized void removeTitleChangedListener(TitleChangedListener li)
    {
        listeners.remove(TitleChangedListener.class, li);
    }

    /**
     * Listen for changes to the title
     * @param ev the event to throw
     */
    protected void fireTitleChanged(TitleChangedEvent ev)
    {
        // Guaranteed to return a non-null array
        Object[] contents = listeners.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = contents.length - 2; i >= 0; i -= 2)
        {
            if (contents[i] == TitleChangedListener.class)
            {
                ((TitleChangedListener) contents[i + 1]).titleChanged(ev);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.common.swing.desktop.event.TitleChangedListener#titleChanged(org.crosswire.common.swing.desktop.event.TitleChangedEvent)
     */
    public void titleChanged(TitleChangedEvent ev)
    {
        if (saved == null)
        {
            fireTitleChanged(new TitleChangedEvent(BibleViewPane.this, getTitle()));
        }
    }

    /**
     * Serialization support.
     * 
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException
    {
        listeners = new EventListenerList();
        is.defaultReadObject();
    }

    protected File saved;
    private transient EventListenerList listeners;
    private DisplaySelectPane pnlSelect;
    protected SplitBookDataDisplay pnlPassg;
    private JFileChooser chooser = new JFileChooser();
    private static final String EXTENSION = ".lst"; //$NON-NLS-1$

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(BibleViewPane.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3258415036346282038L;

    /**
     * Filter out verse lists
     */
    static final class CustomFileFilter extends FileFilter
    {
        /* (non-Javadoc)
         * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
         */
        /* @Override */
        public boolean accept(File file)
        {
            return file.getName().endsWith(EXTENSION);
        }

        /* (non-Javadoc)
         * @see javax.swing.filechooser.FileFilter#getDescription()
         */
        /* @Override */
        public String getDescription()
        {
            return Msg.VERSE_LIST_DESC.toString(EXTENSION);
        }
    }
}
