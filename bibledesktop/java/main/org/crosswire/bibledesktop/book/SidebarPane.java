package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.crosswire.bibledesktop.display.URLEventListener;
import org.crosswire.common.swing.FixedSplitPane;

/**
 * SidebarPane builds a panel containing a set of books in tabbed dialogs.
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
public class SidebarPane extends JPanel
{
    /**
     * Simple ctor
     */
    public SidebarPane()
    {
        init();
    }

    /**
     * GUI initializer.
     */
    private void init()
    {
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        split.setDividerLocation(0.3D);
        split.setDividerSize(7);
        split.setBorder(null);
        // Make resizing affect the right only
        split.setResizeWeight(0.0);
        split.setTopComponent(comments);
        split.setBottomComponent(dicts);

        this.setLayout(new BorderLayout());
        this.add(split, BorderLayout.CENTER);
    }

    /**
     * Add a listener when someone clicks on a browser 'link'
     */
    public void addURLEventListener(URLEventListener li)
    {
        dicts.addURLEventListener(li);
        comments.addURLEventListener(li);
    }

    /**
     * Remove a listener when someone clicks on a browser 'link'
     */
    public void removeURLEventListener(URLEventListener li)
    {
        dicts.removeURLEventListener(li);
        comments.removeURLEventListener(li);
    }

    /**
     * Accessor for the CommentaryPane
     */
    public CommentaryPane getCommentaryPane()
    {
        return comments;
    }

    /**
     * Accessor for the DictionaryPane
     */
    public DictionaryPane getDictionaryPane()
    {
        return dicts;
    }

    private CommentaryPane comments = new CommentaryPane();
    private JSplitPane split = new FixedSplitPane();
    private DictionaryPane dicts = new DictionaryPane();

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256727294637521206L;
}
