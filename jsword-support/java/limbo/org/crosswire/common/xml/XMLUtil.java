package org.crosswire.common.xml;

import java.io.PrintWriter;

import org.crosswire.common.util.Logger;
import org.crosswire.common.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * The XMLUtil class does general stuff that I need in various places
 * to do with XML.
 *
 * <table border='1' cellPadding='3' cellSpacing='0' width="100%">
 * <tr><td bgColor='white'class='TableRowColor'><font size='-7'>
 * Distribution Licence:<br />
 * Project B is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License,
 * version 2 as published by the Free Software Foundation.<br />
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.<br />
 * The License is available on the internet
 * <a href='http://www.gnu.org/copyleft/gpl.html'>here</a>, by writing to
 * <i>Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
 * MA 02111-1307, USA</i>, Or locally at the Licence link below.<br />
 * The copyright to this program is held by it's authors.
 * </font></td></tr></table>
 * @see <a href='http://www.eireneh.com/servlets/Web'>Project B Home</a>
 * @see <{docs.Licence}>
 * @author Joe Walker
 * @version D0.I0.T0
 */
public class XMLUtil
{
    /**
     * Basic constructor
     */
    private XMLUtil()
    {
    }

    /**
     * Display a Document in debug mode to the specified writer
     * @param doc The Document to write
     */
    public static void logDocument(Document doc)
    {
        Node node = doc.getDocumentElement();

        recurseNodes(node, null, 0);
    }

    /**
     * Display a Document in debug mode to the specified writer
     */
    public static void logDocument(Element start)
    {
        recurseNodes(start, null, 0);
    }

    /**
     * Display a Document in debug mode to the specified writer
     * @param doc The Document to write
     * @param out The stream to write to
     */
    public static void printDocument(Document doc, PrintWriter out)
    {
        Node node = doc.getDocumentElement();

        recurseNodes(node, out, 0);
    }

    /**
     * Display a Document in debug mode to the specified writer
     * @param start The Element to start writing at
     * @param out The stream to write to
     */
    public static void printDocument(Element start, PrintWriter out)
    {
        recurseNodes(start, out, 0);
    }

    /**
     * Recurse down a Doument node tree
     * @param node The node to dig into
     * @param out The place to write the text that we find
     * @param depth How far down have we gone?
     */
    private static void recurseNodes(Node node, PrintWriter out, int depth)
    {
        StringBuffer buff = new StringBuffer();

        switch (node.getNodeType())
        {
        case Node.TEXT_NODE:
            String text = node.getNodeValue().trim();
            if (text.length() != 0)
            {
                buff.append(DOT_PADDING.substring(0, depth*2));
                buff.append(text);
            }
            break;

        case Node.CDATA_SECTION_NODE:
            buff.append(DOT_PADDING.substring(0, depth*2));
            buff.append("<![CDATA["); //$NON-NLS-1$
            buff.append(node.getNodeValue());
            buff.append("]]>"); //$NON-NLS-1$
            break;

        case Node.COMMENT_NODE:
            buff.append(DOT_PADDING.substring(0, depth*2));
            buff.append("<!-- "); //$NON-NLS-1$
            buff.append(node.getNodeValue());
            buff.append(" -->"); //$NON-NLS-1$
            break;

        case Node.ELEMENT_NODE:
            buff.append(DOT_PADDING.substring(0, depth*2));
            buff.append("<"); //$NON-NLS-1$
            buff.append(node.getNodeName());

            // The attributes
            NamedNodeMap map = node.getAttributes();
            if (map != null)
            {
                for (int i=0; i<map.getLength(); i++)
                {
                    buff.append(SPACE);
                    buff.append(map.item(i).getNodeName());
                    buff.append("='"); //$NON-NLS-1$
                    buff.append(map.item(i).getNodeValue());
                    buff.append("'"); //$NON-NLS-1$
                }
            }

            // Children
            NodeList list = node.getChildNodes();

            if (list == null || list.getLength() == 0)
            {
                buff.append("/>"); //$NON-NLS-1$
                buff.append(StringUtil.NEWLINE);
            }
            else
            {
                buff.append(">"); //$NON-NLS-1$
                buff.append(StringUtil.NEWLINE);

                for (int i=0; i<list.getLength(); i++)
                {
                    recurseNodes(list.item(i), out, depth+1);
                }

                buff.append(DOT_PADDING.substring(0, depth*2));
                buff.append("</"); //$NON-NLS-1$
                buff.append(node.getNodeName());
                buff.append(">"); //$NON-NLS-1$
                buff.append(StringUtil.NEWLINE);
            }
            break;

        default:
            buff.append(SPACE_PADDING.substring(0, depth*2));
            buff.append("Not sure what to do with node of type "); //$NON-NLS-1$
            buff.append(node.getNodeType());
        }

        if (out != null)
        {
            out.println(buff.toString());
            out.flush();
        }
        else
        {
            log.info(buff.toString());
        }
    }

    private static final String SPACE_PADDING = "                                                                "; //$NON-NLS-1$
    private static final String DOT_PADDING = ".................................................................."; //$NON-NLS-1$
    private static final String SPACE = " "; //$NON-NLS-1$
    private static final String DOT = "."; //$NON-NLS-1$

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(XMLUtil.class);
}