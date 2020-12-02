package jProcessor.core.data;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public final class ModuleData {
    public final Parameter module;
    public final ImmutableList<Binding> bindings;

    public ModuleData(Parameter module, ImmutableList<Binding> bindings) {
        this.module = module;
        this.bindings = bindings;
    }

    public static ModuleData selfCheck(ModuleData moduleData) {
        if (moduleData == null)
            throw new NullPointerException("ModuleData selfCheck");

        return moduleData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ModuleData that = (ModuleData) o;
        return Objects.equal(module, that.module) && Objects.equal(bindings, that.bindings);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(module, bindings);
    }

    @Override
    public String toString() {
        return "ModuleData{" + "module=" + module + ", bindings=" + bindings + '}';
    }
}
