package com.sdu.kob.utils;

import java.util.HashMap;
import java.util.Map;

public class RatingUtil {

    public static String getRating2Level(Integer rating) {
        if (rating > 2000) {
            return "9段";
        } else if (rating > 1900) {
            return "8段";
        } else if (rating > 1800) {
            return "7段";
        } else if (rating > 1700) {
            return "6段";
        } else if (rating > 1620) {
            return "5段";
        } else if (rating > 1550) {
            return "4段";
        } else if (rating > 1480) {
            return "3段";
        } else {
            return "2段";
        }
    }
}