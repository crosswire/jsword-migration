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

import org.crosswire.common.util.MsgBase;

/**
 * Compile safe Msg resource settings.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
final class Msg extends MsgBase
{
    static final Msg AVAILABLE_BOOKS = new Msg("SitesPane.AvailableBooks"); //$NON-NLS-1$
    static final Msg LOCAL_BOOKS = new Msg("SitesPane.Local"); //$NON-NLS-1$

    static final Msg KB_SIZE = new Msg("SitePane.KBSize"); //$NON-NLS-1$
    static final Msg MB_SIZE = new Msg("SitePane.MBSize"); //$NON-NLS-1$
    static final Msg CONFIRMATION_TITLE = new Msg("SitePane.ConfirmationTitle"); //$NON-NLS-1$
    static final Msg INSTALLED = new Msg("SitePane.Installed"); //$NON-NLS-1$
    static final Msg INSTALLED_DESC = new Msg("SitePane.InstalledDesc"); //$NON-NLS-1$
    static final Msg DELETE_FAILED = new Msg("SitePane.DeleteFailed"); //$NON-NLS-1$
    static final Msg AVAILABLE_DESC = new Msg("SitePane.AvailableDesc"); //$NON-NLS-1$
    static final Msg NONE_AVAILABLE_DESC = new Msg("SitePane.NoneAvailableDesc"); //$NON-NLS-1$
    static final Msg CONFIRM_DELETE_TITLE = new Msg("SitePane.ConfirmDeleteTitle"); //$NON-NLS-1$
    static final Msg CONFIRM_DELETE_BOOK = new Msg("SitePane.ConfirmDeleteBook"); //$NON-NLS-1$
    static final Msg UNLOCK_TITLE = new Msg("SitePane.UnlockTitle"); //$NON-NLS-1$
    static final Msg UNLOCK_BOOK = new Msg("SitePane.UnlockBook"); //$NON-NLS-1$
    static final Msg CONFIRM_UNINSTALL_TITLE = new Msg("SitePane.ConfirmUninstallTitle"); //$NON-NLS-1$
    static final Msg CONFIRM_UNINSTALL_BOOK = new Msg("SitePane.ConfirmUninstallBook"); //$NON-NLS-1$
    static final Msg FONT_CHOOSER = new Msg("SitePane.FontChooser"); //$NON-NLS-1$

    static final Msg EDIT_SITE_TITLE = new Msg("EditSitePane.EditSitesTitle"); //$NON-NLS-1$
    static final Msg MISSING_SITE = new Msg("EditSitePane.MissingSite"); //$NON-NLS-1$
    static final Msg DUPLICATE_SITE = new Msg("EditSitePane.DuplicateSite"); //$NON-NLS-1$
    static final Msg NO_SELECTED_SITE = new Msg("EditSitePane.NoSelectedSite"); //$NON-NLS-1$
    static final Msg NO_SITE = new Msg("EditSitePane.NoSite"); //$NON-NLS-1$
    static final Msg CONFIRM_DELETE_SITE = new Msg("EditSitePane.ConfirmDeleteSite"); //$NON-NLS-1$
    static final Msg DELETE_SITE = new Msg("EditSitePane.DeleteSite"); //$NON-NLS-1$

    static final Msg HOW_MESSAGE_TITLE = new Msg("IndexResolver.HowMessageTitle"); //$NON-NLS-1$
    static final Msg HOW_MESSAGE = new Msg("IndexResolver.HowMessage"); //$NON-NLS-1$
    static final Msg HOW_GENERATE_TITLE = new Msg("IndexResolver.HowGenerateTitle"); //$NON-NLS-1$
    static final Msg HOW_GENERATE = new Msg("IndexResolver.HowGenerate"); //$NON-NLS-1$
    static final Msg HOW_SITE_TITLE = new Msg("IndexResolver.HowSiteTitle"); //$NON-NLS-1$
    static final Msg HOW_SITE = new Msg("IndexResolver.HowSite"); //$NON-NLS-1$
    static final Msg OPTION_DOWNLOAD = new Msg("IndexResolver.OptionDownload"); //$NON-NLS-1$
    static final Msg OPTION_GENERATE = new Msg("IndexResolver.OptionGenerate"); //$NON-NLS-1$
    static final Msg OPTION_CANCEL = new Msg("IndexResolver.OptionCancel"); //$NON-NLS-1$

    static final Msg HOST = new Msg("SwordSiteEditor.Host"); //$NON-NLS-1$
    static final Msg CATALOG_DIR = new Msg("SwordSiteEditor.CatalogDir"); //$NON-NLS-1$
    static final Msg PACKAGE_DIR = new Msg("SwordSiteEditor.PackageDir"); //$NON-NLS-1$
    static final Msg PROXY_HOST = new Msg("SwordSiteEditor.ProxyHost"); //$NON-NLS-1$
    static final Msg PROXY_PORT = new Msg("SwordSiteEditor.ProxyPort"); //$NON-NLS-1$

    static final Msg BOOK_LOCKED = new Msg("BookTreeCellRenderer.BookLocked"); //$NON-NLS-1$
    static final Msg BOOK_UNSUPPORTED = new Msg("BookTreeCellRenderer.BookUnsupported"); //$NON-NLS-1$
    static final Msg BOOK_QUESTIONABLE = new Msg("BookTreeCellRenderer.BookQuestionable"); //$NON-NLS-1$

    /**
     * Passthrough ctor
     */
    private Msg(String name)
    {
        super(name);
    }
}
