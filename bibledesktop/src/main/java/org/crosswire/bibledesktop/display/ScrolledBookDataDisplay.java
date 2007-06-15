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
package org.crosswire.bibledesktop.display;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JScrollPane;

import org.crosswire.common.swing.CWScrollPane;

/**
 * An inner component of Passage pane that can't show the list.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class ScrolledBookDataDisplay extends ProxyBookDataDisplay
{
    /**
     * Simple Constructor
     */
    public ScrolledBookDataDisplay(BookDataDisplay child)
    {
        super(child);
        scrView = new CWScrollPane(getProxy().getComponent());
        scrView.getViewport().setPreferredSize(new Dimension(500, 400));
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.display.BookDataDisplay#getComponent()
     */
    /* @Override */
    public Component getComponent()
    {
        return scrView;
    }

    /**
     * The scroller for the TextPaneBookDataDisplay component
     */
    private JScrollPane scrView;
}
