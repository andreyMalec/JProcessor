package jProcessor.core.data;

import com.google.common.base.Objects;

public final class Binding {
    public final Provider provider;
    public final String factory;

    public Binding(Provider provider, String factory) {
        this.provider = provider;
        this.factory = factory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Binding binding = (Binding) o;
        return Objects.equal(provider, binding.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(provider);
    }

    @Override
    public String toString() {
        return "Binding{" + "provider=" + provider + ", factory='" + factory + '\'' + '}';
    }
}
