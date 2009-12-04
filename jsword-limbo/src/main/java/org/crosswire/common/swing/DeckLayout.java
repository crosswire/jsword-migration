package org.crosswire.common.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DeckLayout is very similar to the awt CardLayout, except the latter is
 * supposed to have some focus problems. I've not come across these before, and
 * DeckLayout seems to be broken anyway, so I don't use it at all.
 * <p>
 * DeckLayout treats each component in the container as a card. Only one card is
 * visible at a time, and the container acts like a deck of cards. The ordering
 * of cards is determined by the container's own internal ordering of its
 * component objects. DeckLayout defines a set of methods that allow an
 * application to flip through the cards sequentially, or to show a specified
 * card. The addLayoutComponent method can be used to associate a string
 * identifier with a given card for faster random access.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 * @author Claude Duguay Copyright (c) 1998
 */
public class DeckLayout extends AbstractLayout {
    /**
     * 
     */
    public DeckLayout() {
        this(0, 0, false);
    }

    /**
     * @param wrap
     */
    public DeckLayout(boolean wrap) {
        this(0, 0, wrap);
    }

    /**
     * @param hgap
     * @param vgap
     */
    public DeckLayout(int hgap, int vgap) {
        this(hgap, vgap, false);
    }

    /**
     * @param hgap
     * @param vgap
     * @param wrap
     */
    public DeckLayout(int hgap, int vgap, boolean wrap) {
        super(hgap, vgap);
        this.wrap = wrap;
    }

