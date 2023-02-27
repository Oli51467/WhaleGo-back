package com.sdu.kob.service.impl;

import com.sdu.kob.domain.User;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.LoginService;
import com.sdu.kob.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("LoginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseResult getToken(String userName, String password) {
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
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), jwt);
    }
}
