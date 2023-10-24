package com.mudiocean.playmdapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class PlaymdapiApplication {

    @Autowired
    ApplicationContext context;
    @Bean
    ApplicationRunner init() {
        return args -> {
            System.out.println("Hello World!");
            String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
            System.out.println(today);
            Environment env = context.getEnvironment();
            System.out.println(Arrays.toString(env.getActiveProfiles()));
            System.out.println(env.getProperty("CMEMCD"));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(PlaymdapiApplication.class, args);
    }

}
