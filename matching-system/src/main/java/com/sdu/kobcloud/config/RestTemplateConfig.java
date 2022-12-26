package com.sdu.kobcloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    // 想取得谁就加一个bean注解
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
