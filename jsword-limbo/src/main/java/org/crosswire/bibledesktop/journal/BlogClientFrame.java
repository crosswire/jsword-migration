/*
 * Copyright 2005, Dave Johnson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.crosswire.bibledesktop.journal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.crosswire.bibledesktop.signal.LoadEntrySignal;
import org.crosswire.bibledesktop.signal.ResizeJournalSignal;
import org.crosswire.bibledesktop.signal.SaveConfigSignal;
import org.crosswire.bibledesktop.signal.StatusSignal;
import org.crosswire.common.swing.GriddedPanel;
import org.werx.framework.bus.ReflectionBus;

import com.manning.blogapps.chapter08.blogclient.Blog;
import com.manning.blogapps.chapter08.blogclient.BlogConnection;
import com.manning.blogapps.chapter08.blogclient.BlogConnectionFactory;

/**
 * Simple Swing-based blog client with tabbed UI.
 * 
 * @author David M Johnson
 */
public class BlogClientFrame extends JPanel {

    protected BlogClientFrame() {
        setLayout(new BorderLayout());
        initComponents();
        curPanel = disconnectedPanel;
        add(disconnectedPanel, java.awt.BorderLayout.CENTER);

        setBorder(new TitledBorder(new EtchedBorder(), Msg.BLOG_TITLE.toString()));
    }

    private void initComponents() {
        disconnectedPanel = new GriddedPanel();
        disconnectedPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

        JLabel inst = new JLabel(Msg.START_BLOG.toString());

        disconnectedPanel.addFilledComponent(inst, 1, 1, 3, 1, GridBagConstraints.HORIZONTAL);

        final JTextField userNameField = new JTextField();
        userNameField.setColumns(20);
        JLabel userNameLabel = new JLabel(Msg.ACCOUNT_NAME.toString());
        userNameLabel.setLabelFor(userNameField);
        disconnectedPanel.addComponent(userNameLabel, 2, 1);
        disconnectedPanel.addComponent(userNameField, 2, 2);

        final JPasswordField passwordField = new JPasswordField();
        passwordField.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                passwordField.setSelectionStart(0);
                passwordField.setSelectionEnd(passwordField.getPassword().length);
            }

