package com.sdu.kob.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sdu.kob.domain.Record;
import com.sdu.kob.domain.User;
import com.sdu.kob.repository.RecordDAO;
import com.sdu.kob.repository.UserDAO;
import com.sdu.kob.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static com.sdu.kob.utils.StringUtil.getDoubleListSplitByComma;
import static com.sdu.kob.utils.StringUtil.getSteps;

@Service("RecordService")
public class RecordServiceImpl implements RecordService {

    @Autowired
    private RecordDAO recordDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public JSONObject getAllRecords(Long userId, Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(page, 9, sort);
        Page<Record> recordsPage = recordDAO.findOthers(userId, pageable);
        List<Record> records = recordsPage.toList();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record: records) {
            User userBlack = userDAO.findById((long) record.getBlackId());
            User userWhite = userDAO.findById((long) record.getWhiteId());
            JSONObject item = new JSONObject();
            item.put("id", record.getId());
            item.put("result", record.getResult());
            item.put("create_time", record.getCreateTime().toString().substring(0, record.getCreateTime().toString().lastIndexOf(":")));
            item.put("black_avatar", userBlack.getAvatar());
            item.put("black_username", userBlack.getUserName());
            item.put("black_userid", userBlack.getId());
            item.put("black_level", userBlack.getRating());
            item.put("white_avatar", userWhite.getAvatar());
            item.put("white_username", userWhite.getUserName());
            item.put("white_userid", userWhite.getId());
            item.put("white_level", userWhite.getRating());
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", recordDAO.countOthers(userId));    // 总页数
        return resp;
    }

    @Override
    public JSONObject getMyRecords(Long userId, Integer page) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<Record> records;
        if (page == -1) {
            records = recordDAO.findMyRecords(userId);
        } else {
            Pageable pageable = PageRequest.of(page, 9, sort);
            Page<Record> recordsPage = recordDAO.findMyRecords(userId, pageable);
            records = recordsPage.toList();
        }
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record: records) {
            User userBlack = userDAO.findById((long) record.getBlackId());
            User userWhite = userDAO.findById((long) record.getWhiteId());
            JSONObject item = new JSONObject();
            item.put("id", record.getId());
            item.put("result", record.getResult());
            item.put("create_time", record.getCreateTime().toString().substring(0, record.getCreateTime().toString().lastIndexOf(":")));
            item.put("black_avatar", userBlack.getAvatar());
            item.put("black_username", userBlack.getUserName());
            item.put("black_userid", userBlack.getId());
            item.put("black_level", userBlack.getRating());
            item.put("white_avatar", userWhite.getAvatar());
            item.put("white_username", userWhite.getUserName());
            item.put("white_userid", userWhite.getId());
            item.put("white_level", userWhite.getRating());
            items.add(item);
        }
        resp.put("records", items);
        resp.put("records_count", recordDAO.countByMyRecords(userId));    // 总页数
        return resp;
    }

    @Override
    public JSONObject getRecordDetails(long recordId) {
        JSONObject resp = new JSONObject();
        Record record = recordDAO.findById(recordId);
        User userBlack = userDAO.findById((long) record.getBlackId());
        User userWhite = userDAO.findById((long) record.getWhiteId());
        resp.put("steps", getSteps(record.getSteps()));
        resp.put("win_rate", getDoubleListSplitByComma(record.getWinRate()));
        JSONObject item = new JSONObject();
        item.put("black_avatar", userBlack.getAvatar());
        item.put("black_id", userBlack.getId());
        item.put("black_username", userBlack.getUserName());
        item.put("black_level", userBlack.getRating());
        item.put("white_avatar", userWhite.getAvatar());
        item.put("white_id", userWhite.getId());
        item.put("white_username", userWhite.getUserName());
        item.put("white_level", userWhite.getRating());
        item.put("result", record.getResult());
        resp.put("record", item);
        return resp;
    }


}
