package com.arcvideo.pgcliveplatformserver;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2Doc
public class PgcLivePlatformServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(PgcLivePlatformServerApplication.class, args);
	}
}
