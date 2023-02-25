package com.sdu.kob.controller;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.UserService;
import com.sdu.kob.service.impl.UserDetailsImpl;
import com.sdu.kob.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${file.images-path}")
    private String imagePath;

    @Value("${file.root-path}")
    private String rootPath;

    @Value("${web-ip}")
    private String webIp;

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/api/user/get/", method = RequestMethod.GET)
    public JSONObject searchUser(@RequestParam Map<String, String> data, Principal principal) {
        String searchName = data.get("username");
        String userName = principal.getName();
        return userService.searchUser(searchName, userName);
    }

    @RequestMapping(value = "/api/user/getFollowedAndFollowersCount/", method = RequestMethod.POST)
    public JSONObject getFollowedAndFollowersCountAndGuests(@RequestParam Map<String, String> data, Principal principal) {
        Long userId = Long.parseLong(data.get("user_id"));
        String requestUserName = principal.getName();
        return userService.getFollowedAndFollowersCountAndGuests(userId, requestUserName);
    }

    @RequestMapping(value = "/api/user/updateInfo/", method = RequestMethod.POST)
    public Map<String, String> updateUserUsername(@RequestParam Map<String, String> data) {
        return userService.updateUserInfo(data);
    }

    @RequestMapping(value = "/api/user/updatePassword/", method = RequestMethod.POST)
    public Map<String, String> updateUserPassword(@RequestParam Map<String, String> data) {
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        String confirmPassword = data.get("confirmPassword");
        return userService.updateUserPassword(oldPassword, newPassword, confirmPassword);
    }

    @RequestMapping(value = "/api/user/update/avatar/", method = RequestMethod.POST)
    public JSONObject updateUserAvatar(@RequestParam("file") MultipartFile[] file) {
        //获取文件在服务器的储存位置
        // 0.获取当前用户信息
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();

        JSONObject resp = new JSONObject();

        if (file != null && file.length != 0) {
            // 2.存储文件，并设置用户头像地址信息
            // 2.1.用户头像只有一个
            MultipartFile avatarFile = file[0];
            // 2.2.组织头像文件的存储路径
            String targetPath = Paths.get(imagePath).toString();
            // 2.3.存储文件并返回绝对路径
            String avatarUrlAbs = FileUtil.saveMultipartFile(avatarFile, targetPath);
            //   /opt/web-folder/images/xxx.jpg
            // 2.4.设置用户头像地址(imagePath中去除rootPath的部分)
            //if (StrUtil.isBlank(avatarUrlAbs)) return new ResponseResult(999, "存储文件返回绝对路径为空", avatarFile.getOriginalFilename());
            String avatarUrl = avatarUrlAbs.replace(Paths.get(rootPath).toString(), "");
            // 2.4.1.去掉/images/...最前面的斜杠
            // 3.更新数据库用户信息
            userDAO.updateAvatar(user.getId(), avatarUrl.substring(1));
            resp.put("success", true);
            resp.put("url", webIp + avatarUrl.substring(1));
            resp.put("msg", "上传成功");
            return resp;
        }
        resp.put("success", false);
        resp.put("url", user.getAvatar());
        resp.put("msg", "上传失败");
        return resp;
    }
}
