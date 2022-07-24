package com.orc.orchestration.demo;

import com.orc.orchestration.demo.config.AdvancedClassScanner;
import com.orc.orchestration.demo.service.ExecutorServiceImpl;
import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class OrchestrationDemoApplication implements CommandLineRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AdvancedClassScanner classScanner;

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<ConnectorTask> connectorTask = classScanner.getJobExecutionOrder();
        if (connectorTask.isPresent())
            applicationContext.getBean(ExecutorServiceImpl.class).executeJobOrder(connectorTask.get(), new Object());
    }
}
