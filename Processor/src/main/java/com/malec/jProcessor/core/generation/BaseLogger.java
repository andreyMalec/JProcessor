package com.malec.jProcessor.core.generation;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class BaseLogger implements Logger {
    private final Messager logger;

    public BaseLogger(Messager logger) {
        this.logger = logger;
    }

    @Override
    public void note(String text) {
        logger.printMessage(Diagnostic.Kind.NOTE, text);
    }

    @Override
    public void error(String text) {
        logger.printMessage(Diagnostic.Kind.ERROR, text);
    }
}
