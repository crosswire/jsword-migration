package org.crosswire.jsword.view.web;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crosswire.common.util.Logger;
import org.crosswire.common.xml.SAXEventProvider;
import org.crosswire.common.xml.XMLUtil;
import org.crosswire.jsword.book.Book;
import org.crosswire.jsword.book.BookData;
import org.crosswire.jsword.book.Books;
import org.crosswire.jsword.book.search.parse.IndexSearcher;
import org.crosswire.jsword.book.search.parse.PhraseParamWord;
import org.crosswire.jsword.passage.Key;
import org.crosswire.jsword.passage.Passage;
import org.crosswire.jsword.passage.PassageTally;
import org.crosswire.jsword.passage.RestrictionType;

/**
 * A quick demo of how easy it is to write new front-ends to JSword.
 * 
 * @see gnu.gpl.License for license details.
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class DemoServlet extends HttpServlet
{
    /**
     * @see javax.servlet.Servlet#init(ServletConfig)
     */
    /* @Override */
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        try
        {
            String bookname = config.getInitParameter("book-name"); //$NON-NLS-1$
            book = Books.installed().getBook(bookname);
        }
        catch (Exception ex)
        {
            throw new ServletException(Msg.INIT_FAILED.toString(), ex);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    /* @Override */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            Key key = null;

            String search = request.getParameter(FIELD_SEARCH);
            if (search != null)
            {
                request.setAttribute(FIELD_SEARCH, search);
                key = book.find(search);
            }

            String match = request.getParameter(FIELD_MATCH);
            if (match != null)
            {
                request.setAttribute(FIELD_MATCH, match);
                String quote = IndexSearcher.getPreferredSyntax(PhraseParamWord.class);
                PassageTally tally = (PassageTally) book.find(quote + match + quote);
                tally.setOrdering(PassageTally.ORDER_TALLY);
                tally.trimRanges(tallyTrim, RestrictionType.NONE);
                key = tally;
            }

            String view = request.getParameter(FIELD_VIEW);
            if (view != null)
            {
                request.setAttribute(FIELD_VIEW, view);
                key = book.getKey(view);
            }

            if (key instanceof Passage)
            {
                Passage ref = (Passage) key;

                // Do we need multiple pages
                if (ref.countVerses() > pageSize)
                {
                    Passage waiting = ref.trimVerses(pageSize);

                    // JDK: A deprecation error if you don't, won't build or run on java < 1.4 if you do.
                    //String link = URLEncoder.encode(waiting.getName());
                    String link = URLEncoder.encode(waiting.getName(), "UTF-8"); //$NON-NLS-1$

                    request.setAttribute("next-link", link); //$NON-NLS-1$
                    request.setAttribute("next-name", waiting.getName()); //$NON-NLS-1$
                    request.setAttribute("next-overview", waiting.getOverview()); //$NON-NLS-1$
                }

                BookData data = book.getText(ref);
                SAXEventProvider osissep = data.getSAXEventProvider();
                SAXEventProvider htmlsep = style.convert(osissep);
                String text = XMLUtil.writeToString(htmlsep);

                request.setAttribute("reply", text); //$NON-NLS-1$
            }
        }
        catch (Exception ex)
        {
            log.error("Failed view", ex); //$NON-NLS-1$
            throw new ServletException("Failed view", ex); //$NON-NLS-1$
        }

        getServletContext().getRequestDispatcher("/demo.jsp").forward(request, response); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    /* @Override */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    private static int tallyTrim = 50;
    private static int pageSize = 150;
    private Book book;
    private SimpleWebConverter style = new SimpleWebConverter();

    private static final String FIELD_VIEW = "view"; //$NON-NLS-1$
    private static final String FIELD_MATCH = "match"; //$NON-NLS-1$
    private static final String FIELD_SEARCH = "search"; //$NON-NLS-1$

    /**
     * The log stream
     */
    private static final Logger log = Logger.getLogger(DemoServlet.class);

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3257006549032777012L;
}
