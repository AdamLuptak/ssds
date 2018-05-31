package com.here.owc.repository;

import com.here.owc.model.EmrJobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("jdbcEmrJobExecutionRepository")
public class JdbcEmrJobExecutionRepository implements EmrJobExecutionRepository {

    private JdbcOperations jdbcOperations;

    @Autowired
    public JdbcEmrJobExecutionRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    public EmrJobExecution findByClusterId(String clusterId) {
        return jdbcOperations.queryForObject(
                "SELECT execution_id, cluster_id, job_name, sqs_queue_name from emr_job_execution where cluster_id = 'j-1BXE4KRA2NO9X';",
                new BeanPropertyRowMapper<>(EmrJobExecution.class));
    }

    @Override public EmrJobExecution findByJobName(String jobName) {
        return null;
    }

    @Override public List<EmrJobExecution> findByJobStatus(String jobStatus) {
        return null;
    }

    @Override
    public List<EmrJobExecution> findByJobStatusNotIn(List<String> jobStatusList){
        return null;
    }

    @Override public EmrJobExecution save(EmrJobExecution emrJobExecution) {
        return null;
    }

}
