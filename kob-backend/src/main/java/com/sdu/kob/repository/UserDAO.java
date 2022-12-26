package com.sdu.kob.repository;

import com.sdu.kob.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {
    User findByUserName(String userName);

    User findById(int id);
}
