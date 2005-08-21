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

import org.crosswire.bibledesktop.display.URLEvent;
import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.common.progress.swing.JobsProgressBar;

/**
 * The status bar provides useful info to the user as to the current
 * state of the program.
 * <p>We need to think about the stuff to put in here:<ul>
 * <li>A status message. This changes with what the user is pointing at,
 *     so is very similar to tool-tips. Although they are commonly more
 *     instructional.
 * <li>A set of panels that tell you the time/if CAPS is presses and so on
 * </ul>
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class StatusBar extends JComponent implements MouseListener, URLEventListener
{
    /**
     * Create a new StatusBar
     */
    public StatusBar()
    {
        initialize();
    }

    /**
     * Init the GUI
     */
    private void initialize()
    {
        lbl_message.setText(Msg.STATUS_DEFAULT.toString());

        Font font = pnl_progr.getFont();
        pnl_progr.setFont(font.deriveFont(6.0F));

        /*
        Dimension dim = pnl_progr.getPreferredSize();
        dim.height = lbl_message.getSize().height;
        pnl_progr.setPreferredSize(dim);
        */

        lbl_name.setText(' ' + Msg.getVersionedApplicationTitle() + ' ');

        this.setBorder(BorderFactory.createEtchedBorder());
        this.setLayout(new GridBagLayout());

        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);

        this.add(lbl_message, new GridBagConstraints(0, 0, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(separator,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(pnl_progr,   new GridBagConstraints(2, 0, 1, 1, 0.5, 0.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(separator2,  new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lbl_name,    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));        
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URLEventListener#processURL(org.crosswire.bibledesktop.display.URLEvent)
     */
    public void activateURL(URLEvent ev)
    {
        // We don't care about activate events
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URLEventListener#enterURL(org.crosswire.bibledesktop.display.URLEvent)
     */
    public void enterURL(URLEvent ev)
    {
        String protocol = ev.getProtocol();
        String url = ev.getURL();
        if (protocol.length() == 0)
        {
            lbl_message.setText(url); //$NON-NLS-1$
        }
        else
        {
            lbl_message.setText(protocol + "://" + url); //$NON-NLS-1$
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.URLEventListener#leaveURL(org.crosswire.bibledesktop.display.URLEvent)
     */
    public void leaveURL(URLEvent ev)
    {
        lbl_message.setText(Msg.STATUS_DEFAULT.toString());
    }

    /**
     * When the mouse points at something that has registered with us
     * to be shown on the statusbar
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent ev)
    {
        if (ev.getSource() instanceof AbstractButton)
        {
            AbstractButton button = (AbstractButton) ev.getSource();
            Action action = button.getAction();

            if (action != null)
            {
                Object value = action.getValue(Action.LONG_DESCRIPTION);

                if (value != null)
                {
                    lbl_message.setText(value.toString());
                }
            }
        }
    }

    /**
     * When the mouse no longer points at something that has registered with us
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent ev)
    {
        lbl_message.setText(Msg.STATUS_DEFAULT.toString());
    }

    /**
     * Invoked when the mouse has been clicked on a component.
     * Ignored
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent ev)
    {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * Ignored
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent ev)
    {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * Ignored
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent ev)
    {
    }

    /**
     * Where the progress bars go
     */
    private JobsProgressBar pnl_progr = new JobsProgressBar(true);

    /**
     * Where the help messages go
     */
    private JLabel lbl_message = new JLabel();

    /**
     * Where the product name goes
     */
    private JLabel lbl_name = new JLabel();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3546920264718955568L;
}
