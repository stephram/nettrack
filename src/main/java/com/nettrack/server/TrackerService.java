package com.nettrack.server;

import com.nettrack.model.TrackerStatus;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sg on 29/12/16.
 */
@Service
public class TrackerService {
    private static final Logger LOG = LoggerFactory.getLogger(TrackerService.class);

    @Autowired
    private NodeService nodeService;

    private Map<String, TrackerStatus> trackerStatuses = new HashMap<>();

    public void process(TrackerStatus trackerStatus) {
        trackerStatuses.put(trackerStatus.getCardAddress(), trackerStatus);

        nodeService.trackerUpdate(trackerStatus);
    }

    public Set<TrackerStatus> getTrackerStatuses() {
        return trackerStatuses.values().stream().collect(Collectors.toSet());
    }
}
