package com.here.owc.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "emr_job_execution")
public class EmrJobExecution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "execution_id")
    private Long executionId;

    @Column(name = "cluster_id")
    private String clusterId;

    @Column(name = "job_name")
    private String jobName;

    @Column(name = "job_status")
    private String jobStatus;

    @Column(name = "sqs_queue_name")
    private String sqsQueueName;

    public EmrJobExecution() {

    }

    public EmrJobExecution(String clusterId, String jobName, String sqsQueueName, String jobStatus) {
        this.clusterId = clusterId;
        this.jobName = jobName;
        this.sqsQueueName = sqsQueueName;
        this.jobStatus = jobStatus;
    }

    public EmrJobExecution(Long executionId, String clusterId, String jobName, String sqsQueueName) {
        this.executionId = executionId;
        this.clusterId = clusterId;
        this.jobName = jobName;
        this.sqsQueueName = sqsQueueName;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getSqsQueueName() {
        return sqsQueueName;
    }

    public void setSqsQueueName(String sqsQueueName) {
        this.sqsQueueName = sqsQueueName;
    }

    @Override public String toString() {
        return new ToStringBuilder(this)
                .append("executionId", executionId)
                .append("clusterId", clusterId)
                .append("jobName", jobName)
                .append("jobStatus", jobStatus)
                .append("sqsQueueName", sqsQueueName)
                .toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmrJobExecution that = (EmrJobExecution) o;

        return new EqualsBuilder()
                .append(executionId, that.executionId)
                .append(clusterId, that.clusterId)
                .append(jobName, that.jobName)
                .append(jobStatus, that.jobStatus)
                .append(sqsQueueName, that.sqsQueueName)
                .isEquals();
    }

    @Override public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(executionId)
                .append(clusterId)
                .append(jobName)
                .append(jobStatus)
                .append(sqsQueueName)
                .toHashCode();
    }
}
