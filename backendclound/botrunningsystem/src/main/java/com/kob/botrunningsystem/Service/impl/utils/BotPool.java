package com.kob.botrunningsystem.Service.impl.utils;

import java.awt.peer.CanvasPeer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BotPool extends Thread{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input) {
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode, input));
            condition.signalAll(); //唤醒线程
        } finally {
            lock.unlock();
        }
    }

    // consume 消耗
    private void consume(Bot bot) {
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000, bot); //最多等待5秒。一个bot等待2秒 另外一秒做冗余
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            if (bots.isEmpty()) {
                try {
                    condition.await(); //让线程沉睡 自动将锁释放
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            } else {
                Bot bot = bots.remove();
                lock.unlock();
                consume(bot); //比较耗时，可能会执行几秒钟


            }
        }
    }
}
