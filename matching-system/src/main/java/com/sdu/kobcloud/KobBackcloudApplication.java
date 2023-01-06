package com.sdu.kobcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.sdu.kobcloud.service.impl.GoMatchingServiceImpl.goMatchingPool;

@SpringBootApplication
public class KobBackcloudApplication {
    public static void main(String[] args) {
        goMatchingPool.start();
        SpringApplication.run(KobBackcloudApplication.class, args);
    }
}