package org.crosswire.jsword.rcp.prototype.workbench;

import org.crosswire.jsword.rcp.prototype.views.BooksView;
import org.crosswire.jsword.rcp.prototype.views.ClassicSearchView;
import org.crosswire.jsword.rcp.prototype.views.ContentView;
import org.crosswire.jsword.rcp.prototype.views.KeysView;
import org.crosswire.jsword.rcp.prototype.views.PassageNotesView;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class TestPerspective implements IPerspectiveFactory
{

    public void createInitialLayout(IPageLayout layout)
    {
        layout.setEditorAreaVisible(true);
        layout.addView(PassageNotesView.PART_ID, IPageLayout.LEFT, 0.30f, layout.getEditorArea());
        layout.addView(BooksView.PART_ID, IPageLayout.RIGHT, 0.70f, layout.getEditorArea());
        layout.addView(KeysView.PART_ID, IPageLayout.BOTTOM, 0.20f, BooksView.PART_ID);
        layout.addView(ContentView.PART_ID, IPageLayout.BOTTOM, 0.40f, KeysView.PART_ID);
        layout.addView(ClassicSearchView.PART_ID, IPageLayout.TOP, 0.20f, layout.getEditorArea());

    }

}
