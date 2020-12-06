package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

public final class BindingRequest {
    public final TypeName targetType;
    public final TargetKind targetKind;
    public final ImmutableList<Parameter> parameters;

    public BindingRequest(
            TypeName targetType, TargetKind targetKind, ImmutableList<Parameter> parameters
    ) {
        this.targetType = targetType;
        this.targetKind = targetKind;
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BindingRequest request = (BindingRequest) o;
        return Objects.equal(targetType, request.targetType) && (targetKind == request.targetKind ||
                (targetKind == TargetKind.CONSTRUCTOR &&
                        request.targetKind == TargetKind.SINGLETON_CONSTRUCTOR) ||
                (targetKind == TargetKind.SINGLETON_CONSTRUCTOR &&
                        request.targetKind == TargetKind.CONSTRUCTOR));
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (targetType == null ? 0 : targetType.hashCode());
        result = 31 * result + (targetKind == null ? 0 :
                targetKind == TargetKind.FIELD ? targetKind.hashCode() : TargetKind.CONSTRUCTOR.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "BindingRequest{" + "targetType=" + targetType + ", targetKind=" + targetKind + ", fields=" +
                parameters + '}';
    }

}
