package com.sdu.kob.repository;

import com.sdu.kob.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordDAO extends JpaRepository<Record, Integer> {

}
