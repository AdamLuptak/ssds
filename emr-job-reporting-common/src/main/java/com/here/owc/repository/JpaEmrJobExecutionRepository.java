package com.here.owc.repository;

import com.here.owc.model.EmrJobExecution;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("jpaEmrJobExecutionRepository")
public interface JpaEmrJobExecutionRepository extends CrudRepository<EmrJobExecution, Long>, EmrJobExecutionRepository {
    EmrJobExecution findByClusterId(String clusterId);

    EmrJobExecution findByJobName(String jobName);

    List<EmrJobExecution> findByJobStatus(String jobStatus);

    List<EmrJobExecution> findByJobStatusNot(List<String> jobStatusList);

}
