package jProcessor.core;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

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

import jProcessor.Module;
import jProcessor.core.data.BindingRequest;
import jProcessor.core.data.Injection;
import jProcessor.core.data.ModuleData;
import jProcessor.core.data.Parameter;
import jProcessor.core.generation.InjectorGenerator;
import jProcessor.core.generation.ModuleGenerator;
import jProcessor.util.BaseLogger;
import jProcessor.util.Logger;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"jProcessor.Module", "jProcessor.Provides"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor implements NameManager {
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

            Map<TypeName, List<Parameter>> requests = new HashMap<>();
            handler.handleAnnotation(Inject.class, it -> {
                Element target = it.getEnclosingElement();
                if (it.getKind() == ElementKind.FIELD) {
                    TypeName targetType = name(target.asType());
                    Parameter field = new Parameter(it.getSimpleName().toString(), name(it.asType()));
                    if (requests.containsKey(targetType))
                        requests.get(targetType).add(field);
                    else {
                        List<Parameter> fields = new ArrayList<>();
                        fields.add(field);
                        requests.put(targetType, fields);
                    }
                }
            });

            List<BindingRequest> bindingRequests = new ArrayList<>();
            requests.forEach((key, value) -> bindingRequests
                    .add(new BindingRequest(key, ImmutableList.copyOf(value))));

            List<ModuleData> data = new ArrayList<>();
            handler.handleAnnotation(Module.class, it -> {
                ModuleGenerator generator = new ModuleGenerator(log, filer, roundEnv, it);
                data.add(generator.generate());
            });

            Injection injection = new Injection(ImmutableList.copyOf(data),
                    ImmutableList.copyOf(bindingRequests)
            );

            new InjectorGenerator(log, filer, roundEnv, injection).generate();
        } catch (RuntimeException e) {
            RuntimeException t = new RuntimeException(e);
            log.error(t);
            throw t;
        }

        return true;
    }
}
