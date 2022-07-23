package com.orc.orchestration.demo.executor;

import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.stereotype.Component;

@Component
public class BulkJobExecutor implements  Executor{

    private ConnectorTask bulkJobProcess;

    public void setBulkJobProcess(ConnectorTask bulkJobProcess) {
        this.bulkJobProcess = bulkJobProcess;
    }

    public void execute(Object context){
        bulkJobProcess.executeChain(context);
    }
}
