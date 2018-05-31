package com.here.owc.repository;

import com.here.owc.model.EmrJobExecution;
import com.here.owc.model.EmrJobMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("jdbcEmrJobMessageRepository")
public class JdbcEmrJobMessageRepository implements EmrJobMessageRepository {
    @Override public EmrJobMessage save(EmrJobMessage emrJobMessage) {
        return null;
    }

    @Override public List<EmrJobMessage> findByEmrJobExecution(EmrJobExecution emrJobExecution) {
        return null;
    }

}
