package com.here.owc.client;

import com.here.owc.model.EmrJobExecution;

public interface EmrJobExecutionClient {

    void registerEmrJobExecution(EmrJobExecution emrJobExecution);
}
