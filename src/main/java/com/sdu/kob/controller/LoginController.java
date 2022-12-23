package com.sdu.kob.controller;

import com.sdu.kob.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/api/account/token/", method = RequestMethod.POST)
    public Map<String, String> getToken(@RequestParam Map<String, String> map) {
        String userName = map.get("userName");
        String password = map.get("password");
        return loginService.getToken(userName, password);
    }
}