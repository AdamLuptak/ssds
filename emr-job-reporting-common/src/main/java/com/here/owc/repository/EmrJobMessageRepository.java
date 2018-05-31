package com.here.owc.repository;

import com.here.owc.model.EmrJobExecution;
import com.here.owc.model.EmrJobMessage;

import java.util.List;

public interface EmrJobMessageRepository {
    EmrJobMessage save(EmrJobMessage emrJobMessage);
    List<EmrJobMessage> findByEmrJobExecution(EmrJobExecution emrJobExecution);
}
