package org.crosswire.common.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.crosswire.common.util.Reporter;
import org.crosswire.common.util.StringUtil;

/**
 * TextViewPanel allow viewing of some text in its own standalone frame.
 * The text to be viewed can be grabbed from a String, a URL, or a file.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class TextViewPanel extends JPanel
{
	/**
     * Construct a TextViewPanel by calling jbInit()
     */
    public TextViewPanel()
    {
        jbInit();
    }

    /**
     * Construct a TextViewPanel with some string contents
     * @param text The contents of the text area
     */
    public TextViewPanel(String text)
    {
        jbInit();
        setText(text);
    }

    /**
     * Construct a TextViewPanel with a URL from which to read the text
     * @param url A pointer to the contents of the text area
     */
    public TextViewPanel(URL url) throws IOException
    {
        jbInit();
        setText(url);
    }

    /**
     * Construct a TextViewPanel with a File from which to read the text
     * @param file A pointer to the contents of the text area
     */
    public TextViewPanel(File file) throws IOException
    {
        jbInit();
        setText(file);
    }

    /**
     * Construct a TextViewPanel with some string contents
     * @param text The contents of the text area
     * @param header The string for the header area of the window
     */
    public TextViewPanel(String text, String header)
    {
        jbInit();
        setText(text);
        setHeader(header);
    }

    /**
     * Construct a TextViewPanel with a URL from which to read the text
     * @param url A pointer to the contents of the text area
     * @param header The string for the header area of the window
     */
    public TextViewPanel(URL url, String header) throws IOException
    {
        jbInit();
        setText(url);
        setHeader(header);
    }

    /**
     * Construct a TextViewPanel with a File from which to read the text
     * @param file A pointer to the contents of the text area
     * @param header The string for the header area of the window
     */
    public TextViewPanel(File file, String header) throws IOException
    {
        jbInit();
        setText(file);
        setHeader(header);
    }

    /**
     * Actually create the GUI
     */
    private void jbInit()
    {
        scrText.getViewport().add(txtText, null);
        txtText.setEditable(false);
        txtText.setColumns(80);
        txtText.setRows(24);

        btnClipboard.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                clipboard();
            }
        });
        btnClipboard.setText("Copy"); //$NON-NLS-1$

        layButtons.setAlignment(FlowLayout.TRAILING);
        pnlButtons.setLayout(layButtons);
        pnlButtons.add(btnClipboard, null);

        this.setLayout(new BorderLayout());
        this.add(scrText, BorderLayout.CENTER);
        this.add(pnlButtons, BorderLayout.SOUTH);
    }

    /**
     * Display this Panel in a new JFrame
     */
    public void showInFrame(Frame parent)
    {
        frame = new JDialog(parent, "Text Viewer"); //$NON-NLS-1$

        btnClose = new JButton(LimboMsg.CLOSE.toString());
        btnClose.setMnemonic(LimboMsg.CLOSE.toString().charAt(0));
        btnClose.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                frame.setVisible(false);
                frame.dispose();
            }
        });
        pnlButtons.add(btnClose, null);

        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(this, BorderLayout.CENTER);

        frame.pack();
        GuiUtil.centerWindow(frame);
        frame.setVisible(true);
    }

    /**
     * Copy the current text into the system clipboard
     */
    public void clipboard()
    {
        StringSelection ss = new StringSelection(getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
    }

    /**
     * Setter for the text in the header area
     * @param new_header The new header text
     */
    public void setHeader(String new_header)
    {
        String old_header = lblMain.getText();
        lblMain.setText(new_header);

        if (new_header != null)
        {
            this.add(lblMain, BorderLayout.NORTH);
        }
        else
        {
            this.remove(lblMain);
        }

        listeners.firePropertyChange("header", old_header, new_header); //$NON-NLS-1$
    }

    /**
     * Getter for the text in the header area
     * @return The current header
     */
    public String getHeader()
    {
        return lblMain.getText();
    }

    /**
     * Is the text area editable (default no)
     */
    public void setEditable(boolean editable)
    {
        txtText.setEditable(editable);
    }

    /**
     * Is the text area editable (default no)
     */
    public boolean isEditable()
    {
        return txtText.isEditable();
    }

    /**
     * Setter for the main body of text.
     * @param new_text The text to display
     */
    public void setText(String new_text)
    {
        String old_text = txtText.getText();
        txtText.setText(new_text);
        txtText.setCaretPosition(0);

        if (frame != null)
        {
            GuiUtil.restrainedRePack(frame);
        }

        listeners.firePropertyChange("text", old_text, new_text); //$NON-NLS-1$
    }

    /**
     * Setter for the main body of text
     * @param url A pointer to the text to display
     */
    public void setText(URL url) throws IOException
    {
        setText(url.openStream());
    }

    /**
     * Setter for the main body of text
     * @param file A pointer to the text to display
     */
    public void setText(File file) throws IOException
    {
        setText(new FileInputStream(file));
    }

    /**
     * Setter for the main body of text
     */
    public void setText(final InputStream in)
    {
        // Yes this is twisted, however there is some kind of perverse
        // pleasure in writing this kind of code.
        // But for the setPriority() I might have dispensed with the
        // "Thread work = " bit and just tacked ".start()" to the end
        // This simply creates a thread to read the file, and then a
        // Runnable to update the GUI (swing is single threaded)
        Thread work = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    InputStream pmin = new ProgressMonitorInputStream(TextViewPanel.this, "Loading ...", in); //$NON-NLS-1$
                    Reader rin = new InputStreamReader(pmin);
                    final String data = StringUtil.read(rin);

                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            setText(data);
                        }
                    });
                }
                catch (IOException ex)
                {
                    Reporter.informUser(TextViewPanel.this, ex);
                }
            }
        });

        work.start();
        work.setPriority(Thread.MIN_PRIORITY);
    }

    /**
     * Getter for the main body of text
     * @return The string from the main text area
     */
    public String getText()
    {
        return txtText.getText();
    }

    /**
     * Add a property change listener
     * @param li The property change listener to add
     */
    /* @Override */
    public synchronized void removePropertyChangeListener(PropertyChangeListener li)
    {
        super.removePropertyChangeListener(li);
        listeners.removePropertyChangeListener(li);
    }

    /**
     * Remove a property change listener
     * @param li The property change listener to remove
     */
    /* @Override */
    public synchronized void addPropertyChangeListener(PropertyChangeListener li)
    {
        super.addPropertyChangeListener(li);
        listeners.addPropertyChangeListener(li);
    }

    /**
     * Optional header label
     */
    private JLabel lblMain = new JLabel();

    /**
     * Scroller for the text area
     */
    private JScrollPane scrText = new JScrollPane();

    /**
     * The main text area
     */
    private JTextArea txtText = new JTextArea();

    /**
     * The button bar
     */
    private JPanel pnlButtons = new JPanel();

    /**
     * Button bar layout
     */
    private FlowLayout layButtons = new FlowLayout();

    /**
     * Copy text to clipboard button
     */
    private JButton btnClipboard = new JButton();

    /**
     * Close button
     */
    private JButton btnClose = null;

    /**
     * The frame that we are displayed in
     */
    protected JDialog frame = null;

    /**
     * Property change listener collection
     */
    private transient PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616727167011206964L;
}
