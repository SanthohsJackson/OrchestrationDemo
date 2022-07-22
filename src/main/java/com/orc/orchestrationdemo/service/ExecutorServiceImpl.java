package com.orc.orchestrationdemo.service;

import com.orc.orchestrationdemo.executor.BulkJobExecutor;
import com.orc.orchestrationdemo.executor.Executor;
import com.orc.orchestrationdemo.task.ConnectorTask;
import com.orc.orchestrationdemo.task.JobCompleteTask;
import com.orc.orchestrationdemo.task.JobCreateTask;
import com.orc.orchestrationdemo.task.JobUploadTask;

public class ExecutorServiceImpl {

    public Executor getBulkJobExecutor() {
        ConnectorTask bulkUploadProcess = new JobCreateTask().appendNext(new JobUploadTask()).appendNext(new JobCompleteTask());
        Executor executor = new BulkJobExecutor(bulkUploadProcess);
        return executor;

    }
}
