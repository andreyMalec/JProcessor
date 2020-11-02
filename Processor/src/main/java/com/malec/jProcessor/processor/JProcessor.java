package com.malec.jProcessor.processor;

import com.google.auto.service.AutoService;
import com.malec.jProcessor.processor.annotation.Default;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.malec.jProcessor.processor.annotation.Default"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor {
    private final Map<TypeElement, BuilderVisitor> visitors = new HashMap<>();

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Default.class);

        for (Element element : annotatedElements) {
            TypeElement object = (TypeElement) element;
            BuilderVisitor visitor = visitors.get(object);
            if (visitor == null) {
                visitor = new BuilderVisitor(processingEnv, object);
                visitors.put(object, visitor);
            }
            element.accept(visitor, null);
        }

        for (final BuilderVisitor visitor : visitors.values()) {
            try {
                visitor.generateCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
