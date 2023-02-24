package com.sdu.kob.utils;

import com.sdu.kob.domain.User;
import com.sdu.kob.entity.Player;
import org.springframework.stereotype.Component;

import static com.sdu.kob.consumer.WebSocketServer.userDAO;

@Component
public class RecordUtil {

    public static void updateUserRecord(Player winner, Player loser) {
        User winnerUser = userDAO.findById((long)winner.getId());
        User loserUser = userDAO.findById((long)loser.getId());
        Integer win = winnerUser.getWin() + 1;
        Integer lose = loserUser.getLose() + 1;
        String winnerRecentRecords = winnerUser.getRecentRecords();
        String loserRecentRecords = loserUser.getRecentRecords();
        String winLevel = winnerUser.getRating();
        String loseLevel = loserUser.getRating();
        if (winnerRecentRecords.length() > 15) {
            winnerRecentRecords = winnerRecentRecords.substring(0, 14);
        }
        winnerRecentRecords = "胜" + winnerRecentRecords;
        int winCnt = 0, loseCnt = 0;
        if (winnerRecentRecords.contains("胜")) {
            winCnt = winnerRecentRecords.length() - winnerRecentRecords.replaceAll("胜", "").length();
        }
        if (winCnt >= 12) {
            winnerRecentRecords = "";
            int l = Integer.parseInt(winLevel.substring(0, 1));
            l ++;
            winLevel = l + "段";
        }

        if (loserRecentRecords.length() > 15) {
            loserRecentRecords = loserRecentRecords.substring(0, 14);
        }
        loserRecentRecords = "负" + loserRecentRecords;
        if (loserRecentRecords.contains("负")) {
            loseCnt = loserRecentRecords.length() - loserRecentRecords.replaceAll("负", "").length();
        }
        if (loseCnt >= 10) {
            loserRecentRecords = "";
            int l = Integer.parseInt(loseLevel.substring(0, 1));
            l --;
            loseLevel = l + "段";
        }
        userDAO.updateWin(winner.getId(), win, winnerRecentRecords, winLevel);
        userDAO.updateLose(loser.getId(), lose, loserRecentRecords, loseLevel);
    }
}
