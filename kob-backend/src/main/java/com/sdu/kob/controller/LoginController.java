package com.sdu.kob.controller;

import com.sdu.kob.response.BaseResponse;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@BaseResponse
public class LoginController {

    @Autowired
    private LoginService loginService;

    @CrossOrigin
    @RequestMapping(value = "/api/account/token/", method = RequestMethod.POST)
    public ResponseResult getToken(@RequestParam Map<String, String> map) {
        String userName = map.get("username");
        String password = map.get("password");
        return loginService.getToken(userName, password);
    }
}
