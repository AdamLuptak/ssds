package com.here.owc.service;

import com.here.owc.model.EmrJobExecution;
import com.here.owc.model.EmrJobMessage;
import com.here.owc.repository.EmrJobMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmrJobMessageService {

    @Autowired
    @Qualifier("jpaEmrJobMessageRepository")
    private EmrJobMessageRepository emrJobMessageRepository;

    public EmrJobMessage saveEmrJobMessage(final EmrJobMessage emrJobMessage) {
        return emrJobMessageRepository.save(emrJobMessage);
    }

    @Transactional
    public List<EmrJobMessage> saveEmrJobMessages(final List<EmrJobMessage> emrJobMessages) {
        return emrJobMessages.stream().map(emrJobMessageRepository::save).collect(Collectors.toList());
    }

    public  List<EmrJobMessage> loadByExecutionId(final EmrJobExecution emrJobExecution) {
        return emrJobMessageRepository.findByEmrJobExecution(emrJobExecution);
    }

}
