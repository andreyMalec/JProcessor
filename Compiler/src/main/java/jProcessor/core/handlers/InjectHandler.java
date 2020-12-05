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

import jProcessor.core.NameManager;
import jProcessor.core.data.BindingRequest;
import jProcessor.core.data.Parameter;
import jProcessor.util.Logger;

import static jProcessor.core.IllegalAccessException.checkAccessible;

public class InjectHandler extends AnnotationHandler implements NameManager {
    private final Map<TypeName, List<Parameter>> requests = new HashMap<>();

    public InjectHandler(RoundEnvironment roundEnv, Logger logger) {
        super(roundEnv, logger);
    }

    public ImmutableList<BindingRequest> getBindingRequests() {
        ImmutableList.Builder<BindingRequest> bindingRequests = new ImmutableList.Builder<>();
        requests.forEach((key, value) -> bindingRequests
                .add(new BindingRequest(key, ImmutableList.copyOf(value))));

        return bindingRequests.build();
    }

    @Override
    public void handleAnnotation() {
        for (Element element : roundEnv.getElementsAnnotatedWith(Inject.class)) {
            Element target = element.getEnclosingElement();
            if (element.getKind() == ElementKind.FIELD) {
                checkAccessible(element);

                TypeName targetType = name(target.asType());
                Parameter field = new Parameter(element.getSimpleName().toString(), name(element.asType()));
                if (requests.containsKey(targetType))
                    requests.get(targetType).add(field);
                else {
                    List<Parameter> fields = new ArrayList<>();
                    fields.add(field);
                    requests.put(targetType, fields);
                }
            }
        }
    }
}
