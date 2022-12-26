package com.sdu.kob.service;

import java.util.Map;

public interface LoginService {
    Map<String, String> getToken(String userName, String password);
}
