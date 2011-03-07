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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.crosswire.bibledesktop.BDMsg;
import org.crosswire.bibledesktop.display.URIEvent;
import org.crosswire.bibledesktop.display.URIEventListener;
import org.crosswire.common.progress.swing.JobsProgressBar;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.util.OSType;

/**
 * The status bar provides useful info to the user as to the current state of
 * the program.
 * <p>
 * We need to think about the stuff to put in here:
 * <ul>
 * <li>A status message. This changes with what the user is pointing at, so is
 * very similar to tool-tips. Although they are commonly more instructional.
 * <li>A set of panels that tell you the time/if CAPS is presses and so on
 * </ul>
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StatusBar extends JComponent implements MouseListener, URIEventListener {
    /**
     * Create a new StatusBar
     */
    public StatusBar() {
        initialize();
    }

    /**
     * Init the GUI
     */
    private void initialize() {
        // TRANSLATOR: This is the text in the status bar when there is nothing else to say.
        labelMessage.setText(BDMsg.gettext("Ready ..."));

        Font font = panelProgress.getFont();
        panelProgress.setFont(font.deriveFont(6.0F));

        /*
        Dimension dim = panelProgress.getPreferredSize();
        dim.height = labelMessage.getSize().height;
        panelProgress.setPreferredSize(dim);
        */

        this.setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(new GridBagLayout());

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);

        // Right pad the last entry so that it stays away from the corner.
        // On the Mac, the "grow" handle is in the corner of the app.
        int finalPadX = 0;
        int finalPadY = 0;
        if (OSType.MAC.equals(OSType.getOSType())) {
            finalPadX = 20;
            finalPadY = 5;
        }
        this.add(labelMessage, new GridBagConstraints(0, 0, 1, 1, 0.3, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(separator,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(panelProgress, new GridBagConstraints(2, 0, 1, 1, 0.7, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0),
                finalPadX, finalPadY));
        GuiUtil.applyDefaultOrientation(this);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#activateURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void activateURI(URIEvent ev) {
        // We don't care about activate events
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#enterURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void enterURI(URIEvent ev) {
        String protocol = ev.getScheme();
        String uri = ev.getURI();
        if (protocol.length() == 0) {
            labelMessage.setText(uri);
        } else {
            labelMessage.setText(protocol + "://" + uri);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URIEventListener#leaveURI(org.crosswire.bibledesktop.display.URIEvent)
     */
    public void leaveURI(URIEvent ev) {
        // TRANSLATOR: This is the text in the status bar when there is nothing else to say.
        labelMessage.setText(BDMsg.gettext("Ready ..."));
    }

    /**
     * Sets the text to display
     * 
     * @param txt
     *            The text
     */
    public void setText(String txt) {
        if (txt == null) {
            // TRANSLATOR: This is the text in the status bar when there is nothing else to say.
            labelMessage.setText(BDMsg.gettext("Ready ..."));
        } else {
            labelMessage.setText(txt);
        }
    }

    /**
     * Catches status signals and displays new text
     * 
     * @param signal
     *            The signal with the status text
     */
    // public void channel(final StatusSignal signal)
    // {
    // SwingUtilities.invokeLater(new Runnable()
    // {
    // public void run()
    // {
    // labelMessage.setText(signal.getMessage());
    // }
    // });
    // }
    /**
     * When the mouse points at something that has registered with us to be
     * shown on the statusbar
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent ev) {
        if (ev.getSource() instanceof AbstractButton) {
            AbstractButton button = (AbstractButton) ev.getSource();
            Action action = button.getAction();

            if (action != null) {
                Object value = action.getValue(Action.SHORT_DESCRIPTION);

                if (value != null) {
                    labelMessage.setText(value.toString());
                }
            }
        }
    }

    /**
     * When the mouse no longer points at something that has registered with us
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent ev) {
        // TRANSLATOR: This is the text in the status bar when there is nothing else to say.
        labelMessage.setText(BDMsg.gettext("Ready ..."));
    }

    /**
     * Invoked when the mouse has been clicked on a component. Ignored
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent ev) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component. Ignored
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent ev) {
    }

    /**
     * Invoked when a mouse button has been released on a component. Ignored
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent ev) {
    }

    /**
     * Where the progress bars go
     */
    private JobsProgressBar panelProgress = new JobsProgressBar(true);

    /**
     * Where the help messages go
     */
    protected JLabel labelMessage = new JLabel();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3546920264718955568L;
}
