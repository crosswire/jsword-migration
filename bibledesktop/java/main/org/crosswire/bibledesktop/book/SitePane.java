package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.MapTable;
import org.crosswire.common.swing.MapTableModel;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.BookList;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;

/**
 * A panel for use within a SitesPane to display one set of Books that are
 * installed or could be installed.
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
public class SitePane extends JPanel
{
    /**
     * For local installations
     */
    public SitePane()
    {
        this(null, INSTALLED_BOOKS_LABEL);
    }

    /**
     * For remote installations
     */
    public SitePane(Installer bookListInstaller)
    {
        this(bookListInstaller, AVAILABLE_BOOKS_LABEL);
    }

    private SitePane(Installer bookListInstaller, String labelAcronymn)
    {
        installer = bookListInstaller;

        actions = new ActionFactory(SitePane.class, this);

        BookList bl = installer;
        if (installer == null)
        {
            bl = Books.installed();
        }

        initialize(labelAcronymn, bl);        
    }

    /**
     * Build the GUI components
     */
    private void initialize(String labelAcronymn, BookList books)
    {
        Component left = createAvailablePanel(labelAcronymn, books);
        Component right = createSelectedPanel();
        this.setLayout(new BorderLayout());
        this.add(createSplitPane(left, right), BorderLayout.CENTER);
    }

    private Component createSplitPane(Component left, Component right)
    {
        JSplitPane split = new JSplitPane();
        split.setResizeWeight(0.5);
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        split.setDividerSize(10);
        split.setDividerLocation(200);
        split.add(left, JSplitPane.LEFT);
        split.add(right, JSplitPane.RIGHT);
        return split;
    }
    
    private Component createAvailablePanel(String labelAcronymn, BookList books)
    {
        JLabel lblAvailable = actions.createJLabel(labelAcronymn);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(lblAvailable, BorderLayout.PAGE_START);
        panel.add(createScrolledTree(books), BorderLayout.CENTER);
        panel.add(createPanelActions(), BorderLayout.PAGE_END);

        // Tie the label's mnemonic to the tree
        lblAvailable.setLabelFor(treAvailable);

        return panel;
    }

    private Component createSelectedPanel()
    {
        emptyTableModel = new MapTableModel(null);
        tblSelected = new MapTable(emptyTableModel);
        JLabel lblSelected = actions.createJLabel(SELECTED_BOOK_LABEL);
        lblSelected.setLabelFor(tblSelected);
    
        JScrollPane scrSelected = new JScrollPane();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(lblSelected, BorderLayout.PAGE_START);
        panel.add(scrSelected, BorderLayout.CENTER);
        scrSelected.getViewport().add(tblSelected);
        return panel;
    }

    private Component createScrolledTree(BookList books)
    {
        treAvailable = new JTree();
        treAvailable.setModel(new BooksTreeModel(books));
        treAvailable.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treAvailable.setCellEditor(null);
        treAvailable.setRootVisible(false);
        treAvailable.setShowsRootHandles(true);
        treAvailable.setCellRenderer(new CustomTreeCellRenderer());
        treAvailable.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                selected();
            }
        });
    
        JScrollPane scroller = new JScrollPane();
    
        scroller.getViewport().add(treAvailable);
        scroller.setPreferredSize(new Dimension(300, 400));
    
        return scroller;
    }
    
    private Component createPanelActions()
    {
        JPanel panel = new JPanel();
        if (installer != null)
        {
            panel.add(new JButton(actions.getAction(INSTALL)));
            panel.add(new JButton(actions.getAction(REFRESH)));
        }
//        else
//        {
//            pnlActions.add(new JButton(actions.getAction(DELETE)));
//        }
        return panel;
    }

    /**
     * Delete the current book
     */
    public void doDelete()
    {
    }

    /**
     * Reload and redisplay the list of books
     */
    public void doRefresh()
    {
        if (installer != null)
        {
            try
            {
                installer.reloadIndex();

                treAvailable.setModel(new BooksTreeModel(installer));
            }
            catch (InstallException ex)
            {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Kick off the installer
     */
    public void doInstall()
    {
        if (installer != null)
        {
            TreePath path = treAvailable.getSelectionPath();
            if (path != null)
            {
                Object last = path.getLastPathComponent();
                BookMetaData name = (BookMetaData) last;

                try
                {
                    installer.install(name);
                }
                catch (InstallException ex)
                {
                    Reporter.informUser(this, ex);
                }
            }
        }
    }

    /**
     * Something has been (un)selected in the tree
     */
    protected void selected()
    {
        TreePath path = treAvailable.getSelectionPath();

        boolean bookSelected = false;
        MapTableModel mtm = emptyTableModel;
        if (path != null)
        {
            Object last = path.getLastPathComponent();

            if (last instanceof BookMetaData)
            {
                mtm = new BookMetaDataTableModel((BookMetaData) last);
                bookSelected = true;
            }
        }
        tblSelected.setModel(mtm);
        
        //actions.getAction(DELETE).setEnabled(bookSelected);
        actions.getAction(INSTALL).setEnabled(bookSelected);
    }

    /**
     * Display the BookMetaData as something better than toString()
     */
    private static final class CustomTreeCellRenderer extends DefaultTreeCellRenderer
    {
        /* (non-Javadoc)
         * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row, boolean focus)
        {
            super.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, focus);

            if (value instanceof BookMetaData)
            {
                BookMetaData bmd = (BookMetaData) value;
                setText(bmd.getFullName());
            }

            return this;
        }
    }

    private static final String INSTALLED_BOOKS_LABEL = "InstalledBooksLabel"; //$NON-NLS-1$
    private static final String AVAILABLE_BOOKS_LABEL = "AvailableBooksLabel"; //$NON-NLS-1$
    private static final String SELECTED_BOOK_LABEL = "SelectedBookLabel"; //$NON-NLS-1$
    private static final String REFRESH = "Refresh"; //$NON-NLS-1$
    private static final String INSTALL = "Install"; //$NON-NLS-1$
    //private static final String DELETE = "Delete"; //$NON-NLS-1$

    /**
     * From which we get our list of installable modules
     */
    protected Installer installer;

    /**
     * actions are held by this ActionFactory
     */
    private ActionFactory actions;
    
    /*
     * GUI Components
     */
    private JTree treAvailable;
    private JTable tblSelected;
    private MapTableModel emptyTableModel;

    /**
     * SERIALUID(dms): A placeholder for the ultimate version id.
     */
    private static final long serialVersionUID = 1L;
}
