package com.kob.botrunningsystem;

import com.kob.botrunningsystem.Service.impl.BotRunningServiceimpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args) {
        BotRunningServiceimpl.botPool.start();
        SpringApplication.run(BotRunningSystemApplication.class, args);
    }
}
