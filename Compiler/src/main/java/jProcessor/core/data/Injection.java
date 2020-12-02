package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public final class Injection {
    public final ImmutableList<ModuleData> modulesDatas;
    public final ImmutableList<BindingRequest> requests;

    public Injection(ImmutableList<ModuleData> modulesData, ImmutableList<BindingRequest> requests) {
        this.modulesDatas = modulesData;
        this.requests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Injection injection = (Injection) o;
        return Objects.equal(modulesDatas, injection.modulesDatas) &&
                Objects.equal(requests, injection.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(modulesDatas, requests);
    }

    @Override
    public String toString() {
        return "Injection{" + "module=" + modulesDatas + ", requests=" + requests + '}';
    }
}
