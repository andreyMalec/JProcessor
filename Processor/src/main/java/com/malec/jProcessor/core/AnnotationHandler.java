package com.malec.jProcessor.core;

import com.malec.jProcessor.core.generation.Logger;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class AnnotationHandler {
    private final RoundEnvironment roundEnv;
    private Logger logger;

    public AnnotationHandler(RoundEnvironment roundEnv, Logger logger) {
        this.roundEnv = roundEnv;
        this.logger = logger;
    }

    public void handleClassAnnotation(Class<? extends Annotation> annotation, Consumer<Element> consumer) {
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(annotation);

        for (Element element : annotated) {
            if (element.getKind() == ElementKind.CLASS) {
                logger.note("Working on " + element.asType().toString() + "...");

                consumer.accept(element);
            }
        }
    }
}
