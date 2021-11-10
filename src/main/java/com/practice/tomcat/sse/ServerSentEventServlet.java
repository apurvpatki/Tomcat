package com.practice.tomcat.sse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

public class ServerSentEventServlet extends HttpServlet {
    private static final Logger LOG = LogManager.getLogger(ServerSentEventServlet.class);

    @Override
    public void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LOG.info("the request is {}", servletRequest);
        servletResponse.setContentType("text/event-stream");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.setHeader("Cache-Control", "no-cache");

        PrintWriter writer = null;
        while(true) {
            try {
                LOG.info("Sending data ......");
                double randomWait = Math.random() * 10000;
                writer = servletResponse.getWriter();
                writer.print("data: " + " next event in " + Math.round(randomWait/1000) + " seconds\n");
                writer.print("data: " + " Event Time: " + Calendar.getInstance().getTime() + " \n\n");
                writer.flush();
                LOG.info("Sleeping for {}", randomWait);
                Thread.sleep((long) randomWait);
            } catch (IOException  e) {
                writer.close();
                throw new RuntimeException("IOException during writing the resposne ");
            } catch (InterruptedException e)  {
                Thread.currentThread().interrupt();
                writer.close();
            }
        }
    }
}
