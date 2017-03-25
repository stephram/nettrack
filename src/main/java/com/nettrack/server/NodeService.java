package com.nettrack.server;

import com.nettrack.model.NodeStatus;
import com.nettrack.model.TrackerStatus;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sg on 26/12/16.
 */
@Service
public class NodeService {
    private static final Logger LOG = LoggerFactory.getLogger(NodeService.class);

    @Autowired
    private NodeStates nodeStates;

    public NodeService() {
        LOG.info(toString());
    }

    public void process(NodeStatus nodeStatus) {
        nodeStates.updateNodeState(nodeStatus);
    }

    public void trackerUpdate(TrackerStatus trackerStatus) {
        final NodeStatus nodeStatus = nodeStates.getNodeStates().get(trackerStatus.getBaseAddress());
        if(nodeStatus != null) {
            nodeStatus.addTracker(trackerStatus);
        }
        else {
            LOG.error("Missing: " + trackerStatus.getBaseAddress());
        }
    }

    public Set<NodeStatus> getNodeStates() {
        return nodeStates.getNodeStates().values().stream().collect(Collectors.toSet());
    }
}
