package com.orc.orchestration.demo.executor;

import com.orc.orchestration.demo.task.ConnectorTask;

public interface Executor {

    void execute(Object context);

    void setBulkJobProcess(ConnectorTask bulkJobProcess);
}
