package com.nettrack.server;

import com.nettrack.model.NodeStatus;
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
public class NodeService {
    private static final Logger LOG = LoggerFactory.getLogger(NodeService.class);

    private Map<String, NodeStatus> NodeStatusMap = new HashMap<>();

    public void process(NodeStatus nodeStatus) {
        NodeStatusMap.put(nodeStatus.getBaseAddress(), nodeStatus);
    }

    public void trackerUpdate(TrackerStatus trackerStatus) {
        final NodeStatus nodeStatus = NodeStatusMap.get(trackerStatus.getBaseAddress());
        if(nodeStatus != null) {
            nodeStatus.addTracker(trackerStatus);
        }
    }


    public Set<NodeStatus> getNodeStatusMap() {
        return NodeStatusMap.values().stream().collect(Collectors.toSet());
    }
}
