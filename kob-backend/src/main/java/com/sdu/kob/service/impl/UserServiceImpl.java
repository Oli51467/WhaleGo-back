package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Friend;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.FriendDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.response.ResponseCode;
import com.sdu.kob.response.ResponseResult;
import com.sdu.kob.service.UserService;
import com.sdu.kob.utils.FileUtil;
import com.sdu.kob.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.*;

import static com.sdu.kob.consumer.WebSocketServer.users;
import static com.sdu.kob.utils.StringUtil.isValidPhoneNumber;

@Service("UserService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private FriendDAO friendDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${file.images-path}")
    private String imagePath;

    @Value("${file.root-path}")
    private String rootPath;

    @Value("${web-ip}")
    private String webIp;

    @Override
    public JSONObject searchUser(String searchName, String userName) {
        User searchUser = userDAO.findByUserName(searchName);
        Long userId = userDAO.findByUserName(userName).getId();  // 自己的id
        JSONObject resp = new JSONObject();
        if (searchUser == null) {
            resp.put("user", "none");
        } else {
            Friend relationshipA = friendDAO.findByUserAAndUserB(userId, searchUser.getId());
            Friend relationshipB = friendDAO.findByUserAAndUserB(searchUser.getId(), userId);
            if (relationshipA == null && relationshipB == null) {
                resp.put("relation", "stranger");
            } else if (relationshipA == null) {
                if (relationshipB.getFollowed().equals("false")) {
                    resp.put("relation", "stranger");
                } else if (relationshipB.getFollowed().equals("true")) {
                    resp.put("relation", "follower");
                }
            } else if (relationshipB == null) {
                if (relationshipA.getFollowed().equals("false")) {
                    resp.put("relation", "stranger");
                } else if (relationshipA.getFollowed().equals("true")) {
                    resp.put("relation", "followed");
                }
            } else {
                String ra = relationshipA.getFollowed(), rb = relationshipB.getFollowed();
                if (ra.equals("true") && rb.equals("true")) {
                    resp.put("relation", "friend");
                } else if (ra.equals("false") && rb.equals("true")) {
                    resp.put("relation", "follower");
                } else if (ra.equals("true") && rb.equals("false")) {
                    resp.put("relation", "followed");
                } else {
                    resp.put("relation", "stranger");
                }
            }
            resp.put("user", "exist");
            JSONObject item = new JSONObject();
            item.put("id", searchUser.getId());
            item.put("username", searchUser.getUserName());
            item.put("avatar", searchUser.getAvatar());
            item.put("level", searchUser.getRating());
            resp.put("info", item);
        }
        return resp;
    }

    @Override
    public JSONObject getFollowedAndFollowersCountAndGuests(Long userId, String userName) {
        User user = userDAO.findById((long) userId);
        User me = userDAO.findByUserName(userName);
        int followed = friendDAO.countByUserAAndFollowed(userId, "true");
        int followers = friendDAO.countByUserBAndFollowed(userId, "true");
        List<JSONObject> itemsFront = new LinkedList<>();
        List<JSONObject> itemsBack = new LinkedList<>();
        String guests = user.getGuests();
        LinkedList<String> clearIdx = new LinkedList<>(Arrays.asList(guests.split(";")));
        LinkedList<String> guestIds = new LinkedList<>();
        for (String idx : clearIdx) {
            if (idx != null && !idx.equals("")) guestIds.add(idx);
        }
        if (!userName.equals(user.getUserName())) {
            userDAO.updateGuestsCount(userId);
            for (int i = 0; i < guestIds.size(); i ++ ) {
                if (Long.parseLong(guestIds.get(i)) == me.getId()) {
                    guestIds.remove(i);
                    break;
                }
            }
            if (guestIds.size() >= 12) {
                guestIds.remove(guestIds.size() - 1);
            }
            guestIds.add(0, me.getId().toString());
            StringBuffer sb = new StringBuffer();
            for (String guestId : guestIds) {
                sb.append(";").append(guestId);
            }
            userDAO.updateRecentGuests(userId, sb.toString());
        }
        int cnt = 0;
        for (String guestId : guestIds) {
            if (guestId.equals("")) continue;
            JSONObject item = new JSONObject();
            User guest = userDAO.findById(Long.parseLong(guestId));
            item.put("guests_id", guest.getId().toString());
            item.put("guests_username", guest.getUserName());
            item.put("guests_avatar", guest.getAvatar());
            if (cnt <= 5) itemsFront.add(item);
            else itemsBack.add(item);
            cnt ++;
        }
        int guestsCount = user.getGuestsCount();
        JSONObject resp = new JSONObject();
        resp.put("followed_count", followed);
        resp.put("followers_count", followers);
        resp.put("guests_count", guestsCount);
        resp.put("username", user.getUserName());
        resp.put("id", user.getId());
        resp.put("recent_records", user.getRecentRecords());
        resp.put("user_avatar", user.getAvatar());
        resp.put("user_level", user.getRating());
        resp.put("profile", user.getProfile());
        resp.put("guests_front", itemsFront);
        resp.put("guests_back", itemsBack);
        return resp;
    }

    @Override
    public Map<String, String> updateUserInfo(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();

        String username = data.get("username");
        String profile = data.get("profile");
        String phone = data.get("phone");

        Map<String, String> map = new HashMap<>();

        if (username == null || username.equals("")) {
            map.put("msg", "用户名不能为空");
            return map;
        } else if (!isValidPhoneNumber(phone)) {
            map.put("msg", "手机号格式错误");
            return map;
        }
        userDAO.updateUserInfo(user.getId(), username, phone, profile);
        map.put("msg", "success");
        return map;
    }

    @Override
    public Map<String, String> updateUserPassword(String oldPassword, String newPassword, String confirmPassword) {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();
        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        Map<String, String> map = new HashMap<>();

        if (!matches) {
            map.put("msg", "原密码错误");
            return map;
        }
        if (newPassword == null || confirmPassword == null || newPassword.equals("") || confirmPassword.equals("")) {
            map.put("msg", "密码不能为空");
            return map;
        }
        if (newPassword.equals(oldPassword)) {
            map.put("msg", "新旧密码不能相同");
            return map;
        }
        if (!newPassword.equals(confirmPassword)) {
            map.put("msg", "两次输入的密码不同");
            return map;
        }

        String passwordEncode = passwordEncoder.encode(newPassword);
        userDAO.updateUserInfo(user.getId(), user.getUserName(), user.getPhone(), passwordEncode);
        String jwt = JwtUtil.createJWT(user.getId().toString());
        map.put("msg", "success");
        map.put("token", jwt);
        return map;
    }

    @Override
    public ResponseResult updateUserAvatar(MultipartFile[] file) {
        //获取文件在服务器的储存位置
        // 0.获取当前用户信息
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authentication.getPrincipal();
        User user = loginUser.getUser();

        // 1.存储文件，并设置用户头像地址信息
        if (file != null && file.length != 0) {
            // 1.1.用户头像只有一个
            MultipartFile avatarFile = file[0];
            // 1.2.组织头像文件的存储路径
            String targetPath = Paths.get(imagePath).toString();
            // 1.3.存储文件并返回绝对路径
            String avatarUrlAbs = FileUtil.saveMultipartFile(avatarFile, targetPath, user.getUserName());
            // /opt/web-folder/images/xxx.jpg
            // 1.4.设置用户头像地址(imagePath中去除rootPath的部分)
//            if (StrUtil.isBlank(avatarUrlAbs)) return new ResponseResult(ResponseCode.PATH_NOT_EXIST.getCode(),
//                                                        ResponseCode.PATH_NOT_EXIST.getMsg(),
//                                                            avatarFile.getOriginalFilename());
            // 1.4.1.去掉/images/...最前面的斜杠
            String avatarUrl = avatarUrlAbs.replace(Paths.get(rootPath).toString(), "");
            // 2.更新数据库用户信息
            userDAO.updateAvatar(user.getId(), avatarUrl.substring(1));
            return new ResponseResult(ResponseCode.UPLOAD_SUCCESS.getCode(), ResponseCode.UPLOAD_SUCCESS.getMsg(),
                    webIp + avatarUrl.substring(1));
        }
        return new ResponseResult(ResponseCode.UPLOAD_FAILED.getCode(), ResponseCode.UPLOAD_FAILED.getMsg(),
                user.getAvatar());
    }

    @Override
    public ResponseResult getUsersOnline(String username) {
        List<JSONObject> items = new LinkedList<>();
        for (Long userId: users.keySet()) {
            User user = userDAO.findById((long)userId);
            if (user.getUserName().equals(username)) continue;
            JSONObject item = new JSONObject();
            item.put("id", userId);
            item.put("username", user.getUserName());
            item.put("avatar", user.getAvatar());
            item.put("state", 1);
            item.put("status", user.getStatus());
            item.put("level", user.getRating());
            item.put("win", user.getWin());
            item.put("lose", user.getLose());
            items.add(item);
        }
        return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMsg(), items);
    }
}
