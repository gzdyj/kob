package com.kob.matchingsystem.service;

public interface MatchingService {
    String addPlayer(Integer userId, Integer rating, Integer botId); //从匹配池添加一位玩家
    String removePlayer( Integer userId);
}
