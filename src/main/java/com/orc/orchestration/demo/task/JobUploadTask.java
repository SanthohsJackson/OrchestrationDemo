package com.orc.orchestration.demo.task;

import com.orc.orchestration.demo.config.JobOrder;
import org.springframework.stereotype.Component;

@Component
@JobOrder(value = 2)
public final class JobUploadTask implements ConnectorTask {


    @Override
    public void process(Object context) {
        System.out.println("Job uploaded");
    }
}
