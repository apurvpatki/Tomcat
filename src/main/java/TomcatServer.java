import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TomcatServer {
    private static final int THREADS = 5;
    private final Tomcat tomcat;
    private static final Logger LOG = LogManager.getLogger(TomcatServer.class);

    public TomcatServer() {
        tomcat = new Tomcat();

    }

    public void startServer() {
        StandardHost host = (StandardHost) tomcat.getHost();
        host.setErrorReportValveClass(null);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        ctx.addServletMappingDecoded("/*", "Echo");
        Tomcat.addServlet(ctx, "Echo", new EchoServlet());

        Connector connector = new Connector(new Http11NioProtocol());
        connector.setProperty("address", "0.0.0.0");
        connector.setPort(9003);
        connector.setProperty("maxHttpHeaderSize", "16384");
        connector.setProperty("connectionTimeout", "50");
        connector.setProperty("maxKeepAliveRequests", "-1");
        connector.setProperty("socket.soLingerOn", "false");
        connector.setEncodedSolidusHandling("passthrough");

        final Executor executor = new ThreadPoolExecutor(THREADS, THREADS,
                0, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("tomcat-%d")
                        .setDaemon(true).build());
        connector.getProtocolHandler().setExecutor(executor);

        tomcat.getService().addConnector(connector);

        try {
            tomcat.start();
        } catch(LifecycleException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static void main(String[] args) throws LifecycleException {
        TomcatServer server = new TomcatServer();
        server.startServer();
    }

}
