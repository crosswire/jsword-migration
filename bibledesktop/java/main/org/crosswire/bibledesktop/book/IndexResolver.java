package org.crosswire.bibledesktop.book;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.install.InstallException;
import org.crosswire.jsword.book.install.InstallManager;
import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.search.IndexManager;
import org.crosswire.jsword.book.search.IndexManagerFactory;

/**
 * A class to prompt the user to download or create a search index and to do
 * carry out the users wishes.
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
public class IndexResolver
{
    /**
     * Prevent instansiation
     */
    private IndexResolver()
    {
    }

    /**
     * The options that we show to the user for how to get a search index
     */
    private static Object[] options = new Object[]
    {
        Msg.OPTION_DOWNLOAD,
        Msg.OPTION_GENERATE,
        Msg.OPTION_CANCEL,
    };

    /**
     * @param parent
     * 
     */
    public static void scheduleIndex(BookMetaData bmd, Component parent)
    {
        String title = Msg.HOW_MESSAGE_TITLE.toString();
        Msg msg = Msg.HOW_MESSAGE;
        int choice = JOptionPane.showOptionDialog(parent, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice)
        {
            case 0: // download
                Installer installer = selectInstaller(parent);
                if (installer != null)
                {
                    try
                    {
                        downloadIndex(bmd, installer);
                    }
                    catch (Exception ex)
                    {
                        log.error("index download failed: ", ex); //$NON-NLS-1$
                        //Reporter.informUser(parent, ex);

                        String gtitle = Msg.HOW_GENERATE_TITLE.toString();
                        Msg gmsg = Msg.HOW_GENERATE;
                        int yn = JOptionPane.showConfirmDialog(parent, gmsg, gtitle, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                        if (yn == JOptionPane.YES_OPTION)
                        {
                            IndexManagerFactory.getIndexManager().scheduleIndexCreation(bmd.getBook());
                        }
                    }
                }
                break;

            case 1: // generate
                IndexManagerFactory.getIndexManager().scheduleIndexCreation(bmd.getBook());
                break;

            default: // cancel
                break;
        }
    }
    
    /**
     * Download and install a search index
     * @param bmd The book to get an index for
     */
    private static void downloadIndex(BookMetaData bmd, Installer installer) throws IOException, InstallException, BookException
    {
        // Get a temp home
        URL tempDownload = NetUtil.getTemporaryURL(TEMP_PREFIX, TEMP_SUFFIX);

        try
        {
            // Now we know what installer to use, download to the temp file
            installer.downloadSearchIndex(bmd, tempDownload);

            // And install from that file.
            IndexManager idxman = IndexManagerFactory.getIndexManager();
            idxman.installDownloadedIndex(bmd.getBook(), tempDownload);
        }
        finally
        {
            // tidy up after ourselves
            if (tempDownload != null)
            {
                NetUtil.delete(tempDownload);
            }
        }
    }

    /**
     * Pick an installer
     * @param parent A component to tie dialogs to
     * @return The chosen installer or null if the user cancelled.
     */
    private static Installer selectInstaller(Component parent)
    {
        // Pick an installer
        InstallManager insman = new InstallManager();
        Map installers = insman.getInstallers();
        Installer installer = null;
        if (installers.size() == 1)
        {
            Iterator it = installers.values().iterator();
            assert it.hasNext();
            installer = (Installer) it.next();
        }
        else
        {
            JComboBox choice = new JComboBox(new InstallManagerComboBoxModel(insman));
            JLabel label = new JLabel(Msg.HOW_SITE.toString());
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.NORTH);
            panel.add(choice, BorderLayout.CENTER);

            String title = Msg.HOW_SITE_TITLE.toString();

            int yn = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.YES_OPTION);
            if (yn == JOptionPane.YES_OPTION)
            {
                installer = (Installer) choice.getSelectedItem();
            }
        }

        return installer;
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(IndexResolver.class);

    private static final String TEMP_SUFFIX = "dat"; //$NON-NLS-1$
    private static final String TEMP_PREFIX = "jsword-index"; //$NON-NLS-1$
}
