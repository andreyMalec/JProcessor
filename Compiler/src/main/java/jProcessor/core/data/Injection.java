package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

public final class Injection {
    public final ImmutableList<TypeName> modules;
    public final ImmutableList<Binding> bindings;
    public final ImmutableList<BindingRequest> requests;

    public Injection(
            ImmutableList<TypeName> modules,
            ImmutableList<Binding> bindings,
            ImmutableList<BindingRequest> requests
    ) {
        this.modules = modules;
        this.bindings = bindings;
        this.requests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Injection injection = (Injection) o;
        return Objects.equal(modules, injection.modules) && Objects.equal(bindings, injection.bindings) &&
                Objects.equal(requests, injection.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(modules, bindings, requests);
    }

    @Override
    public String toString() {
        return "Injection{" + "modules=" + modules + ", bindings=" + bindings + ", requests=" + requests +
                '}';
    }
}
