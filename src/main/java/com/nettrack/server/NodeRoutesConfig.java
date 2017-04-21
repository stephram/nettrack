package com.nettrack.server;

import com.nettrack.common.utils.JsonConfig;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.spring.spi.SpringTransactionPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.connection.JmsTransactionManager;

/**
 * Created by sgraham on 18/4/17.
 */
@Configuration
@PropertySource({ "classpath:/application.yml" })
@Import({ JsonConfig.class })
public class NodeRoutesConfig {

    @Value("${activemq.broker.url}")
    private String activemqBrokerUrl;

    public String getActivemqBrokerUrl() {
        return activemqBrokerUrl;
    }

    @Bean(name = "activeMq")
    public ActiveMQComponent activeMq() {
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(activeMqConnectionFactory());
        return activeMQComponent;
    }

    @Bean(name = "activeMqConnectionFactory")
    public ConnectionFactory activeMqConnectionFactory() {
        return new ActiveMQConnectionFactory(getActivemqBrokerUrl());
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
}
