package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class S3UserHtmlReporterHandler implements RequestHandler<SNSEvent, String> {
    private static final String SUCCESS_MSG = "SUCCESS saving of S3UserHtmlReporterHandler";
    private static final String FAIL_MSG = "FAIL to create report S3UserHtmlReporterHandler";
    private static ApplicationContext ctx;
    static final Logger logger = LoggerFactory.getLogger(S3UserHtmlReporterHandler.class);

    static {
        ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
    }

    @Override
    public String handleRequest(SNSEvent scheduledEvent, Context context) {
        try {
            S3UserHtmlReporter s3UserHtmlReporter = ctx.getBean(S3UserHtmlReporter.class);
            s3UserHtmlReporter.run(scheduledEvent);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return FAIL_MSG;
        }
        return SUCCESS_MSG;
    }
}
