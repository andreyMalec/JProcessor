package jProcessor.core.data;

import com.google.common.base.Objects;
import com.squareup.javapoet.TypeName;

import java.util.List;

public class ModuleData {
    public final String name;
    public final String packageName;
    public final TypeName type;
    public final List<ProviderData> providers;

    public ModuleData(String name, String packageName, TypeName type, List<ProviderData> providers) {
        this.name = name;
        this.packageName = packageName;
        this.type = type;
        this.providers = providers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ModuleData that = (ModuleData) o;
        return Objects.equal(name, that.name) && Objects
                .equal(packageName, that.packageName) && Objects.equal(type, that.type) && Objects
                .equal(providers, that.providers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, packageName, type, providers);
    }

    @Override
    public String toString() {
        return "ModuleData{" + "name='" + name + '\'' + ", packageName='" + packageName + '\'' + ", type=" + type + ", providers=" + providers + '}';
    }
}
