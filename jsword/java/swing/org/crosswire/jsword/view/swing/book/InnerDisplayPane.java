package org.crosswire.jsword.view.swing.book;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.Reporter;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.SerializingContentHandler;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.util.ConverterFactory;
import org.crosswire.jsword.util.Project;
import org.crosswire.jsword.view.swing.display.BookDataDisplay;

/**
 * An inner component of Passage pane that can't show the list.
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
public class InnerDisplayPane extends JPanel implements FocusablePart
{
    /**
     * Simple Constructor
     */
    public InnerDisplayPane()
    {
        initialize();
    }

    /**
     * Makes the second invocation much faster
     */
    public static void preload()
    {
        final Thread worker = new Thread("DisplayPreLoader")
        {
            public void run()
            {
                URL predicturl = Project.instance().getWritablePropertiesURL("display");
                Job job = JobManager.createJob("Display Pre-load", predicturl, this, true);

                try
                {
                    job.setProgress("Setup");
                    List booklist = Books.installed().getBookMetaDatas();
                    if (booklist.size() == 0)
                    {
                        return;
                    }

                    Book test = ((BookMetaData) booklist.get(0)).getBook();
                    if (interrupted())
                    {
                        return;
                    }

                    job.setProgress("Getting initial data");
                    BookData data = test.getData(test.getGlobalKeyList().get(0));
                    if (interrupted())
                    {
                        return;
                    }

                    job.setProgress("Getting event provider");
                    SAXEventProvider provider = data.getSAXEventProvider();
                    if (interrupted())
                    {
                        return;
                    }

                    job.setProgress("Compiling stylesheet");
                    Converter converter = ConverterFactory.getConverter();
                    converter.convert(provider);
                    if (interrupted())
                    {
                        return;
                    }
                }
                catch (Exception ex)
                {
                    job.ignoreTimings();
                    log.error("View pre-load failed", ex);
                }
                finally
                {
                    job.done();
                    log.debug("View pre-load finished");
                }
            }
        };

        worker.setPriority(Thread.MIN_PRIORITY);
        worker.start();
    }

    /**
     * Gui creation
     */
    private void initialize()
    {
        scrView.getViewport().setPreferredSize(new Dimension(500, 400));
        scrView.getViewport().add(txtView.getComponent(), null);

        this.setLayout(new BorderLayout());
        this.add(scrView, BorderLayout.CENTER);
    }

    /**
     * Set the version used for lookup
     */
    public void setBook(Book book)
    {
        this.book = book;
    }

    /**
     * Set the passage being viewed
     */
    public void setPassage(Passage ref) throws BookException
    {
        this.ref = ref;

        if (ref == null || book == null)
        {
            txtView.setBookData(null);
        }
        else
        {
            BookData data = book.getData(ref);
            txtView.setBookData(data);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getHTMLSource()
     */
    public String getHTMLSource()
    {
        return txtView.getHTMLSource();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getOSISSource()
     */
    public String getOSISSource()
    {
        if (ref == null || book == null)
        {
            return "";
        }

        try
        {
            BookData data = book.getData(ref);
            SAXEventProvider provider = data.getSAXEventProvider();
            SerializingContentHandler handler = new SerializingContentHandler(true);
            provider.provideSAXEvents(handler);

            return handler.toString();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
            return "";
        }
    }

    /**
     * Accessor for the current passage
     */
    public Passage getPassage()
    {
        return ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#getKey()
     */
    public Key getKey()
    {
        return ref;
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#copy()
     */
    public void copy()
    {
        txtView.copy();
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#addHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void addHyperlinkListener(HyperlinkListener li)
    {
        txtView.addHyperlinkListener(li);
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.view.swing.book.FocusablePart#removeHyperlinkListener(javax.swing.event.HyperlinkListener)
     */
    public void removeHyperlinkListener(HyperlinkListener li)
    {
        txtView.removeHyperlinkListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void removeMouseListener(MouseListener li)
    {
        txtView.removeMouseListener(li);
    }

    /**
     * Forward the mouse listener to our child components
     */
    public void addMouseListener(MouseListener li)
    {
        txtView.addMouseListener(li);
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(InnerDisplayPane.class);

    /**
     * What version is currently being used for display
     */
    private Book book = null;

    /**
     * What was the last passage to be viewed
     */
    private Passage ref = null;

    /**
     * The scroller for the BookDataDisplay component
     */
    private JScrollPane scrView = new JScrollPane();

    /**
     * The display of OSIS data
     */
    private BookDataDisplay txtView = new BookDataDisplay();
}
