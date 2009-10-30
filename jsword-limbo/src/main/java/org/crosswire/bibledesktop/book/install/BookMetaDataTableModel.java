/**
 * Distribution License:
 * BibleDesktop is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License, version 2 as published by
 * the Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * The License is available on the internet at:
 *       http://www.gnu.org/copyleft/gpl.html
 * or by writing to:
 *      Free Software Foundation, Inc.
 *      59 Temple Place - Suite 330
 *      Boston, MA 02111-1307, USA
 *
 * Copyright: 2005
 *     The copyright to this program is held by it's authors.
 *
 * ID: $Id$
 */
package org.crosswire.bibledesktop.book.install;

import org.crosswire.common.swing.MapTableModel;
import org.crosswire.jsword.book.BookMetaData;

/**
 * A TableModel that displays the data in a BookMetaData object.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class BookMetaDataTableModel extends MapTableModel {
    /**
     * Simple ctor
     */
    public BookMetaDataTableModel() {
        setBookMetaData(null);
    }

    /**
     * Simple ctor with default BookMetaData
     */
    public BookMetaDataTableModel(BookMetaData bmd) {
        setBookMetaData(bmd);
    }

    /**
     * @return Returns the BookMetaData.
     */
    public BookMetaData getBookMetaData() {
        return bmd;
    }

    /**
     * @param bmd
     *            The BookMetaData to set.
     */
    public final void setBookMetaData(BookMetaData bmd) {
        if (bmd != this.bmd) {
            if (bmd == null) {
                setMap(null);
            } else {
                setMap(bmd.getProperties());
            }

            this.bmd = bmd;
        }
    }

    /**
     * The meta data that we are displaying
     */
    private BookMetaData bmd;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257566222043460664L;
}
