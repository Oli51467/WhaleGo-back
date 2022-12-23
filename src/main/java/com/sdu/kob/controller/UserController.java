package com.sdu.kob.controller;

import com.sdu.kob.dao.UserDAO;
import com.sdu.kob.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserDAO userDAO;

    @RequestMapping(value = "/api/getUsers", method = RequestMethod.GET)
    public List<User> getAll() {
        return userDAO.findAll();
    }

    @RequestMapping(value = "/api/addUser", method = RequestMethod.POST)
    public String addUser(@RequestBody User user) {
        String status = "";
        // 密码加密
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        try {
            userDAO.save(user);
            status = "success";
        } catch (Exception e) {
            status = String.valueOf(e);
        }
        return status;
    }
}
