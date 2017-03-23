package com.nettrack.server;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sg on 12-Mar-17.
 */
@Configuration
public class NodeRoutesConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    CamelContextConfiguration camelContextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {

            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {

            }
        };
    }

//    @Bean(name = "camelContext")
//    public CamelContext camel() throws Exception {
//        final SpringCamelContext camelContext = SpringCamelContext.springCamelContext(applicationContext, false);
//
//        // Register with Camel context all routes found in the Spring application context.
//        // We need to loop since as of Camel version 2.4, automatic scanning is only possible
//        // to be specified in the xml config.
//
//        final Map<String, SpringRouteBuilder> routeBuilders = applicationContext.getBeansOfType(SpringRouteBuilder.class);
//        for (SpringRouteBuilder routeBuilder : routeBuilders.values()) {
//            camelContext.addRoutes(routeBuilder);
//        }
//        camelContext.setTracing(true);
//        camelContext.start();
//
//        return camelContext;
//    }
}
