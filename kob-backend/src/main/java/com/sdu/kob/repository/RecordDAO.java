package com.sdu.kob.repository;

import com.sdu.kob.domain.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecordDAO extends JpaRepository<Record, Integer> {

    @Query(value = "select * from record where black_id = :userId or white_id = :userId", nativeQuery = true)
    Page<Record> findMyRecords(@Param("userId") Integer Id, Pageable pageable);

    @Query(value = "select * from record where black_id != :userId and white_id != :userId", nativeQuery = true,
            countQuery = "select count(*) from record where black_id != :userId and white_id != :userId")
    Page<Record> findOthers(@Param("userId") Integer Id, Pageable pageable);
}
