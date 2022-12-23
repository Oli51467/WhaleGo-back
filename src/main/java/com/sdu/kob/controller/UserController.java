package com.sdu.kob.controller;

import com.sdu.kob.dao.UserDAO;
import com.sdu.kob.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserDAO userDAO;

    @GetMapping("/user/all")
    public List<User> getAll() {
        return userDAO.findAll();
    }
}
