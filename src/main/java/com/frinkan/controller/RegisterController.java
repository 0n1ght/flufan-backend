package com.frinkan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegisterController {

    @GetMapping("/req/signup")
    public String signUp() {
        return "signup";
    }
}
