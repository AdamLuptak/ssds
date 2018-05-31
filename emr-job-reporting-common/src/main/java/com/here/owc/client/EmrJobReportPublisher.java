package com.here.owc.client;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.here.owc.model.EmrJobExecution;
import com.here.owc.utils.EmrJobExecutionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EmrJobReportPublisher {

    static final Logger logger = LoggerFactory.getLogger(EmrJobReportPublisher.class);

    private AmazonSNS snsClient;
    private String topicArn;

    public EmrJobReportPublisher(AmazonSNS snsClient, String topicArn) {
        this.snsClient = snsClient;
        this.topicArn = topicArn;
    }

    public void publishToReporters(EmrJobExecution emrJobExecution) {
        try {
            PublishRequest publishRequest = new PublishRequest(topicArn,
                    EmrJobExecutionConverter.convert(emrJobExecution));
            PublishResult publishResult = snsClient.publish(publishRequest);
            logger.info("Publish message to reporters - {}", publishResult);
        } catch (IOException e) {
            logger.error("Can't write or send message to topic for {}", topicArn);
            logger.error(e.getMessage(), e);
        }
    }
}
