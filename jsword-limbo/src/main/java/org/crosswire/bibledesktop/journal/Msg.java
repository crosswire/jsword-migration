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
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
final class Msg extends MsgBase
{
    static final Msg BLOG_TITLE = new Msg("BlogClientFrame.BlogTitle"); //$NON-NLS-1$
    static final Msg START_BLOG = new Msg("BlogClientFrame.StartBlog"); //$NON-NLS-1$
    static final Msg ACCOUNT_NAME = new Msg("BlogClientFrame.AccountName"); //$NON-NLS-1$
    static final Msg PASSWORD = new Msg("BlogClientFrame.Password"); //$NON-NLS-1$
    // I18N: migrate this to an ActionFactory
    static final Msg SUBMIT = new Msg("BlogClientFrame.Submit"); //$NON-NLS-1$
    static final Msg EDIT_ENTRY = new Msg("BlogClientFrame.EditEntry"); //$NON-NLS-1$
    static final Msg ALL_ENTRIES = new Msg("BlogClientFrame.AllEntries"); //$NON-NLS-1$
    static final Msg CONNECTING = new Msg("BlogClientFrame.Connecting"); //$NON-NLS-1$
    static final Msg CONNECTED = new Msg("BlogClientFrame.Connected"); //$NON-NLS-1$
    static final Msg JOURNAL_RECEIVED = new Msg("BlogClientFrame.JournalReceived"); //$NON-NLS-1$
    static final Msg ALL_DONE = new Msg("BlogClientFrame.AllDone"); //$NON-NLS-1$
    static final Msg MORE = new Msg("BlogClientFrame.More"); //$NON-NLS-1$
    static final Msg NO_JOURNALS = new Msg("BlogClientFrame.NoJournals"); //$NON-NLS-1$
    static final Msg CANNOT_CONNECT = new Msg("BlogClientFrame.CannotConnect"); //$NON-NLS-1$

    static final Msg DELETE_ERROR = new Msg("BlogClientPanel.DeleteError"); //$NON-NLS-1$
    static final Msg UPLOAD_ERROR = new Msg("BlogClientPanel.UploadError"); //$NON-NLS-1$
    static final Msg SAVE_SUCCESS = new Msg("BlogClientPanel.SaveSuccess"); //$NON-NLS-1$
    static final Msg MISSING_CONTENT = new Msg("BlogClientPanel.MissingContent"); //$NON-NLS-1$
    static final Msg TITLE_ENTRY = new Msg("BlogClientPanel.TitleEntry"); //$NON-NLS-1$
    static final Msg CATEGORY_ENTRY = new Msg("BlogClientPanel.CategoryEntry"); //$NON-NLS-1$
    static final Msg PUBLISH = new Msg("BlogClientPanel.Publish"); //$NON-NLS-1$
    static final Msg NEW = new Msg("BlogClientPanel.New"); //$NON-NLS-1$
    static final Msg SAVE_DRAFT = new Msg("BlogClientPanel.SaveDraft"); //$NON-NLS-1$
    static final Msg DELETE = new Msg("BlogClientPanel.Delete"); //$NON-NLS-1$

    static final Msg ENTRY_TITLE = new Msg("BlogEntriesPanel.EntryTitle"); //$NON-NLS-1$

    static final Msg TITLE_COLUMN = new Msg("BlogEntriesPanel.TitleColumn"); //$NON-NLS-1$
    static final Msg ID_COLUMN = new Msg("BlogEntriesPanel.IdColumn"); //$NON-NLS-1$
    static final Msg DATE_COLUMN = new Msg("BlogEntriesPanel.DateColumn"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
