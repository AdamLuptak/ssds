package com.here.owc;

import com.here.owc.model.EmrJobExecution;
import com.here.owc.service.EmrJobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmrJobRegistration {

    static final Logger logger = LoggerFactory.getLogger(EmrJobRegistration.class);

    @Autowired
    EmrJobExecutionService emrJobExecutionService;

    public void run(EmrJobExecution emrJobExecution) {
        emrJobExecutionService.saveEmrJobExecution(emrJobExecution);
        logger.info("Successfully saved {}", emrJobExecution);
    }

}