            public void focusLost(FocusEvent e) {
            }
        });
        passwordField.setColumns(20);
        JLabel passwordLabel = new JLabel(Msg.PASSWORD.toString());
        passwordLabel.setLabelFor(passwordField);
        disconnectedPanel.addComponent(passwordLabel, 3, 1);
        disconnectedPanel.addComponent(passwordField, 3, 2);

        JButton submit = new JButton(Msg.SUBMIT.toString());
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                signin(userNameField.getText(), passwordField.getPassword());
                Properties props = new Properties();
                props.put(USER_NAME, userNameField.getText());
                props.put(PASSWORD, passwordField.getPassword());

                ReflectionBus.broadcast(new SaveConfigSignal(props));
            }
        });
        disconnectedPanel.addComponent(submit, 4, 1);
    }

    public static BlogClientFrame getInstance() {
        return SELF;
    }

    public static void setUrl(String value) {
        BlogClientFrame.url = value;
        getInstance().resetBlogClientLib();
    }

    public static void setPassword(String value) {
        BlogClientFrame.password = value;
        getInstance().resetBlogClientLib();
    }

    public static void setUserName(String value) {
        BlogClientFrame.userName = value;
        getInstance().resetBlogClientLib();
    }

    public static void setType(int value) {
        BlogClientFrame.type = BlogType.fromInteger(value);
        getInstance().resetBlogClientLib();
    }

    public static String getUrl() {
        return url;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUserName() {
        return userName;
    }

    public static int getType() {
        return type.toInteger();
    }

    public void signin(String name, char[] pswd) {
        BlogClientFrame.userName = name;
        BlogClientFrame.password = new String(pswd);
        getInstance().resetBlogClientLib();
    }

    private JTabbedPane initBlogClientUI(Blog blog) {

        final BlogClientPanel clientPanel = new BlogClientPanel();
        final BlogEntriesPanel entriesPanel = new BlogEntriesPanel();

        final JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(new EmptyBorder(new java.awt.Insets(5, 5, 5, 5)));
        tabs.addTab(Msg.EDIT_ENTRY.toString(), clientPanel);
        tabs.addTab(Msg.ALL_ENTRIES.toString(), entriesPanel);

        clientPanel.setBlog(blog);
        entriesPanel.setBlog(blog);

        // FIXME: this should be unplugged on reload
        ReflectionBus.plug(new Object() {
            public void channel(LoadEntrySignal signal) {
                tabs.setSelectedComponent(clientPanel);
            }
        });

        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                JTabbedPane pane = (JTabbedPane) evt.getSource();
                BlogClientTab tab = (BlogClientTab) pane.getSelectedComponent();
                tab.onSelected();
            }
        });
        return tabs;
    }

    private void setStatus(Msg msg) {
        ReflectionBus.broadcast(new StatusSignal(msg.toString()));
    }

    private void setStatus(Msg msg, Object[] objs) {
        ReflectionBus.broadcast(new StatusSignal(msg.toString(objs)));
    }

    private void resetBlogClientLib() {
        if (url != null && userName != null && password != null && type != null) {
            try {
                setStatus(Msg.CONNECTING, new Object[] {
                        url, userName
                });
                BlogConnection blogConn = newBlogConnection(url, userName, password, type);
                setStatus(Msg.CONNECTED);
                List blogs = blogConn.getBlogs();
                if (blogs.size() == 1) {
                    JTabbedPane tab = initBlogClientUI((Blog) blogs.get(0));
                    remove(curPanel);
                    curPanel = tab;
                    add(tab, BorderLayout.CENTER);
                    setStatus(Msg.JOURNAL_RECEIVED);
                } else if (blogs.size() > 1) {
                    JTabbedPane blogTabs = new JTabbedPane(SwingConstants.LEFT);
                    Blog blog;
                    Iterator i = blogs.iterator();
                    while (i.hasNext()) {
                        blog = (Blog) i.next();
                        JTabbedPane tab = initBlogClientUI(blog);
                        String name = blog.getName();
                        if (name.length() > 10) {
                            name = name.substring(0, 10) + Msg.MORE;
                        }
                        blogTabs.addTab(name, tab);
                    }
                    remove(curPanel);
                    curPanel = blogTabs;
                    add(blogTabs, java.awt.BorderLayout.CENTER);
                    setStatus(Msg.ALL_DONE);
                } else {
                    setStatus(Msg.NO_JOURNALS);
                }

            } catch (Exception ex) {
                setStatus(Msg.CANNOT_CONNECT);
                if (curPanel != disconnectedPanel) {
                    remove(curPanel);
                    curPanel = disconnectedPanel;
                    add(disconnectedPanel, java.awt.BorderLayout.CENTER);
                }
                ex.printStackTrace(System.err);
            }

            ReflectionBus.broadcast(new ResizeJournalSignal());
            /*
             * invalidate(); if (getRootPane() != null) {
             * getRootPane().validate(); getRootPane().repaint(); }
             */
            // revalidate();
            // repaint();
        }
    }

    protected BlogConnection newBlogConnection(String theUrl, String theUserName, String thePassword, BlogType theType) throws MalformedURLException, Exception {
        return BlogConnectionFactory.getBlogConnection(theType.toString().toLowerCase(), theUrl, theUserName, thePassword);
    }

    public void setEnabled(final Container cont, final boolean enabled) {
        if (!SwingUtilities.isEventDispatchThread()) {
            Runnable t = new Runnable() {
                public void run() {
                    setEnabledRecursive(cont, enabled);
                }
            };
            SwingUtilities.invokeLater(t);

        } else {
            setEnabledRecursive(cont, enabled);
        }
    }

    void setEnabledRecursive(final Container cont, final boolean enabled) {
        cont.setEnabled(enabled);

        for (int i = 0; i < cont.getComponentCount(); i++) {
            cont.getComponent(i).setEnabled(enabled);

            if (cont.getComponent(i) instanceof Container)
                setEnabledRecursive((Container) cont.getComponent(i), enabled);
        }
    }

    private static final String USER_NAME = "WebJournal.UserName";
    private static final String PASSWORD = "WebJournal.Password";
    private static String userName;
    private static String password;
    private static String url = "http://www.bibleblogs.net/roller/xmlrpc";
    private static BlogType type = BlogType.META_WEBLOG;

    private static final BlogClientFrame SELF = new BlogClientFrame();
    private static final long serialVersionUID = 1L;

    private GriddedPanel disconnectedPanel;
    private Component curPanel;
}
