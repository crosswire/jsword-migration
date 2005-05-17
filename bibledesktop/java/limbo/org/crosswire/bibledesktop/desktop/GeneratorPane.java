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
package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.crosswire.bibledesktop.book.BookListCellRenderer;
import org.crosswire.bibledesktop.book.BooksComboBoxModel;
import org.crosswire.bibledesktop.book.DriversComboBoxModel;
import org.crosswire.common.progress.Job;
import org.crosswire.common.progress.WorkEvent;
import org.crosswire.common.progress.WorkListener;
import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.swing.ExceptionPane;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.basic.Verifier;

/**
 * Bible Generator allows the creation of new Books - although it
 * really only converts from one implementation of Book to another.
 * This is needed because I drivers like JDBCBook and GBMLBook will not
 * be very speed optimized.
 * <p>To start one of these call:
 * <pre>
 * MaintenancePane maint = new MaintenancePane();
 * maint.showInDialog(getComponent());
 * </pre>
 *
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class GeneratorPane extends EirPanel
{
	// I18N: This class has not been internationalized, because it is not used.
    /**
     * Construct a Bible Generator tool, this simply calls jbInit
     */
    public GeneratorPane()
    {
        init();
    }

    /**
     * Create the GUI components.
     */
    private void init()
    {
        cboSource.setModel(mdlSource);
        cboSource.setRenderer(new BookListCellRenderer());
        lblSource.setText("  Source Bible: "); //$NON-NLS-1$
        pnlSource.setLayout(new BorderLayout());
        pnlSource.setBorder(BorderFactory.createTitledBorder("Source")); //$NON-NLS-1$
        pnlSource.add(lblSource, BorderLayout.WEST);
        pnlSource.add(cboSource, BorderLayout.CENTER);

        lblName.setText("New Name:"); //$NON-NLS-1$
        lblDriver.setText("Driver Class:"); //$NON-NLS-1$

        cboDriver.setModel(mdlDriver);
        pnlDest.setLayout(layDest);
        pnlDest.setBorder(BorderFactory.createTitledBorder("Destination")); //$NON-NLS-1$

        pnlDest.add(lblName, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
        pnlDest.add(lblDriver, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        pnlDest.add(cboDriver, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 5, 5), 0, 0));

        barProg.setBorderPainted(true);
        barProg.setMaximum(100);
        barProg.setString(""); //$NON-NLS-1$
        barProg.setStringPainted(true);
        pnlProg.setLayout(new BorderLayout());
        pnlProg.setBorder(BorderFactory.createTitledBorder("Progress")); //$NON-NLS-1$
        pnlProg.add(barProg, BorderLayout.CENTER);

        boxMain = Box.createVerticalBox();
        boxMain.add(pnlSource, null);
        boxMain.add(pnlDest, null);

        btnGenerate.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                generate();
            }
        });
        btnGenerate.setText("Generate"); //$NON-NLS-1$
        btnGenerate.setMnemonic('G');

        chkVerify.setText("Verify After Generation"); //$NON-NLS-1$
        chkVerify.setMnemonic('V');
        chkVerify.setSelected(false);
        layButtons.setAlignment(FlowLayout.RIGHT);
        pnlButtons.setLayout(layButtons);
        pnlButtons.add(chkVerify, null);
        pnlButtons.add(btnGenerate, null);

        this.setLayout(new BorderLayout());
        this.add(boxMain, BorderLayout.NORTH);
        this.add(pnlProg, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Generator", false); //$NON-NLS-1$
    }

    /**
     * This allows up to easily display this component in a window and
     * have the 2 work together on close actions and so on.
     */
    public void showInFrame(Frame parent)
    {
        final JDialog frame = new JDialog(parent, "Bible Generator"); //$NON-NLS-1$

        btnClose = new JButton("Close"); //$NON-NLS-1$
        btnClose.setMnemonic('C');
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev)
            {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnlButtons.add(btnClose, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent ev)
            {
                if (work != null)
                    work.interrupt();
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
     * Actually start generating the new Book
     */
    public void generate()
    {
        // New thread to do the real work
        work = new Thread(new GeneratorRunnable());
        work.start();
        work.setPriority(Thread.MIN_PRIORITY);
    }

    /**
     * Holder for the source and destination area
     */
    private Box boxMain;

    /**
     * The Source area
     */
    private JPanel pnlSource = new JPanel();

    /**
     * The destination area
     */
    private JPanel pnlDest = new JPanel();

    /**
     * The source book label
     */
    private JLabel lblSource = new JLabel();

    /**
     * The source picker
     */
    protected JComboBox cboSource = new JComboBox();

    /**
     * The model for the sources.
     * Bibles are required in GeneratorRunnable.run()
     */
    protected BooksComboBoxModel mdlSource = new BooksComboBoxModel(BookFilters.getBibles());

    /**
     * Layout for the destination panel
     */
    private GridBagLayout layDest = new GridBagLayout();

    /**
     * The new version name label
     */
    private JLabel lblName = new JLabel();

    /**
     * Label for the new name class
     */
    private JLabel lblDriver = new JLabel();

    /**
     * Input field for the name class
     */
    protected JComboBox cboDriver = new JComboBox();

    /**
     * The model for the drivers
     */
    protected DriversComboBoxModel mdlDriver = new DriversComboBoxModel(false);

    /**
     * The progress area
     */
    private JPanel pnlProg = new JPanel();

    /**
     * The progress bar
     */
    protected JProgressBar barProg = new JProgressBar();

    /**
     * The button bar
     */
    private JPanel pnlButtons = new JPanel();

    /**
     * Layout for the button bar
     */
    private FlowLayout layButtons = new FlowLayout();

    /**
     * The generate button
     */
    protected JButton btnGenerate = new JButton();

    /**
     * The close button, only used if we are in our own Frame
     */
    protected JButton btnClose = null;

    /**
     * The verify checkbox
     */
    protected JCheckBox chkVerify = new JCheckBox();

    /**
     * Work in progress
     */
    protected Thread work =  null;

    /**
     * The progress listener
     */
    protected CustomProgressListener cpl = new CustomProgressListener();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3979270239726743601L;

    /**
     * A class to be run in a Thread to do the real work of generating the
     * new Bible
     */
    class GeneratorRunnable implements Runnable
    {
        public void run()
        {
            // While we are working stop anyone editing the values
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    cboSource.setEnabled(false);
                    cboDriver.setEnabled(false);
                    btnGenerate.setEnabled(false);
                    chkVerify.setEnabled(false);
                    btnClose.setText("Cancel"); //$NON-NLS-1$
                }
            });

            try
            {
                // Get the values
                // This cast is safe because the ctor filers for Bibles
                Book source = mdlSource.getSelectedBook();
                BookDriver destDriver = mdlDriver.getSelectedDriver();

                // The real work
                // This cast is safe because we passed in a Bible
                Book destVersion = destDriver.create(source);

                // Check
                if (chkVerify.isEnabled())
                {
                    Verifier ver = new Verifier(source, destVersion);

                    CompareResultsPane results = new CompareResultsPane(ver);
                    results.setCheckText(""); //$NON-NLS-1$
                    results.setCheckPassages(null);
                    results.showInFrame(GuiUtil.getFrame(GeneratorPane.this));
                    results.startStop();
                }
            }
            catch (final Exception ex)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                        ExceptionPane.showExceptionDialog(GeneratorPane.this, ex);
                    }
                });
            }

            // Re-enable the values
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    cboSource.setEnabled(true);
                    cboDriver.setEnabled(true);
                    btnGenerate.setEnabled(true);
                    chkVerify.setEnabled(true);
                    btnClose.setText("Close"); //$NON-NLS-1$
                }
            });
        }
    }

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
                    barProg.setString(job.getStateDescription() + ": (" + percent + "%)"); //$NON-NLS-1$  //$NON-NLS-2$
                    barProg.setValue(percent);
                }
            });
        }
    }
}