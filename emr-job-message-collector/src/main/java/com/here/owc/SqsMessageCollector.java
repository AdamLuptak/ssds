package com.here.owc;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.here.owc.model.EmrJobExecution;
import com.here.owc.model.EmrJobMessage;
import com.here.owc.service.EmrJobMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class SqsMessageCollector {

    @Autowired
    private AmazonSQS sqs;

    @Autowired
    private EmrJobMessageService emrJobMessageService;

    public SqsMessageCollector(AmazonSQS sqs) {
        this.sqs = sqs;
    }

    public void collectAndSaveToDB(List<EmrJobExecution> runningEmrJobs)
            throws ExecutionException, InterruptedException {
        List<CompletableFuture> completableFutureList = runningEmrJobs.stream().map(
                p -> CompletableFuture.runAsync(() -> collectAllMessageFromQueue(p)))
                .collect(Collectors.toList());

        CompletableFuture<Void> allFuturesDone = CompletableFuture.allOf(
                completableFutureList.toArray(new CompletableFuture[0]));

        allFuturesDone.get();
    }

    private void collectAllMessageFromQueue(EmrJobExecution emrJobExecution) {
        boolean flag = true;
        String queueName = emrJobExecution.getSqsQueueName();
        List<EmrJobMessage> emrJobMessages = new ArrayList<>();
        while (flag) {
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueName);
            receiveMessageRequest.setMaxNumberOfMessages(10);
            receiveMessageRequest.withMaxNumberOfMessages(10).withWaitTimeSeconds(1);
            List<Message> messages = new ArrayList<>();
            try {
                messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            } catch (Exception e) {
                flag = false;
            }
            for (Message message : messages) {
                String messageReceiptHandle = message.getReceiptHandle();
                sqs.deleteMessage(
                        new DeleteMessageRequest().withQueueUrl(queueName).withReceiptHandle(messageReceiptHandle));
                EmrJobMessage emrJobMessage = new EmrJobMessage();
                emrJobMessage.setEmrJobExecution(emrJobExecution);
                emrJobMessage.setMessage(message.getBody());
                emrJobMessages.add(emrJobMessage);
            }
            if (CollectionUtils.isEmpty(messages)) {
                flag = false;
            }

        }
        emrJobMessageService.saveEmrJobMessages(emrJobMessages);
    }
}
