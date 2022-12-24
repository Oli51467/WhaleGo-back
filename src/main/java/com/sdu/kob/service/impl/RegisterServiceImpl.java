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
    public Map<String, String> register(String userName, String password, String confirmedPassword) {
        Map<String, String> result = new HashMap<>();
        if (userName == null) {
            result.put("msg", "用户名不能为空");
            return result;
        }
        if (password == null || confirmedPassword == null) {
            result.put("mas", "密码不能为空");
            return result;
        }
        userName = userName.trim();
        if (userName.length() == 0) {
            result.put("msg", "用户名不能为空");
            return result;
        }
        if (password.length() == 0 || confirmedPassword.length() == 0) {
            result.put("msg", "密码不能为空");
        }
        if (userName.length() > 20) {
            result.put("msg", "用户名长度不能大于20");
            return result;
        }
        if (password.length() > 30 || confirmedPassword.length() > 30) {
            result.put("msg", "密码长度不能大于30");
            return result;
        }
        if (!password.equals(confirmedPassword)) {
            result.put("msg", "两次输入的密码不一致");
        }
        if (userDAO.findByUserName(userName) != null) {
            result.put("msg", "用户名已存在");
            return result;
        }

        String encodedPassword = passwordEncoder.encode(password); // 密码加密
        String avatar = "https://cdn.acwing.com/media/user/profile/photo/73457_lg_28f38d989d.jpeg";
        User user = new User(userName, encodedPassword, 1500, avatar);
        userDAO.save(user);
        result.put("msg", "success");
        return result;
    }
}
