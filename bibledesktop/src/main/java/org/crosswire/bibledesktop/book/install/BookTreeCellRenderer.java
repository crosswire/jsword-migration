/**
 * Distribution Licence:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * The License is available on the internet at:
 *      http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 * 
 * The copyright to this program is held by it's authors.
 */

package org.crosswire.bibledesktop.book.install;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.crosswire.jsword.book.Book;

/**
 * Provides appropriate icons for books.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class BookTreeCellRenderer extends DefaultTreeCellRenderer {

    /* (non-Javadoc)
     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean focus) {
        String tooltip = null;
        if (leaf && value instanceof BookNode) {
            Object obj = ((BookNode) value).getUserObject();
            if (obj instanceof Book) {
                Book book = (Book) obj;
                setLeafIcon(BookIcon.getIcon(book));

                if (book.isQuestionable()) {
                    // TRANSLATOR: The book is categorized as either the work of a cult, or it is unorthodox or it is otherwise questionable.
                    tooltip = Msg.gettext("Cult / Unorthodox / Questionable");
                }

                if (!book.isSupported()) {
                    // TRANSLATOR: The book is not supported by JSword
                    tooltip = Msg.gettext("Unsupported");
                } else if (book.isLocked()) {
                    // TRANSLATOR: The book is enciphered and needs to be unlocked
                    tooltip = Msg.gettext("Locked");
                }
            }
        }

        setToolTipText(tooltip);
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = -942626483282049048L;

}
