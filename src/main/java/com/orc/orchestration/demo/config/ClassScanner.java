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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class ClassScanner {


    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Executor executor;

    public void setJobOrder() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(JobOrder.class));

        Map<Integer, ConnectorTask> jobs = new HashMap<>();
        ConnectorTask bulkUploadProcess = new ConnectorTask() {
            @Override
            public void process(Object context) {
                System.out.println("Pre-configuration");
            }
        };

        Set<BeanDefinition> beanDefs = provider.findCandidateComponents("com.orc.orchestration.demo");
        beanDefs.stream().map(BeanDefinition::getBeanClassName).forEach(beanClass -> {
            try {
                Object connectorTask = applicationContext.getBean(Class.forName(beanClass));
                if (connectorTask instanceof ConnectorTask) {
                    jobs.put(connectorTask.getClass().getAnnotation(JobOrder.class).value(), (ConnectorTask) connectorTask);
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Error reading beach definitions");
            }
        });

        ConnectorTask currentTask = bulkUploadProcess;
        for (int x = 1; x <= jobs.size(); x++) {
          currentTask = currentTask.appendNext(jobs.get(x));

        }
        executor.setBulkJobProcess(currentTask);

    }
}