/*
 * Copyright 2005, Dave Johnson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.crosswire.bibledesktop.journal;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.crosswire.bibledesktop.signal.LoadEntrySignal;
import org.crosswire.common.swing.GriddedPanel;
import org.werx.framework.bus.ReflectionBus;

import com.manning.blogapps.chapter08.blogclient.Blog;
import com.manning.blogapps.chapter08.blogclient.BlogEntry;
import com.manning.blogapps.chapter08.blogclient.BlogResource;

import de.xeinfach.kafenio.KafenioPanel;
import de.xeinfach.kafenio.KafenioPanelConfiguration;
import de.xeinfach.kafenio.KafenioToolBar;

/**
 * Form for editing a single blog entry.
 * @author Dave Johnson
 */
public class BlogClientPanel extends JPanel implements BlogClientTab
{

    /**
     * Creates new form BlogClientPanel
     */
    public BlogClientPanel()
    {
        initComponents();

        window = SwingUtilities.getWindowAncestor(this);
        ReflectionBus.plug(this);
    }

    /**
     * Inject BlogSite dependency.
     */
    public void setBlog(Blog blogSite)
    {
        this.blogSite = blogSite;
        try
        {
            List categories = blogSite.getCategories();
            if (categories != null)
                setCategories(categories);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    public void setCategories(List cats)
    {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        Iterator iter = cats.iterator();
        while (iter.hasNext())
        {
            model.addElement(iter.next());
        }
        mCategoryCombo.setModel(model);
    }

    public void channel(LoadEntrySignal signal)
    {
        if (this.blogSite == signal.getBlog())
        {
            loadEntry(signal.getId());
        }
    }

    /**
     * Load blog entry into form
     */
    public void loadEntry(String id)
    {
        try
        {
            entry = blogSite.getEntry(id);
            List allCats = blogSite.getCategories();
            List entryCats = entry.getCategories();
            if (allCats != null && entryCats != null)
            {
                setCategories(allCats);
                Iterator iter = entryCats.iterator();
                while (iter.hasNext())
                {
                    BlogEntry.Category cat = (BlogEntry.Category) iter.next();
                    mCategoryCombo.setSelectedItem(cat);
                    //mCategoryList.setSelectedValue(cat, true);
                }
            }
            else if (entryCats != null)
            {
                setCategories(entryCats);
                Iterator iter = entryCats.iterator();
                while (iter.hasNext())
                {
                    BlogEntry.Category cat = (BlogEntry.Category) iter.next();
                    mCategoryCombo.setSelectedItem(cat);
                    //mCategoryList.setSelectedValue(cat, true);
                }
            }
            if (entry.getTitle() != null)
            {
                mTitleField.setText(entry.getTitle());
            }
            if (entry.getPublicationDate() != null)
            {
                mPubDateField.setText(entry.getPublicationDate().toString());
            }
            if (entry.getContent() != null)
            {
                editorPanel.setDocumentText(entry.getContent().getValue());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Publish entry to blog
     */
    public void publishButtonPressed()
    {
        try
        {
            postEntry(true);
        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }
    }

    /**
     * Save entry to blog as draft
     */
    public void draftButtonPressed()
    {
        postEntry(false);
    }

    /**
     * New button was pressed to start new entry
     */
    public void newButtonPressed()
    {
        reset();
    }

    /**
     * Delete current entry
     */
    public void deleteButtonPressed()
    {
        if (entry != null)
        {
            try
            {
                entry.delete();
                reset();
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
                JOptionPane.showMessageDialog(window, Msg.DELETE_ERROR);
            }
        }
    }

    /**
     * Upload image and add <img> tag to blog entry
     */
    public void uploadButtonPressed()
    {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(window);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            String fileName = chooser.getSelectedFile().getName();
            try
            {
                int lastDot = fileName.lastIndexOf('.');
                String ext = fileName.substring(lastDot + 1);
                BlogResource res = blogSite.newResource(fileName, "image/" + ext, chooser.getSelectedFile()); //$NON-NLS-1$
                res.save();
                editorPanel.setDocumentText(editorPanel.getDocumentText() + "<img src=\"" + res.getURL() + "\" />"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
                JOptionPane.showMessageDialog(window, Msg.UPLOAD_ERROR);
            }
        }
    }

    /**
     * New button was pressed to start new entry
     */
    public void reset()
    {
        entry = null;
        mTitleField.setText(EMPTY_STRING);
        //mCategoryList.clearSelection();
        mPubDateField.setText(EMPTY_STRING);
        mUpdateDateField.setText(EMPTY_STRING);
        //mTextArea.setText(EMPTY_STRING);
        editorPanel.setDocumentText(EMPTY_STRING);
    }

    /**
     * Called by tabbed container
     */
    public void onSelected()
    {
        // nothing to do
    }

    /**
     * Post entry to blog
     */
    public void postEntry(boolean publish)
    {
        try
        {
            boolean hasContent = editorPanel.getDocumentText() != null && editorPanel.getDocumentText().trim().length() > 0;
            boolean hasTitle = mTitleField.getText() != null && mTitleField.getText().trim().length() > 0;
            if (hasTitle || hasContent)
            {
                if (entry == null)
                {
                    entry = blogSite.newEntry();
                }
                BlogEntry.Content content = new BlogEntry.Content(editorPanel.getDocumentText());
                content.setType("text/html"); //$NON-NLS-1$
                entry.setContent(content);

                entry.setTitle(mTitleField.getText());
                if (mCategoryCombo.getSelectedItem() != null)
                {
                    ArrayList list = new ArrayList();
                    list.add(mCategoryCombo.getSelectedItem());
                    entry.setCategories(list);
                }
                entry.setDraft(!publish);
                entry.save();
                JOptionPane.showMessageDialog(window, Msg.SAVE_SUCCESS);
                reset();
            }
            else
            {
                JOptionPane.showMessageDialog(window, Msg.MISSING_CONTENT);
            }
        }
        catch (Exception e)
        {
            // TODO: indicate error to user
            e.printStackTrace(System.err);
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents()
    {
        mTitleField = new javax.swing.JTextField();
        mTitleLabel = new javax.swing.JLabel();
        mCategoryLabel = new javax.swing.JLabel();
        mCategoryCombo = new javax.swing.JComboBox();
        //mTextArea = new javax.swing.JTextArea();
        mPublishButton = new javax.swing.JButton();
        mNewButton = new javax.swing.JButton();
        mDraftButton = new javax.swing.JButton();
        mPubDateField = new javax.swing.JLabel();
        mUpdateDateField = new javax.swing.JLabel();
        mDeleteButton = new javax.swing.JButton();
        //mUploadImage = new javax.swing.JButton();

        propsPanel = new GriddedPanel();

        mTitleLabel.setText(Msg.TITLE_ENTRY.toString());
        mTitleLabel.setLabelFor(mTitleField);
        propsPanel.addComponent(mTitleLabel, 1, 1);
        propsPanel.addFilledComponent(mTitleField, 1, 2, 3, 1, GridBagConstraints.HORIZONTAL);

        mCategoryLabel.setText(Msg.CATEGORY_ENTRY.toString());
        mCategoryLabel.setLabelFor(mCategoryCombo);
        propsPanel.addComponent(mCategoryLabel, 2, 1);
        propsPanel.addComponent(mCategoryCombo, 2, 2);

        textPanel = new JPanel(new BorderLayout());
        //textPanel.setBorder(new TitledBorder(new EtchedBorder(), "Content"));

        //mTextArea.setLineWrap(true);
        //mTextArea.setRows(3);
        //final Dimension dim = mTextArea.getPreferredScrollableViewportSize();
        //mScrollPane.setViewportView(mTextArea);
        /*mScrollPane.setViewportView(mTextArea);
         mScrollPane.setMinimumSize(dim);
         mScrollPane.invalidate();
         textPanel.add(mScrollPane, BorderLayout.CENTER);*/

        KafenioPanelConfiguration config = new KafenioPanelConfiguration();
        Vector v = new Vector();
        v.add(KafenioToolBar.KEY_TOOL_BOLD);
        v.add(KafenioToolBar.KEY_TOOL_ITALIC);
        v.add(KafenioToolBar.KEY_TOOL_UNDERLINE);
        v.add(KafenioToolBar.KEY_TOOL_SEP);
        v.add(KafenioToolBar.KEY_TOOL_CUT);
        v.add(KafenioToolBar.KEY_TOOL_COPY);
        v.add(KafenioToolBar.KEY_TOOL_PASTE);
        v.add(KafenioToolBar.KEY_TOOL_SEP);
        v.add(KafenioToolBar.KEY_TOOL_ALIGNLEFT);
        v.add(KafenioToolBar.KEY_TOOL_ALIGNRIGHT);
        v.add(KafenioToolBar.KEY_TOOL_ALIGNCENTER);
        v.add(KafenioToolBar.KEY_TOOL_SEP);
        v.add(KafenioToolBar.KEY_TOOL_ULIST);
        v.add(KafenioToolBar.KEY_TOOL_OLIST);
        v.add(KafenioToolBar.KEY_TOOL_SEP);
        v.add(KafenioToolBar.KEY_TOOL_INDENTLEFT);
        v.add(KafenioToolBar.KEY_TOOL_INDENTRIGHT);
        v.add(KafenioToolBar.KEY_TOOL_SEP);
        v.add(KafenioToolBar.KEY_TOOL_UNDO);
        v.add(KafenioToolBar.KEY_TOOL_REDO);
        v.add(KafenioToolBar.KEY_TOOL_SEP);
        v.add(KafenioToolBar.KEY_TOOL_TABLE);
        v.add(KafenioToolBar.KEY_TOOL_FIND);

        config.setCustomToolBar1(v);
        config.setShowToolbar2(false);
        //config.setImageDir("file://");
        editorPanel = new KafenioPanel(config);
        textPanel.add(editorPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4, 1, 5, 5));

        mPublishButton.setText(Msg.PUBLISH.toString());
        mPublishButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                publishButtonPressed();
            }
        });
        buttonsPanel.add(mPublishButton);

        mNewButton.setText(Msg.NEW.toString());
        mNewButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                newButtonPressed();
            }
        });

        buttonsPanel.add(mNewButton);

        mDraftButton.setText(Msg.SAVE_DRAFT.toString());
        mDraftButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                draftButtonPressed();
            }
        });

        buttonsPanel.add(mDraftButton);

        mDeleteButton.setText(Msg.DELETE.toString());
        mDeleteButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                deleteButtonPressed();
            }
        });

        buttonsPanel.add(mDeleteButton);
        btnPanel.add(buttonsPanel);
        /*
         mUploadImage.setText("Upload Image...");
         mUploadImage.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
         uploadButtonPressed();
         }
         });
         */

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        setLayout(new BorderLayout());
        add(propsPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.EAST);
    }


    public static final String EMPTY_STRING = ""; //$NON-NLS-1$
    private Blog blogSite;
    private BlogEntry entry;
    private Window window;

    private javax.swing.JLabel mCategoryLabel;
    private javax.swing.JButton mDeleteButton;
    private javax.swing.JButton mDraftButton;
    private javax.swing.JButton mNewButton;
    private javax.swing.JLabel mPubDateField;
    private javax.swing.JButton mPublishButton;
    //private javax.swing.JTextArea mTextArea;
    private javax.swing.JTextField mTitleField;
    private javax.swing.JLabel mTitleLabel;
    private javax.swing.JLabel mUpdateDateField;
    //private javax.swing.JButton mUploadImage;
    private javax.swing.JComboBox mCategoryCombo;
    private KafenioPanel editorPanel;
    private GriddedPanel propsPanel;
    private JPanel textPanel;
    private JPanel buttonsPanel;
    
    private static final long serialVersionUID = 1L;
}
