package com.sdu.kob.controller;

import com.sdu.kob.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @RequestMapping(value = "/api/account/register", method = RequestMethod.POST)
    public Map<String, String> register(@RequestParam Map<String, String> info) {
        String userName = info.get("username");
        String password = info.get("password");
        String confirmedPassword = info.get("confirmedPassword");
        return registerService.register(userName, password, confirmedPassword);
    }
}
