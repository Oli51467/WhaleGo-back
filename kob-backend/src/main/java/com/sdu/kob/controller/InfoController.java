package com.sdu.kob.controller;

import com.sdu.kob.response.BaseResponse;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.InfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@BaseResponse
public class InfoController {

    @Autowired
    private InfoService infoService;

    @RequestMapping(value = "/api/account/info/", method = RequestMethod.GET)
    public ResponseResult getInfo() {
        return infoService.getInfo();
    }
}
