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
package org.crosswire.bibledesktop.journal;

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
final class Msg extends MsgBase {
    static final Msg BLOG_TITLE = new Msg("BlogClientFrame.BlogTitle");
    static final Msg START_BLOG = new Msg("BlogClientFrame.StartBlog");
    static final Msg ACCOUNT_NAME = new Msg("BlogClientFrame.AccountName");
    static final Msg PASSWORD = new Msg("BlogClientFrame.Password");
    // I18N: migrate this to an ActionFactory
    static final Msg SUBMIT = new Msg("BlogClientFrame.Submit");
    static final Msg EDIT_ENTRY = new Msg("BlogClientFrame.EditEntry");
    static final Msg ALL_ENTRIES = new Msg("BlogClientFrame.AllEntries");
    static final Msg CONNECTING = new Msg("BlogClientFrame.Connecting");
    static final Msg CONNECTED = new Msg("BlogClientFrame.Connected");
    static final Msg JOURNAL_RECEIVED = new Msg("BlogClientFrame.JournalReceived");
    static final Msg ALL_DONE = new Msg("BlogClientFrame.AllDone");
    static final Msg MORE = new Msg("BlogClientFrame.More");
    static final Msg NO_JOURNALS = new Msg("BlogClientFrame.NoJournals");
    static final Msg CANNOT_CONNECT = new Msg("BlogClientFrame.CannotConnect");

    static final Msg DELETE_ERROR = new Msg("BlogClientPanel.DeleteError");
    static final Msg UPLOAD_ERROR = new Msg("BlogClientPanel.UploadError");
    static final Msg SAVE_SUCCESS = new Msg("BlogClientPanel.SaveSuccess");
    static final Msg MISSING_CONTENT = new Msg("BlogClientPanel.MissingContent");
    static final Msg TITLE_ENTRY = new Msg("BlogClientPanel.TitleEntry");
    static final Msg CATEGORY_ENTRY = new Msg("BlogClientPanel.CategoryEntry");
    static final Msg PUBLISH = new Msg("BlogClientPanel.Publish");
    static final Msg NEW = new Msg("BlogClientPanel.New");
    static final Msg SAVE_DRAFT = new Msg("BlogClientPanel.SaveDraft");
    static final Msg DELETE = new Msg("BlogClientPanel.Delete");

    static final Msg ENTRY_TITLE = new Msg("BlogEntriesPanel.EntryTitle");

    static final Msg TITLE_COLUMN = new Msg("BlogEntriesPanel.TitleColumn");
    static final Msg ID_COLUMN = new Msg("BlogEntriesPanel.IdColumn");
    static final Msg DATE_COLUMN = new Msg("BlogEntriesPanel.DateColumn");

    /**
     * Passthrough ctor
     */
    private Msg(String name) {
        super(name);
    }
}
