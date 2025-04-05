package com.frinkan.service;

import com.frinkan.dto.LoginDto;
import com.frinkan.entity.Account;
import com.frinkan.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    void saveAccount(RegisterDto accountDto);
    void deleteAccount(LoginDto loginDto);
    Account getAuthenticatedAccount();
    String verify(LoginDto loginDto);
    Account getById(Long id);
    void changeLoginData(LoginDto loginDto);
    void updateAccount(Account account);
}
