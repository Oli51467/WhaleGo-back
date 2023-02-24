package com.sdu.kob.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoardUtil {
    public static List<String> getStrContainData(String str, String start, String end) {
        List<String> result = new ArrayList<>();
        String regex = start + "(.*?)" + end;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String key = matcher.group(1);
            if (!key.contains(start) && !key.contains(end)) {
                result.add(key);
            }
        }
        return result;
    }

    public static String getPositionByIndex(int x, int y) {
        String position = "";
        int cnt = 1;
        for (char c = 'A'; c <= 'T'; c ++ ) {
            if (c == 'I') continue;
            if (cnt == y) {
                position += c;
                break;
            }
            cnt ++;
        }
        position += 20 - x;
        return position;
    }

    public static int[] getNext(String alpha, String number) {
        int[] res = new int[2];
        res[0] = 20 - Integer.parseInt(number);
        int cnt = 1;
        for (char c = 'A'; c <= 'T'; c++) {
            if (c == 'I') continue;
            if (String.valueOf(c).equals(alpha)) break;
            cnt++;
        }
        res[1] = cnt;
        return res;
    }
}
