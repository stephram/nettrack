package com.nettrack.server;

import com.nettrack.model.LocatorStatus;
import com.nettrack.model.TrackerStatus;
import java.util.Set;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.websocket.WebsocketConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;

/**
 * Created by sg on 26/12/16.
 */
//@Component
public class LocatorRoute extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(LocatorRoute.class);

    @Autowired
    private HealthEndpoint health;

    @Autowired
    private MetricsEndpoint metrics;

    @Autowired
    private LocatorService locatorService;

    @Autowired
    private TrackerService trackerService;

    @Autowired
    private InfoEndpoint info;

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void configure() throws Exception {
        from("netty:tcp://0.0.0.0:8080?sync=false&allowDefaultCodec=false&encoder=#stringEncoder&decoder=#stringDecoder")
            .routeId("locatorRoute")
            .onException(Exception.class)
                .process(exchange -> LOG.error("Error while receiving Locator message.",
                    exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
                .handled(true)
            .end()
            .log("${body}")
            .choice()
                .when(bodyAs(String.class).startsWith("{\"BA\""))
                    .unmarshal().json(JsonLibrary.Jackson, LocatorStatus.class)
                    .to("bean:locatorService")
                    .setHeader(WebsocketConstants.SEND_TO_ALL, constant(true))
                    .process(exchange -> exchange.getIn().setBody(locatorService.getLocatorStatuses()))
                    .marshal().json(JsonLibrary.Jackson, Set.class)
                    .to("websocket://locators")
                .when(bodyAs(String.class).startsWith("{\"CA\""))
                    .unmarshal().json(JsonLibrary.Jackson, TrackerStatus.class)
                    .to("bean:trackerService")
                    .setHeader(WebsocketConstants.SEND_TO_ALL, constant(true))
                    .process(exchange -> exchange.getIn().setBody(trackerService.getTrackerStatuses()))
                    .marshal().json(JsonLibrary.Jackson, Set.class)
                    .to("websocket://trackers")
                .otherwise()
                    .log("Unhandled message: ${body}")
            .endChoice()
        .end();

        from("timer:status")
            .routeId("endpoints")
            .bean(health, "invoke")
            .bean(metrics, "invoke")
        .end();

        from("websocket://trackers")
            .routeId("trackers")
            .onException(Exception.class)
                .process(exchange -> LOG.error("Error in trackers endpoint",
                    exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
                .handled(true)
            .end()
            .process(exchange -> exchange.getIn().setBody(trackerService.getTrackerStatuses()))
            .marshal().json(JsonLibrary.Jackson, Set.class)
            .log("trackers websocket >>> ${body}")
            .to("websocket://trackers")
        .end();

        from("websocket://locators")
            .routeId("locators")
            .onException(Exception.class)
                .process(exchange -> LOG.error("Error in locators endpoint",
                    exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
                .handled(true)
            .end()
            .process(exchange -> exchange.getIn().setBody(locatorService.getLocatorStatuses()))
            .marshal().json(JsonLibrary.Jackson, Set.class)
            .log("${body}")
            .to("websocket://locators")
        .end();
    }
}
