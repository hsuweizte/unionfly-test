package com.hsuweizte.unionflytest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableCaching
@MapperScan("com.hsuweizte.unionflytest.mapper.*")
@SpringBootApplication
public class UnionflyTestApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(UnionflyTestApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
