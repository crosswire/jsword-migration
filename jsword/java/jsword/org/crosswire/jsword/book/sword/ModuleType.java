package org.crosswire.jsword.book.sword;

import java.io.File;
import java.io.Serializable;

import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookException;
import org.crosswire.jsword.book.BookType;

/**
 * Data about module types.
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
 * @author DM Smith [dmsmith555 at yahoo dot com]
 * @version $Id$
 */
public abstract class ModuleType implements Serializable
{
    /**
     * Uncompressed Bibles
     */
    public static final ModuleType RAW_TEXT = new ModuleType("RawText", "texts/rawtext", BookType.BIBLE) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257004341402874419L;
    };

    /**
     * Compressed Bibles
     */
    public static final ModuleType Z_TEXT = new ModuleType("zText", "texts/ztext", BookType.BIBLE) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return getCompressedBackend(sbmd, path);
        }

        protected boolean isBackendSupported(SwordBookMetaData sbmd)
        {
            return isCompressedBackendSupported(sbmd);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258697597915772985L;
    };

    /**
     * Uncompressed Commentaries
     */
    public static final ModuleType RAW_COM = new ModuleType("RawCom", "comments/rawcom", BookType.COMMENTARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3762250850940433716L;
    };

    /**
     * Compressed Commentaries
     */
    public static final ModuleType Z_COM = new ModuleType("zCom", "comments/zcom", BookType.COMMENTARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return getCompressedBackend(sbmd, path);
        }

        protected boolean isBackendSupported(SwordBookMetaData sbmd)
        {
            return isCompressedBackendSupported(sbmd);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257008752367580211L;
    };

    /**
     * Uncompresses HREF Commentaries
     */
    public static final ModuleType HREF_COM = new ModuleType("HREFCom", "comments/hrefcom", BookType.COMMENTARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257567291372548153L;
    };

    /**
     * Uncompressed Commentaries
     */
    public static final ModuleType RAW_FILES = new ModuleType("RawFiles", "comments/rawfiles", BookType.COMMENTARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257005475290625843L;
    };

    /**
     * 2-Byte Index Uncompressed Dictionaries
     */
    public static final ModuleType RAW_LD = new ModuleType("RawLD", "lexdict/rawld", BookType.DICTIONARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordDictionary(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawLDBackend(sbmd, path, 2);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258412833061352242L;
    };

    /**
     * 4-Byte Index Uncompressed Dictionaries
     */
    public static final ModuleType RAW_LD4 = new ModuleType("RawLD4", "lexdict/rawld4", BookType.DICTIONARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordDictionary(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawLDBackend(sbmd, path, 4);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3258689892711149625L;
    };

    /**
     * Compressed Dictionaries
     */
    public static final ModuleType Z_LD = new ModuleType("zLD", "lexdict/zld", BookType.DICTIONARY) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordDictionary(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new ZLDBackend(sbmd);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257849883074573619L;
    };

    /**
     * Generic Books
     */
    public static final ModuleType RAW_GEN_BOOK = new ModuleType("RawGenBook", "genbook/rawgenbook", null) //$NON-NLS-1$ //$NON-NLS-2$
    {
        protected Book getBook(SwordBookMetaData sbmd, Backend backend)
        {
            return new SwordBook(sbmd, backend);
        }

        protected Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException
        {
            return new RawBackend(path);
        }

        /**
         * Serialization ID
         */
        private static final long serialVersionUID = 3257849895976449588L;
    };

    /**
     * Simple ctor
     */
    public ModuleType(String name, String install, BookType type)
    {
        this.name = name;
        this.install = install;
        this.type = type;
    }

    /**
     * Find a ModuleType from a name.
     * @param name The name of the ModuleType to look up
     * @return The found ModuleType or null if the name is not found
     */
    public static ModuleType getModuleType(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            ModuleType mod = VALUES[i];
            if (mod.name.equalsIgnoreCase(name))
            {
                return mod;
            }
        }

        throw new IllegalArgumentException(Msg.UNDEFINED_MODULE.toString(name));
    }

    /**
     * Strings for where the modules are installed.
     * @return Returns the install directory for this module type.
     */
    public String getInstallDirectory()
    {
        return install;
    }

    /**
     * The book type of this module
     */
    public BookType getBookType()
    {
        return type;
    }

    /**
     * Given a SwordBookMetaData determine whether this ModuleType
     * will work for it.
     * @param sbmd the BookMetaData that this ModuleType works upon
     * @return true if this is a useable ModuleType
     */
    public boolean isSupported(SwordBookMetaData sbmd)
    {
        return type != null && isBackendSupported(sbmd);
    }

    /**
     * By default the backend is supported if the BookMetaData is not null.
     * @return true if this is a useable BackEnd
     */
    protected boolean isBackendSupported(SwordBookMetaData sbmd)
    {
        return sbmd != null;
    }

    /**
     * Create a Book appropriate for the BookMetaData
     * @throws BookException
     */
    public Book createBook(SwordBookMetaData sbmd, File progdir) throws BookException
    {
        String dataPath = sbmd.getProperty(ConfigEntry.DATA_PATH);
        File baseurl = new File(progdir, dataPath);
        String path = baseurl.getAbsolutePath();
        Backend backend = getBackend(sbmd, path);
        Book book = getBook(sbmd, backend);
        sbmd.setBook(book);
        return book;
    }

    /**
     * Create a Book with the given backend
     */
    protected abstract Book getBook(SwordBookMetaData sbmd, Backend backend);

    /**
     * Create a the appropriate backend for this type of book
     */
    protected abstract Backend getBackend(SwordBookMetaData sbmd, String path) throws BookException;

    /**
     * 
     */
    protected static Backend getCompressedBackend(SwordBookMetaData sbmd, String path) throws BookException
    {
        String cStr = sbmd.getProperty(ConfigEntry.COMPRESS_TYPE);
        if (cStr != null)
        {
            return CompressionType.fromString(cStr).getBackend(sbmd, path);
        }
        throw new BookException(Msg.COMPRESSION_UNSUPPORTED, new Object[] { cStr });
    }

    /**
     * 
     */
    protected static boolean isCompressedBackendSupported(SwordBookMetaData sbmd)
    {
        String cStr = sbmd.getProperty(ConfigEntry.COMPRESS_TYPE);
        if (cStr != null)
        {
            return CompressionType.fromString(cStr).isSupported();
        }
        return false;
    }

    /**
     * The name of the ModuleType
     */
    private String name;

    /**
     * Where are modules of this type installed relative to the sword module
     * directory?
     */
    private String install;

    /**
     * What booktype is this module
     */
    private BookType type;

    /**
     * Lookup method to convert from a String
     */
    public static ModuleType fromString(String name)
    {
        for (int i = 0; i < VALUES.length; i++)
        {
            ModuleType mod = VALUES[i];
            if (mod.name.equalsIgnoreCase(name))
            {
                return mod;
            }
        }

        throw new ClassCastException(Msg.UNDEFINED_DATATYPE.toString(name));
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public final boolean equals(Object o)
    {
        return super.equals(o);
    }

    /**
     * Prevent subclasses from overriding canonical identity based Object methods
     * @see java.lang.Object#hashCode()
     */
    public final int hashCode()
    {
        return super.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    // Support for serialization
    static final long serialVersionUID = 1417463751329673026L;
    private static int nextObj;
    private final int obj = nextObj++;

    Object readResolve()
    {
        return VALUES[obj];
    }

    private static final ModuleType[] VALUES =
    {
        RAW_TEXT,
        Z_TEXT,
        RAW_COM,
        Z_COM,
        RAW_COM,
        HREF_COM,
        RAW_FILES,
        RAW_LD,
        RAW_LD4,
        Z_LD,
        RAW_GEN_BOOK,
    };
}
