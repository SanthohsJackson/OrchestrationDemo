package com.orc.orchestration.demo.config;

import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AdvancedClassScanner {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${connector.task.basePackage}")
    private String CONNECTOR_BASE_PACKAGE;


    public Optional<ConnectorTask> getJobExecutionOrder() {
        // Use the classpath scanner to scanner for the annotation
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(JobOrder.class));

        //Sort the task based on who will come before them ,using the "after" value
        LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks = sortConnectedTasks(provider);

        //In case we need to add pre-configuration to be executed, can be removed if not needed
        ConnectorTask currentTask = context -> System.out.println("Pre-configurations");

        //Append the tasks based on priority and after value.
        if (connectorTasks.containsKey(ConnectorTask.class)) {
            List<ConnectorTask> initialList = connectorTasks.get(ConnectorTask.class);
            Optional<ConnectorTask> connectorTask = appendTasksBasedOnPriorityAndAfterValue(currentTask, initialList, connectorTasks);
            return connectorTask;

        }

        return Optional.empty();

    }

    private LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> sortConnectedTasks(ClassPathScanningCandidateComponentProvider provider) {
        LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>> connectorTasks = new LinkedHashMap<Class<? extends ConnectorTask>, List<ConnectorTask>>();
        Set<BeanDefinition> beanDefs = provider.findCandidateComponents(CONNECTOR_BASE_PACKAGE);
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

    /**
     * @param parentTask
     * @param connectorTaskList
     * @param connectorTasksMap
     * @return This method recursively call itself till there is no after task(s) to append to a parent task and then returns
     * consolidated tasks in priority and after value order of execution.
     */
    private Optional<ConnectorTask> appendTasksBasedOnPriorityAndAfterValue(ConnectorTask parentTask, List<ConnectorTask> connectorTaskList,
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


