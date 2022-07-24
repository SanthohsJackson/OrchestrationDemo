package com.orc.orchestration.demo.service;

import com.orc.orchestration.demo.config.AnnotationScanner;
import com.orc.orchestration.demo.config.JobOrder;
import com.orc.orchestration.demo.task.ConnectorTask;
import com.orc.orchestration.demo.utils.ConnectorTaskHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConnectorTaskServiceImpl implements ConnectorTaskService{

private ConnectorTaskHelper connectorTaskHelper;

    public ConnectorTaskServiceImpl(ConnectorTaskHelper connectorTaskHelper) {
        this.connectorTaskHelper = connectorTaskHelper;
    }

    public Optional<ConnectorTask> getJobExecutionOrder() {
        //Sort the task based on who will come before them ,using the "after" value
        LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks = connectorTaskHelper.sortConnectedTasks();

        //In case we need to add pre-configuration to be executed, can be removed if not needed
        ConnectorTask currentTask = context -> System.out.println("Pre-configurations");

        //Append the tasks based on priority and after value.
        if (connectorTasks.containsKey(ConnectorTask.class)) {
            List<ConnectorTask> initialList = connectorTasks.get(ConnectorTask.class);
            Optional<ConnectorTask> connectorTask = connectorTaskHelper.appendTasksBasedOnPriorityAndAfterValue(currentTask, initialList, connectorTasks);
            return connectorTask;
        }
        return Optional.empty();

    }







}


