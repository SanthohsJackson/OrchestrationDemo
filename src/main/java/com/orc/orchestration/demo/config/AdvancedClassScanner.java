package com.orc.orchestration.demo.config;

import com.orc.orchestration.demo.executor.Executor;
import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.*;

@Component
public class AdvancedClassScanner {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Executor executor;

    public void setJobOrder() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(JobOrder.class));

        LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks = sortConnectedTasks(provider);

        ConnectorTask currentTask = new ConnectorTask() {
            @Override
            public void process(Object context) {
                System.out.println("Pre-configurations");
            }
        };


        if (connectorTasks.containsKey(ConnectorTask.class)) {
            List<ConnectorTask> initialList = connectorTasks.get(ConnectorTask.class);

            Optional<ConnectorTask> connectorTask = reduceConnectTask(initialList, connectorTasks);
            if (connectorTask.isPresent()) {
                executor.setBulkJobProcess(connectorTask.get());
            }
        }


    }

    private LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> sortConnectedTasks(ClassPathScanningCandidateComponentProvider provider) {
        LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks = new LinkedHashMap();
        Set<BeanDefinition> beanDefs = provider.findCandidateComponents("com.orc.orchestration.demo");
        beanDefs.stream().map(BeanDefinition::getBeanClassName).forEach(beanClass -> {
            try {
                Object connectorObj = applicationContext.getBean(Class.forName(beanClass));
                if (connectorObj instanceof ConnectorTask) {
                    ConnectorTask connectorTask = (ConnectorTask) connectorObj;
                    JobOrder jobOrderAnnotation = connectorTask.getClass().getAnnotation(JobOrder.class);
                    Class<? extends ConnectorTask> afterClass = jobOrderAnnotation.after();

                    if (connectorTasks.containsKey(afterClass)) {
                        connectorTasks.get(afterClass).add(connectorTask);
                    } else {
                        List<ConnectorTask> tasks = new ArrayList<>();
                        tasks.add(connectorTask);
                        connectorTasks.put(afterClass, tasks);
                    }

                }
            } catch (ClassNotFoundException e) {
                System.out.println("Error reading beach definitions");
            }
        });

        return connectorTasks;
    }


    private Optional<ConnectorTask> reduceConnectTask(List<ConnectorTask> connectorTaskList, LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasksMap) {
        Optional<ConnectorTask> result = Optional.empty();
        if (connectorTaskList != null && connectorTaskList.size() > 0) {
            result = connectorTaskList.stream().sorted(Comparator.comparingInt(x -> x.getClass().getAnnotation(JobOrder.class).priority())).reduce((task , nextask) -> {
                List<ConnectorTask> afterTaskList = connectorTasksMap.get(task.getClass());
                System.out.println("Task class: " + task.getClass());

                if (afterTaskList != null && afterTaskList.size() > 0) {
                    ConnectorTask nextConnectorTask = reduceConnectTask(afterTaskList, connectorTasksMap).get();
                    nextConnectorTask = task.appendNext(nextConnectorTask);
                    return nextConnectorTask;
                }
                return task;
            });
        }
        return result;
    }

}


