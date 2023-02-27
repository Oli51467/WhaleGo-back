package com.sdu.kob.service.impl;

import com.sdu.kob.domain.User;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("RegisterService")
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseResult register(String userName, String password) {
        if (userDAO.findByUserName(userName) != null) {
            return new ResponseResult(ResponseCode.USERNAME_EXIST.getCode(), ResponseCode.USERNAME_EXIST.getMsg(), null);
        }

        String encodedPassword = passwordEncoder.encode(password); // 密码加密
        User user = new User(userName, encodedPassword);
        userDAO.save(user);
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), null);
    }
}
