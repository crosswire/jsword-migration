package org.crosswire.bibledesktop.desktop;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import org.crosswire.bibledesktop.book.BibleViewPane;

/**
 * TDI manager of how we layout views.
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
public class TDIViewLayout implements ViewLayout
{
    /**
     * ctor that connects to the Desktop that we manage
     */
    public TDIViewLayout()
    {
    }

    /**
     * If we do this setup in the ctor then the look and feel settings will be
     * missed, so we delay it to when this class is used.
     */
    private void delayedCreate()
    {
        if (tabMain == null)
        {
            tabMain = new JTabbedPane();
            tabMain.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
            // NOTE: when we tried dynamic laf update, tab_main needed special treatment
            //LookAndFeelUtil.addComponentToUpdate(tab_main);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.desktop.ViewLayout#getRootComponent()
     */
    public Component getRootComponent()
    {
        delayedCreate();

        if (views.size() == 1)
        {
            return (Component) views.get(0);
        }
        else
        {
            return tabMain;
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.desktop.ViewLayout#add(org.crosswire.bibledesktop.book.BibleViewPane)
     */
    public void add(BibleViewPane view)
    {
        delayedCreate();

        switch (views.size())
        {
        case 0:
            // Don't add the view to tab_main
            break;

        case 1:
            // We used to be in SDI mode, but we are about to go into TDI mode
            // (when getRootComponent() is called is a few secs. So we need
            // to construct tab_main properly
            BibleViewPane first = (BibleViewPane) views.get(0); 
            tabMain.add(first, first.getTitle());
            tabMain.add(view, view.getTitle());
            tabMain.setSelectedComponent(view);
            break;

        default:
            // So we are well into tabbed mode
            tabMain.add(view, view.getTitle());
            tabMain.setSelectedComponent(view);
            break;
        }

        views.add(view);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.desktop.ViewLayout#remove(org.crosswire.bibledesktop.book.BibleViewPane)
     */
    public void remove(BibleViewPane view)
    {
        delayedCreate();

        if (views.size() == 2)
        {
            // remove both tabs, because 0 will be reparented
            tabMain.removeTabAt(0);
        }

        tabMain.remove(view);

        views.remove(view);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.desktop.ViewLayout#update(org.crosswire.bibledesktop.book.BibleViewPane)
     */
    public void updateTitle(BibleViewPane view)
    {
        delayedCreate();

        if (views.size() > 1)
        {
            int index = tabMain.indexOfComponent(view);
            tabMain.setTitleAt(index, view.getTitle());
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.desktop.ViewLayout#getSelected()
     */
    public BibleViewPane getSelected()
    {
        delayedCreate();

        if (views.size() == 1)
        {
            return (BibleViewPane) views.get(0);
        }
        else
        {
            return (BibleViewPane) tabMain.getSelectedComponent();
        }
    }

    /**
     * The list of views. We maintain this separately so we don't have
     * a dependency on Desktop, which has caused loops before
     */
    private List views = new ArrayList();

    /**
     * The tabbed view pane
     */
    private JTabbedPane tabMain = null;
}
