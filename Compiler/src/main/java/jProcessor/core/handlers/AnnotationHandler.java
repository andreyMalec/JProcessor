package jProcessor.core.handlers;

import javax.annotation.processing.RoundEnvironment;

import jProcessor.util.Logger;

public abstract class AnnotationHandler {
    protected final RoundEnvironment roundEnv;
    protected final Logger log;

    public AnnotationHandler(RoundEnvironment roundEnv, Logger log) {
        this.roundEnv = roundEnv;
        this.log = log;
    }

    public abstract void handleAnnotation();
}
