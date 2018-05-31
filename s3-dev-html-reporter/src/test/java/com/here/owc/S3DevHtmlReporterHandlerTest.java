package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

class S3DevHtmlReporterHandlerTest {

    @Test
    void handleRequest() {
        Context mockContext = Mockito.mock(Context.class);
        S3DevHtmlReporterHandler s3DevHtmlReporterHandler = new S3DevHtmlReporterHandler();
        SNSEvent snsEvent = new SNSEvent();
        SNSEvent.SNSRecord snsRecord = new SNSEvent.SNSRecord();
        snsRecord.setSns(new SNSEvent.SNS());
        snsRecord.getSNS().setMessage(
                "{\"executionId\": 4 ,\"clusterId\": \"j-3E29AFNNY1ONS\",\"jobName\": \"sdsfd\",\"jobStatus\": \"pending\",\"sqsQueueName\": \"dsdsdsd\"}");
        snsEvent.setRecords(Arrays.asList(snsRecord));

        s3DevHtmlReporterHandler.handleRequest(snsEvent, mockContext);
    }
}
