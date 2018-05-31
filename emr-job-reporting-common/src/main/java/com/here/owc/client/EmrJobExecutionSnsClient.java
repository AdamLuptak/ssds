package com.here.owc.client;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.here.owc.model.EmrJobExecution;
import com.here.owc.utils.EmrJobExecutionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EmrJobExecutionSnsClient implements EmrJobExecutionClient {
    static final Logger logger = LoggerFactory.getLogger(EmrJobExecutionSnsClient.class);

    private AmazonSNS snsClient;
    private String topicArn;

    public EmrJobExecutionSnsClient(AmazonSNS snsClient, String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn;
    }

    @Override public void registerEmrJobExecution(EmrJobExecution emrJobExecution) {
        try {
            PublishRequest publishRequest = new PublishRequest(topicArn,
                    EmrJobExecutionConverter.convert(emrJobExecution));
            PublishResult publishResult = snsClient.publish(publishRequest);
            logger.info("Registering new emr job for monitoring and reporting - {}", publishResult);
        } catch (IOException e) {
            logger.error("Can't write or send message to topic for emrJobRegistration");
            logger.error(e.getMessage(), e);
        }
    }
}
