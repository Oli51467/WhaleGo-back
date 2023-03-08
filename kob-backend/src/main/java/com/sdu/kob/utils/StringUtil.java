package com.sdu.kob.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.sdu.kob.utils.BoardUtil.getStrContainData;

public class StringUtil {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if ((phoneNumber != null) && (!phoneNumber.isEmpty())) {
            return Pattern.matches("^1[3-9]\\d{9}$", phoneNumber);
        }
        return false;
    }

    public static List<String> getSteps(String content) {
        List<String> blackMoves = getStrContainData(content, "B\\[", "\\]");
        List<String> whiteMoves = getStrContainData(content, "W\\[", "\\]");
        List<String> moves = new LinkedList<>();
        int bs = blackMoves.size(), ws = whiteMoves.size();
        int i = 0;
        while(i < bs || i < ws) {
            if (i < bs) moves.add(blackMoves.get(i));
            if (i < ws) moves.add(whiteMoves.get(i));
            i ++;
        }
        return moves;
    }

    public static List<Double> getDoubleListSplitByComma(String content) {
        if (null == content || content.equals("")) return null;
        return Arrays.stream(content.split(",")).map(s -> Double.parseDouble(s.trim())).collect(Collectors.toList());
    }
}
