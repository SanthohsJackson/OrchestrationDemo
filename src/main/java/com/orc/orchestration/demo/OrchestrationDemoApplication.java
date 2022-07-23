package com.orc.orchestration.demo;

import com.orc.orchestration.demo.config.ClassScanner;
import com.orc.orchestration.demo.config.JobOrder;
import com.orc.orchestration.demo.service.ExecutorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Set;

@SpringBootApplication
public class OrchestrationDemoApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClassScanner classScanner;

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        classScanner.setJobOrder();
        applicationContext.getBean(ExecutorServiceImpl.class).getBulkJobExecutor().execute(new Object());
    }
}
