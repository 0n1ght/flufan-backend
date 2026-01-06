package com.flufan.modules.user.repo;

import com.flufan.modules.user.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BannedAccountRepo extends JpaRepository<Account, Long> {

    Optional<Account> findByEmailIgnoreCase(String email);

    Optional<Account> findByUsernameIgnoreCase(String username);
}
