package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.here.owc.model.EmrJobExecution;
import com.here.owc.utils.EmrJobExecutionConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class EmrJobRegistrationHandler implements RequestHandler<SNSEvent, String> {
    private static final String SUCCESS_MSG = "SUCCESS saving of EmrJobExecution";
    private static final String FAIL_MSG = "FAIL to save EmrJobExecution";
    private static ApplicationContext ctx;
    static final Logger logger = LoggerFactory.getLogger(EmrJobRegistration.class);

    static {
        ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
    }

    @Override
    public String handleRequest(SNSEvent snsEvent, Context context) {
        try {
            EmrJobRegistration emrJobRegistration = ctx.getBean(EmrJobRegistration.class);
            validateSNSEvent(snsEvent);
            EmrJobExecution emrJobExecution = EmrJobExecutionConverter.convert(snsEvent);
            emrJobRegistration.run(emrJobExecution);
        } catch (IOException e) {
            logger.info(e.getMessage(), e);
            return FAIL_MSG;
        }
        return SUCCESS_MSG;
    }

    private void validateSNSEvent(SNSEvent snsEvent) {
        if (snsEvent != null &&
                CollectionUtils.isNotEmpty(snsEvent.getRecords()) &&
                snsEvent.getRecords().get(0).getSNS() != null &&
                snsEvent.getRecords().get(0).getSNS().getMessage() != null) {
        } else {
            throw new IllegalArgumentException("SNS event is not valid");
        }
    }
}
