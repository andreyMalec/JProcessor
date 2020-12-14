package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.TypeName;

public class Provider {
    public final String name;
    public final TypeName type;
    public final ImmutableList<Parameter> parameters;
    public final boolean isLazy;
    public final TypeName module;

    public Provider(
            String name, TypeName type, ImmutableList<Parameter> parameters, boolean isLazy, TypeName module
    ) {
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.isLazy = isLazy;
        this.module = module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Provider provider1 = (Provider) o;
        return Objects.equal(name, provider1.name) && Objects.equal(type, provider1.type) &&
                Objects.equal(parameters, provider1.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, type, parameters);
    }

    @Override
    public String toString() {
        return "Provider{" + "name='" + name + '\'' + ", type=" + type + ", parameters=" + parameters +
                ", isLazy=" + isLazy + ", module=" + module + '}';
    }
}
