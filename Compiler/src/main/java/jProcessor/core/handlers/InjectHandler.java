package jProcessor.core.handlers;

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import jProcessor.core.NameManager;
import jProcessor.core.data.BindingRequest;
import jProcessor.core.data.Parameter;
import jProcessor.core.data.TargetKind;
import jProcessor.util.Logger;
import jProcessor.util.Pair;

import static jProcessor.core.IllegalAccessException.checkAccessible;

public class InjectHandler extends AnnotationHandler implements NameManager {
    private final Map<Pair<TypeName, TargetKind>, List<Parameter>> requests = new HashMap<>();

    public InjectHandler(RoundEnvironment roundEnv, Logger logger) {
        super(roundEnv, logger);
    }

    public ImmutableList<BindingRequest> getBindingRequests() {
        ImmutableList.Builder<BindingRequest> bindingRequests = new ImmutableList.Builder<>();
        requests.forEach((key, value) -> bindingRequests
                .add(new BindingRequest(key.first, key.second, ImmutableList.copyOf(value))));

        return bindingRequests.build();
    }

    @Override
    public void handleAnnotation() {
        for (Element element : roundEnv.getElementsAnnotatedWith(Inject.class)) {
            Element target = element.getEnclosingElement();
            checkAccessible(element);
            if (element.getKind() == ElementKind.FIELD) {
                TypeName targetType = name(target.asType());
                Pair<TypeName, TargetKind> key = Pair.of(targetType, TargetKind.FIELD);
                Parameter field = new Parameter(element.getSimpleName().toString(), name(element.asType()));
                if (requests.containsKey(key))
                    requests.get(key).add(field);
                else {
                    List<Parameter> fields = new ArrayList<>();
                    fields.add(field);
                    requests.put(key, fields);
                }
            } else if (element.getKind() == ElementKind.CONSTRUCTOR) {
                ExecutableElement constructor = (ExecutableElement) element;
                TypeName targetType = name(constructor.getEnclosingElement().asType());
                Pair<TypeName, TargetKind> key = Pair.of(targetType, TargetKind.CONSTRUCTOR);
                List<Parameter> fields = new ArrayList<>();
                for (VariableElement parameter : constructor.getParameters())
                    fields.add(new Parameter(parameter.getSimpleName().toString(), name(parameter.asType())));
                requests.put(key, fields);
            }
        }
    }
}
