package com.nettrack.server;

import com.nettrack.model.LocatorStatus;
import com.nettrack.model.TrackerStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by sg on 26/12/16.
 */
@Service
public class LocatorService {
    private static final Logger LOG = LoggerFactory.getLogger(LocatorService.class);

    private Map<String, LocatorStatus> locatorStatuses = new HashMap<>();

    public void process(LocatorStatus locatorStatus) {
        locatorStatuses.put(locatorStatus.getBaseAddress(), locatorStatus);
    }

    public void trackerUpdate(TrackerStatus trackerStatus) {
        final LocatorStatus locatorStatus = locatorStatuses.get(trackerStatus.getBaseAddress());
        if(locatorStatus != null) {
            locatorStatus.addTracker(trackerStatus);
        }
    }


    public Set<LocatorStatus> getLocatorStatuses() {
        return locatorStatuses.values().stream().collect(Collectors.toSet());
    }
}
