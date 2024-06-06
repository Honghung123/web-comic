package com.group17.comic.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CommonConfiguration {
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}