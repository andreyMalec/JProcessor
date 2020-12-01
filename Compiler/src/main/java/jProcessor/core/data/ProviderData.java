package jProcessor.core.data;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProviderData that = (ProviderData) o;
        return Objects.equal(returnType, that.returnType) && Objects.equal(params, that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(returnType, params);
    }

    @Override
    public String toString() {
        return "ProviderData{" + "name=" + name + ", returnType=" + returnType + ", params=" + params + ", factory='" + factory + '\'' + '}';
    }
}
