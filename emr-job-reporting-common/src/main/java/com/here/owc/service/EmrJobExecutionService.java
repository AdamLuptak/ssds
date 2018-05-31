package com.here.owc.service;

import com.amazonaws.services.sns.AmazonSNS;
import com.here.owc.client.EmrJobReportPublisher;
import com.here.owc.model.EmrJobExecution;
import com.here.owc.repository.EmrJobExecutionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EmrJobExecutionService {

    public static final List<String> NOT_RUNNING_CLUSTER_STATE_LIST = Arrays.asList("TERMINATED",
            "TERMINATED_WITH_ERRORS");
    public List<String> finishedJobStateList = Arrays.asList("TERMINATED", "TERMINATED_WITH_ERRORS");

    @Autowired
    @Qualifier("jpaEmrJobExecutionRepository")
    private EmrJobExecutionRepository emrJobExecutionRepository;

    @Autowired
    private EmrJobReportPublisher emrJobReportPublisher;

    public EmrJobExecution loadByClusterId(final String clusterId) {
        return emrJobExecutionRepository.findByClusterId(clusterId);
    }

    public List<EmrJobExecution> loadRunningEmrJobs() {
        return emrJobExecutionRepository.findByJobStatusNotIn(NOT_RUNNING_CLUSTER_STATE_LIST);
    }

    public EmrJobExecution saveEmrJobExecution(final EmrJobExecution emrJobExecution) {
        return emrJobExecutionRepository.save(emrJobExecution);
    }

    public EmrJobExecution handleEmrJobExecutionStatus(final String clusterId, final String emrJobExecutionStatus)
            throws EmrJobExecutionServiceException {
        EmrJobExecution foundedCluster = emrJobExecutionRepository.findByClusterId(clusterId);
        if (foundedCluster != null) {

            if (isJobFinished(emrJobExecutionStatus)) {
                publishToEmrReportTopic(foundedCluster);
            }

            foundedCluster.setJobStatus(emrJobExecutionStatus);
            emrJobExecutionRepository.save(foundedCluster);
            return foundedCluster;
        } else {
            throw new EmrJobExecutionServiceException("There is no record in database with clusterId: " + clusterId);
        }
    }

    private boolean isJobFinished(String emrJobExecutionStatus) {
        return finishedJobStateList.contains(emrJobExecutionStatus);
    }

    private void publishToEmrReportTopic(EmrJobExecution emrJobExecution) {
        emrJobReportPublisher.publishToReporters(emrJobExecution);
    }

    public class EmrJobExecutionServiceException extends Throwable {
        public EmrJobExecutionServiceException(String message) {
            super(message);
        }
    }
}
