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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.crosswire.jsword.book.install.Installer;
import org.crosswire.jsword.book.install.sword.AbstractSwordInstaller;

/**
 * A representation of a Sword SiteEditor.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author DM Smith [dmsmith555 at yahoo dot com]
 */
public class SwordSiteEditor extends JPanel implements SiteEditor {
    public void initialize() {
        host = new JTextField();
        // TRANSLATOR: Label for the host field.
        // This is something like www.crosswire.org.
        JLabel hostLabel = getLabelForText(Msg.gettext("Host:"), host);

        catalogDir = new JTextField();
        // TRANSLATOR: Label for the catalog directory field.
        // This is the folder that contains mods.d.tar.gz.
        JLabel catalogDirLabel = getLabelForText(Msg.gettext("Catalog Directory:"), catalogDir);

        packageDir = new JTextField();
        // TRANSLATOR: Label for the zip directory field.
        // SWORD modules are cached as zip files in this directory.
        JLabel packageDirLabel = getLabelForText(Msg.gettext("Zip Directory:"), packageDir);

        proxyHost = new JTextField();
        // TRANSLATOR: Label for the proxy host field.
        // Sometimes users have their internet access proxied. This field allows the user to enter the proxy host.
        JLabel proxyHostLabel = getLabelForText(Msg.gettext("Proxy Host:"), proxyHost);

        proxyPort = new JTextField();
        // TRANSLATOR: Label for the proxy port field.
        // Sometimes users have their internet access proxied. This field allows the user to enter the proxy port, if not 80.
        JLabel proxyPortLabel = getLabelForText(Msg.gettext("Proxy Port:"), proxyPort);

        setLayout(new GridBagLayout());
        add(hostLabel,       new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        add(host,            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        add(catalogDirLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        add(catalogDir,      new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        add(packageDirLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        add(packageDir,      new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        add(proxyHostLabel,  new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        add(proxyHost,       new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));
        add(proxyPortLabel,  new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(2, 10, 2, 2), 0, 0));
        add(proxyPort,       new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 10), 0, 0));

        reset();
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.install.SiteEditor#save()
     */
    public void save() {
        if (installer == null) {
            return;
        }

        installer.setHost(host.getText());
        installer.setCatalogDirectory(catalogDir.getText());
        installer.setPackageDirectory(packageDir.getText());
        installer.setProxyHost(proxyHost.getText());
        Integer pport = null;
        try {
            pport = new Integer(proxyPort.getText());
        } catch (NumberFormatException e) {
            pport = null; // or -1
        }
        installer.setProxyPort(pport);
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.install.SiteEditor#reset()
     */
    public void reset() {
        if (installer == null) {
            return;
        }

        host.setText(installer.getHost());
        catalogDir.setText(installer.getCatalogDirectory());
        packageDir.setText(installer.getPackageDirectory());
        proxyHost.setText(installer.getProxyHost());
        Integer port = installer.getProxyPort();
        proxyPort.setText(port == null ? null : port.toString());
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.install.SiteEditor#setEditable(boolean)
     */
    public void setEditable(boolean editable) {
        if (host != null) {
            host.setEditable(editable);
        }

        if (catalogDir != null) {
            catalogDir.setEditable(editable);
        }

        if (packageDir != null) {
            packageDir.setEditable(editable);
        }

        if (proxyHost != null) {
            proxyHost.setEditable(editable);
        }

        if (proxyPort != null) {
            proxyPort.setEditable(editable);
        }
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.install.SiteEditor#getInstaller()
     */
    public Installer getInstaller() {
        return installer;
    }

    /* (non-Javadoc)
     * @see org.crosswire.bibledesktop.book.install.SiteEditor#setInstaller()
     */
    public void setInstaller(Installer newInstaller) {
        assert newInstaller == null || newInstaller instanceof AbstractSwordInstaller;
        Installer old = installer;
        installer = (AbstractSwordInstaller) newInstaller;
        if (newInstaller == null) {
            removeAll();
        } else if (!newInstaller.equals(old)) {
            removeAll();
            initialize();
        }
    }

    private JLabel getLabelForText(String title, JTextField field) {
        JLabel label = new JLabel();
        label.setText(title);
        label.setLabelFor(field);
        return label;
    }

    /**
     * Serialization support.
     * 
     * @param is
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream is) throws IOException, ClassNotFoundException {
        installer = null;
        is.defaultReadObject();
    }

    private transient AbstractSwordInstaller installer;
    private JTextField host;
    private JTextField catalogDir;
    private JTextField packageDir;
    private JTextField proxyHost;
    private JTextField proxyPort;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3834589894202175795L;

}
