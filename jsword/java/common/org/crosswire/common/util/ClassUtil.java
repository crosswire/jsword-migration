
package org.crosswire.common.util;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang.StringUtils;

/**
 * Various Java Class Utilities.
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
public class ClassUtil
{
    /**
     * This function finds the first matching filename for a Java class
     * file from the classpath, if none is found it returns null.
     */
    public static String findClasspathEntry(String classname, String classpath)
    {
        String full = null;

        String paths[] = StringUtils.split(classpath, File.pathSeparator);
        for (int i=0; i<paths.length; i++)
        {
            // Search the jar
            if (paths[i].endsWith(".zip") || paths[i].endsWith(".jar"))
            {
                try
                {
                    String file_name = StringUtils.replace(classname, ".", "/") + ".class";
                    ZipFile zip = new ZipFile(paths[i]);
                    ZipEntry entry = zip.getEntry(file_name);

                    if (entry != null && !entry.isDirectory())
                    {
                        if (full != null && !full.equals(file_name))
                        {
                            log.warn("Warning duplicate " + classname + " found: " + full + " and " + paths[i]);
                        }
                        else
                        {
                            full = paths[i];
                        }
                    }
                }
                catch (IOException ex)
                {
                    // If that zip file failed, then ignore it and more on.
                }
            }
            else
            {
                // Search for the file
                String extra = StringUtils.replace(classname, ".", File.separator);

                if (!paths[i].endsWith(File.separator))
                    paths[i] = paths[i] + File.separator;

                String file_name = paths[i] + extra + ".class";

                if (new File(file_name).isFile())
                {
                    if (full != null && !full.equals(file_name))
                    {
                        log.warn("Warning duplicate " + classname + " found: " + full + " and " + paths[i]);
                    }
                    else
                    {
                        full = paths[i];
                    }
                }
            }
        }

        return full;
    }

    /**
     * This function find the first matching filename for a Java class
     * file from the classpath, if none is found it returns null.
     */
    public static String findClasspathEntry(String classname)
    {
        String classpath = System.getProperty("java.class.path", "");
        return findClasspathEntry(classname, classpath);
    }

    /** The log stream */
    private static final Logger log = Logger.getLogger(StringUtil.class);
}
