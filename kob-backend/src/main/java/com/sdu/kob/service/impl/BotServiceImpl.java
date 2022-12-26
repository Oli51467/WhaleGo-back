package com.sdu.kob.service.impl;

import com.sdu.kob.domain.Bot;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.BotDAO;
import com.sdu.kob.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("BotService")
public class BotServiceImpl implements BotService {

    @Autowired
    private BotDAO botDAO;

    @Override
    public Map<String, String> addBot(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("msg", "标题不能为空");
            return map;
        }

        if (title.length() > 100) {
            map.put("msg", "标题长度不能大于100");
            return map;
        }

        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 300) {
            map.put("msg", "Bot描述的长度不能大于300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("msg", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("msg", "代码长度不能超过10000");
            return map;
        }
        System.out.println(title + " " + description + " " + content + "\n");
        Date now = new Date();
        Bot bot = new Bot(user.getId(), title, description, content, now, now);

        botDAO.save(bot);
        map.put("msg", "success");

        return map;
    }


    @Override
    public List<Bot> getUserBots() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();
        int userID = user.getId();
        return botDAO.findByUserId(userID);
    }

    @Override
    public Map<String, String> removeBot(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int bot_id = Integer.parseInt(data.get("bot_id"));
        Bot bot = botDAO.findById(bot_id);
        Map<String, String> map = new HashMap<>();

        if (bot == null) {
            map.put("msg", "Bot不存在或已被删除");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("msg", "没有权限删除该Bot");
            return map;
        }

        botDAO.deleteById(bot_id);

        map.put("msg", "success");
        return map;
    }

    @Override
    public Map<String, String> updateBot(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        int bot_id = Integer.parseInt(data.get("bot_id"));

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();

        if (title == null || title.length() == 0) {
            map.put("msg", "标题不能为空");
            return map;
        }

        if (title.length() > 100) {
            map.put("msg", "标题长度不能大于100");
            return map;
        }

        if (description == null || description.length() == 0) {
            description = "这个用户很懒，什么也没留下~";
        }

        if (description.length() > 300) {
            map.put("msg", "Bot描述的长度不能大于300");
            return map;
        }

        if (content == null || content.length() == 0) {
            map.put("msg", "代码不能为空");
            return map;
        }

        if (content.length() > 10000) {
            map.put("msg", "代码长度不能超过10000");
            return map;
        }

        Bot bot = botDAO.findById(bot_id);

        if (bot == null) {
            map.put("msg", "Bot不存在或已被删除");
            return map;
        }

        if (!bot.getUserId().equals(user.getId())) {
            map.put("msg", "没有权限修改该Bot");
            return map;
        }

        botDAO.update(bot_id, user.getId(), title, description, content, new Date());

        map.put("msg", "success");

        return map;
    }

}
