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
package org.crosswire.bibledesktop.book.install;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.crosswire.bibledesktop.BDMsg;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.CWAction;
import org.crosswire.common.swing.CWLabel;
import org.crosswire.common.swing.CWOptionPane;
import org.crosswire.common.swing.CWScrollPane;
import org.crosswire.common.swing.FixedSplitPane;
import org.crosswire.common.swing.FontChooser;
import org.crosswire.common.util.Language;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookCategory;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookList;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookSet;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.BooksEvent;
import org.crosswire.jsword.book.BooksListener;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.index.IndexManager;
import org.crosswire.jsword.index.IndexManagerFactory;
import org.crosswire.jsword.util.WebWarning;

/**
 * A panel for use within a SitesPane to display one set of Books that are
 * installed or could be installed.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SitePane extends JPanel {
    /**
     * For local installations
     */
    public SitePane() {
        // TRANSLATOR: This is the label for a list of installed books
        this(null, BDMsg.gettext("Installed Books:"));
    }

    /**
     * For remote installations
     */
    public SitePane(Installer bookListInstaller) {
        // TRANSLATOR: This is the label for a list of available books
        this(bookListInstaller, BDMsg.gettext("Available Books:"));
    }

    /**
     * Internal ctor
     */
    private SitePane(Installer bookListInstaller, String labelAcronymn) {
        installer = bookListInstaller;

        actions = new ActionFactory(this);

        BookList bl = installer;
        if (bl == null) {
            bl = Books.installed();
            bl.addBooksListener(new CustomBooksListener());
        }

        initialize(labelAcronymn, bl);
    }

    /**
     * Build the GUI components
     */
    private void initialize(String labelAcronymn, BookList books) {
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
    private void updateDescription() {
        String desc = "#ERROR#";

        if (installer == null) {
            int bookCount = Books.installed().getBooks().size();
            // TRANSLATOR: This label give the number of books that are installed. {0} is a placeholder for the number.
            desc = BDMsg.gettext("{0} books installed.", Integer.valueOf(bookCount));
        } else {
            int bookCount = installer.getBooks().size();
            if (bookCount == 0) {
                // TRANSLATOR: This label shows up when the list of available books for a download site is missing.
                // Change the text between <html><b> and </b>.
                desc = BDMsg.gettext("<html><b>Click 'Update Available Books' to download an up to date book list.</b>");
            } else {
                // TRANSLATOR: This label gives the number of books available at a download site. {0} is a placeholder for the number.
                desc = BDMsg.gettext("{0} books available for download.", Integer.valueOf(bookCount));
            }
        }

        lblDesc.setText(desc);
    }

    /**
     *
     */
    private Component createSplitPane(Component left, Component right) {
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
    private Component createAvailablePanel(String labelAcronymn, BookList books) {
        JLabel lblAvailable = CWLabel.createJLabel(labelAcronymn);

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
    private Component createSelectedPanel() {

        // TRANSLATOR: This is the label for the display of information about the selected book
        JLabel lblSelected = CWLabel.createJLabel(BDMsg.gettext("Selected Book:"));
        display = new TextPaneBookMetaDataDisplay();
        lblSelected.setLabelFor(display.getComponent());

        JScrollPane scrSelected = new CWScrollPane();
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
    private Component createScrolledTree(BookList books) {
        treAvailable = new JTree();
        // Turn on tooltips so that they will show
        ToolTipManager.sharedInstance().registerComponent(treAvailable);
        treAvailable.setCellRenderer(new BookTreeCellRenderer());

        setTreeModel(books);
        // Add lines if viewed in Java Look & Feel
        treAvailable.putClientProperty("JTree.lineStyle", "Angled");
        treAvailable.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treAvailable.setCellEditor(null);
        treAvailable.setRootVisible(false);
        treAvailable.setShowsRootHandles(true);
        treAvailable.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent ev) {
                selected();
            }
        });

        return new CWScrollPane(treAvailable);
    }

    private TreeModel createTreeModel(BookList books) {
        // return new BooksTreeModel(books);
        BookSet bmds = new BookSet(books.getBooks());
        TreeNode bookRoot = new BookNode("root", bmds, 0, BookMetaData.KEY_CATEGORY, BookMetaData.KEY_XML_LANG);
        return new DefaultTreeModel(bookRoot);
    }

    private Book getBook(Object anObj) {
        Object obj = anObj;
        if (obj instanceof DefaultMutableTreeNode) {
            obj = ((DefaultMutableTreeNode) obj).getUserObject();
        }
        if (obj instanceof Book) {
            return (Book) obj;
        }
        return null;
    }

    private Language getLanguage(Object anObj) {
        Object obj = anObj;
        if (obj instanceof DefaultMutableTreeNode) {
            obj = ((DefaultMutableTreeNode) obj).getUserObject();
        }
        if (obj instanceof Language) {
            return (Language) obj;
        }
        return null;
    }

    /**
     *
     */
    private Component createPanelActions() {
        JPanel panel = new JPanel();
        CWAction action;
        if (installer != null) {
            panel.setLayout(new GridLayout(1, 2, 3, 3));

            // TRANSLATOR: This is the text on an "Install" button.
            action = actions.addAction("Install", BDMsg.gettext("Install"));
            // TRANSLATOR: This is the tooltip for an "Install" button.
            action.setTooltip(BDMsg.gettext("Install the selected book"));
            action.enable(false);
            panel.add(new JButton(action));

            // LATER(DMS): Put back when this works
            // action = actions.addAction("InstallSearch", UserMsg.gettext("Install with Search"));
            // action.setTooltip(UserMsg.gettext("Install the selected book along with a search index."));
            // action.enable(false);
            // panel.add(new JButton(action));

            // TRANSLATOR: This is the text on a button that will refresh the list of available books
            // from a download site
            action = actions.addAction("Refresh", BDMsg.gettext("Update Available Books"));
            action.setTooltip(BDMsg.gettext("Download a current listing of books."));
            panel.add(new JButton(action));
        } else {
            panel.setLayout(new GridLayout(3, 2, 3, 3));

            // TRANSLATOR: This is the text on a "Delete Book" button.
            action = actions.addAction("Delete", BDMsg.gettext("Delete Book"));
            // TRANSLATOR: This is the tooltip for a "Delete Book" button.
            action.setTooltip(BDMsg.gettext("Delete the selected book"));
            action.enable(false);
            panel.add(new JButton(action));

            // TRANSLATOR: This is the text on a "Remove Search Index" button.
            action = actions.addAction("Unindex", BDMsg.gettext("Remove Search Index"));
            // TRANSLATOR: This is the tooltip for a "Remove Search Index" button.
            action.setTooltip(BDMsg.gettext("Remove the search index of the selected book"));
            action.enable(false);
            panel.add(new JButton(action));

            // TRANSLATOR: This is the text on a "Font..." button that brings up a font selection dialog.
            action = actions.addAction("ChooseFont", BDMsg.gettext("Font..."));
            // TRANSLATOR: This is the tooltip for a "Font..." button that brings up a font selection dialog.
            action.setTooltip(BDMsg.gettext("Choose a font for the language or book"));
            action.enable(false);
            panel.add(new JButton(action));

            // TRANSLATOR: This is the text on an "Unlock" button that brings up a dialog box to enter an unlock key.
            action = actions.addAction("Unlock", BDMsg.gettext("Unlock"));
            // TRANSLATOR: This is the tooltip for an "Unlock" button that brings up a dialog box to enter an unlock key.
            action.setTooltip(BDMsg.gettext("Unlock the selected book"));
            action.enable(false);
            panel.add(new JButton(action));

            // TRANSLATOR: This is the text on a "Reset Font" button.
            // Clicking on this button will restore the original font for the language or book
            action = actions.addAction("ResetFont", BDMsg.gettext("Reset Font"));
            // TRANSLATOR: This is the tooltip for a "Reset Font" button.
            action.setTooltip(BDMsg.gettext("Reset the custom font set for this language or book"));
            action.enable(false);
            panel.add(new JButton(action));

        }
        return panel;
    }

    /**
     * Delete the current book
     */
    public void doDelete() {
        TreePath path = treAvailable.getSelectionPath();
        if (path == null) {
            return;
        }

        Object last = path.getLastPathComponent();
        Book book = getBook(last);

        try {
            // TRANSLATOR: Message asking for confirmation of a delete of a book.
            String msg = BDMsg.gettext("Are you sure you want to delete {0}?", book.getName());
            // TRANSLATOR: Title of a dialog that asks whether the book should be deleted.
            if (CWOptionPane.showConfirmDialog(this, msg, BDMsg.gettext("Delete Book"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                book.getDriver().delete(book);

                IndexManager imanager = IndexManagerFactory.getIndexManager();
                if (imanager.isIndexed(book)) {
                    imanager.deleteIndex(book);
                }
            }
        } catch (BookException e) {
            Reporter.informUser(this, e);
        }
    }

    /**
     * Unlock the current book
     */
    public void doUnlock() {
        TreePath path = treAvailable.getSelectionPath();
        if (path == null) {
            return;
        }

        Object last = path.getLastPathComponent();
        Book book = getBook(last);

        // TRANSLATOR: Title to a dialog asking the user to provide an unlock key.
        String title = BDMsg.gettext("Unlock Book");
        // TRANSLATOR: Message asking the user to provide an unlock key.
        // The unlock key is typically a string like AbCd8364efGH8472.
        // {0} is a placeholder for the books name.
        // In order to have long titles on the next line we use <html> and <br> to provide this.
        String msg = BDMsg.gettext("<html>Please enter the unlock key for:<br> {0}?", book.getName());
        String unlockKey = (String) CWOptionPane.showInputDialog(this, msg, title, JOptionPane.QUESTION_MESSAGE, null, null, book.getUnlockKey());

        if (unlockKey != null && unlockKey.length() > 0) {
            book.unlock(unlockKey);
            Books.installed().addBook(book);
        }
    }

    /**
     * Delete the current book
     */
    public void doUnindex() {
        TreePath path = treAvailable.getSelectionPath();
        if (path == null) {
            return;
        }

        Object last = path.getLastPathComponent();
        Book book = getBook(last);

        try {
            IndexManager imanager = IndexManagerFactory.getIndexManager();
            if (imanager.isIndexed(book)) {
                // TRANSLATOR: Message asking the user to confirm the delete of a search index for a book.
                // {0} is a placeholder for the name of the book.
                String formattedMsg = BDMsg.gettext("Are you sure you want to remove the index for {0}?", book.getName());
                // TRANSLATOR: Title to the dialog that asks for confirmation of the deletion 
                // of a book's search index.
                if (CWOptionPane.showConfirmDialog(this, formattedMsg, BDMsg.gettext("Remove Index for Book"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    imanager.deleteIndex(book);
                }
            }
            actions.findAction("Unindex").setEnabled(imanager.isIndexed(book));
        } catch (BookException e) {
            Reporter.informUser(this, e);
        }
    }

    /**
     * Reload and redisplay the list of books
     */
    public void doRefresh() {
        if (installer != null) {
            try {
                int webAccess = InternetWarning.GRANTED;
                if (WebWarning.instance().isShown()) {
                    webAccess = InternetWarning.showDialog(this, "?");
                }

                if (webAccess == InternetWarning.GRANTED) {
                    installer.reloadBookList();
                    setTreeModel(installer);
                }
            } catch (InstallException ex) {
                Reporter.informUser(this, ex);
            }
        }
    }

    /**
     * Kick off the installer
     */
    public void doInstall() {
        if (installer == null) {
            return;
        }

        TreePath path = treAvailable.getSelectionPath();
        if (path == null) {
            return;
        }

        int webAccess = InternetWarning.GRANTED;
        if (WebWarning.instance().isShown()) {
            webAccess = InternetWarning.showDialog(this, "?");
        }

        if (webAccess != InternetWarning.GRANTED) {
            return;
        }

        Object last = path.getLastPathComponent();
        Book name = getBook(last);

        try {
            // Is the book already installed? Then nothing to do.
            Book book = Books.installed().getBook(name.getName());
            if (book != null && !installer.isNewer(name)) {
                // TRANSLATOR: Popup message indicating that the book is already installed.
                // {0} is a placeholder for the name of the book.
                Reporter.informUser(this, BDMsg.gettext("Book already installed: {0}", name.getName()));
                return;
            }

            float size = installer.getSize(name) / 1024.0F;

            String formattedMsg = "";
            if (size > 1024.0F) {
                size /= 1024.0F;
                // TRANSLATOR: The size of the book is provided so that the user can decide whether to continue a download.
                // {0} is a placeholder for the name of the book.
                // {1,number,###,###,###.#} is a placeholder for the size of the download in megabytes.
                // The pattern ###,###,###.# says to separate the number at every third digit and
                //    to show one digit of fractional part.
                // The , and . will automatically be converted into the user's proper separators.
                formattedMsg = BDMsg.gettext("{0} is {1,number,###,###,###.#}MB. Continue?", name.getName(), Float.valueOf(size));
            } else {
                // TRANSLATOR: The size of the book is provided so that the user can decide whether to continue a download.
                // {0} is a placeholder for the name of the book.
                // {1,number,###,###,###.#} is a placeholder for the size of the download in kilobytes.
                // The pattern ###,###,###.# says to separate the number at every third digit and
                //    to show one digit of fractional part.
                // The , and . will automatically be converted into the user's proper separators.
                formattedMsg = BDMsg.gettext("{0} is {1,number,###,###,###.#}KB. Continue?", name.getName(), Float.valueOf(size));
            }

            // TRANSLATOR: Title to a dialog asking whether the user should download the book based on it's size.
            if (CWOptionPane.showConfirmDialog(this, formattedMsg, BDMsg.gettext("Download Book"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                installer.install(name);
            }
        } catch (InstallException ex) {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Kick off the installer
     */
    public void doInstallSearch() {
        doInstall();

        TreePath path = treAvailable.getSelectionPath();
        if (path != null) {
            Object last = path.getLastPathComponent();
            Book book = getBook(last);
            IndexResolver.scheduleIndex(book, this);
        }
    }

    /**
     * Get a font for the current selection
     */
    public void doChooseFont() {
        TreePath path = treAvailable.getSelectionPath();
        if (path == null) {
            return;
        }

        Object last = path.getLastPathComponent();
        Book book = getBook(last);
        if (book != null) {
            // TRANSLATOR: Title to a dialog allowing the user to choose a font face, size and style.
            Font picked = FontChooser.showDialog(this, BDMsg.gettext("Choose Font"), BookFont.instance().getFont(book));
            BookFont.instance().setFont(book, picked);
        }

        Language language = getLanguage(last);
        if (language != null) {
            // TRANSLATOR: Title to a dialog allowing the user to choose a font face, size and style.
            Font picked = FontChooser.showDialog(this, BDMsg.gettext("Choose Font"), BookFont.instance().getFont(language));
            BookFont.instance().setFont(language, picked);
        }
        actions.findAction("ResetFont").setEnabled(BookFont.instance().isSet(book, language));
    }

    /**
     * Resets any font specifically set for this Book / Language
     */
    public void doResetFont() {
        TreePath path = treAvailable.getSelectionPath();
        if (path == null) {
            return;
        }

        Object last = path.getLastPathComponent();
        Book book = getBook(last);
        Language language = getLanguage(last);
        BookFont.instance().resetFont(book, language);
        actions.findAction("ResetFont").setEnabled(false);
    }

    /**
     * Something has been (un)selected in the tree
     */
    protected void selected() {
        TreePath path = treAvailable.getSelectionPath();

        Book book = null;
        Language lang = null;
        if (path != null) {
            Object last = path.getLastPathComponent();
            book = getBook(last);
            lang = getLanguage(last);
        }

        display.setBook(book);

        actions.findAction("Delete").setEnabled(book != null && book.getDriver().isDeletable(book));
        actions.findAction("Unlock").setEnabled(book != null && book.isEnciphered());
        actions.findAction("Unindex").setEnabled(book != null && IndexManagerFactory.getIndexManager().isIndexed(book));
        actions.findAction("Install").setEnabled(book != null && book.isSupported());
        actions.findAction("InstallSearch").setEnabled(book != null && book.isSupported() && book.getBookCategory() == BookCategory.BIBLE);
        actions.findAction("ChooseFont").setEnabled(book != null || lang != null);
        actions.findAction("ResetFont").setEnabled(BookFont.instance().isSet(book, lang));
    }

    public void setTreeModel(BookList books) {
        treAvailable.setModel(createTreeModel(books));
    }

    /**
     * When new books are added we need to reflect the change in this tree.
     */
    final class CustomBooksListener implements BooksListener {
        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookAdded(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookAdded(BooksEvent ev) {
            setTreeModel((BookList) ev.getSource());
        }

        /* (non-Javadoc)
         * @see org.crosswire.jsword.book.BooksListener#bookRemoved(org.crosswire.jsword.book.BooksEvent)
         */
        public void bookRemoved(BooksEvent ev) {
            setTreeModel((BookList) ev.getSource());
        }
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        // Broken but we don't serialize views
        installer = null;
        display = null;
        actions = new ActionFactory(this);
        is.defaultReadObject();
    }


    /**
     * From which we get our list of installable books
     */
    protected transient Installer installer;

    /**
     * actions are held by this ActionFactory
     */
    private transient ActionFactory actions;

    /*
     * GUI Components
     */
    private JTree treAvailable;
    private transient TextPaneBookMetaDataDisplay display;
    private JLabel lblDesc;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3616445692051075634L;
}
