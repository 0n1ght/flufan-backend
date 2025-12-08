package com.flufan.config.security;

import com.flufan.entity.Account;
import com.flufan.service.AccountService;
import com.flufan.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AccountService accountService;
    private final JWTService jwtService;

    public CustomOAuth2SuccessHandler(AccountService accountService, JWTService jwtService) {
        this.accountService = accountService;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuthUser = (OAuth2User) authentication.getPrincipal();
        String email = oAuthUser.getAttribute("email");

        if (email == null) {
            email = oAuthUser.getName() + "@facebook.local";
        }

        Account acc = accountService.loadOrCreateGoogleUser(email);

        String token = jwtService.generateToken(acc.getEmail());

        response.setContentType("application/json");
        response.getWriter().write(token);
        response.getWriter().flush();
    }
}
