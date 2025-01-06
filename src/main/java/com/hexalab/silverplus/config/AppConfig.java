package com.hexalab.silverplus.config;

import com.hexalab.silverplus.common.FTPUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public FTPUtility ftpUtility() {
        return new FTPUtility();
    }
}
