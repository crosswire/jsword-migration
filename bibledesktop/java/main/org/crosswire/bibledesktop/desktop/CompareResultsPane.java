package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.Document;

import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.DocumentWriter;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.basic.Verifier;
import org.crosswire.jsword.passage.Passage;

/**
 * This displays the results of a comparision that occurs in a separate
 * thread.
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
public class CompareResultsPane extends JPanel implements Runnable
{
    // DEAD(DM): This class is not used. Find a use for it or delete it.
    /**
     * Basic Constructor
     */
    public CompareResultsPane(Verifier ver)
    {
        this.ver = ver;
        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        setTitles();
        boxBibles = Box.createVerticalBox();
        boxBibles.add(lblBible1, null);
        boxBibles.add(lblBible2, null);

        barProgress.setString(""); //$NON-NLS-1$
        barProgress.setStringPainted(true);
        txtResults.setRows(5);
        txtResults.setColumns(40);
        scrResults.getViewport().add(txtResults, null);
        pnlResults.setLayout(new BorderLayout(5, 5));
        pnlResults.setBorder(new TitledBorder(Msg.RESULTS_TITLE.toString()));
        pnlResults.add(scrResults, BorderLayout.CENTER);
        pnlResults.add(barProgress, BorderLayout.NORTH);

        btnStop.setText(Msg.RESULTS_START.toString());
        btnStop.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                startStop();
            }
        });
        pnlButtons.add(btnStop, null);

        this.setLayout(new BorderLayout());
        this.add(boxBibles, BorderLayout.NORTH);
        this.add(pnlResults, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
    }

    /**
     * This allows up to easily display this component in a window and
     * have the 2 work together on close actions and so on.
     */
    public void showInFrame(Frame parent)
    {
        final JDialog frame = new JDialog(parent, Msg.RESULTS_DIALOG.toString());

        btnClose = new JButton(Msg.RESULTS_CLOSE.toString());
        btnClose.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                if (work != null)
                    startStop();
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnlButtons.add(btnClose, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosed(WindowEvent ev)
            {
                if (work != null)
                    startStop();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        frame.pack();
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Start running the tests
     */
    public void startStop()
    {
        if (work == null)
        {
            // New thread to do the real work
            work = new Thread(this);
            work.start();
            work.setPriority(Thread.MIN_PRIORITY);
        }
        else
        {
            work.interrupt();
            work = null;
        }
    }

    /**
     * The text that we will check, null for no check, we apply startsWith
     * to the given word before we run the check.
     */
    public void setCheckText(String check_text)
    {
        this.checkText = check_text;
        setTitles();
    }

    /**
     * The Passage that we will check, null for no check.
     */
    public void setCheckPassages(Passage check_ref)
    {
        this.checkRef = check_ref;
        setTitles();
    }

    /**
     * Set the title of the pane to what we are doing
     */
    private void setTitles()
    {
        lblBible1.setText("<html><b>" + Msg.RESULTS_BOOKS + "</b> " //$NON-NLS-1$ //$NON-NLS-2$
                           + ver.getBible1().getBookMetaData().getName() + " / " //$NON-NLS-1$
                           + ver.getBible2().getBookMetaData().getName());

        String compare = "<html><b>" + Msg.RESULTS_COMPARING + "</b> "; //$NON-NLS-1$ //$NON-NLS-2$
        if (checkRef != null)
        {
            compare += Msg.RESULTS_PASSAGE + "=" + checkRef + " "; //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (checkText != null)
        {
            compare += Msg.RESULTS_WORDS + "=" + (checkText.equals("") ? "*" : checkText); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        lblBible2.setText(compare);
    }

    /**
     * A class to be run in a Thread to do the real work of comparing the
     * selected Books
     */
    public void run()
    {
        // While we are working stop anyone editing the values
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                btnStop.setText(Msg.RESULTS_STOP.toString());
            }
        });

        Document doc = txtResults.getDocument();
        dout.setDocument(doc);
        PrintWriter out = new PrintWriter(dout);
        alive = true;

        try
        {
            JobManager.addWorkListener(cpl);

            if (checkText != null && checkText.equals("") && alive) //$NON-NLS-1$
            {
                ver.checkPassage(checkText, out);
            }

            if (checkRef != null && checkRef.isEmpty() && alive)
            {
                ver.checkText(checkRef, out);
            }
        }
        catch (final Exception ex)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    ExceptionPane.showExceptionDialog(CompareResultsPane.this, ex);
                }
            });
        }
        finally
        {
            JobManager.removeWorkListener(cpl);
        }

        // Re-enable the values
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                btnStop.setText(Msg.RESULTS_START.toString());
            }
        });
    }

    /**
     * Are we being told to die
     */
    private boolean alive = true;

    /**
     * The text to check
     */
    private String checkText = null;

    /**
     * The passage to check
     */
    private Passage checkRef = null;

    /**
     * The Bible verifier
     */
    private Verifier ver;

    /**
     * The DocumentWriter that the comparison can write to
     */
    private DocumentWriter dout = new DocumentWriter();

    /**
     * Work in progress
     */
    protected Thread work;

    /**
     * The progress listener
     */
    private CustomProgressListener cpl = new CustomProgressListener();

    /* GUI components */
    private JPanel pnlResults = new JPanel();
    private JScrollPane scrResults = new JScrollPane();
    private JTextArea txtResults = new JTextArea();
    protected JProgressBar barProgress = new JProgressBar();
    private Box boxBibles;
    private JLabel lblBible1 = new JLabel();
    private JPanel pnlButtons = new JPanel();
    protected JButton btnStop = new JButton();
    private JButton btnClose = null;
    private JLabel lblBible2 = new JLabel();

    /**
     * Report progress changes to the screen
     */
    class CustomProgressListener implements WorkListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.common.progress.WorkListener#progressMade(org.crosswire.common.progress.WorkEvent)
         */
        public void workProgressed(final WorkEvent ev)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    Job job = ev.getJob();
                    int percent = job.getPercent();
                    barProgress.setString(job.getStateDescription() + ": (" + percent + "%)"); //$NON-NLS-1$ //$NON-NLS-2$
                    barProgress.setValue(percent);
                }
            });
        }
    }
}