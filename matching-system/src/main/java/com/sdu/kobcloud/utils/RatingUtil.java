package com.sdu.kobcloud.utils;

public class RatingUtil {
    public static Integer getRating(String level) {
        int f = Integer.parseInt(level.substring(0, 1));
        String c = level.substring(1);
        if (c.equals("段")) {
            f = 2000 + 100 * f;
        }
        else {
            f = 2000 - 100 * f;
        }
        return f;
    }
}
