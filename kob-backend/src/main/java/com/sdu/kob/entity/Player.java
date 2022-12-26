package com.sdu.kob.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;
    private Integer sx, sy;
    private List<Integer> steps;

    /**
     * 检查当前回合蛇的长度是否增加
     * @param steps 回合数
     * @return 蛇长是否增加
     */
    private boolean checkTailIncreasing(int steps) {
        if (steps <= 10) return true;
        return steps % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));
        for (int d: steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            // 蛇尾如果不增加就把蛇尾删掉
            if (!checkTailIncreasing( ++ step)) {
                res.remove(0);
            }
        }
        return res;
    }

    public String getSteps2String() {
        StringBuilder res = new StringBuilder();
        for (Integer step : steps) {
            res.append(step);
        }
        return res.toString();
    }
}
