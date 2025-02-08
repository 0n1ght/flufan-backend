package com.frinkan.service;

import com.frinkan.dto.RegisterDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends UserDetailsService {
    void saveAccount(RegisterDto accountDto);
}
