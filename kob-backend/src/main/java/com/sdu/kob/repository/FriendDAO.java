package com.sdu.kob.repository;

import com.sdu.kob.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface FriendDAO extends JpaRepository<Friend, Integer> {

    Friend findByUserAAndUserB(Integer userA, Integer userB);

    Friend findByUserAOrUserB(Integer userA, Integer userB);

    @Modifying
    @Transactional
    @Query(value = "update friend set followed = ?3 where user_a = ?1 and user_b = ?2", nativeQuery = true)
    void update(int userId, int followerId, String relation);

    List<Friend> findByUserAAndFollowed(Integer userId, String followed);

    List<Friend> findByUserBAndFollowed(Integer userId, String followed);
}
