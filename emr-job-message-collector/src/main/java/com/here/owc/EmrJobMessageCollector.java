package com.here.owc;

import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.here.owc.model.EmrJobExecution;
import com.here.owc.service.EmrJobExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class EmrJobMessageCollector {

    static final Logger logger = LoggerFactory.getLogger(EmrJobMessageCollector.class);

    @Autowired
    EmrJobExecutionService emrJobExecutionService;

    @Autowired
    SqsMessageCollector sqsMessageCollector;

    public void run(ScheduledEvent event) throws ExecutionException, InterruptedException {
        List<EmrJobExecution> runningEmrJobs = emrJobExecutionService.loadRunningEmrJobs();
        sqsMessageCollector.collectAndSaveToDB(runningEmrJobs);
    }
}
