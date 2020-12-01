package jProcessor.core.data;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public class InjectorData {
    public final List<ModuleData> modulesData;
    public final List<TypeMirror> targetTypes;
    public final List<List<Element>> fields;

    public InjectorData(List<ModuleData> modulesData, List<TypeMirror> targetTypes, List<List<Element>> fields) {
        this.modulesData = modulesData;
        this.targetTypes = targetTypes;
        this.fields = fields;
    }
}
