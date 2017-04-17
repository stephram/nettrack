package com.nettrack.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nettrack.common.utils.JsonMapperFactory;
import com.nettrack.model.NodeStatus;
import com.nettrack.model.TrackerStatus;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.websocket.WebsocketConstants;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.spring.boot.FatJarRouter;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.BeansEndpoint;
import org.springframework.boot.actuate.endpoint.EnvironmentEndpoint;
import org.springframework.boot.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.connection.JmsTransactionManager;

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

//  @Autowired
//  private DumpEndpoint dumpEndpoint;

//  @Autowired
  private ObjectMapper objectMapper = JsonMapperFactory.objectMapperUnquotedFieldNames();

  @Autowired
  private NodeService nodeService;

  @Autowired
  private TrackerService trackerService;

  @Value("${activemq.broker.url}")
  private String activemqBrokerUrl = "tcp://localhost:61616";

  @Override
  public void configure() throws Exception {

    from("netty:tcp://0.0.0.0:6266?sync=false&allowDefaultCodec=false")
        .routeId("nodesRoute")
        .onException(Exception.class)
        .process(exchange -> LOG.error("Error while receiving Locator message.",
            exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class)))
        .handled(true)
        .end()
        .choice()
        .when(bodyAs(String.class).startsWith("{\"BA\""))
          .unmarshal().json(JsonLibrary.Jackson, NodeStatus.class)
          .to("bean:nodeService")
//          .process(exchange -> logNodeStatus())
//          .setHeader(WebsocketConstants.SEND_TO_ALL, constant(true))
          .process(exchange -> exchange.getIn().setBody(nodeService.getNodeStates()))
//          .marshal().json(JsonLibrary.Jackson, Set.class)
//          .log(body().toString())
//          .to("websocket://nodes")
        .when(bodyAs(String.class).startsWith("{\"CA\""))
          .unmarshal().json(JsonLibrary.Jackson, TrackerStatus.class)
          .to("bean:trackerService")
//          .process(exchange -> logNodeStatus())
          .setHeader(WebsocketConstants.SEND_TO_ALL, constant(true))
          .process(exchange -> exchange.getIn().setBody(trackerService.getTrackerStatuses()))
          .marshal().json(JsonLibrary.Jackson, Set.class)
          .to("direct:multicaster")
        .otherwise()
          .log("Unhandled message: ${body}")
        .endChoice()
        .end();

//    from("websocket://trackers")
//      .routeId("trackers")
//      .log("${body}")
//    .end();

    from("direct:multicaster")
        .routeId("nodesMulticaster")
        .multicast()
            .to("websocket://nodes")
            .to("jms:nettrack:node.nodes")
        .end()
    .end();

    from("websocket://nodes")
        .routeId("websocketNodes")
        .to("direct:null")
    .end();

    from("activemq:nettrack:node.nodes?jmsMessageType=Text")
        .routeId("activemqNodes")
        .unmarshal().json(JsonLibrary.Jackson, Set.class)
        .split(body())
          .process(exchange -> {
            final String json = objectMapper.writeValueAsString(exchange.getIn().getBody());
            final TrackerStatus trackerStatus = objectMapper.readValue(json, TrackerStatus.class);
            LOG.info(String.format("trackerStatus: %s", trackerStatus.toString()));
          })
        .end()
    .end();

//    from("jms:nettrack:node.nodes")
//        .routeId("jmsNodes")
//        //.convertBodyTo(List.class)
//        .process(exchange -> {
////          exchange.getIn(JmsMessage.class).getJmsMessage().
////          List<NodeStatus> nodeStatusList = exchange.getIn().getBody(List.class);
////          LOG.info("jms:" + Objects.toString(nodeStatusList));
//          LOG.info(bodyAs(List.class).toString())
//        })
//        //.log(String.format("jms: %s", body().toString()))
//    .end();

//    from("timer:nodeStatus")
//      .routeId("nodeStatus")
//      .process(exchange -> logNodeStatus())
//    .end();

//    from("timer:status?fixedRate=true&period=30000")
//      .routeId("status")
//      .bean(health, "invoke").log("/health : ${body}")
//      .bean(metrics, "invoke").log("/metrics : ${body}")
//      .bean(info, "invoke").log("/info : ${body}")
//      .bean(environmentEndpoint, "invoke").log("/environment : ${body}")
//      .bean(beansEndpoint, "invoke").log("/beans : ${body}")
////      .bean(dumpEndpoint, "invoke").log("/dump : ${body}")
//    .end();
  }

  @Bean(name = "activeMq")
  public ActiveMQComponent activeMq() {
    ActiveMQComponent activeMQComponent = new ActiveMQComponent();
    activeMQComponent.setConnectionFactory(activeMqConnectionFactory());
    return activeMQComponent;
  }

  @Bean(name = "activeMqConnectionFactory")
  public ConnectionFactory activeMqConnectionFactory() {
    return new ActiveMQConnectionFactory(activemqBrokerUrl);
  }

  @Bean(name = "jmsConfig")
  @DependsOn(value = { "activeMqConnectionFactory", "jmsTransactionManager" })
  public JmsConfiguration jmsConfiguration() {
    JmsConfiguration jmsConfiguration = new JmsConfiguration(activeMqConnectionFactory());
    jmsConfiguration.setTransacted(true);
    jmsConfiguration.setTransactionManager(jmsTransactionManager());
    jmsConfiguration.setCacheLevelName("CACHE_CONNECTION");
    return jmsConfiguration;
  }

  @Bean(name = "jmsTransactionManager")
  @DependsOn(value = { "activeMqConnectionFactory" })
  public JmsTransactionManager jmsTransactionManager() {
    JmsTransactionManager transactionManager = new JmsTransactionManager();
    transactionManager.setConnectionFactory(activeMqConnectionFactory());
    return transactionManager;
  }

  @Bean(name = "jmsTransactionPolicy")
  @DependsOn(value = { "activeMqConnectionFactory" })
  public SpringTransactionPolicy jmsTransactionPolicy() {
    SpringTransactionPolicy springTransactionPolicy = new SpringTransactionPolicy();
    springTransactionPolicy.setTransactionManager(jmsTransactionManager());
    return springTransactionPolicy;
  }

  private void logNodeStatus() {
    final List<NodeStatus> nodeStatusList = nodeService.getNodeStates().stream().collect(Collectors.toList());
    nodeStatusList.sort(Comparator.comparing((NodeStatus ns1) -> ns1.getBaseStationCode())
        .thenComparing(ns2 -> ns2.getBaseStationCode()));

    nodeStatusList.forEach(nodeStatus -> {
      LOG.info(nodeStatus.getBaseAddress());
      nodeStatus.getTrackers().forEach((trackerAddress, trackerStatus) -> {
//        if(Instant.now().minusSeconds(30).isBefore(trackerStatus.getTimestamp())) {
          LOG.info(String.format("    CA: %s, RSSI: %s, TS: %s, BATT: %s",
            trackerStatus.getCardAddress(),
            trackerStatus.getSignalStrength(),
            trackerStatus.getTimestamp(),
            trackerStatus.getBatteryPercentage()));
//        }
      });
    });
  }
}
