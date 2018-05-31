package com.here.owc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.here.owc.model.EmrErrorMessage;
import com.here.owc.model.EmrInfoMessage;
import com.here.owc.model.EmrJobMessage;
import com.here.owc.model.EmrMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class EmrJobMessageConverter {
    static final Logger logger = LoggerFactory.getLogger(EmrJobMessageConverter.class);

    public static List<EmrMessage> convertFrom(List<EmrJobMessage> emrJobMessages) {
        ObjectMapper objectMapper = new ObjectMapper();
        return emrJobMessages.stream().map(m -> {
            try {
                return objectMapper.readValue(m.getMessage(), EmrMessage.class);
            } catch (IOException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static Optional<String> convertFrom(EmrInfoMessage emrMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of(objectMapper.writeValueAsString(emrMessage));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<String> convertFrom(EmrErrorMessage emrMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Optional.of(objectMapper.writeValueAsString(emrMessage));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

}
