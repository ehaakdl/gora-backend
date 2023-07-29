package com.gora.backend.config;

import com.querydsl.core.annotations.Config;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
