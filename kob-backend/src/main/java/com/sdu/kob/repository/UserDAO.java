package com.sdu.kob.repository;

import com.sdu.kob.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUserName(String userName);

    User findById(int id);

    @Modifying
    @Transactional
    @Query(value = "update user set state = ?2 where id = ?1", nativeQuery = true)
    void updateState(int userId, Integer state);

    @Modifying
    @Transactional
    @Query(value = "update user set guests_count = guests_count + 1 where id = ?1", nativeQuery = true)
    void updateGuestsCount(int userId);

    @Modifying
    @Transactional
    @Query(value = "update user set recent_guests = ?2 where id = ?1", nativeQuery = true)
    void updateRecentGuests(int userId, String guests);

    @Modifying
    @Transactional
    @Query(value = "update user set username = ?2, profile = ?3 where id = ?1", nativeQuery = true)
    void updateUserInfo(int userId, String userName, String profile);

    @Modifying
    @Transactional
    @Query(value = "update user set win = ?2, recent_records = ?3, rating = ?4 where id = ?1", nativeQuery = true)
    void updateWin(int userId, Integer win, String recentRecords, String rating);

    @Modifying
    @Transactional
    @Query(value = "update user set lose = ?2, recent_records = ?3, rating = ?4 where id = ?1", nativeQuery = true)
    void updateLose(int userId, Integer lose, String recentRecords, String rating);

}
