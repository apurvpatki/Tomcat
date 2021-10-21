import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EchoServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(EchoServlet.class);

    @Override
    public void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LOG.info("Example servlet code");
    }
}
