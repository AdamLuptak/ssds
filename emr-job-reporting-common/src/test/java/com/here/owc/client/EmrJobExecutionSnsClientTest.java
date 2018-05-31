package com.here.owc.client;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.here.owc.model.EmrJobExecution;
import org.junit.jupiter.api.Test;

class EmrJobExecutionSnsClientTest {
    @Test void registerEmrJobExecution() {

        AmazonSNS sns = AmazonSNSClient.builder().build();
        EmrJobExecutionSnsClient emrJobExecutionSnsClient = new EmrJobExecutionSnsClient(sns,"arn:aws:sns:us-east-1:846469392269:emrJobRegistrationTopic");
        EmrJobExecution emrJobExecution = new EmrJobExecution();
        emrJobExecution.setJobName("TestJOIB");
        emrJobExecution.setClusterId("CLusterID 1");
        emrJobExecution.setSqsQueueName("MyQUQUQ");
        emrJobExecutionSnsClient.registerEmrJobExecution(emrJobExecution);
    }

}
