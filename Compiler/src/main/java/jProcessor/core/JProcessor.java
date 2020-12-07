package jProcessor.core;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import jProcessor.core.data.Binding;
import jProcessor.core.data.BindingRequest;
import jProcessor.core.data.Injection;
import jProcessor.core.generation.InjectorGenerator;
import jProcessor.core.generation.ProviderGenerator;
import jProcessor.core.handlers.InjectHandler;
import jProcessor.core.handlers.ModuleHandler;
import jProcessor.core.validation.BindingDuplicateValidator;
import jProcessor.core.validation.BindingRequestDuplicateValidator;
import jProcessor.core.validation.InjectionValidator;
import jProcessor.util.BaseLogger;
import jProcessor.util.Logger;

@AutoService(Processor.class)
@SupportedAnnotationTypes({"jProcessor.Module", "jProcessor.Provides"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JProcessor extends AbstractProcessor {
    private final InjectionValidator injectionValidator = new InjectionValidator(new BindingDuplicateValidator(),
            new BindingRequestDuplicateValidator()
    );
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
            InjectHandler injectHandler = new InjectHandler(roundEnv, log);
            injectHandler.handleAnnotation();
            ImmutableList<BindingRequest> bindingRequests = injectHandler.getBindingRequests();

            ImmutableList.Builder<Binding> bindings = new ImmutableList.Builder<>();

            ModuleHandler moduleHandler = new ModuleHandler(roundEnv, log);
            moduleHandler.handleAnnotation();
            for (ExecutableElement provider : moduleHandler.getProviders())
                bindings.add(new ProviderGenerator(log, filer, provider).generate());

            ImmutableList<TypeName> modules = moduleHandler.getModules();

            Injection injection = new Injection(modules, bindings.build(), bindingRequests);

            injectionValidator.validate(injection);

            new InjectorGenerator(log, filer, injection).generate();
        } catch (RuntimeException e) {
            log.error(e);
        }

        return true;
    }
}
