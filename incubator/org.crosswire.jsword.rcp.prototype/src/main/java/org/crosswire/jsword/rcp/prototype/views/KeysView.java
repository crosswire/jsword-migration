package org.crosswire.jsword.rcp.prototype.views;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.passage.PreferredKey;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class KeysView extends ViewPart
{
    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.keysview";

    private ListViewer viewer;

    private BookSelectionListener bookSelectionListener;

    public KeysView()
    {
        super();
    }

    public void createPartControl(Composite parent)
    {
        viewer = new ListViewer(parent);
        viewer.setContentProvider(new KeysContentProvider());
        viewer.setLabelProvider(new KeyLabelProvider());
        IWorkbenchPartSite site = getSite();
        site.setSelectionProvider(viewer);
        bookSelectionListener = new BookSelectionListener();
        site.getPage().addPostSelectionListener(BooksView.PART_ID, bookSelectionListener);

    }

    public void dispose()
    {
        getSite().getPage().removePostSelectionListener(BooksView.PART_ID, bookSelectionListener);
        super.dispose();
    }

    public void setFocus()
    {
        if (viewer != null && !viewer.getControl().isDisposed())
        {
            viewer.getControl().setFocus();
        }
    }

    /**
     * @param first
     */
    private void setBook(Book book)
    {
        viewer.setInput(book);
        if (book instanceof PreferredKey)
        {
            viewer.setSelection(new StructuredSelection(((PreferredKey) book).getPreferred()));
        }
    }

    public class BookSelectionListener implements ISelectionListener
    {
        public void selectionChanged(IWorkbenchPart part, ISelection selection)
        {
            if (selection.isEmpty())
            {
                viewer.setInput(null);
            }
            else
            {
                Object first = ((IStructuredSelection) selection).getFirstElement();
                if (!first.equals(viewer.getInput()))
                {
                    setBook((Book) first);
                }
            }
        }
    }

}
