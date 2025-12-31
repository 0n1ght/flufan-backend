package com.flufan.repo;

import com.flufan.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender.id = :userId AND m.receiver.id = :otherUserId) OR (m.sender.id = :otherUserId AND m.receiver.id = :userId) ORDER BY m.sentAt ASC")
    Page<Message> findConversation(@Param("userId") Long userId, @Param("otherUserId") Long otherUserId, Pageable pageable);

    @Modifying
    @Query("""
    UPDATE Message m
    SET m.readStatus = true
    WHERE m.sender.id = :senderId
      AND m.receiver.id = :receiverId
      AND m.readStatus = false
      AND m.sentAt <= :limit
""")
    int markAsReadUpTo(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId,
            @Param("limit") Instant limit
    );
}
