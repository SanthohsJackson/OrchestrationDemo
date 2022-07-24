package com.orc.orchestration.demo.executor;

import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.stereotype.Component;

@Component
public class BulkJobExecutor implements  Executor{

    private ConnectorTask bulkJobProcess;


    public void execute(ConnectorTask bulkJobProcess,Object context){
        this.bulkJobProcess = bulkJobProcess;
        bulkJobProcess.executeChain(context);
    }
}
