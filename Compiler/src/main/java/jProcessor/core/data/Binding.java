package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public final class Binding {
    public final Parameter provider;
    public final ImmutableList<Parameter> providerParams;
    public final int providerParamsCount;
    public final String factory;

    public Binding(
            Parameter provider,
            ImmutableList<Parameter> providerParams,
            int providerParamsCount,
            String factory
    ) {
        this.provider = provider;
        this.providerParams = providerParams;
        this.providerParamsCount = providerParamsCount;
        this.factory = factory;
    }

    public static Binding selfCheck(Binding binding) {
        if (binding == null)
            throw new NullPointerException("Binding selfCheck");

        return binding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Binding binding = (Binding) o;
        return Objects.equal(provider.type, binding.provider.type) &&
                Objects.equal(providerParams, binding.providerParams);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(provider, providerParams);
    }

    @Override
    public String toString() {
        return "Binding{" + "provider=" + provider + ", providerParams=" + providerParams + ", factory='" +
                factory + '\'' + '}';
    }
}
