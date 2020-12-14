package jProcessor.core.handlers;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

import java.util.Comparator;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Singleton;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import jProcessor.Module;
import jProcessor.core.Names;
import jProcessor.core.data.Parameter;
import jProcessor.core.data.Provider;
import jProcessor.util.Logger;

import static jProcessor.core.Names.name;
import static jProcessor.util.GuavaCollectors.toImmutableList;

public class ModuleHandler extends AnnotationHandler implements Names {
    private final ImmutableList.Builder<ExecutableElement> providersElements = new ImmutableList.Builder<>();
    private final ImmutableList.Builder<TypeName> modules = new ImmutableList.Builder<>();

    public ModuleHandler(RoundEnvironment roundEnv, Logger logger) {
        super(roundEnv, logger);
    }

    public ImmutableList<Provider> getProviders() {
        ImmutableList<ExecutableElement> elements = providersElements.build().stream()
                .sorted(Comparator.comparingInt(it -> it.getParameters().size())).collect(toImmutableList());
        ImmutableList.Builder<Provider> providers = new ImmutableList.Builder<>();
        for (ExecutableElement element : elements) {
            String name = element.getSimpleName().toString();
            TypeName type = name(element.getReturnType());
            ImmutableList<Parameter> parameters = element.getParameters().stream()
                    .map(it -> new Parameter(providerName(it.asType()), name(it.asType())))
                    .collect(toImmutableList());
            boolean isLazy = element.getAnnotation(Singleton.class) != null;
            TypeName module = name(element.getEnclosingElement().asType());

            providers.add(new Provider(name, type, parameters, isLazy, module));
        }
        return providers.build();
    }

    public ImmutableList<TypeName> getModules() {
        return modules.build();
    }

    @Override
    public void handleAnnotation() {
        for (Element element : roundEnv.getElementsAnnotatedWith(Module.class)) {
            modules.add(name(element.asType()));
            providersElements.addAll(findProviders(element));
        }
    }

    private ImmutableList<ExecutableElement> findProviders(Element module) {
        ProviderVisitor v = new ProviderVisitor();
        module.accept(v, null);

        return v.providers();
    }
}
