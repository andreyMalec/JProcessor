package jProcessor.core;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

import jProcessor.util.Logger;

public class AnnotationHandler {
    private final RoundEnvironment roundEnv;
    private Logger logger;

    public AnnotationHandler(RoundEnvironment roundEnv, Logger logger) {
        this.roundEnv = roundEnv;
        this.logger = logger;
    }

    public void handleAnnotation(Class<? extends Annotation> annotation, Consumer<Element> consumer) {
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(annotation);

        for (Element element : annotated)
            consumer.accept(element);
    }
}
