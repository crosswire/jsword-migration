package org.crosswire.jsword.view.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.crosswire.common.util.Reporter;
import org.crosswire.jsword.book.remote.Converter;
import org.crosswire.jsword.book.remote.HttpRemoter;
import org.crosswire.jsword.book.remote.LocalRemoter;
import org.crosswire.jsword.book.remote.MethodName;
import org.crosswire.jsword.book.remote.ParamName;
import org.crosswire.jsword.book.remote.RemoteMethod;
import org.crosswire.jsword.book.remote.Remoter;
import org.crosswire.jsword.book.remote.RemoterException;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;

/**
 * A quick demo of how easy it is to write new front-ends to JSword.
 * 
 * @see gnu.gpl.License for license details.<br>
 *      The copyright to this program is held by it's authors.
 * @author Joe Walker [joe at eireneh dot com]
 */
public class RemoterServlet extends HttpServlet {
    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        remoter = new LocalRemoter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        XMLOutputter output = new XMLOutputter();

        try {
            RemoteMethod method = requestToMethod(request);
            Document doc = remoter.execute(method);

            output.output(doc, response.getOutputStream());
        } catch (RemoterException ex) {
            Reporter.informUser(this, ex);

            Document doc = Converter.convertExceptionToDocument(ex);
            output.output(doc, response.getOutputStream());
        }
    }

    /**
     * Convert an HttpServletRequest into a RemoteMethod call. This is the
     * inverse of
     * {@link org.crosswire.jsword.book.remote.HttpRemoter#execute(RemoteMethod)}
     */
    public static RemoteMethod requestToMethod(HttpServletRequest request) {
        Map params = request.getParameterMap();
        String methodname = request.getParameter(HttpRemoter.METHOD_KEY);
        RemoteMethod method = new RemoteMethod(MethodName.fromString(methodname));

        Iterator it = params.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String[] val = (String[]) params.get(key);
            ParamName param = ParamName.fromString(key);

            // This is slightly dodgy - we basically ignore the fact that HTTP
            // GET and POST allow multiple values for each key, however since we
            // get to define the interface i.e. what the allowed keys are, I
            // don't see this as a big problem.
            if (val.length > 0 && param != null) {
                method.addParam(param, val[0]);
            }
        }

        return method;
    }

    /**
     * The way we answer any questions asked of us.
     */
    private Remoter remoter;

    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 3256442491094773810L;
}
