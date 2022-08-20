package com.kob.backend.consumer.utils;

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
    private Integer sx; //行数
    private Integer sy; //列数
    private List<Integer> steps; //方向

    private boolean check_tail_increasing(int step) { //检测当前回合，判断长度是否会增加
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();
        int step = 0;
        //四个方向
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0 , -1};
        int x = sx , y = sy;
        res.add(new Cell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing( ++ step)) { //如果蛇身增加就不会删除尾巴。不增加就要蛇头移动一格，再把尾巴删掉
                res.remove(0);
            }
        }
        return res;

    }

    public String getStepString() {
        StringBuilder res = new StringBuilder();
        for (int d : steps) {
            res.append(d);
        }
        return res.toString();
    }

}
