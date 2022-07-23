package com.orc.orchestration.demo.config;


import com.orc.orchestration.demo.task.ConnectorTask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobOrder {
    int priority() default Integer.MAX_VALUE;
    Class<? extends ConnectorTask> after() default ConnectorTask.class;
}
