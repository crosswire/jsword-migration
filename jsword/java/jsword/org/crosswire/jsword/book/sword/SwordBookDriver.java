package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.SystemUtils;
import org.crosswire.common.util.Logger;
import org.crosswire.common.util.NetUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookDriver;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookMetaData;
import org.crosswire.jsword.book.BookType;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.basic.AbstractBookDriver;

/**
 * This represents all of the SwordBibles.
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
public class SwordBookDriver extends AbstractBookDriver
{
    /**
     * Some basic name initialization
     */
    public SwordBookDriver()
    {
        log.debug("Starting Sword drivers");
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getBooks()
     */
    public BookMetaData[] getBooks()
    {
        if (dirs == null)
        {
            return new BookMetaData[0];
        }

        List valid = new ArrayList();

        // Loop through the dirs in the lookup path
        for (int j=0; j<dirs.length; j++)
        {
            URL mods = NetUtil.lengthenURL(dirs[j], "mods.d");
            if (NetUtil.isDirectory(mods))
            {
                try
                {
                    File modsdir = NetUtil.getAsFile(mods);
                    String[] bookdirs = modsdir.list(new CustomFilenameFilter());
                    //String[] bookdirs = NetUtil.listByFile(mods, new CustomURLFilter());

                    // Loop through the entries in this mods.d directory
                    for (int i=0; i<bookdirs.length; i++)
                    {
                        String bookdir = bookdirs[i];
                        try
                        {
                            File configfile = new File(modsdir, bookdir);
                            SwordConfig config = new SwordConfig(configfile);

                            if (config.isSupported())
                            {
                                Book book = createBook(config, dirs[j]);
                                valid.add(book.getBookMetaData());
                            }
                            else
                            {
                                String name = bookdir.substring(0, bookdir.indexOf(".conf"));
                                log.warn("Unsupported Book: "+name);
                            }
                        }
                        catch (Exception ex)
                        {
                            log.warn("Couldn't create SwordBookMetaData", ex);
                        }
                    }            
                }
                catch (IOException ex)
                {
                    log.warn("Couldn't list mods.d at: "+mods, ex);
                }
            }
            else
            {
                log.debug("mods.d directory at "+mods+" does not exist");
            }
        }

        return (BookMetaData[]) valid.toArray(new BookMetaData[valid.size()]);
    }

    /**
     * Create a Book to wrap the given backend
     */
    private Book createBook(SwordConfig config, URL progdir) throws BookException, MalformedURLException, ParseException
    {
        String dataPath = config.getFirstValue("DataPath");
        URL baseurl = NetUtil.lengthenURL(progdir, dataPath);
        if (!baseurl.getProtocol().equals("file"))
        {
            throw new BookException(Msg.FILE_ONLY, new Object[] { baseurl.getProtocol()});
        }
        String path = baseurl.getFile();
        
        Backend backend = null;
        Book book = null;

        int moddrv = config.getModDrv();
        switch (moddrv)
        {
        case SwordConstants.DRIVER_RAW_LD:
            backend = new RawLDBackend(config, path, 2);
            book = new SwordDictionary(this, config, backend, BookType.DICTIONARY);
            break;

        case SwordConstants.DRIVER_RAW_LD4:
            backend = new RawLDBackend(config, path, 4);
            book = new SwordDictionary(this, config, backend, BookType.DICTIONARY);
            break;

        case SwordConstants.DRIVER_Z_LD:
            backend = new ZLDBackend(config);
            book = new SwordDictionary(this, config, backend, BookType.DICTIONARY);
            break;

        case SwordConstants.DRIVER_RAW_TEXT:
            backend = new RawBackend(path);
            book = new SwordBook(this, config, backend, BookType.BIBLE);
            break;
        
        case SwordConstants.DRIVER_RAW_COM:
            backend = new RawBackend(path);
            book = new SwordBook(this, config, backend, BookType.COMMENTARY);
            break;
            
        case SwordConstants.DRIVER_HREF_COM:
            backend = new RawBackend(path);
            book = new SwordBook(this, config, backend, BookType.COMMENTARY);
            break;
        
        case SwordConstants.DRIVER_RAW_FILES:
            backend = new RawBackend(path);
            book = new SwordBook(this, config, backend, BookType.COMMENTARY);
            break;

        case SwordConstants.DRIVER_Z_TEXT:
            backend = getCompressedBackend(config, path);
            book = new SwordBook(this, config, backend, BookType.BIBLE);
            break;

        case SwordConstants.DRIVER_Z_COM:
            backend = getCompressedBackend(config, path);
            book = new SwordBook(this, config, backend, BookType.COMMENTARY);
            break;

        case SwordConstants.DRIVER_RAW_GEN_BOOK:
            // LATER(joe): how do we support books?
            log.warn("No support for book type: DRIVER_RAW_GEN_BOOK");
            throw new BookException(Msg.TYPE_UNSUPPORTED);

        default:
            throw new BookException(Msg.TYPE_UNKNOWN, new Object[] { ""+moddrv, path });
        }

        return book;
    }

    /**
     * 
     */
    private Backend getCompressedBackend(SwordConfig config, String path) throws BookException
    {
        switch (config.matchingIndex(SwordConstants.COMPRESSION_STRINGS, "CompressType"))
        {
        case SwordConstants.COMPRESSION_ZIP:
            // The default blocktype (when we used fields) was SwordConstants.BLOCK_CHAPTER (2);
            // but the specified default here is BLOCK_BOOK (0)
            int blocktype = config.matchingIndex(SwordConstants.BLOCK_STRINGS, "BlockType", SwordConstants.BLOCK_BOOK);
            return new GZIPBackend(path, blocktype);
      
        case SwordConstants.COMPRESSION_LZSS:
            return new LZSSBackend(config);
      
        default:
            throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { config.getFirstValue("CompressType") });
        }
    }

    /**
     * Accessor for the Sword directory
     * @param paths The new Sword directory
     */
    public static void setSwordDir(String[] paths) throws MalformedURLException, BookException
    {
        // Fist we need to unregister any registered books from ourselves
        BookDriver[] matches = Books.getDriversByClass(SwordBookDriver.class);
        for (int i=0; i<matches.length; i++)
        {
            Books.unregisterDriver(matches[i]);
        }

        // If the new paths are empty then guess ...
        if (paths == null || paths.length == 0)
        {
            paths = getDefaultPaths();
        }

        dirs = new URL[paths.length];
        for (int i = 0; i < dirs.length; i++)
        {
            dirs[i] = new URL("file", null, paths[i]);
            if (!NetUtil.isDirectory(dirs[i]))
            {
                log.warn("No sword source found under: "+dirs[i]);
            }
        }

        // Now we need to register ourselves
        Books.registerDriver(new SwordBookDriver());
    }

    /**
     * Have an OS dependent guess at where Sword might be installed
     */
    private static String[] getDefaultPaths()
    {
        List reply = new ArrayList();

        if (SystemUtils.IS_OS_WINDOWS)
        {
            reply.add("C:\\Program Files\\CrossWire\\The SWORD Project");
        }
        else
        {
            // If it isn't unix then assume some sort of unix
            File sysconfig = new File("/etc/sword.conf");
            if (sysconfig.canRead())
            {
                try
                {
                    Properties prop = new Properties();
                    prop.load(new FileInputStream(sysconfig));
                    String datapath = prop.getProperty("DataPath");
                    testDefaultPath(reply, datapath+"/mods.d");
                }
                catch (IOException ex)
                {
                    log.warn("Failed to read system config file", ex);
                }
            }
        }

        // if there is a property set for the sword home directory
        String swordhome = System.getProperty("sword.home");
        if (swordhome != null)
        {
            testDefaultPath(reply, swordhome+"/mods.d");
        }

        // .sword in the users home directory?
        testDefaultPath(reply, System.getProperty("user.home")+"/.sword/mods.d");

        // .jsword in the users home directory?
        testDefaultPath(reply, System.getProperty("user.home")+"/.jsword/mods.d");

        // mods.d in the current directory?
        testDefaultPath(reply, new File(".").getAbsolutePath()+"/mods.d");

        return (String[]) reply.toArray(new String[reply.size()]);
    }

    /**
     * Check to see if the given directory is a Sword mods.d directory
     * and then add it to the list if it is.
     */
    private static void testDefaultPath(List reply, String path)
    {
        File test = new File(path);
        if (test.isDirectory())
        {
            reply.add(path);
        }
    }

    /**
     * Accessor for the Sword directory
     * @return The new Sword directory
     */
    public static String[] getSwordDir()
    {
        if (dirs == null || dirs.length == 0)
        {
            return new String[0];
        }

        String[] paths = new String[dirs.length];

        for (int i = 0; i < paths.length; i++)
        {
            paths[i] = dirs[i].getFile();
        }

        return paths;
    }

    /**
     * The directory URL
     */
    private static URL[] dirs;

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(SwordBookDriver.class);

    /**
     * Check that the directories in the version directory really
     * represent versions.
     */
    private static class CustomFilenameFilter implements FilenameFilter
    {
        /* (non-Javadoc)
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        public boolean accept(File parent, String name)
        {
            return !name.startsWith("globals.") && name.endsWith(".conf");
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.jsword.book.BookDriver#getName()
     */
    public String getDriverName()
    {
        return "Sword";
    }
}
