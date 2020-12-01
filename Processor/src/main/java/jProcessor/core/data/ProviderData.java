package jProcessor.core.data;

import java.util.List;

import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class ProviderData {
    public final Name name;
    public final TypeMirror returnType;
    public final List<? extends VariableElement> params;
    public final String factory;

    public ProviderData(Name name, TypeMirror returnType, List<? extends VariableElement> params, String factory) {
        this.name = name;
        this.returnType = returnType;
        this.params = params;
        this.factory = factory;
    }
}
