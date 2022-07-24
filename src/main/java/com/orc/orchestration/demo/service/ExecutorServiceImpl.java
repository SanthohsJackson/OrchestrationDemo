package com.orc.orchestration.demo.service;

import com.orc.orchestration.demo.executor.BulkJobExecutor;
import com.orc.orchestration.demo.executor.Executor;
import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExecutorServiceImpl {

    @Autowired
    private Executor executor;

    public Executor getBulkJobExecutor() {
        return executor;
    }


    public void executeJobOrder(ConnectorTask connectorTask ,Object context){
        executor.execute(connectorTask,context);
    }
}
