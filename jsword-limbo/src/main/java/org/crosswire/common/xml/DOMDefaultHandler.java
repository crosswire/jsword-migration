package org.crosswire.common.xml;

import java.util.Stack;

import org.crosswire.common.util.Reporter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The DOMDefaultHandler class implements the SAX class HandlerBase and from the
 * SAX events generated from a parse, generates a DOM XML document, embedded
 * into another.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker
 */
public class DOMDefaultHandler extends DefaultHandler {
    /**
     * Default constructor. A null base element means we assume that the
     * document is all ours. In this case we do insert PIs into the Document.
     * Otherwise we dont.
     * 
     * @param base
     *            The Element in the document to start at
     */
    public DOMDefaultHandler(Node base) {
        this.doc = base.getOwnerDocument();
        this.base = base;

        if (this.base == null)
            current = doc;
        else
            current = base;
    }

    /**
     * Processing instruction
     */
    @Override
    public void processingInstruction(String target, String data) {
        if (base == null) {
            doc.createProcessingInstruction(target, data);
        }
    }

    /**
     * Start document.
     */
    @Override
    public void startDocument() {
        // TODO(joe): what should I do here?
        // out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(String uri, String name, String qName, Attributes attrs) throws SAXException {
        Element ele = doc.createElement(name);
        current.appendChild(ele);

        stack.push(current);
        current = ele;

        if (attrs != null) {
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                ele.setAttribute(attrs.getLocalName(i), attrs.getValue(i));
            }
        }
    }

    /**
     * Some text data
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        current.appendChild(doc.createTextNode(new String(ch, start, length)));
    }

    /**
     * Ignorable whitespace
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) {
        current.appendChild(doc.createTextNode(new String(ch, start, length)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName) {
        current = (Node) stack.pop();
    }

    /**
     * End document
     */
    @Override
    public void endDocument() {
    }

    /**
     * Warning
     */
    @Override
    public void warning(SAXParseException ex) {
        Reporter.informUser(this, ex);
    }

    /**
     * Error
     */
    @Override
    public void error(SAXParseException ex) {
        Reporter.informUser(this, ex);
    }

    /**
     * Fatal error
     */
    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        Reporter.informUser(this, ex);
        throw ex;
    }

    /*
     * Normalizes the given string
     * 
     * private String normalize(String s) { StringBuilder str = new
     * StringBuilder();
     * 
     * int len = (s != null) ? s.length() : 0; for (int i = 0; i < len; i++) {
     * char ch = s.charAt(i); switch (ch) { case '<': str.append("&lt;"); break;
     * 
     * case '>': str.append("&gt;"); break;
     * 
     * case '&': str.append("&amp;"); break;
     * 
     * case '"': str.append("&quot;"); break;
     * 
     * case '\r': case '\n': if (canonical) { str.append("&#");
     * str.append(Integer.toString(ch)); str.append(';'); } else {
     * str.append(ch); } break;
     * 
     * default: str.append(ch); } }
     * 
     * return str.toString(); }
     * 
     * /** The DOM Document to add to
     */
    private Document doc = null;

    /** The Element to start adding at */
    private Node base = null;

    /** The Element that we are currently adding at */
    private Node current = null;

    /** The stack of elements that we have built on */
    private Stack stack = new Stack();
}
