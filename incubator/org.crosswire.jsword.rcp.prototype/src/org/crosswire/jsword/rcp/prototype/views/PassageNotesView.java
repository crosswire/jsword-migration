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

import java.util.Iterator;
import java.util.List;

import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.Verse;
import org.crosswire.jsword.passage.VerseFactory;
import org.crosswire.jsword.rcp.prototype.editors.IOsisElementSelectionListener;
import org.crosswire.jsword.rcp.prototype.editors.IOsisElementSelectionProvider;
import org.crosswire.jsword.rcp.prototype.editors.OsisElementSelectionEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Phillip [phillip at paristano dot org]
 *
 */
public class PassageNotesView extends ViewPart
{
    public static final String PART_ID = "org.crosswire.jsword.rcp.prototype.passagenotesview";

    private Composite root;
    private ScrolledForm form;
    private FormToolkit toolkit;
    private PartAdapter partListener;
    private IEditorInput currentInput;
    private IOsisElementSelectionListener selectionListener;

    private Job readNotesJob;

    public PassageNotesView()
    {
        super();
    }

    public void createPartControl(Composite parent)
    {
        this.toolkit = new FormToolkit(parent.getDisplay());
        root = toolkit.createComposite(parent);
        root.setLayout(new FillLayout());
        createForm("No notes available");

        //This listener waits for a note to be activated. When that 
        //happens, it moves the corresponding note section to the 
        //top of the view.
        this.selectionListener = new IOsisElementSelectionListener()
        {
            public void elementSelected(OsisElementSelectionEvent event)
            {
                if ("note".equals(event.getElementType()) && form != null)
                {
                    String osisId = event.getElementValue();
                    Control[] children = form.getBody().getChildren();
                    for (int i = 0; i < children.length; i++)
                    {
                        Control child = children[i];
                        if (osisId.equals(child.getData("osisID")))
                        {
                            //scroll the section to the top of the notes.
                            Rectangle bounds = child.getBounds();
                            form.setOrigin(bounds.x, bounds.y);
                            break;
                        }
                    }
                }
            }
        };

        //This listener listens for a change in editor. When the editor changes,
        //it looks for the new editor's notes.
        this.partListener = new PartAdapter()
        {
            public void partActivated(IWorkbenchPart part)
            {
                if (part instanceof IEditorPart)
                {
                    IEditorPart editor = (IEditorPart) part;
                    IEditorInput input = editor.getEditorInput();
                    
                    IOsisElementSelectionProvider provider = (IOsisElementSelectionProvider) editor.getAdapter(IOsisElementSelectionProvider.class);
                    if (provider != null)
                    {
                        provider.addSelectionListener(selectionListener);
                    }
                    
                    if (!input.equals(currentInput))
                    {
                        currentInput = input;
                        load(input);
                    }
                }
            }

            public void partDeactivated(IWorkbenchPart part)
            {
                if (part instanceof IEditorPart)
                {
                    IEditorPart editor = (IEditorPart) part;
                    IOsisElementSelectionProvider provider = (IOsisElementSelectionProvider) editor.getAdapter(IOsisElementSelectionProvider.class);
                    if (provider != null)
                    {
                        provider.removeSelectionListener(selectionListener);
                    }
                }
            }
        };

        IEditorPart editor = getSite().getPage().getActiveEditor();
        if (editor != null)
        {
            //When the application opens, we aren't told about the first active editor.
            //Handle this manually.
            partListener.partActivated(editor);
        }
        getSite().getWorkbenchWindow().getPartService().addPartListener(partListener);
    }

    /**
     * @param text TODO
     * 
     */
    private void createForm(String text)
    {
        if (form != null && !form.isDisposed())
        {
            form.dispose();
        }

        form = toolkit.createScrolledForm(root);
        form.setText(text);
        Composite body = form.getBody();
        body.setLayout(new TableWrapLayout());
    }

    public void dispose()
    {
        currentInput = null;
        getSite().getWorkbenchWindow().getPartService().removePartListener(partListener);
    }

