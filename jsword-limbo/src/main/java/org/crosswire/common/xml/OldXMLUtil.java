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
 * The XMLUtil class does general stuff that I need in various places to do with
 * XML.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
public class OldXMLUtil {
    /**
     * Basic constructor
     */
    private OldXMLUtil() {
    }

    /**
     * Display a Document in debug mode to the specified writer
     * 
     * @param doc
     *            The Document to write
     */
    public static void logDocument(Document doc) {
        Node node = doc.getDocumentElement();

        recurseNodes(node, null, 0);
    }

    /**
     * Display a Document in debug mode to the specified writer
     */
    public static void logDocument(Element start) {
        recurseNodes(start, null, 0);
    }

    /**
     * Display a Document in debug mode to the specified writer
     * 
     * @param doc
     *            The Document to write
     * @param out
     *            The stream to write to
     */
    public static void printDocument(Document doc, PrintWriter out) {
        Node node = doc.getDocumentElement();

        recurseNodes(node, out, 0);
    }

    /**
     * Display a Document in debug mode to the specified writer
     * 
     * @param start
     *            The Element to start writing at
     * @param out
     *            The stream to write to
     */
    public static void printDocument(Element start, PrintWriter out) {
        recurseNodes(start, out, 0);
    }

    /**
     * Recurse down a Doument node tree
     * 
     * @param node
     *            The node to dig into
     * @param out
     *            The place to write the text that we find
     * @param depth
     *            How far down have we gone?
     */
    private static void recurseNodes(Node node, PrintWriter out, int depth) {
        StringBuilder buff = new StringBuilder();

        switch (node.getNodeType()) {
        case Node.TEXT_NODE:
            String text = node.getNodeValue().trim();
            if (text.length() != 0) {
                buff.append(DOT_PADDING.substring(0, depth * 2));
                buff.append(text);
            }
            break;

        case Node.CDATA_SECTION_NODE:
            buff.append(DOT_PADDING.substring(0, depth * 2));
            buff.append("<![CDATA[");
            buff.append(node.getNodeValue());
            buff.append("]]>");
            break;

        case Node.COMMENT_NODE:
            buff.append(DOT_PADDING.substring(0, depth * 2));
            buff.append("<!-- ");
            buff.append(node.getNodeValue());
            buff.append(" -->");
            break;

        case Node.ELEMENT_NODE:
            buff.append(DOT_PADDING.substring(0, depth * 2));
            buff.append('<');
            buff.append(node.getNodeName());

            // The attributes
            NamedNodeMap map = node.getAttributes();
            if (map != null) {
                for (int i = 0; i < map.getLength(); i++) {
                    buff.append(SPACE);
                    buff.append(map.item(i).getNodeName());
                    buff.append("='");
                    buff.append(map.item(i).getNodeValue());
                    buff.append('\'');
                }
            }

            // Children
            NodeList list = node.getChildNodes();

            if (list == null || list.getLength() == 0) {
                buff.append("/>");
                buff.append(StringUtil.NEWLINE);
            } else {
                buff.append('>');
                buff.append(StringUtil.NEWLINE);

                for (int i = 0; i < list.getLength(); i++) {
                    recurseNodes(list.item(i), out, depth + 1);
                }

                buff.append(DOT_PADDING.substring(0, depth * 2));
                buff.append("</");
                buff.append(node.getNodeName());
                buff.append('>');
                buff.append(StringUtil.NEWLINE);
            }
            break;

        default:
            buff.append(SPACE_PADDING.substring(0, depth * 2));
            buff.append("Not sure what to do with node of type ");
            buff.append(node.getNodeType());
        }

        if (out != null) {
            out.println(buff.toString());
            out.flush();
        } else {
            log.info(buff.toString());
        }
    }

    private static final String SPACE_PADDING = "                                                                ";
    private static final String DOT_PADDING = "..................................................................";
    private static final String SPACE = " ";

    /**
     * The log stream
     */
    protected static Logger log = Logger.getLogger(XMLUtil.class);
}
