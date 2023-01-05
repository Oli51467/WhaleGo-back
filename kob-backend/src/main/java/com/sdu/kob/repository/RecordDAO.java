package com.sdu.kob.repository;

import com.sdu.kob.domain.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecordDAO extends JpaRepository<Record, Integer> {

    @Query(value = "select * from record where black_id = ?1 or white_id = ?1 ORDER BY ?#{#pageable}", nativeQuery = true)
    Page<Record> findMyRecords(Integer Id, Pageable pageable);

    @Query(value = "select * from record where black_id != ?1 and white_id != ?1 ORDER BY ?#{#pageable}", nativeQuery = true)
    Page<Record> findOthers(Integer Id, Pageable pageable);
}
