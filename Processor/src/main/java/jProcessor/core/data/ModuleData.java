package jProcessor.core.data;

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
}
