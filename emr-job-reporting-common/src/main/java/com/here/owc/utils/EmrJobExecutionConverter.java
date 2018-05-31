package com.here.owc.utils;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.here.owc.model.EmrJobExecution;

import java.io.IOException;

public final class EmrJobExecutionConverter {

    public static EmrJobExecution convert(SNSEvent snsEvent) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String message = snsEvent.getRecords().get(0).getSNS().getMessage();
        return objectMapper.readValue(message, EmrJobExecution.class);
    }

    public static String convert(EmrJobExecution emrJobExecution) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(emrJobExecution);
    }
}
