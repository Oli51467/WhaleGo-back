package com.sdu.kob.repository;

import com.sdu.kob.domain.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordDAO extends JpaRepository<Record, Long> {

    @Query(value = "select * from record where black_id = :userId or white_id = :userId", nativeQuery = true)
    Page<Record> findMyRecords(@Param("userId") Long Id, Pageable pageable);

    @Query(value = "select * from record where black_id = ?1 or white_id = ?1 order by id DESC", nativeQuery = true)
    List<Record> findMyRecords(Long Id);

    @Query(value = "select * from record where black_id != :userId and white_id != :userId", nativeQuery = true)
    Page<Record> findOthers(@Param("userId") Long Id, Pageable pageable);

    @Query(value = "select count(*) from record where black_id = :userId or white_id = :userId", nativeQuery = true)
    int countByMyRecords(@Param("userId") Long Id);

    @Query(value = "select count(*) from record where black_id != :userId and white_id != :userId", nativeQuery = true)
    int countOthers(@Param("userId") Long Id);

    Record findById(long id);
}
