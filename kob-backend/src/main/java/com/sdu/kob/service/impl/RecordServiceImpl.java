package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdu.kob.domain.SnakeRecord;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.SnakeRecordDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service("RecordService")
public class RecordServiceImpl implements RecordService {

    @Autowired
    private SnakeRecordDAO snakeRecordDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public JSONObject getRecordList(Integer page) {
        IPage<SnakeRecord> recordIPage = new Page<>(page, 10);
        QueryWrapper<SnakeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        List<SnakeRecord> records = snakeRecordDAO.selectPage(recordIPage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (SnakeRecord snakeRecord: records) {
            User userA, userB;
            if (snakeRecord.getAId() < 0) {
                userA = new User("AI", "1", 1500, "https://cdn.acwing.com/media/article/image/2022/07/07/1_535cd642fd-kob2.png");
            } else {
                userA = userDAO.findById((int)snakeRecord.getAId());
            }
            if (snakeRecord.getBId() < 0) {
                userB = new User("AI", "1", 1500, "https://cdn.acwing.com/media/article/image/2022/07/07/1_535cd642fd-kob2.png");
            } else {
                userB = userDAO.findById((int)snakeRecord.getBId());
            }
            JSONObject item = new JSONObject();
            item.put("a_avatar", userA.getAvatar());
            item.put("a_username", userA.getUserName());
            item.put("b_avatar", userB.getAvatar());
            item.put("b_username", userB.getUserName());
            item.put("record", snakeRecord);
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", snakeRecordDAO.selectCount(null));    // 总页数
        return resp;
    }
}
