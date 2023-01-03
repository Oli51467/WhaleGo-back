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
}
