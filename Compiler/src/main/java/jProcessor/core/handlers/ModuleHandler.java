package jProcessor.core.handlers;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

import java.util.Comparator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import jProcessor.Module;
import jProcessor.core.NameManager;
import jProcessor.util.Logger;

public class ModuleHandler extends AnnotationHandler implements NameManager {
    private final ImmutableList.Builder<ExecutableElement> providers = new ImmutableList.Builder<>();
    private final ImmutableList.Builder<TypeName> modules = new ImmutableList.Builder<>();

    public ModuleHandler(RoundEnvironment roundEnv, Logger logger) {
        super(roundEnv, logger);
    }

    @SuppressWarnings("UnstableApiUsage")
    public ImmutableList<ExecutableElement> getProviders() {
        return providers.build().stream().sorted(Comparator.comparingInt(it -> it.getParameters().size()))
                .collect(ImmutableList.toImmutableList());
    }

    public ImmutableList<TypeName> getModules() {
        return modules.build();
    }

    @Override
    public void handleAnnotation() {
        for (Element element : roundEnv.getElementsAnnotatedWith(Module.class)) {
            modules.add(name(element.asType()));
            providers.addAll(findProviders(element));
        }
    }

    private ImmutableList<ExecutableElement> findProviders(Element module) {
        ProviderVisitor v = new ProviderVisitor();
        module.accept(v, null);

        return v.providers();
    }
}
