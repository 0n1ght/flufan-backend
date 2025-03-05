package com.frinkan.repo;

import com.frinkan.entity.Account;
import com.frinkan.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationRepo extends
        JpaRepository<Conversation, Long> {
    Optional<Conversation> findByUserAndInfluencer(Account user, Account influencer);
}
