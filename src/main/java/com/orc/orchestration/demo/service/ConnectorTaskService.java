package com.orc.orchestration.demo.service;

import com.orc.orchestration.demo.task.ConnectorTask;

import java.util.Optional;

public interface ConnectorTaskService {
    Optional<ConnectorTask> getJobExecutionOrder();
}
