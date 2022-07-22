package com.orc.orchestrationdemo.task;

import com.orc.orchestrationdemo.task.ConnectorTask;

public class JobCreateTask implements ConnectorTask {


    @Override
    public void process(Object context) {
        System.out.println("Job Created");

    }


}
