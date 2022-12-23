package com.sdu.kob.service;

import java.util.Map;

public interface RegisterService {
    public Map<String, String> register(String userName, String password, String confirmedPassword);
}
