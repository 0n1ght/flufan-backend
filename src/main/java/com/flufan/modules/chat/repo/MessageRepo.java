package com.flufan.modules.chat.repo;

import com.flufan.modules.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {

    @Query("""
    SELECT m FROM Message m
    WHERE (m.sender.publicId = :userPublicId AND m.receiver.publicId = :otherPublicId)
       OR (m.sender.publicId = :otherPublicId AND m.receiver.publicId = :userPublicId)
    ORDER BY m.sentAt ASC
""")
    Page<Message> findConversation(
            @Param("userPublicId") UUID userPublicId,
            @Param("otherPublicId") UUID otherPublicId,
            Pageable pageable
    );

    @Modifying
    @Query("""
    UPDATE Message m
    SET m.readStatus = true
    WHERE m.sender.publicId = :senderPublicId
      AND m.receiver.publicId = :receiverPublicId
      AND m.readStatus = false
      AND m.sentAt <= :limit
""")
    int markAsReadUpTo(
            @Param("senderPublicId") UUID senderPublicId,
            @Param("receiverPublicId") UUID receiverPublicId,
            @Param("limit") Instant limit
    );
}
