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
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.crosswire.bibledesktop.signal.LoadEntrySignal;
import org.werx.framework.bus.ReflectionBus;

import com.manning.blogapps.chapter08.blogclient.Blog;

/**
 * Panel with table that lists recent blog entries.
 * 
 * @author David M Johnson
 */
public class BlogEntriesPanel extends JPanel implements BlogClientTab {
    /**
     * Creates new form BlogEntriesPanel
     */
    public BlogEntriesPanel() {
        initComponents();
        mBlogEntries.addMouseListener(new MouseAdapter() {
            /* @Override */
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = mBlogEntries.rowAtPoint(e.getPoint());
                    TableModel model = mBlogEntries.getModel();
                    String id = (String) model.getValueAt(row, ID_COLUMN);
                    ReflectionBus.broadcast(new LoadEntrySignal(blogSite, id));
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    private void initComponents() {
        mScrollPane = new JScrollPane();
        mBlogEntries = new JTable();

        setLayout(new BorderLayout());

        setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        mScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mBlogEntries.setModel(new DefaultTableModel(new Object[][] {
                {
                        null, null, null, null
                }, {
                        null, null, null, null
                }, {
                        null, null, null, null
                }, {
                        null, null, null, null
                }
        }, new String[] {
                Msg.ENTRY_TITLE.toString(new Integer(1)), Msg.ENTRY_TITLE.toString(new Integer(2)), Msg.ENTRY_TITLE.toString(new Integer(3)),
                Msg.ENTRY_TITLE.toString(new Integer(4)),
        }));
        mScrollPane.setViewportView(mBlogEntries);

        add(mScrollPane, BorderLayout.NORTH);
    }

    /**
     * Inject BlogSite dependency.
     */
    public void setBlog(Blog blogSite) {
        this.blogSite = blogSite;
        reset();
    }

    /**
     * Update table of blog entries.
     */
    public void onSelected() {
        reset();
    }

    public void reset() {
        AbstractTableModel model = new BlogEntriesTableModel(blogSite);
        mBlogEntries.setModel(model);
        model.fireTableDataChanged();
    }

    JTable mBlogEntries;
    private JScrollPane mScrollPane;

    Blog blogSite;
    public static final int TITLE_COLUMN = 0;
    public static final int DATE_COLUMN = 1;
    public static final int ID_COLUMN = 2;
    public static final int COLUMN_COUNT = 3;

    private static final long serialVersionUID = 1L;
}
