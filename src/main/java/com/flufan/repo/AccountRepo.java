package com.flufan.repo;

import com.flufan.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, Long> {
    Optional<Account> findByEmailIgnoreCase(String email);
    Optional<Account> findByUsernameIgnoreCase(String username);
    List<Account> findAllByDeletedAtBefore(LocalDateTime date);
}
