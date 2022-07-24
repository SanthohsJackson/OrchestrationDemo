package com.orc.orchestration.demo.config;

import com.orc.orchestration.demo.task.ConnectorTask;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationScanner {

    public static Optional<Set<String>> getBeanDefinitions(Class<? extends Annotation> annotation , String basePackage) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        Set<BeanDefinition> beanDefs = provider.findCandidateComponents(basePackage);
        Set<String> beanNames = beanDefs.stream().map(BeanDefinition::getBeanClassName).collect(Collectors.toSet());
        return Optional.of(beanNames);
    }
}
