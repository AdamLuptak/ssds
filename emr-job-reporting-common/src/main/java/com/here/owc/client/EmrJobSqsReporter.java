package com.here.owc.client;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmrJobSqsReporter implements EmrJobReporter {
    private static final Logger logger = LoggerFactory.getLogger(EmrJobSqsReporter.class);
    private String queueName;
    private AmazonSQS sqs;

    public EmrJobSqsReporter(String queueName, AmazonSQS sqs) {
        this.queueName = queueName;
        this.sqs = sqs;
    }

    public static EmrJobSqsReporter create(String queueName, Regions region) {
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        AmazonSQS sqs = AmazonSQSClientBuilder.standard().withCredentials(
                credentialsProvider).withRegion(region).build();
        if (!queueExists(queueName, sqs)) {
            sqs.createQueue(queueName);
        }

        return new EmrJobSqsReporter(queueName, sqs);
    }

    public static boolean queueExists(String queueName, AmazonSQS sqs) {
        try {
            sqs.getQueueUrl(new GetQueueUrlRequest(queueName));
            return true;
        } catch (QueueDoesNotExistException var3) {
            return false;
        }
    }

    public void sendMessage(String message) {
        try {
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(this.queueName);
            String myQueueUrl = this.sqs.createQueue(createQueueRequest).getQueueUrl();
            this.sqs.sendMessage(new SendMessageRequest(myQueueUrl, message));
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
    }
}
