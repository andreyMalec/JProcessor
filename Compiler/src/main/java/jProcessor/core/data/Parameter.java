package jProcessor.core.data;

import com.google.common.base.Objects;
import com.squareup.javapoet.TypeName;

public final class Parameter {
    public final String name;
    public final TypeName type;

    public Parameter(String name, TypeName type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Parameter parameter = (Parameter) o;
        return Objects.equal(name, parameter.name) && Objects.equal(type, parameter.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, type);
    }

    @Override
    public String toString() {
        return "(" + type + ' ' + name + ')';
    }
}
