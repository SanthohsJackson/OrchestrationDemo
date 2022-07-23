package com.orc.orchestration.demo.task;

import com.orc.orchestration.demo.config.JobOrder;
import org.springframework.stereotype.Component;

@Component
@JobOrder(priority = 1, after = JobUploadTask.class)
public final class JobCompleteTask implements ConnectorTask {
    @Override
    public void process(Object context) {
        System.out.println("JobComplete Task");
    }
}