    /**
     * Adds the specified component to this deck layout's internal table, by
     * name. The object specified by constraints must be a string. The deck
     * layout stores this string as a key-value pair that can be used for random
     * access to a particular card. By calling the show method, an application
     * can display the component with the specified name.
     * 
     * @param comp
     *            The component to be added.
     * @param constraints
     *            A name that identifies the component
     */
    /* @Override */
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof String || constraints == null) {
            addLayoutComponent((String) constraints, comp);
        } else {
            throw new IllegalArgumentException("cannot add to layout: constraint must be a string"); //$NON-NLS-1$
        }
    }

    /**
     * Removes the specified component from the layout.
     * 
     * @param comp
     *            The component to be removed.
     */
    /* @Override */
    public void removeLayoutComponent(Component comp) {
        Iterator it = tab.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (tab.get(key) == comp) {
                tab.remove(key);
                count--;
                return;
            }
        }
    }

    /**
     * Calculates the preferred size for the specified panel.
     * 
     * @param parent
     *            The name of the parent container
     * @return minimum dimensions required to lay out the components.
     */
    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int ncomponents = parent.getComponentCount();
        int w = 0;
        int h = 0;

        for (int i = 0; i < ncomponents; i++) {
            Component comp = parent.getComponent(i);
            Dimension d = comp.getPreferredSize();
            if (d.width > w) {
                w = d.width;
            }
            if (d.height > h) {
                h = d.height;
            }
        }
        return new Dimension(insets.left + insets.right + w + hgap * 2, insets.top + insets.bottom + h + vgap * 2);
    }

    /**
     * Calculates the minimum size for the specified panel.
     * 
     * @param parent
     *            The name of the parent container
     * @return minimum dimensions required to lay out the components.
     */
    public Dimension minimumLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int ncomponents = parent.getComponentCount();
        int w = 0;
        int h = 0;

        for (int i = 0; i < ncomponents; i++) {
            Component comp = parent.getComponent(i);
            Dimension d = comp.getMinimumSize();
            if (d.width > w) {
                w = d.width;
            }
            if (d.height > h) {
                h = d.height;
            }
        }
        return new Dimension(insets.left + insets.right + w + hgap * 2, insets.top + insets.bottom + h + vgap * 2);
    }

    /**
     * Lays out the specified container using this deck layout. Each component
     * in the parent container is reshaped to be the same size as the container,
     * minus insets, horizontal and vertical gaps.
     * 
     * @param parent
     *            The name of the parent container
     */
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int ncomponents = parent.getComponentCount();
        for (int i = 0; i < ncomponents; i++) {
            Component comp = parent.getComponent(i);
            if (comp.isVisible()) {
                comp.setBounds(hgap + insets.left, vgap + insets.top, parent.getSize().width - (hgap * 2 + insets.left + insets.right), parent.getSize().height
                        - (vgap * 2 + insets.top + insets.bottom));
            }
        }
    }

    /**
     * Make sure that the Container really has this layout installed, to avoid
     * serious problems.
     */
    private void checkLayout(Container parent) {
        if (parent.getLayout() != this) {
            throw new IllegalArgumentException("wrong parent for CardLayout"); //$NON-NLS-1$
        }
    }

    /**
     * Enable or disable the specified component and all its children. This
     * makes focus traversal function properly. The side effect is that all
     * children are enabled or disabled and specific contexts are not
     * maintained. You can get around this by intercepting setEnabled in your
     * component to restore state if this is important in your context.
     */
    private void setActive(Component comp, boolean enabled) {
        comp.setVisible(enabled);
        comp.setEnabled(enabled);
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            int cnt = cont.getComponentCount();
            for (int i = 0; i < cnt; i++) {
                setActive(cont.getComponent(i), enabled);
            }
        }
    }

    /**
     * Flips to the first card of the container.
     * 
     * @param parent
     *            The name of the parent container
     */
    public void first(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            int ncomponents = parent.getComponentCount();
            for (int i = 0; i < ncomponents; i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    setActive(comp, false);
                    comp = parent.getComponent(0);
                    setActive(comp, true);
                    parent.validate();
                    return;
                }
            }
        }
    }

    /**
     * Flips to the next card of the specified container. If the currently
     * visible card is the last one, this method flips to the first card in the
     * layout.
     * 
     * @param parent
     *            The name of the parent container
     * @return Index of the selected component
     */
    public int next(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            int ncomponents = parent.getComponentCount();
            for (int i = 0; i < ncomponents; i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    if (i + 1 >= ncomponents && !wrap)
                        return i;
                    int index = (i + 1 < ncomponents) ? i + 1 : 0;
                    setActive(comp, false);
                    comp = parent.getComponent(index);
                    setActive(comp, true);
                    parent.validate();
                    return index;
                }
            }
            return -1;
        }
    }

    /**
     * Flips to the previous card of the specified container. If the currently
     * visible card is the first one, this method flips to the last card in the
     * layout.
     * 
     * @param parent
     *            The name of the parent container
     * @return Index of the selected component
     */
    public int previous(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            int ncomponents = parent.getComponentCount();
            for (int i = 0; i < ncomponents; i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    if (i <= 0 && !wrap)
                        return i;
                    setActive(comp, false);
                    int index = (i > 0) ? i - 1 : ncomponents - 1;
                    comp = parent.getComponent(index);
                    setActive(comp, true);
                    parent.validate();
                    return index;
                }
            }
            return -1;
        }
    }

    /**
     * Flips to the last card of the container.
     * 
     * @param parent
     *            The name of the parent container
     */
    public void last(Container parent) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            int ncomponents = parent.getComponentCount();
            for (int i = 0; i < ncomponents; i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    setActive(comp, false);
                    comp = parent.getComponent(ncomponents - 1);
                    setActive(comp, true);
                    parent.validate();
                    return;
                }
            }
        }
    }

    /**
     * Flips to the component that was added to this layout using the specified
     * name. If no such component exists, nothing happens.
     * 
     * @param parent
     *            The name of the parent container in which to do the layout.
     * @param name
     *            The component name.
     */
    public void show(Container parent, String name) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            Component next = (Component) tab.get(name);
            if (next != null && !next.isVisible()) {
                int ncomponents = parent.getComponentCount();
                for (int i = 0; i < ncomponents; i++) {
                    Component comp = parent.getComponent(i);
                    if (comp.isVisible()) {
                        setActive(comp, false);
                        break;
                    }
                }
                setActive(next, true);
                parent.validate();
            }
        }
    }

    /**
     * Flips to the component at the numbered position. If no such component
     * exists, nothing happens.
     * 
     * @param parent
     *            The name of the parent container in which to do the layout.
     * @param index
     *            The index (between 0 and component count - 1)
     */
    public void show(Container parent, int index) {
        synchronized (parent.getTreeLock()) {
            checkLayout(parent);
            if (index < 0 || index > parent.getComponentCount() - 1)
                return;
            int ncomponents = parent.getComponentCount();

            for (int i = 0; i < ncomponents; i++) {
                Component comp = parent.getComponent(i);
                if (comp.isVisible()) {
                    setActive(comp, false);
                    comp = parent.getComponent(index);
                    setActive(comp, true);
                    parent.validate();
                    return;
                }
            }
        }
    }

    /**
     * @param name
     * @return return the named component
     */
    public Component getComponent(String name) {
        return (Component) tab.get(name);
    }

    /**
     * @param parent
     * @param index
     * @return return the name of the component
     */
    public String getName(Container parent, int index) {
        Component comp = parent.getComponent(index);

        Iterator it = tab.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Object val = tab.get(key);
            if (comp == val) {
                return key;
            }
        }

        return null;
    }

    /**
     * @param parent
     * @param name
     * @return the index of the component
     */
    public int getIndex(Container parent, String name) {
        Component comp = getComponent(name);
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == comp)
                return i;
        }

        return -1;
    }

    protected Map tab = new HashMap();
    protected int count = 0;
    protected boolean wrap = false;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256444715787105330L;
}
