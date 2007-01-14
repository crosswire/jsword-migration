/**
 * Distribution License:
 * JSword is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License, version 2.1 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/lgpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2006
 *
 */
package org.crosswire.jsword.rcp.prototype.views;

import java.util.List;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.KeyUtil;
import org.crosswire.jsword.passage.NoSuchKeyException;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.rcp.prototype.actions.ViewPassageAction;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class ClassicSearchView extends ViewPart
{

    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.classicsearchview";
    private Composite root;
    private Text passageText;
    private ComboViewer bibleViewer;

    public ClassicSearchView()
    {
        super();
    }

    public void createPartControl(Composite parent)
    {
        root = new Composite(parent, SWT.NONE);
        root.setLayout(new GridLayout(4, false));

        Label bibleLabel = new Label(root, SWT.NONE);
        bibleLabel.setText("Bible:");
        bibleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        bibleViewer = new ComboViewer(root);
        bibleViewer.setSorter(new ViewerSorter());
        bibleViewer.setLabelProvider(new BookLabelProvider());
        bibleViewer.setContentProvider(new ArrayContentProvider());
        List books = Books.installed().getBooks(BookFilters.getBibles());
        bibleViewer.setInput(books);
        if (!books.isEmpty())
        {
            bibleViewer.setSelection(new StructuredSelection(books.get(0)));
        }
        bibleViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

        Label showPassageLabel = new Label(root, SWT.NONE);
        showPassageLabel.setText("Sho&w Passage:");
        showPassageLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        passageText = new Text(root, SWT.SINGLE | SWT.BORDER);
        passageText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        passageText.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.character == '\r')
                {
                    showPassage();
                }
            }
        });

        Button selectButton = new Button(root, SWT.NONE);
        selectButton.setText("Select");
        selectButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        selectButton.setEnabled(false);

        Button goButton = new Button(root, SWT.NONE);
        goButton.setText("&Go");
        goButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        goButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                showPassage();
            }
        });

    }

    /**
     * 
     */
    protected void showPassage()
    {
        String text = passageText.getText();
        Book bible = (Book) ((IStructuredSelection)bibleViewer.getSelection()).getFirstElement();
        Key key;
        try
        {
            key = bible.getKey(text);
        }
        catch (NoSuchKeyException e)
        {
            MessageBox message = new MessageBox(getSite().getShell());
            message.setMessage(e.getDetailedMessage());
            message.setText("Error");
            message.open();
            return;
        }

        Passage passage = KeyUtil.getPassage(key);
        final String newText = passage.getName();
        Display.getDefault().asyncExec(new Runnable() {
            public void run()
            {
                passageText.setText(newText);
            }
            
        });
        
        new ViewPassageAction(getSite().getWorkbenchWindow(), bible.getInitials(), newText).run();
    }

    public void setFocus()
    {
        if (root != null && !root.isDisposed())
        {
            root.setFocus();
        }
    }

}
