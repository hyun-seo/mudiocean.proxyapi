package com.mudiocean.playmdapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.web.util.WebUtils.getCookie;

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
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(PlaymdapiApplication.class, args);
    }

}
