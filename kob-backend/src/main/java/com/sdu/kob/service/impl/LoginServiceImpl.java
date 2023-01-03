package com.sdu.kob.service.impl;

import com.sdu.kob.domain.User;
import com.sdu.kob.service.LoginService;
import com.sdu.kob.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("LoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public Map<String, String> getToken(String userName, String password) {
        // 将用户名和密码加密 封装一层
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userName, password);
        // 如果登陆失败会自动处理
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 登陆成功
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
        User user = loginUser.getUser();
        // 封装成jwt-token
        String jwt = JwtUtil.createJWT(user.getId().toString());

        Map<String, String> result = new HashMap<>();
        result.put("msg", "success");
        result.put("token", jwt);
        return result;
    }
}
