package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Record;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.RecordDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.RecordService;
import com.sdu.kob.utils.RatingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service("RecordService")
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordDAO recordDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public JSONObject getAllRecords(Integer userId, Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Record> recordsPage = recordDAO.findOthers(userId, pageable);
        List<Record> records = recordsPage.toList();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record: records) {
            User userBlack = userDAO.findById((int) record.getBlackId());
            User userWhite = userDAO.findById((int) record.getWhiteId());
            JSONObject item = new JSONObject();
            item.put("black_avatar", userBlack.getAvatar());
            item.put("black_username", userBlack.getUserName());
            item.put("black_level", RatingUtil.getRating2Level(userBlack.getRating()));
            item.put("white_avatar", userWhite.getAvatar());
            item.put("white_username", userWhite.getUserName());
            item.put("white_level", RatingUtil.getRating2Level(userWhite.getRating()));
            item.put("result", record.getResult());
            item.put("record", record);
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", recordDAO.count());    // 总页数
        return resp;
    }

    @Override
    public JSONObject getMyRecords(Integer userId, Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, 10, sort);
        Page<Record> recordsPage = recordDAO.findMyRecords(userId, pageable);
        List<Record> records = recordsPage.toList();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record: records) {
            User userBlack = userDAO.findById((int) record.getBlackId());
            User userWhite = userDAO.findById((int) record.getWhiteId());
            JSONObject item = new JSONObject();
            item.put("black_avatar", userBlack.getAvatar());
            item.put("black_username", userBlack.getUserName());
            item.put("black_level", RatingUtil.getRating2Level(userBlack.getRating()));
            item.put("white_avatar", userWhite.getAvatar());
            item.put("white_username", userWhite.getUserName());
            item.put("white_level", RatingUtil.getRating2Level(userWhite.getRating()));
            item.put("result", record.getResult());
            item.put("record", record);
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", recordDAO.count());    // 总页数
        return resp;
    }
}
