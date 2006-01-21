package org.crosswire.jsword.view.cli;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * A Quick test GUI to see if we want to re-introduce a script pane.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BSFGui extends JPanel
{
    /**
     * Start point
     */
    public static void main(String[] args)
    {
        JFrame frame = BSFGui.showInFrame("Test GUI"); //$NON-NLS-1$
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * 
     */
    private static JFrame showInFrame(String title)
    {
        BSFGui gui = new BSFGui(title);

        JFrame frame = new JFrame();
        frame.setContentPane(gui);
        frame.setSize(246, 183);
        frame.setTitle(gui.getTitle());
        frame.setVisible(true);

        return frame;
    }

    /**
     * Simple ctor
     */
    public BSFGui(String title)
    {
        this.title = title;
        jbInit();
    }

    /**
     * Initialise the GUI
     */
    private void jbInit()
    {
        txtcmd.setColumns(10);
        txtcmd.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                exec();
            }
        });

        lblcmd.setText(" > "); //$NON-NLS-1$

        pnlcmd.setLayout(new BorderLayout());
        pnlcmd.add(lblcmd, BorderLayout.WEST);
        pnlcmd.add(txtcmd, BorderLayout.CENTER);

        txtresult.setEditable(false);
        scrresult.setViewportView(txtresult);

        pnlentry.setLayout(new BorderLayout(5, 5));
        pnlentry.add(scrresult, BorderLayout.CENTER);
        pnlentry.add(pnlcmd, BorderLayout.NORTH);
        pnlentry.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        scrvars.setViewportView(lstvars);

        pnlvars.setLayout(new BorderLayout());
        pnlvars.add(scrvars, BorderLayout.CENTER);
        pnlvars.setSize(234, 153);
        pnlvars.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        sptmain.setLeftComponent(pnlentry);
        sptmain.setRightComponent(pnlvars);
        sptmain.setDividerLocation(150);

        this.setLayout(new BorderLayout());
        this.add(sptmain, BorderLayout.CENTER);
    }

    /**
     * Execute the next command
     */
    protected void exec()
    {
        Object reply = null;

        String command = txtcmd.getText();
        txtcmd.setText(""); //$NON-NLS-1$
        System.out.println(command);

        Context cx = Context.enter();
        try
        {
            Scriptable scope = cx.initStandardObjects(null);

            Scriptable jsout = Context.toObject(System.out, scope);
            scope.put("out", scope, jsout); //$NON-NLS-1$

            reply = cx.evaluateString(scope, command, "<cmd>", 1, jsout); //$NON-NLS-1$
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            reply = ex;
        }
        finally
        {
            Context.exit();
        }
        
        String results = txtresult.getText();
        results = results + "\n" + Context.toString(reply); //$NON-NLS-1$
        txtresult.setText(results);
    }

    /**
     * Accessor for the window/script title
     */
    private String getTitle()
    {
        return title;
    }

    private String title;
    private JSplitPane sptmain = new JSplitPane();
    private JPanel pnlentry = new JPanel();
    private JScrollPane scrvars = new JScrollPane();
    private JList lstvars = new JList();
    private JScrollPane scrresult = new JScrollPane();
    private JPanel pnlcmd = new JPanel();
    private JLabel lblcmd = new JLabel();
    private JTextField txtcmd = new JTextField();
    private JTextArea txtresult = new JTextArea();
    private JPanel pnlvars = new JPanel();
}
