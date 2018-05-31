package com.here.owc;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.here.owc.service.EmrJobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmrJobStateUpdater {

    static final Logger logger = LoggerFactory.getLogger(EmrJobStateUpdater.class);

    @Autowired
    EmrJobExecutionService emrJobExecutionService;

    public void run(ScheduledEvent scheduledEvent) throws EmrJobExecutionService.EmrJobExecutionServiceException {
        Map<String, Object> detail = scheduledEvent.getDetail();
        String clusterId = (String) detail.get("clusterId");
        String clusterState = (String) detail.get("state");
        logger.info("Updating cluster state for cluster:{} to state:{}", clusterId, clusterState);
        emrJobExecutionService.handleEmrJobExecutionStatus(clusterId, clusterState);
    }

}
