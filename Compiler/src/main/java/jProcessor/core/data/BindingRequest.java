package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

public final class BindingRequest {
    public final TypeName targetType;
    public final TargetKind targetKind;
    public final ImmutableList<Parameter> fields;

    public BindingRequest(
            TypeName targetType, TargetKind targetKind, ImmutableList<Parameter> fields
    ) {
        this.targetType = targetType;
        this.targetKind = targetKind;
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BindingRequest request = (BindingRequest) o;
        return Objects.equal(targetType, request.targetType) && targetKind == request.targetKind &&
                Objects.equal(fields, request.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(targetType, targetKind, fields);
    }

    @Override
    public String toString() {
        return "BindingRequest{" + "targetType=" + targetType + ", targetKind=" + targetKind + ", fields=" +
                fields + '}';
    }

}
