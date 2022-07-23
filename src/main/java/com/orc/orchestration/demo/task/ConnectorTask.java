package com.orc.orchestration.demo.task;

public interface ConnectorTask {

    default void preProcess(Object context) {
        // do pre-config
    }

    void process(Object context);

    default void postProcess(Object context) {
        // do post-config
    }

    default ConnectorTask appendNext(ConnectorTask nextLogger) {
        return (context) -> {
            executeChain(context);
            nextLogger.executeChain(context);
        };
    }

    default void executeChain(Object context) {
        preProcess(context);
        process(context);
        postProcess(context);
    }

}
