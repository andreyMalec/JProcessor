package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

public final class BindingRequest {
    public final TypeName targetType;
    public final ImmutableList<Parameter> fields;

    public BindingRequest(TypeName targetType, ImmutableList<Parameter> fields) {
        this.targetType = targetType;
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BindingRequest that = (BindingRequest) o;
        return Objects.equal(targetType, that.targetType) && Objects.equal(fields, that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetType, fields);
    }

    @Override
    public String toString() {
        return "BindingRequest{" + "targetType=" + targetType + ", fields=" + fields + '}';
    }
}
