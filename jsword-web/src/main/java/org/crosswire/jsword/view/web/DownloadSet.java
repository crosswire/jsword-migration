package org.crosswire.jsword.view.web;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import org.crosswire.common.util.Logger;

/**
 * A helper for the download.jsp page.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DownloadSet implements Comparable<DownloadSet> {
    public static final String BIN_ZIP = "-bin.zip";
    public static final String BIN_TGZ = "-bin.tar.gz";
    public static final String SRC_ZIP = "-src.zip";
    public static final String SRC_TGZ = "-src.tar.gz";
    public static final String DOC_ZIP = "-doc.zip";
    public static final String DOC_TGZ = "-doc.tar.gz";

    /**
     * Get an Iterator over all the Downloads in the specified Directory
     */
    public static DownloadSet[] getDownloadSets(String localprefix, String webprefix, boolean datesort) throws IOException {
        File dir = new File(localprefix);
        if (!dir.isDirectory()) {
            throw new IOException(Msg.NON_DIR.toString(localprefix));
        }

        log.debug("dig " + localprefix);
        File[] files = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                log.debug("found " + name);
                return file.canRead() && name.startsWith(TEST_PREFIX) && name.endsWith(TEST_SUFFIX);
            }
        });

        SortedSet<DownloadSet> reply = new TreeSet<DownloadSet>();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            log.debug("adding " + name);
            String sets = name.substring(TEST_PREFIX.length(), name.length() - TEST_SUFFIX.length());
            reply.add(new DownloadSet(localprefix, webprefix, sets, datesort));
        }

        return reply.toArray(new DownloadSet[reply.size()]);
    }

    /**
     * Create a set of downloads
     */
    private DownloadSet(String localprefix, String webprefix, String setname, boolean datesort) {
        this.localprefix = localprefix;
        this.webprefix = webprefix;
        this.setname = setname;
        this.datesort = datesort;

        log.debug("ctor " + webprefix);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(DownloadSet that) {
        if (datesort) {
            try {
                // The setname may either be a VERSION_DATE or
                // x.x.x.x-VERSION_DATE.
                String thisSetdate = this.setname.substring(this.setname.length() - VERSION_DATE.length());
                Date thisdate = DF_DISK.parse(thisSetdate);
                String thatSetdate = that.setname.substring(that.setname.length() - VERSION_DATE.length());
                Date thatdate = DF_DISK.parse(thatSetdate);

                return thisdate.compareTo(thatdate);
            } catch (ParseException ex) {
                log.error("Failed to parse dates", ex);
                return 0;
            }
        }
        return that.setname.compareTo(this.setname);
    }

    /**
     * When was the set of files created (using the file name string)
     */
    public String getDateString() throws ParseException {
        // The setname may either be a VERSION_DATE or x.x.x.x-VERSION_DATE.
        String setdate = setname.substring(setname.length() - VERSION_DATE.length());
        Date date = DF_DISK.parse(setdate);
        return DF_USER.format(date);
    }

    /**
     * What is the version number (using the file name string)
     */
    public String getVersionString() {
        return Msg.VERSION.toString(setname);
    }

    /**
     * Get a short HTML string for the download link. Purists would complain
     * that this is UI specific code embedded where it ought not be. So such I
     * would argue - rewrite this so that it still works (not easy given the
     * JSP/XML use) and so that it is just as simple and so that it can actually
     * be reused in a more general UI.
     */
    public String getLinkString(String extension) {
        File file = new File(localprefix, TEST_PREFIX + setname + extension);
        String size = NF.format(file.length() / (1024.0F * 1024.0F));
        String reply = "<a href='" + webprefix + '/' + TEST_PREFIX + setname + extension + "'>" + size + " Mb</a>";

        log.debug("link=" + reply);

        return reply;
    }

    private boolean datesort;
    private String webprefix;
    private String localprefix;
    private String setname;

    private static final String TEST_PREFIX = "jsword-";
    private static final String TEST_SUFFIX = BIN_ZIP;

    private static final NumberFormat NF = NumberFormat.getNumberInstance();
    private static final String VERSION_DATE = "yyyyMMdd";
    private static final DateFormat DF_DISK = new SimpleDateFormat(VERSION_DATE);
    private static final DateFormat DF_USER = new SimpleDateFormat("dd MMM yyyy");
    static {
        NF.setMaximumFractionDigits(2);
    }

    /**
     * The log stream
     */
    protected static final Logger log = Logger.getLogger(DownloadSet.class);
}
