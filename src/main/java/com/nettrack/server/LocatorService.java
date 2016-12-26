package com.nettrack.server;

import com.nettrack.model.LocatorStatus;
import com.nettrack.model.TrackerStatus;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by sg on 26/12/16.
 */
@Service
public class LocatorService {
    private static final Logger LOG = LoggerFactory.getLogger(LocatorService.class);

    public void processMessage(String message) {
        LOG.error("Unsupported message type: " + message);
    }

    public void processLocatorStatus(LocatorStatus locatorStatus) {
        LOG.info(Objects.toString(locatorStatus));
    }

    public void processTrackerStatus(TrackerStatus trackerStatus) {
        LOG.info(Objects.toString(trackerStatus));
    }
}
