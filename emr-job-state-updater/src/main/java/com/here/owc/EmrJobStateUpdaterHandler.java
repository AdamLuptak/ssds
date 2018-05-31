package com.here.owc;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.here.owc.service.EmrJobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class EmrJobStateUpdaterHandler implements RequestHandler<ScheduledEvent, String> {
    private static final String SUCCESS_MSG = "SUCCESS saving of EmrJobStateUpdater";
    private static final String FAIL_MSG = "FAIL to save EmrJobStateUpdater";
    private static ApplicationContext ctx;
    static final Logger logger = LoggerFactory.getLogger(EmrJobStateUpdaterHandler.class);

    static {
        ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
    }

    @Override
    public String handleRequest(ScheduledEvent scheduledEvent, Context context) {
        try {
            EmrJobStateUpdater emrJobStateUpdater = ctx.getBean(EmrJobStateUpdater.class);
            emrJobStateUpdater.run(scheduledEvent);
        } catch (Exception | EmrJobExecutionService.EmrJobExecutionServiceException e) {
            logger.info(e.getMessage(), e);
            return FAIL_MSG;
        }
        return SUCCESS_MSG;
    }

}
