package com.nettrack.server;

import com.nettrack.model.NodeStatus;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Created by sg on 25/3/17.
 */
@Component
public class NodeStates {

    private Map<String, NodeStatus> nodeStates = new HashMap<>();

    public Map<String, NodeStatus> getNodeStates() {
        return Collections.unmodifiableMap(nodeStates);
    }

    public void updateNodeState(NodeStatus nodeStatus) {
        final NodeStatus existingNodeStatus = nodeStates.get(nodeStatus.getBaseAddress());

        if(existingNodeStatus == null) {
            nodeStates.put(nodeStatus.getBaseAddress(), nodeStatus);
        } else {
        }

//        if (nodeStates.containsKey(nodeStatus.getBaseAddress())) {
//            final NodeStatus existingNodesStatus = nodeStates.get(nodeStatus.getBaseAddress());
//            existingNodesStatus.getTrackers().values().forEach(trackerStatus -> {
//                if (!nodeStatus.getTrackers().containsKey(trackerStatus.getCardAddress())) {
//                    nodeStatus.addTracker(trackerStatus);
//                }
//            });
//        } else {
//            nodeStates.put(nodeStatus.getBaseAddress(), nodeStatus);
//        }
    }
}
