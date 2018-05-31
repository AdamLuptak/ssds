package com.here.owc.repository;

import com.here.owc.model.EmrJobExecution;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EmrJobExecutionRepository {
    EmrJobExecution findByClusterId(String clusterId);

    EmrJobExecution findByJobName(String jobName);

    List<EmrJobExecution> findByJobStatus(String jobStatus);

    List<EmrJobExecution> findByJobStatusNotIn(List<String> jobStatusList);

    EmrJobExecution save(EmrJobExecution emrJobExecution);
}
