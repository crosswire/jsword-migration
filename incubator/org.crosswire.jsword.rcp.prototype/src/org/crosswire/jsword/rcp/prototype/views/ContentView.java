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

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.passage.Key;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class ContentView extends ViewPart
{

    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.contentview";
    private FormText text;
    private ScrolledForm form;
    private Key currentKey;
    private KeySelectionListener keySelectionListener;

    public ContentView()
    {
        super();
    }

    public void createPartControl(Composite parent)
    {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        this.form = toolkit.createScrolledForm(parent);
        form.setText("(no selection)");
        Composite body = form.getBody();
        body.setLayout(new TableWrapLayout());
        text = toolkit.createFormText(body, false);
        text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
        text.setText("", false, false);
        IWorkbenchPartSite site = getSite();
        keySelectionListener = new KeySelectionListener();
        site.getPage().addPostSelectionListener(KeysView.PART_ID, keySelectionListener);
    }

    public void dispose()
    {
        IWorkbenchPartSite site = getSite();
        site.getPage().removePostSelectionListener(KeysView.PART_ID, keySelectionListener);
        super.dispose();
    }
    /**
     * 
     */
    private void clearText()
    {
        form.setText("(no selection)");
        text.setText("", false, false);
        form.reflow(true);
    }

    protected void setKey(Key key)
    {
        this.currentKey = key;
        ISelection selection = getSite().getWorkbenchWindow().getSelectionService().getSelection(BooksView.PART_ID);
        if (selection == null || selection.isEmpty() || !(selection instanceof IStructuredSelection)) {
            return;
        }
        
        Book currentBook = (Book) ((IStructuredSelection)selection).getFirstElement();
        
        if (currentBook == null || currentKey == null)
        {
            this.form.setText(key.toString());
            clearText();
            return;
        }

        BookData content;
        try
        {
            content = currentBook.getData(key);
            FormTextContentHandler handler = new FormTextContentHandler();
            content.getSAXEventProvider().provideSAXEvents(handler);
            String title = handler.getTitle();
            if (title != null) {
                form.setText(title);
            } else {
                form.setText(key.toString());
            }
            text.setText(handler.getFormText(), false, false);
            form.reflow(true);
        }
        catch (BookException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setFocus()
    {
        if (text != null && !text.isDisposed())
        {
            text.setFocus();
        }
    }

    private final class FormTextContentHandler extends DefaultHandler
    {
        private StringBuffer buffer = new StringBuffer();
        private boolean inDiv;
        private String title;
        private boolean inTitle;
        

        private FormTextContentHandler()
        {
            super();
        }

        /**
         * @return
         */
        public String getFormText()
        {
            return buffer.toString();
        }
        
        public String getTitle() {
            return title;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
        {
            if (localName.equals("div")) {
                this.inDiv = true;
            } else if (localName.equals("title")) {
                this.inTitle = true;
            }
            
        }

        public void endElement(String uri, String localName, String qName) throws SAXException
        {
            if (localName.equals("div")) {
                this.inDiv = false;
            } else if (localName.equals("title")) {
                this.inTitle = false;
            }
        }

        public void characters(char[] ch, int start, int length) throws SAXException
        {
            if (inDiv) {
                if (inTitle) {
                    title = new String(ch, start, length);
                } else {
                    buffer.append(ch, start, length);
                }
            }
        }
    }

    public class KeySelectionListener implements ISelectionListener
    {

        /* (non-Javadoc)
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
         */
        public void selectionChanged(IWorkbenchPart part, ISelection selection)
        {
            if (selection.isEmpty())
            {
                clearText();
            }
            else
            {
                setKey((Key) ((IStructuredSelection) selection).getFirstElement());
            }
        }
    }
}
