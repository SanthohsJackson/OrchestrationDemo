package com.orc.orchestration.demo.utils;

import com.orc.orchestration.demo.config.AnnotationScanner;
import com.orc.orchestration.demo.config.JobOrder;
import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Component
public final class ConnectorTaskHelper {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${connector.task.basePackage}")
    private String CONNECTOR_BASE_PACKAGE;


    private ConnectorTaskHelper(){
        super();
    }

    public   LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> sortConnectedTasks() {
        // Use the classpath scanner to scanner for the annotation
        Optional<Set<String>> beanName = AnnotationScanner.getBeanDefinitions(JobOrder.class, CONNECTOR_BASE_PACKAGE);
        LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks =
                new LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>>();
        if(beanName.isPresent()){
            beanName.get().forEach(beanClass -> {
                processBeanName(connectorTasks, beanClass);
            });
        }
        return connectorTasks;
    }


    /**
     * @param connectorTasks
     * @param beanClass
     */
    private   void processBeanName(LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks, String beanClass) {
        try {
            Object connectorObj = applicationContext.getBean(Class.forName(beanClass));
            if (connectorObj instanceof ConnectorTask) {
                ConnectorTask connectorTask = (ConnectorTask) connectorObj;
                JobOrder jobOrderAnnotation = connectorTask.getClass().getAnnotation(JobOrder.class);
                Class<? extends ConnectorTask> afterClass = jobOrderAnnotation.after();

                if (connectorTasks.containsKey(afterClass)) {
                    connectorTasks.get(afterClass).add(connectorTask);
                } else {
                    List<ConnectorTask> afterTasks = new ArrayList<>();
                    afterTasks.add(connectorTask);
                    connectorTasks.put(afterClass, afterTasks);
                }
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading beach definitions");
        }
    }


    /**
     * @param parentTask
     * @param connectorTaskList
     * @param connectorTasksMap
     * @return This method recursively call itself till there is no after task(s) to append to a parent task and then returns
     * consolidated tasks in priority and after value order of execution.
     */
    public Optional<ConnectorTask> appendTasksBasedOnPriorityAndAfterValue(ConnectorTask parentTask, List<ConnectorTask> connectorTaskList,
                                                                            LinkedHashMap<Class<? extends ConnectorTask>,
                                                                                    List<ConnectorTask>> connectorTasksMap) {
        ConnectorTask currentTask = parentTask;
        if (connectorTaskList != null && connectorTaskList.size() > 0) {
            List<ConnectorTask> sortedConnectorTask = connectorTaskList.stream().
                    sorted(Comparator.comparingInt(x -> x.getClass().getAnnotation(JobOrder.class).priority()))
                    .collect(Collectors.toList());

            for (ConnectorTask sortedTask : sortedConnectorTask) {
                List<ConnectorTask> afterTaskList = connectorTasksMap.get(sortedTask.getClass());

                if (afterTaskList != null && afterTaskList.size() > 0) {
                    ConnectorTask nextConnectorTask = appendTasksBasedOnPriorityAndAfterValue(sortedTask, afterTaskList, connectorTasksMap).get();
                    currentTask = currentTask.appendNext(nextConnectorTask);
                } else {
                    currentTask = currentTask.appendNext(sortedTask);
                }

            }
        }
        return Optional.ofNullable(currentTask);
    }

}
