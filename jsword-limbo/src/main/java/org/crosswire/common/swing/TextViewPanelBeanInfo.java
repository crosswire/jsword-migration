
package org.crosswire.common.swing;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

import org.crosswire.common.util.Logger;

/**
 * BeanInfo for the TextViewer. This was mostly generate using
 * BeansExpress.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class TextViewPanelBeanInfo extends SimpleBeanInfo
{
    // DEAD(DM): This class is not used. Find a use for it or delete it.
    /**
    * Info about the extra properties we provide
    * @return an array of property descriptors
    */
    @Override
    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            // The header property
            PropertyDescriptor header = new PropertyDescriptor("header", TextViewPanel.class, "getHeader", "setHeader"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            header.setDisplayName("Header"); //$NON-NLS-1$
            header.setShortDescription("Header"); //$NON-NLS-1$
            header.setBound(true);

            // The main text property
            PropertyDescriptor text = new PropertyDescriptor("text", TextViewPanel.class, "getText", "setText"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            text.setDisplayName("Text"); //$NON-NLS-1$
            text.setShortDescription("Text"); //$NON-NLS-1$
            text.setBound(true);

            return new PropertyDescriptor[] { header, text, };
        }
        catch (IntrospectionException ex)
        {
            log.info("Failure", ex); //$NON-NLS-1$
            return null;
        }
    }

    /**
    * Get additional information from the superclass, in this case JPanel
    */
    @Override
    public BeanInfo[] getAdditionalBeanInfo()
    {
        Class superclass = TextViewPanel.class.getSuperclass();
        try
        {
            BeanInfo superBeanInfo = Introspector.getBeanInfo(superclass);
            return new BeanInfo[] { superBeanInfo };
        }
        catch (IntrospectionException ex)
        {
            log.info("Failure", ex); //$NON-NLS-1$
            return null;
        }
    }

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(TextViewPanelBeanInfo.class);
}
