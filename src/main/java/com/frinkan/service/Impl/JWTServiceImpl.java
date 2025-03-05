package com.frinkan.service.Impl;

import com.frinkan.service.JWTService;
import org.springframework.stereotype.Service;

@Service
public class JWTServiceImpl implements JWTService {

    @Override
    public String generateToken() {
        return "TOKENN";
    }
}
