package com.orc.orchestration.demo.task;

import com.orc.orchestration.demo.config.JobOrder;
import org.springframework.stereotype.Component;

@Component
@JobOrder(priority = Integer.MAX_VALUE, after = JobCompleteTask.class)
public class SetRecordCompleteTask implements ConnectorTask {
    @Override
    public void process(Object context) {
        System.out.println("Clean up and commit record status");
    }
}
