package com.practice.tomcat.sse;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import com.practice.tomcat.TomcatServer;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ServerSentEventIntegrationTest {

    private static final TomcatServer SERVER = new TomcatServer();

    @BeforeAll
    public static void init() {
        SERVER.startServer();
    }

    @AfterAll
    public static void destroy() throws LifecycleException {
        SERVER.stopServer();
    }

    @Test
    public void testSSE() {
        EventHandler eventHandler = new SSEEventHandler();
        String url = "http://localhost:9003/sse";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url))
                .reconnectTime(Duration.ofMillis(3000));

        try (EventSource eventSource = builder.build()) {
            eventSource.start();

            TimeUnit.MINUTES.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException("Test was interrupted");
        }
    }

    public static class SSEEventHandler implements EventHandler {

        @Override
        public void onOpen() throws Exception {
            System.out.println("onOpen");
        }

        @Override
        public void onClosed() throws Exception {
            System.out.println("onClosed");
        }

        @Override
        public void onMessage(String event, MessageEvent messageEvent) throws Exception {
            System.out.println(messageEvent.getData());
        }

        @Override
        public void onComment(String comment) throws Exception {
            System.out.println("onComment");
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("onError: " + t);
        }

    }
}
