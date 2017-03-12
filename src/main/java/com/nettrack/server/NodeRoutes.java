package com.nettrack.server;

import com.nettrack.model.NodeStatus;
import com.nettrack.model.TrackerStatus;
import org.apache.camel.Exchange;
import org.apache.camel.component.websocket.WebsocketConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.boot.FatJarRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sg on 12-Mar-17.
 */
@SpringBootApplication
public class NodeRoutes extends FatJarRouter {
  private static final Logger LOG = LoggerFactory.getLogger(NodeRoutes.class);

  @Autowired
  private HealthEndpoint health;

  @Autowired
  private MetricsEndpoint metrics;

  @Autowired
  private InfoEndpoint info;

  @Autowired
  private EnvironmentEndpoint environmentEndpoint;

  @Autowired
  private BeansEndpoint beansEndpoint;

  @Autowired
  private DumpEndpoint dumpEndpoint;

  @Autowired
  private NodeService nodeService;

  @Autowired
  private TrackerService trackerService;

  @Override
  public void configure() throws Exception {
    //from("netty:tcp://0.0.0.0:8081?sync=false&allowDefaultCodec=false&encoder=#stringEncoder&decoder=#stringDecoder")
    from("netty:tcp://0.0.0.0:8081?sync=false&allowDefaultCodec=false")
      .routeId("locatorRoute")
      .onException(Exception.class)
        .process(exchange -> LOG.error("Error while receiving Locator message.",
          exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
        .handled(true)
      .end()
      .choice()
        .when(bodyAs(String.class).startsWith("{\"BA\""))
          .unmarshal().json(JsonLibrary.Jackson, NodeStatus.class)
          .to("bean:locatorService")
          .setHeader(WebsocketConstants.SEND_TO_ALL, constant(true))
          .process(exchange -> exchange.getIn().setBody(nodeService.getNodeStatusMap()))
          .marshal().json(JsonLibrary.Jackson, Set.class)
          .to("websocket://nodes")
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

    from("timer:nodeStatus")
      .routeId("nodeStatus")
      .process(exchange -> {
        LOG.info("");
        final List<NodeStatus> nodeStatusList = nodeService.getNodeStatusMap().stream().collect(Collectors.toList());
        nodeStatusList.sort((ns1, ns2) -> ns1.getBaseStationCode().compareTo(ns2.getBaseStationCode()));
        nodeStatusList.forEach(nodeStatus -> {
          LOG.info(nodeStatus.getBaseAddress());
          nodeStatus.getTrackers().forEach((trackerAddress, trackerStatus) -> {
            if(Instant.now().minusSeconds(30).isBefore(trackerStatus.getTimestamp())) {
              LOG.info(String.format("    CA: %s, RSSI: %s, TS: %s, BATT: %s",
                trackerStatus.getCardAddress(),
                trackerStatus.getSignalStrength(),
                trackerStatus.getTimestamp(),
                trackerStatus.getBatteryPercentage()));
            }
          });
        });
      })
      .end();

    from("timer:status?fixedRate=true&period=30000")
      .routeId("status")
      .bean(health, "invoke")
      .log("/health : ${body}")
      .bean(metrics, "invoke")
      .log("/metrics : ${body}")
      .bean(info, "invoke")
      .log("/info : ${body}")
      .bean(environmentEndpoint, "invoke")
      .log("/environment : ${body}")
      .bean(beansEndpoint, "invoke")
      .log("/beans : ${body}")
//      .bean(dumpEndpoint, "invoke")
//      .log("/dump : ${body}")
    .end();

    from("websocket://trackers")
      .routeId("trackers")
      .onException(Exception.class)
        .process(exchange -> LOG.error("Error in " + exchange.getFromEndpoint().getEndpointUri(),
          exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
        .handled(true)
      .end()
      .process(exchange -> exchange.getIn().setBody(trackerService.getTrackerStatuses()))
      .marshal().json(JsonLibrary.Jackson, Set.class)
      .log("${body}")
      .to("websocket://trackers")
    .end();

    from("websocket://nodes")
      .routeId("nodes")
      .onException(Exception.class)
        .process(exchange -> LOG.error("Error in " + exchange.getFromEndpoint().getEndpointUri(),
          exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
        .handled(true)
      .end()
      .process(exchange -> exchange.getIn().setBody(nodeService.getNodeStatusMap()))
      .marshal().json(JsonLibrary.Jackson, Set.class)
      .log("${body}")
      .to("websocket://nodes")
    .end();
  }
}
