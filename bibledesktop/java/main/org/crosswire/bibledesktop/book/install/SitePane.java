package org.crosswire.bibledesktop.book.install;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookList;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookSet;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.book.search.IndexManagerFactory;

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

    /**
     * Internal ctor
     */
    private SitePane(Installer bookListInstaller, String labelAcronymn)
    {
        installer = bookListInstaller;

        actions = new ActionFactory(SitePane.class, this);

        BookList bl = installer;
        if (bl == null)
        {
            bl = Books.installed();
            bl.addBooksListener(new CustomBooksListener());
        }

        initialize(labelAcronymn, bl);
    }

    /**
     * Build the GUI components
     */
    private void initialize(String labelAcronymn, BookList books)
    {
        lblDesc = new JLabel();
        lblDesc.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));

        Component left = createAvailablePanel(labelAcronymn, books);
        Component right = createSelectedPanel();
        this.setLayout(new BorderLayout());
        this.add(lblDesc, BorderLayout.NORTH);
        this.add(createSplitPane(left, right), BorderLayout.CENTER);

        updateDescription();
    }

    /**
     *
     */
    private void updateDescription()
    {
        String desc = "#ERROR#"; //$NON-NLS-1$

        if (installer == null)
        {
            int bookCount = Books.installed().getBooks().size();
            desc = Msg.INSTALLED_DESC.toString(new Object[] { new Integer(bookCount) });
        }
        else
        {
            int bookCount = installer.getBooks().size();
            if (bookCount == 0)
            {
                desc = Msg.NONE_AVAILABLE_DESC.toString();
            }
            else
            {
                desc = Msg.AVAILABLE_DESC.toString(new Object[] { new Integer(bookCount) });
            }
        }

        lblDesc.setText(desc);
    }

    /**
     *
     */
    private Component createSplitPane(Component left, Component right)
    {
        JSplitPane split = new FixedSplitPane();
        split.setDividerLocation(0.3D);
        split.setResizeWeight(0.3D);
        split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        split.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        split.setDividerSize(10);
        split.setLeftComponent(left);
        split.setRightComponent(right);
        return split;
    }

    /**
     *
     */
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

    /**
     *
     */
    private Component createSelectedPanel()
    {

        JLabel lblSelected = actions.createJLabel(SELECTED_BOOK_LABEL);
        display = new TextPaneBookMetaDataDisplay();
        lblSelected.setLabelFor(display.getComponent());

        JScrollPane scrSelected = new JScrollPane();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(lblSelected, BorderLayout.PAGE_START);
        panel.add(scrSelected, BorderLayout.CENTER);
        scrSelected.getViewport().add(display.getComponent());
        return panel;
    }

    /**
     *
     */
    private Component createScrolledTree(BookList books)
    {
        treAvailable = new JTree();
        setTreeModel(books);
        // Add lines if viewed in Java Look & Feel
        treAvailable.putClientProperty("JTree.lineStyle", "Angled"); //$NON-NLS-1$ //$NON-NLS-2$
        treAvailable.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treAvailable.setCellEditor(null);
        treAvailable.setRootVisible(false);
        treAvailable.setShowsRootHandles(true);
        treAvailable.addTreeSelectionListener(new TreeSelectionListener()
        {
            public void valueChanged(TreeSelectionEvent ev)
            {
                selected();
            }
        });

        JScrollPane scroller = new JScrollPane();
        scroller.getViewport().add(treAvailable);

        return scroller;
    }

    private TreeModel createTreeModel(BookList books)
    {
        // return new BooksTreeModel(books);
        BookSet bmds = new BookSet(books.getBooks());
        TreeNode bookRoot = new BookNode("root", bmds, new Object[] { BookMetaData.KEY_TYPE, BookMetaData.KEY_LANGUAGE }, 0); //$NON-NLS-1$
        return new DefaultTreeModel(bookRoot);
    }

    // provide for backward compatibility
    private Book getBook(Object obj)
    {
        // new way
        if (obj instanceof DefaultMutableTreeNode)
        {
            obj = ((DefaultMutableTreeNode) obj).getUserObject();
        }
        // Old way
        if (obj instanceof Book)
        {
            return (Book) obj;
        }
        return null;
    }

    /**
     *
     */
    private Component createPanelActions()
    {
        JPanel panel = new JPanel();
        if (installer != null)
        {
            panel.add(new JButton(actions.getAction(INSTALL)));
            panel.add(new JButton(actions.getAction(INSTALL_SEARCH)));
            panel.add(new JButton(actions.getAction(REFRESH)));
        }
        else
        {
            panel.add(new JButton(actions.getAction(DELETE)));
        }
        return panel;
    }

    /**
     * Delete the current book
     */
    public void doDelete()
    {
        TreePath path = treAvailable.getSelectionPath();
        if (path == null)
        {
            return;
        }

        Object last = path.getLastPathComponent();
        Book book = getBook(last);

        try
        {
            book.getBookMetaData().getDriver().delete(book);

            IndexManager imanager = IndexManagerFactory.getIndexManager();
            if (imanager.isIndexed(book))
            {
                imanager.deleteIndex(book);
                
            }
//            // unselect it and then remove from list.
//            treAvailable.removeSelectionPath(path);
//            ((DefaultTreeModel)treAvailable.getModel()).removeNodeFromParent((MutableTreeNode) last);
        }
        catch (BookException e)
        {
            Reporter.informUser(this, e);
        }
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
                installer.reloadBookList();
                setTreeModel(installer);
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
        if (installer == null)
        {
            return;
        }

        TreePath path = treAvailable.getSelectionPath();
        if (path == null)
        {
            return;
        }

        Object last = path.getLastPathComponent();
        Book name = getBook(last);

        try
        {
            // Is the book already installed? Then nothing to do.
            Book book = Books.installed().getBook(name.getBookMetaData().getName());
            if (book != null && !installer.isNewer(name))
            {
                Reporter.informUser(this, Msg.INSTALLED, name.getBookMetaData().getName());
                return;
            }

            float size = NetUtil.getSize(installer.toRemoteURL(name)) / 1024.0F;
            if (JOptionPane.showConfirmDialog(this, Msg.SIZE.toString(new Object[] {name.getBookMetaData().getName(), new Float(size)}),
                            Msg.CONFIRMATION_TITLE.toString(),
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            {
                installer.install(name);
            }
        }
        catch (InstallException ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Kick off the installer
     */
    public void doInstallSearch()
    {
        doInstall();

        TreePath path = treAvailable.getSelectionPath();
        if (path != null)
        {
            try
            {
                Object last = path.getLastPathComponent();
                Book book = getBook(last);
                IndexResolver.scheduleIndex(book, this);
            }
            catch (Exception ex)
            {
                Reporter.informUser(this, ex);
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
        Book book = null;
        if (path != null)
        {
            Object last = path.getLastPathComponent();
            book = getBook(last);
            if (book != null)
            {
                bookSelected = true;
            }
        }
        display.setBook(book);

        actions.getAction(DELETE).setEnabled(bookSelected && book.getDriver().isDeletable(book));
        actions.getAction(INSTALL).setEnabled(bookSelected);
        actions.getAction(INSTALL_SEARCH).setEnabled(bookSelected && book.getType() == BookType.BIBLE);
    }

    public void setTreeModel(BookList books)
    {
        treAvailable.setModel(createTreeModel(books));
    }

    /**
     * When new books are added we need to relfect the change in this tree.
     */
    private final class CustomBooksListener implements BooksListener
    {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev)
        {
            setTreeModel((BookList) ev.getSource());
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev)
        {
            setTreeModel((BookList) ev.getSource());
        }
    }


    private static final String INSTALLED_BOOKS_LABEL = "InstalledBooksLabel"; //$NON-NLS-1$
    private static final String AVAILABLE_BOOKS_LABEL = "AvailableBooksLabel"; //$NON-NLS-1$
    private static final String SELECTED_BOOK_LABEL = "SelectedBookLabel"; //$NON-NLS-1$
    private static final String REFRESH = "Refresh"; //$NON-NLS-1$
    private static final String INSTALL = "Install"; //$NON-NLS-1$
    private static final String INSTALL_SEARCH = "InstallSearch"; //$NON-NLS-1$
    private static final String DELETE = "Delete"; //$NON-NLS-1$

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
    private TextPaneBookMetaDataDisplay display;
    private JLabel lblDesc;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616445692051075634L;
}