    /**
     * @param data
     * @throws Exception 
     */
    protected void load(final IEditorInput input)
    {
        if (readNotesJob != null)
        {
            //We may have an old job running that's trying to
            //fill the view up. Cancelling alone isn't enough since
            //the old job may write its last bit to our new view, so
            //join its thread with ours. 
            readNotesJob.cancel();
            try
            {
                readNotesJob.join();
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        createForm("Loading...");
        form.reflow(true);
        root.layout(true);

        //This job reads the notes from the data. After it reads one,
        //we write it to the view.
        this.readNotesJob = new ReadNotesJob("Load Bible Notes")
        {
            protected BookData getBookData()
            {
                return (BookData) input.getAdapter(BookData.class);
            }
            
            protected void addNotes(String key, List notes)
            {
                PassageNotesView.this.addNote(key, notes);
            }

            protected void endNotes()
            {
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        form.setText(null);
                    }
                });
            }

            protected void handleNoNotes()
            {
                Display.getDefault().syncExec(new Runnable()
                {
                    public void run()
                    {
                        form.setText("No notes available");
                        form.reflow(true);
                        root.layout(true);
                    }
                });
            }
        };

        readNotesJob.setSystem(true);
        readNotesJob.setPriority(Job.LONG);
        readNotesJob.schedule(250L);
    }

    /**
     * @param key
     * @param notes
     */
    protected void addNote(final String osisId, List notes)
    {
        final String sectionTitle;
        Verse verse = null;
        try
        {
            verse = VerseFactory.fromString(osisId);
        }
        catch (NoSuchVerseException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (verse != null)
        {
            sectionTitle = verse.getName();
        }
        else
        {
            sectionTitle = osisId;
        }

        final StringBuffer buffer = new StringBuffer("<form>");
        char letter = 'a';
        for (Iterator noteIt = notes.iterator(); noteIt.hasNext();)
        {
            String note = (String) noteIt.next();
            buffer.append("<p><b>").append(letter++).append("</b>: ").append(note).append("</p>");
        }

        buffer.append("</form>");

        Display.getDefault().syncExec(new Runnable()
        {
            public void run()
            {
                Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);// | Section.EXPANDED | Section.TWISTIE);
                section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
                section.setLayout(new FillLayout());

                section.setText(sectionTitle);
                section.setData("osisID", osisId);
                FormText text = toolkit.createFormText(section, false);
                section.setClient(text);

                text.setText(buffer.toString(), true, false);
                form.reflow(true);
                root.layout(true);
            }
        });
    }

    public void setFocus()
    {
        if (root != null && !root.isDisposed())
        {
            root.setFocus();
        }
    }

    //    private final class NoteContentHandler extends DefaultHandler
    //    {
    //        private boolean inVerse;
    //        private boolean inNote;
    //        private StringBuffer buffer;
    //        private String currentOsisId;
    //        private int noteIndex;
    //
    //        public void startDocument() throws SAXException
    //        {
    //        }
    //        
    //        public void endDocument() throws SAXException
    //        {
    //            form.reflow(true);
    //            form.getParent().layout();
    //        }
    //        
    //        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    //        {
    //            if ("verse".equals(localName))
    //            {
    //                currentOsisId = attributes.getValue("osisID");
    //                if (currentOsisId != null)
    //                {
    //                    //don't bother looking for notes without an osis id.
    //                    inVerse = true;
    //                    noteIndex = 0;
    //                    buffer = new StringBuffer("<form>");
    //                }
    //            }
    //            else if (inVerse && "note".equals(localName))
    //            {
    //                buffer.append("<p><b>").append((char)('a' + (noteIndex++))).append("</b>: ");
    //                inNote = true;
    //            }
    //        }
    //
    //        public void endElement(String uri, String localName, String qName) throws SAXException
    //        {
    //            if (inVerse && "verse".equals(localName))
    //            {
    //                inVerse = false;
    //                if (noteIndex > 0) {
    //                    buffer.append("</form>");
    //                    Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);// | Section.EXPANDED | Section.TWISTIE);
    //                    section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB));
    //                    section.setLayout(new FillLayout());
    //                    section.setText(currentOsisId);
    //                    FormText text = toolkit.createFormText(section, false);
    //                    section.setClient(text);
    //                    text.setText(buffer.toString(), true, false);
    //                    osisIdToSection.put(currentOsisId, section);
    //                }
    //            }
    //            else if (inNote && "note".equals(localName))
    //            {
    //                buffer.append("</p>");
    //                inNote = false;
    //            }
    //        }
    //
    //        /* (non-Javadoc)
    //         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
    //         */
    //        public void characters(char[] ch, int start, int length) throws SAXException
    //        {
    //            if (inNote)
    //            {
    //                buffer.append(ch, start, length);
    //            }
    //        }
    //    }

}
