package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EmrJobMessageCollectorHandler implements RequestHandler<ScheduledEvent, String> {
    private static ApplicationContext ctx;
    private static final String SUCCESS_MSG = "SUCCESS saving of EmrJobExecution";
    private static final String FAIL_MSG = "FAIL to save EmrJobExecution";
    static final Logger logger = LoggerFactory.getLogger(EmrJobMessageCollectorHandler.class);

    static {
        ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
    }

    @Override
    public String handleRequest(ScheduledEvent event, Context context) {
        try {
            EmrJobMessageCollector emrJobMessageCollector = ctx.getBean(EmrJobMessageCollector.class);
            emrJobMessageCollector.run(event);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return FAIL_MSG;
        }
        return SUCCESS_MSG;

    }
}
