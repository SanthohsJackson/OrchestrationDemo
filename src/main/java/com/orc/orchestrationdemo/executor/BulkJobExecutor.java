package com.orc.orchestrationdemo.executor;

import com.orc.orchestrationdemo.task.ConnectorTask;

public class BulkJobExecutor implements  Executor{

    private ConnectorTask bulkJobProcess;

    public BulkJobExecutor(ConnectorTask bulkJobProcess) {
        this.bulkJobProcess = bulkJobProcess;
    }


    public void execute(Object context){
        bulkJobProcess.executeChain(context);
    }
}
