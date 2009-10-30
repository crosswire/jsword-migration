package org.crosswire.jsword.view.web;

import java.net.URL;
import java.util.MissingResourceException;

import javax.xml.transform.TransformerException;

import org.crosswire.common.util.NetUtil;
import org.crosswire.common.util.ResourceUtil;
import org.crosswire.common.xml.Converter;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.TransformingSAXEventProvider;

/**
 * Turn XML from a Bible into HTML according to a Display style.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class SimpleWebConverter implements Converter {
    /*
     * (non-Javadoc)
     * 
     * @seeorg.crosswire.common.xml.Converter#convert(org.crosswire.common.xml.
     * SAXEventProvider)
     */
    public SAXEventProvider convert(SAXEventProvider xmlsep) throws TransformerException {
        try {
            URL xslurl = ResourceUtil.getResource("xsl/web/simple.xsl"); //$NON-NLS-1$

            // We used to do:
            // transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
            // "yes");
            // however for various reasons, now we don't but nothing seems to be
            // broken ...
            return new TransformingSAXEventProvider(NetUtil.toURI(xslurl), xmlsep);
        } catch (MissingResourceException ex) {
            throw new TransformerException(ex);
        }
    }
}
