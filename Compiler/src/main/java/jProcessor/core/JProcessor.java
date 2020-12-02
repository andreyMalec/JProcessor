package jProcessor.core;

import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.inject.Inject;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import jProcessor.Module;
import jProcessor.core.data.InjectorData;
import jProcessor.core.data.ModuleData;
import jProcessor.core.generation.InjectorGenerator;
import jProcessor.core.generation.ModuleGenerator;
import jProcessor.util.BaseLogger;
import jProcessor.util.Logger;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"jProcessor.Module", "jProcessor.Provides"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor {
    private Logger log;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();

        log = new BaseLogger(env.getMessager());
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty())
            return false;

        try {
            AnnotationHandler handler = new AnnotationHandler(roundEnv, log);

            Map<Element, List<Element>> injections = new HashMap<>();
            handler.handleAnnotation(Inject.class, it -> {
                Element target = it.getEnclosingElement();
                if (it.getKind() == ElementKind.FIELD)
                    if (injections.containsKey(target))
                        injections.get(target).add(it);
                    else {
                        List<Element> fields = new ArrayList<>();
                        fields.add(it);
                        injections.put(target, fields);
                    }
            });

            List<TypeMirror> types = new ArrayList<>();
            List<List<Element>> fields = new ArrayList<>();
            injections.forEach((key, value) -> {
                types.add(key.asType());
                fields.add(value);
            });

            List<ModuleData> data = new ArrayList<>();
            handler.handleAnnotation(Module.class, it -> {
                ModuleGenerator generator = new ModuleGenerator(log, filer, roundEnv, it);
                data.add(generator.generate());
            });

            InjectorData injectorData = new InjectorData(data, types, fields);

            new InjectorGenerator(log, filer, roundEnv, injectorData).generate();
        } catch (RuntimeException e) {
            RuntimeException t = new RuntimeException(e);
            log.error(t);
            throw t;
        }

        return true;
    }
}
