package com.sdu.kob.service.impl;

import com.sdu.kob.domain.User;
import com.sdu.kob.service.InfoService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("InfoService")
public class InfoServiceImpl implements InfoService {

    // 根据token获取用户信息 若授权成功 则将用户信息从上下文中提取出来
    @Override
    public Map<String, String> getInfo() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();
        Map<String, String> result = new HashMap<>();
        result.put("msg", "success");
        result.put("id", user.getId().toString());
        result.put("username", user.getUserName());
        result.put("avatar", user.getAvatar());
        result.put("profile", user.getProfile());
        result.put("phone", user.getPhone());
        return result;
    }
}
