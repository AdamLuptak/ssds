package com.here.owc.repository;

import com.here.owc.model.EmrJobExecution;
import com.here.owc.model.EmrJobMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Qualifier("JpaEmrJobMessageRepository")
public interface JpaEmrJobMessageRepository extends CrudRepository<EmrJobMessage, Long>, EmrJobMessageRepository {
    List<EmrJobMessage> findByEmrJobExecution(EmrJobExecution emrJobExecution);

}
