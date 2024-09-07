package com.example.flim.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Đăng ký mô-đun JavaTimeModule
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false); // Viết ngày giờ dưới dạng chuỗi ISO-8601
        return mapper;
    }
}
