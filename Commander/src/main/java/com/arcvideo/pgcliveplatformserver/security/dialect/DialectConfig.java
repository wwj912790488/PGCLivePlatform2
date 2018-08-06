package com.arcvideo.pgcliveplatformserver.security.dialect;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DialectConfig {

    @Bean
    LoginModeDialect loginModeDialect() {
        return new LoginModeDialect();
    }
}
