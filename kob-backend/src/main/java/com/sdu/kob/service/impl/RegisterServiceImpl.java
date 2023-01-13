package com.sdu.kob.service.impl;

import com.sdu.kob.domain.User;
import com.sdu.kob.repository.UserDAO;
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
    public Map<String, String> register(String userName, String password) {
        Map<String, String> result = new HashMap<>();
        if (userDAO.findByUserName(userName) != null) {
            result.put("msg", "用户名已存在");
            return result;
        }

        String encodedPassword = passwordEncoder.encode(password); // 密码加密
        String avatar = "https://cdn.acwing.com/media/user/profile/photo/73457_lg_28f38d989d.jpeg";
        User user = new User(userName, encodedPassword, "", 1500, avatar, 0, 0, 0, "");
        userDAO.save(user);
        result.put("msg", "success");
        return result;
    }
}
