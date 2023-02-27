package com.sdu.kob.controller;

import com.sdu.kob.response.BaseResponse;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@BaseResponse
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @RequestMapping(value = "/api/account/register/", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult register(@RequestParam Map<String, String> info) {
        String userName = info.get("username");
        String password = info.get("password");
        return registerService.register(userName, password);
    }
}
