package com.sdu.kob.repository;

import com.sdu.kob.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface FriendDAO extends JpaRepository<Friend, Long> {

    Friend findByUserAAndUserB(Long userA, Long userB);

    Friend findByUserAOrUserB(Long userA, Long userB);

    @Modifying
    @Transactional
    @Query(value = "update friend set followed = ?3 where user_a = ?1 and user_b = ?2", nativeQuery = true)
    void update(long userId, long followerId, String relation);

    List<Friend> findByUserAAndFollowed(Long userId, String followed);

    List<Friend> findByUserBAndFollowed(Long userId, String followed);

    int countByUserAAndFollowed(Long userId, String followed);

    int countByUserBAndFollowed(Long userId, String followed);

    @Query(value = "select unread_msg_count from friend where user_a = ?1 and user_b = ?2", nativeQuery = true)
    int findUnreadMessageCount(Long sendId, Long receiveId);

    @Modifying
    @Transactional
    @Query(value = "update friend set unread_msg_count = 0 where user_a = ?1 and user_b = ?2", nativeQuery = true)
    void clearUnreadMessage(Long sendId, Long receiveId);

    @Modifying
    @Transactional
    @Query(value = "update friend set unread_msg_count = unread_msg_count + 1 where user_a = ?1 and user_b = ?2", nativeQuery = true)
    void increaseUnreadMessage(Long sendId, Long receiveId);

    @Query(value = "select sum(unread_msg_count) from friend where user_b = ?1", nativeQuery = true)
    int getMessageSum(Long userId);
}
