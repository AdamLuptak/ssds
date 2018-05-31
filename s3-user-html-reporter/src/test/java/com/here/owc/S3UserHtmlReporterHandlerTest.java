package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class S3UserHtmlReporterHandlerTest {

    @Test
    void handleRequest() {
        Context mockContext = Mockito.mock(Context.class);
        S3UserHtmlReporterHandler s3UserHtmlReporterHandler = new S3UserHtmlReporterHandler();
        SNSEvent event = new SNSEvent();

        s3UserHtmlReporterHandler.handleRequest(event, mockContext);
    }
}
