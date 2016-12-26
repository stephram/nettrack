package com.nettrack.server;

import com.nettrack.model.LocatorStatus;
import com.nettrack.model.TrackerStatus;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sg on 26/12/16.
 */
public class LocatorRoute extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(LocatorRoute.class);

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void configure() throws Exception {
        from("netty:tcp://0.0.0.0:8080?sync=false&allowDefaultCodec=false&encoder=#stringEncoder&decoder=#stringDecoder")
            .onException(Exception.class)
                .process(exchange -> LOG.error("Error while receiving Locator message.",
                exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
                .handled(true)
            .end()
            .choice()
                .when(body(String.class).startsWith("{\"BA\""))
                    .unmarshal().json(JsonLibrary.Jackson, LocatorStatus.class)
                    .to("bean:locatorService")
                .when(body(String.class).startsWith("{\"CA\""))
                    .unmarshal().json(JsonLibrary.Jackson, TrackerStatus.class)
                    .to("bean:locatorService")
                .otherwise()
                    .to("bean:locatorService")
            .endChoice()
        .end();
    }
}
