package com.nettrack.server;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Created by sgraham on 21/4/17.
 */
@SpringBootApplication
public class App {

    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        final ApplicationContext applicationContext = SpringApplication.run(App.class, args);
        LOG.info(String.format("Started '%s', Id: '%s'.\nActive profiles: %s\nDefault profiles: %s.",
            applicationContext.getId(),
            applicationContext.getDisplayName(),
            JSONObject.valueToString(applicationContext.getEnvironment().getActiveProfiles()),
            JSONObject.valueToString(applicationContext.getEnvironment().getDefaultProfiles())));
    }
}
