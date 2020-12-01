package jProcessor.core.data;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class InjectorData {
    public final ModuleData moduleData;
    public final List<TypeMirror> targetTypes;
    public final List<List<Element>> fields;

    public InjectorData(ModuleData moduleData, List<TypeMirror> targetTypes, List<List<Element>> fields) {
        this.moduleData = moduleData;
        this.targetTypes = targetTypes;
        this.fields = fields;
    }
}
