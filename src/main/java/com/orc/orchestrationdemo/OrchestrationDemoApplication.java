package com.orc.orchestrationdemo;

import com.orc.orchestrationdemo.service.ExecutorServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrchestrationDemoApplication {

    public static void main(String[] args) {

        new ExecutorServiceImpl().getBulkJobExecutor().execute(new Object());
        SpringApplication.run(OrchestrationDemoApplication.class, args);
    }

}
