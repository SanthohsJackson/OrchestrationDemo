package com.orc.orchestration.demo.task;

import com.orc.orchestration.demo.config.JobOrder;
import org.springframework.stereotype.Component;

@Component
@JobOrder(priority = 1,after = JobCompleteTask.class)
public class GetJobResultTask implements ConnectorTask {
    @Override
    public void process(Object context) {
        System.out.println("Got Job Result");
    }
}
