package com.practice.tomcat;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.practice.tomcat.sse.ServerSentEventServlet;
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
    private static final int THREADS = 10;
    private final Tomcat tomcat;
    private Executor executor = null;
    private static final Logger LOG = LogManager.getLogger(TomcatServer.class);

    public TomcatServer() {
        tomcat = new Tomcat();
        executor = new ThreadPoolExecutor(THREADS, THREADS,
                0, TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("tomcat-%d")
                        .setDaemon(true).build());
    }

    public void startServer() {
        StandardHost host = (StandardHost) tomcat.getHost();
        host.setErrorReportValveClass(null);

        Context ctx = tomcat.addContext("", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "sse", new ServerSentEventServlet());

        ctx.addServletMappingDecoded("/sse", "sse");

        Connector connector = new Connector(new Http11NioProtocol());
        connector.setProperty("address", "0.0.0.0");
        connector.setPort(9003);
        connector.setProperty("maxHttpHeaderSize", "16384");
        connector.setProperty("connectionTimeout", "50");
        connector.setProperty("maxKeepAliveRequests", "-1");
        connector.setProperty("socket.soLingerOn", "false");
        connector.setEncodedSolidusHandling("passthrough");


        connector.getProtocolHandler().setExecutor(executor);

        tomcat.getService().addConnector(connector);

        try {
            tomcat.start();
        } catch(LifecycleException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void stopServer() throws LifecycleException {
        tomcat.stop();
        tomcat.destroy();
    }

    public static void main(String[] args) throws LifecycleException {
        TomcatServer server = new TomcatServer();
        server.startServer();
    }

}
