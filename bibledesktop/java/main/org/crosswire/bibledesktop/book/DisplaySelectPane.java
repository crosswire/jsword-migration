package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.bibledesktop.book.install.IndexResolver;
import org.crosswire.bibledesktop.passage.KeyChangeEvent;
import org.crosswire.bibledesktop.passage.KeyChangeListener;
import org.crosswire.common.swing.ActionFactory;
import org.crosswire.common.swing.GuiUtil;
import org.crosswire.common.swing.QuickHelpDialog;
import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookFilters;
import org.crosswire.jsword.book.IndexStatus;
import org.crosswire.jsword.book.search.parse.IndexSearcher;
import org.crosswire.jsword.book.search.parse.PhraseParamWord;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.NoSuchVerseException;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * Passage Selection area.
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
public class DisplaySelectPane extends JPanel implements KeyChangeListener
{
    /**
     * General constructor
     */
    public DisplaySelectPane()
    {
        initialize();
    }

    /**
     * Initialize the GUI
     */
    private void initialize()
    {
        advanced = new AdvancedSearchPane();

        title = Msg.UNTITLED.toString(new Integer(base++));

        actions = new ActionFactory(DisplaySelectPane.class, this);

        // search() and version() rely on this returning only Bibles
        mdlBible = new BooksComboBoxModel(BookFilters.getBibles());
        JComboBox cboBible = new JComboBox(mdlBible);
        selected = mdlBible.getSelectedBook();
        if (selected != null)
        {
            selected.addPropertyChangeListener(pcl);
            cboBible.setToolTipText(selected.toString());
        }
        else
        {
            // The application has started and there are no installed bibles.
            // So make the combo box a reasonable size.
            cboBible.setPrototypeDisplayValue("                                                            "); //$NON-NLS-1$
        }
        cboBible.setRenderer(new BookListCellRenderer());
        cboBible.addItemListener(new ItemListener()
        {
            public void itemStateChanged(ItemEvent ev)
            {
                if (ev.getStateChange() == ItemEvent.SELECTED)
                {
                    changeVersion();
                    JComboBox combo = (JComboBox) ev.getSource();
                    combo.setToolTipText(combo.getSelectedItem().toString());
                }
            }
        });
        cboBible.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JComboBox cbo = (JComboBox) e.getSource();
                if (cbo.getSelectedIndex() == -1 && cbo.getItemCount() > 0)
                {
                    cbo.setSelectedIndex(0);
                }
            }
        });
        JLabel lblBible = actions.createJLabel(BIBLE);
        lblBible.setLabelFor(cboBible);

        /* LATER(JOE)
        JButton btnMenu = new JButton();
        btnMenu.setIcon(ICON_MENU);
        btnMenu.setBorderPainted(false);
        */

        JPanel pnlBible = new JPanel();
        pnlBible.setLayout(new BorderLayout());
        pnlBible.add(lblBible, BorderLayout.WEST);
        pnlBible.add(cboBible, BorderLayout.EAST); // CENTER);
        // pnlBible.add(btnMenu, BorderLayout.EAST);

        JLabel lblKey = actions.createJLabel(VIEW_LABEL);
        txtKey = new JTextField();
        txtKey.setAction(actions.getAction(PASSAGE_FIELD));
        txtKey.addKeyListener(new KeyAdapter()
        {
            public void keyTyped(KeyEvent ev)
            {
                if (ev.getKeyChar() == '\n' && ev.getModifiers() == Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())
                {
                    showSelectDialog();
                }
            }
        });
        btnKey = new JButton(actions.getAction(MORE));
        btnKey.setIcon(ICON_SELECT);
        btnKey.setBorderPainted(false);
        btnKeyGo = new JButton(actions.getAction(GO_PASSAGE));

        txtSearch = new JTextField();
        txtSearch.setAction(actions.getAction(SEARCH_FIELD));
        JLabel lblSearch = actions.createJLabel(SEARCH_LABEL);
        lblSearch.setLabelFor(txtSearch);
        btnSearch = new JButton(actions.getAction(GO_SEARCH));

        JButton btnHelp = new JButton(actions.getAction(HELP));
        btnHelp.setBorder(BorderFactory.createLineBorder(SystemColor.control, 5));
        btnHelp.setBackground(Color.yellow);
        btnHelp.setText(null);
        dlgHelp = new QuickHelpDialog(GuiUtil.getFrame(this), Msg.HELP_TITLE.toString(), Msg.HELP_TEXT.toString());

        btnAdvanced = new JButton(actions.getAction(ADVANCED));
        btnAdvanced.setBorderPainted(false);
        btnIndex = new JButton(actions.getAction(INDEX));

        chkMatch = new JCheckBox(actions.getAction(MATCH));

        this.setLayout(new GridBagLayout());
        this.add(pnlBible, new GridBagConstraints(0, 0, 6, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        this.add(lblKey, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        this.add(txtKey, new GridBagConstraints(2, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 0, 0, 0), 0, 0));
        this.add(btnKey, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(btnKeyGo, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        this.add(btnHelp, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(lblSearch, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        this.add(btnIndex, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(txtSearch, new GridBagConstraints(2, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(chkMatch, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        this.add(btnAdvanced, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        this.add(btnSearch, new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

        enableComponents();
    }

    /**
     * What is the currently selected Book?
     */
    public Book getBook()
    {
        return mdlBible.getSelectedBook();
    }

    /**
     *
     */
    public void clear()
    {
        if (isClear())
        {
            return;
        }

        txtKey.setText(""); //$NON-NLS-1$
        txtSearch.setText(""); //$NON-NLS-1$

        title = Msg.UNTITLED.toString(new Integer(base++));

        updateDisplay();
    }

    /**
     *
     */
    public boolean isClear()
    {
        return title.indexOf(Msg.CLEAR.toString()) != -1;
    }

    /**
     * More (...) button was clicked
     */
    public void doMore()
    {
        showSelectDialog();
    }

    /**
     * Go button was clicked
     */
    public void doGoPassage()
    {
        doPassageAction();
    }

    /**
     * Go button was clicked
     */
    public void doGoSearch()
    {
        doSearchAction();
    }

    /**
     * Someone pressed return in the passage area
     */
    public void doPassageAction()
    {
        setTitle(txtKey.getText());
        updateDisplay();
    }

    /**
     * Someone pressed return in the search area
     */
    public void doSearchAction()
    {
        Book book = mdlBible.getSelectedBook();
        if (book == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            String param = txtSearch.getText();

            if (chkMatch.isSelected())
            {
                String quote = IndexSearcher.getPreferredSyntax(PhraseParamWord.class);
                param = quote + param + quote;
            }

            Key key = book.find(param);

            // we get PassageTallys for best match searches
            if (key instanceof PassageTally)
            {
                PassageTally tally = (PassageTally) key;
                tally.setOrdering(PassageTally.ORDER_TALLY);
                // TODO: Make the number of ranges in a tally be an option.
                tally.trimRanges(20, RestrictionType.NONE);
            }

            txtKey.setText(key.getName());
            setTitle(param);
            updateDisplay();
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Someone has clicked on the advanced search button
     */
    public void doAdvanced()
    {
        String reply = advanced.showInDialog(this, Msg.ADVANCED_TITLE.toString(), true, txtSearch.getText());
        if (reply != null)
        {
            txtSearch.setText(reply);
        }
    }

    /**
     * Match is an action, but we don't need to do anything because match is
     * only used when search is clicked. But ActionFactory will complain if we
     * leave it out.
     */
    public void doMatch()
    {
        // Do nothing
    }

    /**
     * Someone clicked help
     */
    public void doHelpAction()
    {
        dlgHelp.setVisible(true);
    }

    /**
     * Someone clicked one the index button
     */
    public void doIndex()
    {
        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        IndexResolver.scheduleIndex(selected, this);
        enableComponents();
    }

    /**
     * Sync the viewed passage with the passage text box
     */
    private void updateDisplay()
    {
        Book book = mdlBible.getSelectedBook();
        if (book == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            Key key = book.getKey(txtKey.getText());

            fireCommandMade(new DisplaySelectEvent(this, key, book));
        }
        catch (NoSuchVerseException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), Msg.BAD_VERSE.toString(), JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Accessor for the default name
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the default name
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Sets the default name
     */
    public void setText(String text)
    {
        String currentText = txtKey.getText();
        if (!currentText.equals(text))
        {
            txtKey.setText(text);
            setTitle(text);
            updateDisplay();
        }
    }

    /**
     * Someone changed the version combo
     */
    protected void changeVersion()
    {
        Book newSelected = mdlBible.getSelectedBook();

        if (selected != null && selected != newSelected)
        {
            selected.removePropertyChangeListener(pcl);
            newSelected.addPropertyChangeListener(pcl);
        }

        selected = newSelected;

        enableComponents();

        if (selected == null)
        {
            noBookInstalled();
            return;
        }

        try
        {
            Key key = selected.getKey(txtKey.getText());

            fireVersionChanged(new DisplaySelectEvent(this, key, selected));
        }
        catch (Exception ex)
        {
            Reporter.informUser(this, ex);
        }
    }

    /**
     * Keep the selection up to date with indexing.
     */
    private PropertyChangeListener pcl = new PropertyChangeListener()
    {
        public void propertyChange(PropertyChangeEvent ev)
        {
            enableComponents();
        }
    };

    /**
     * Display a dialog indicating that no Bible is installed.
     */
    private void noBookInstalled()
    {
        String noBible = Msg.NO_INSTALLED_BIBLE.toString();
        JOptionPane.showMessageDialog(this, noBible, noBible, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Ensure that the right components are enabled
     */
    protected void enableComponents()
    {
        boolean readable = selected != null;
        boolean searchable = readable && selected.getIndexStatus().equals(IndexStatus.DONE);
        boolean indexable = readable && selected.getIndexStatus().equals(IndexStatus.UNDONE);

        txtSearch.setEnabled(searchable);
        txtSearch.setBackground(searchable ? SystemColor.text : SystemColor.control);
        txtSearch.setVisible(searchable);
        chkMatch.setEnabled(searchable);
        btnAdvanced.setEnabled(searchable);
        btnSearch.setEnabled(searchable);
        txtKey.setEnabled(readable);
        txtKey.setBackground(readable ? SystemColor.text : SystemColor.control);
        btnKey.setEnabled(readable);
        btnKeyGo.setEnabled(readable);
        btnIndex.setVisible(indexable);
        btnIndex.setEnabled(indexable);
    }

    /**
     * Someone clicked the "..." button
     */
    protected void showSelectDialog()
    {
        if (dlgSelect == null)
        {
            dlgSelect = new PassageSelectionPane();
        }

        String passg = dlgSelect.showInDialog(this, Msg.SELECT_PASSAGE_TITLE.toString(), true, txtKey.getText());
        if (passg != null)
        {
            txtKey.setText(passg);
            doPassageAction();
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.KeyChangeListener#keyChanged(org.crosswire.bibledesktop.book.KeyChangeEvent)
     */
    public void keyChanged(KeyChangeEvent ev)
    {
        String text = ev.getKey().getName();
        setText(text);
    }

    /**
     * Add a command listener
     */
    public synchronized void addCommandListener(DisplaySelectListener li)
    {
        List temp = new ArrayList(2);

        if (listeners != null)
        {
            temp.addAll(listeners);
        }

        if (!temp.contains(li))
        {
            temp.add(li);
            listeners = temp;
        }
    }

    /**
     * Remove a command listener
     */
    public synchronized void removeCommandListener(DisplaySelectListener li)
    {
        if (listeners != null && listeners.contains(li))
        {
            List temp = new ArrayList();
            temp.addAll(listeners);

            temp.remove(li);
            listeners = temp;
        }
    }

    /**
     * Inform the command listeners
     */
    protected void fireCommandMade(DisplaySelectEvent ev)
    {
        if (listeners != null)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                DisplaySelectListener li = (DisplaySelectListener) listeners.get(i);
                li.passageSelected(ev);
            }
        }
    }

    /**
     * Inform the version listeners
     */
    protected void fireVersionChanged(DisplaySelectEvent ev)
    {
        if (listeners != null)
        {
            int count = listeners.size();
            for (int i = 0; i < count; i++)
            {
                ((DisplaySelectListener) listeners.get(i)).bookChosen(ev);
            }
        }
    }

    // For the Passage card
    private static final String VIEW_LABEL = "ViewLabel"; //$NON-NLS-1$
    private static final String PASSAGE_FIELD = "PassageAction"; //$NON-NLS-1$
    private static final String MORE = "More"; //$NON-NLS-1$
    private static final String GO_PASSAGE = "GoPassage"; //$NON-NLS-1$
    private static final String HELP = "HelpAction"; //$NON-NLS-1$
    private static final String SEARCH_LABEL = "SearchLabel"; //$NON-NLS-1$
    private static final String GO_SEARCH = "GoSearch"; //$NON-NLS-1$
    private static final String SEARCH_FIELD = "SearchAction"; //$NON-NLS-1$
    private static final String ADVANCED = "Advanced"; //$NON-NLS-1$
    private static final String MATCH = "Match"; //$NON-NLS-1$
    private static final String BIBLE = "Bible"; //$NON-NLS-1$
    private static final String INDEX = "Index"; //$NON-NLS-1$

    private static final ImageIcon ICON_SELECT = GuiUtil.getIcon("toolbarButtonGraphics/general/Edit16.gif"); //$NON-NLS-1$

//    private static final ImageIcon ICON_MENU = GuiUtil.getIcon("toolbarButtonGraphics/general/Preferences16.gif"); //$NON-NLS-1$

    private static int base = 1;

    private String title;

    private transient List listeners;

    private QuickHelpDialog dlgHelp;

    private ActionFactory actions;

    private Book selected;

    /*
     * GUI Components
     */
    private BooksComboBoxModel mdlBible;
    private PassageSelectionPane dlgSelect;
    private JTextField txtKey;
    private JTextField txtSearch;
    private JCheckBox chkMatch;
    private JButton btnAdvanced;
    private JButton btnSearch;
    private JButton btnKey;
    private JButton btnKeyGo;
    private AdvancedSearchPane advanced;
    private JButton btnIndex;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256446910616057650L;
}
