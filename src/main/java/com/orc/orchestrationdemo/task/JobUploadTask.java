package com.orc.orchestrationdemo.task;

import com.orc.orchestrationdemo.task.ConnectorTask;

public class JobUploadTask implements ConnectorTask {


    @Override
    public void process(Object context) {
        System.out.println("Job uploaded");
    }
}
