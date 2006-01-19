package org.crosswire.bibledesktop.journal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import com.manning.blogapps.chapter08.blogclient.Blog;
import com.manning.blogapps.chapter08.blogclient.BlogEntry;

/**
 * Simple table model of recent blog entries
 * @author Don Brown [mrdon at twdata dot org]
 */
public class BlogEntriesTableModel extends DefaultTableModel
{
    public BlogEntriesTableModel(Blog blogSite)
    {
        try
        {
            int count = 0;
            Iterator entryIter = blogSite.getEntries();
            while (entryIter.hasNext() && count++ < 50)
            {
                BlogEntry entry = (BlogEntry) entryIter.next();
                entries.add(entry);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Object getValueAt(int row, int col)
    {
        if (entries != null)
        {
            BlogEntry entry = (BlogEntry) entries.get(row);
            switch (col)
            {
                case BlogEntriesPanel.TITLE_COLUMN:
                    return entry.getTitle();
                case BlogEntriesPanel.DATE_COLUMN:
                    return entry.getModificationDate();
                case BlogEntriesPanel.ID_COLUMN:
                    return entry.getToken();
                default:
                    assert false;
            }
        }
        assert false;
        return "ERROR"; //$NON-NLS-1$
    }

    public int getColumnCount()
    {
        return BlogEntriesPanel.COLUMN_COUNT;
    }

    public int getRowCount()
    {
        return (entries != null) ? entries.size() : 0;
    }

    public String getColumnName(int column)
    {
        switch (column)
        {
            case BlogEntriesPanel.TITLE_COLUMN:
                return Msg.TITLE_COLUMN.toString();
            case BlogEntriesPanel.ID_COLUMN:
                return Msg.ID_COLUMN.toString();
            case BlogEntriesPanel.DATE_COLUMN:
                return Msg.DATE_COLUMN.toString();
            default:
                assert false;
        }
        assert false;
        return "ERROR: Invalid column index"; //$NON-NLS-1$
    }

    public boolean isCellEditable(int row, int col)
    {
        return false;
    }

    private List entries = new ArrayList();
}
