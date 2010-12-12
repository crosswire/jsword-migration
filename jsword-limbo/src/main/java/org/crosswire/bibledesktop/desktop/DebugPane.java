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
package org.crosswire.bibledesktop.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.crosswire.bibledesktop.book.BibleViewPane;
import org.crosswire.common.progress.JobManager;
import org.crosswire.common.progress.Progress;
import org.crosswire.common.util.CWProject;
import org.crosswire.common.util.FileUtil;
import org.crosswire.common.util.Logger;

/**
 * Various debug actions, for easy editing to help us hack over time.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DebugPane extends JPanel {
    /**
     * Simple ctor
     */
    public DebugPane(Desktop desktop) {
        this.desktop = desktop;

        Method[] methods = getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getParameterTypes().length == 0 && method.getDeclaringClass() == getClass() && Modifier.isPublic(method.getModifiers())) {
                mdlMethods.addElement(method);
            }
        }

        init();
    }

    /**
     * Setup the GUI
     */
    private void init() {
        lblMethod.setLabelFor(cboMethod);
        lblMethod.setText(LimboMsg.DEBUG_METHOD.toString());

        cboMethod.setModel(mdlMethods);
        cboMethod.setRenderer(new CustomListCellRenderer());

        // I18N: migrate this to an ActionFactory
        btnMethod.setText(LimboMsg.DEBUG_GO.toString());
        btnMethod.setMnemonic(LimboMsg.DEBUG_GO.toString().charAt(0));
        btnMethod.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                action();
            }
        });

        pnlMethod.setLayout(new BorderLayout(5, 5));
        pnlMethod.add(lblMethod, BorderLayout.LINE_START);
        pnlMethod.add(cboMethod, BorderLayout.CENTER);
        pnlMethod.add(btnMethod, BorderLayout.LINE_END);

        scrResults.getViewport().add(txtResults);

        this.setLayout(new BorderLayout(5, 5));
        this.add(scrResults, BorderLayout.CENTER);
        this.add(pnlMethod, BorderLayout.NORTH);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Call the chosen method
     */
    protected void action() {
        try {
            Method method = (Method) cboMethod.getSelectedItem();
            Object reply = method.invoke(this, new Object[0]);
            if (reply == null) {
                txtResults.setText("");
            } else {
                txtResults.setText(reply.toString());
            }
        } catch (Exception ex) {
            StringWriter sout = new StringWriter();
            PrintWriter out = new PrintWriter(sout);
            ex.printStackTrace(out);
            txtResults.setText(sout.toString());
        }
    }

    /**
     * Some debug action that we can configure
     */
    public String showViews() {
        StringBuilder reply = new StringBuilder();

        reply.append('\n');
        reply.append(LimboMsg.DEBUG_VIEWS.toString());
        int i = 0;
        Iterator it = desktop.getViews().iterator();
        while (it.hasNext()) {
            BibleViewPane view = (BibleViewPane) it.next();
            reply.append(i++);
            reply.append(": ");
            reply.append(view.getTitle());
            reply.append(' ');
            reply.append(view.toString());
        }

        return reply.toString();
    }

    /**
     * Create some test jobs
     */
    public void createTestJobs() {
        createTestJob(30000, "test1", 20);
        createTestJob(30000, "test2", 3);
        createTestJob(30000, "test3", 3);
    }

    /**
     * Create a test job
     */
    public static void createTestJob(final long millis, final String predictbase, final int steps) {
        final URI predicturl = CWProject.instance().getWritableURI(predictbase, FileUtil.EXTENSION_PROPERTIES);
        final Thread test = new Thread() {
            /*
             * (non-Javadoc)
             * 
             * @see java.lang.Thread#run()
             */
            @Override
            public synchronized void run() {
                Progress job = JobManager.createJob(predictbase, Thread.currentThread());
                job.beginJob(predictbase, predicturl);

                job.setSectionName(LimboMsg.DEBUG_STEPS.toString(new Object[] {
                        Integer.valueOf(0), Integer.valueOf(steps)
                }));
                log.debug("starting test job:");

                for (int i = 1; i <= steps && !Thread.interrupted(); i++) {
                    try {
                        wait(millis / steps);
                    } catch (InterruptedException ex) {
                        log.warn("Exception while waiting", ex);
                    }

                    job.setWork((i * 100) / steps);
                    job.setSectionName(LimboMsg.DEBUG_STEPS.toString(new Object[] {
                            Integer.valueOf(i), Integer.valueOf(steps)
                    }));
                }

                job.done();
                log.debug("finishing test job:");
            }
        };
        test.start();
    }

    /**
     *
     */
    public void openSplash() {
        if (splash == null) {
            splash = new Splash();
            splash.pack();
        }
    }

    /**
     *
     */
    public void closeSplash() {
        if (splash != null) {
            splash.close();
            splash = null;
        }
    }

    private Splash splash;

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(DebugPane.class);

    /**
     * The main window
     */
    private Desktop desktop;

    /*
     * GUI Components
     */
    private DefaultComboBoxModel mdlMethods = new DefaultComboBoxModel();
    private JScrollPane scrResults = new JScrollPane();
    private JPanel pnlMethod = new JPanel();
    private JLabel lblMethod = new JLabel();
    private JComboBox cboMethod = new JComboBox();
    private JButton btnMethod = new JButton();
    private JTextPane txtResults = new JTextPane();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257853185987983152L;

    /**
     * Simpler method name display
     */
    static final class CustomListCellRenderer extends DefaultListCellRenderer {
        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
         * .JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Method) {
                setText(((Method) value).getName() + "()");
            }
            return this;
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257853185987983152L;
    }
}
