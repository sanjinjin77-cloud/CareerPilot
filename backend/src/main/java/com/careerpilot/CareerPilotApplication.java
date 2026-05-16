package com.careerpilot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.careerpilot.mapper")
public class CareerPilotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerPilotApplication.class, args);
    }
}
