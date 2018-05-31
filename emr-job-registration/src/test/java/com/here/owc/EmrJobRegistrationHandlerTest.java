package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;

class EmrJobRegistrationHandlerTest {

    @Ignore
    @Test
    void handleRequest() throws IOException {
        Context mockContext = Mockito.mock(Context.class);
        EmrJobRegistrationHandler emrJobRegistrationHandler = new EmrJobRegistrationHandler();

        SNSEvent snsEvent = new SNSEvent();
        SNSEvent.SNSRecord snsRecord = new SNSEvent.SNSRecord();
        snsRecord.setSns(new SNSEvent.SNS());
        snsRecord.getSNS().setMessage(
                "{\"clusterId\": \"j-3E29AFNNY1Sf\",\"jobName\": \"sdsfd\",\"jobStatus\": \"pending\",\"sqsQueueName\": \"dsdsdsd\"}");
        snsEvent.setRecords(Arrays.asList(snsRecord));

        emrJobRegistrationHandler.handleRequest(snsEvent, mockContext);
    }
}
