package com.dst.locarusdbcloner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class LocarusdbclonerApplication {

    public static void main(String[] args) {
//        SpringApplication.run(LocarusdbclonerApplication.class, args);

        DbCloner dbCloner = new DbCloner();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        long delay  = 1L;
        long period = 60L;
        executorService.scheduleWithFixedDelay(dbCloner, delay, period, TimeUnit.SECONDS);
    }
}
