
package org.crosswire.bible.view.swing.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.common.swing.EirPanel;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.util.Project;

/**
 * A Simple pane that contains the Apache Java web server for testing
 * purposes
 *
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
public class WebServerPane extends EirPanel
{
    /**
     * Basic Constructor
     */
    public WebServerPane()
    {
        jbInit();
    }

    /**
     * Create the GUI
     */
    private void jbInit()
    {
        chk_started.setMnemonic('W');
        chk_started.setText("Web Server Running");
        chk_started.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent ev) { startStop(); }
        });

        lay_state.setAlignment(FlowLayout.LEFT);
        pnl_state.setLayout(lay_state);
        pnl_state.setBorder(new TitledBorder("Web Server State"));
        pnl_state.add(chk_started, null);

        txt_results.setColumns(30);
        txt_results.setRows(10);
        scr_results.getViewport().add(txt_results, null);

        this.setLayout(new BorderLayout());
        this.add(scr_results, BorderLayout.CENTER);
        this.add(pnl_state, BorderLayout.NORTH);
    }

    /**
     * Show this Panel in a new dialog
     */
    public void showInDialog(Component parent)
    {
        showInDialog(parent, "Web Server", false);
    }

    /**
     * When someone toggles the state of the internal web server
     */
    private void startStop()
    {
        // if we are not started but should be
        if (chk_started.isSelected() && work == null)
        {
            work = new Thread(new Runnable() {
                public void run()
                {
                    try
                    {
                        URL url = NetUtil.lengthenURL(Project.getConfigRoot(), "server.xml");
                        String file = url.getFile().replace('\\', '/');

                        throw new Exception("Since tomcat 3.2 changed the embedded start system, this has been broken");
                        /*
                        Startup start = new Startup();
                        start.configure(new String[] { "-config", file });
                        */
                    }
                    catch (Exception ex)
                    {
                        Reporter.informUser(this, ex);
                    }
                }
            });
            work.start();
        }

        // if we are started but shouldn't be
        if (!chk_started.isSelected() && work != null)
        {
            /*
            Shutdown.main(new String[0]);
            work = null;
            */
        }
    }

    /** The web server thread */
    private Thread work = null;

    /* GUI Components */
    private JScrollPane scr_results = new JScrollPane();
    private JTextArea txt_results = new JTextArea();
    private JPanel pnl_state = new JPanel();
    private JCheckBox chk_started = new JCheckBox();
    private FlowLayout lay_state = new FlowLayout();
}
