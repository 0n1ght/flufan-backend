package com.frinkan.service.Impl;

import com.frinkan.dto.RegisterDto;
import com.frinkan.entity.Account;
import com.frinkan.repo.AccountRepo;
import com.frinkan.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Account> account = accountRepo.findByEmail(username);
        if (account.isPresent()) {

            var accountObj = account.get();
            return User.builder()
                    .username(accountObj.getEmail())
                    .password(accountObj.getPassword())
                    .build();
        }
        throw new UsernameNotFoundException(username);
    }

    @Override
    public void saveAccount(RegisterDto accountDto) {
        Optional<Account> existingAccount = accountRepo.findByEmail(accountDto.getEmail());
        if (existingAccount.isPresent()) {
            throw new IllegalArgumentException("Email is already in use");
        }
        accountRepo.save(new Account(accountDto.getUsername(), accountDto.getEmail(), accountDto.getPassword()));
    }
}
