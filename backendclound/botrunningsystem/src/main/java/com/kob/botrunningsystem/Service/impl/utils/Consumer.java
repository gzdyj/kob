package com.kob.botrunningsystem.Service.impl.utils;

import com.kob.botrunningsystem.utils.BotInterface;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;
@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }
    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        this.start();

        try {
            this.join(timeout); //最多等待timeout秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt(); // 中断当前线程
        }

    }

    private String addUid(String code, String uid) { //在code中的Bot类后添加uid
        int k = code.indexOf(" implements com.kob.botrunningsystem.utils.BotInterface");
        return code.substring(0, k) + uid + code.substring(k);

    }

    @Override
    public void run() {
        //应为动态编译的类名如果跟上次一样的话，则不会编译
        //每次返回一个不同的id 99.99的概率
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);

        // Reflect 反射的意思 compile汇编的意思 动态编译一个字符串代码
        BotInterface botInterface = Reflect.compile(
                "com.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();
        Integer direction = botInterface.nextMove(bot.getInput());
        System.out.println("move-direction: " + bot.getUserId() + " " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());

        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);

    }

}
