package com.dst.locarusdbcloner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LocarusdbclonerApplication {

    public static void main(String[] args) {
//        SpringApplication.run(LocarusdbclonerApplication.class, args);

        DbCloner dbCloner = new DbCloner();

        while (true) {
            dbCloner.operate();
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
