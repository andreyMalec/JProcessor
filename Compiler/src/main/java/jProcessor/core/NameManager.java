package jProcessor.core;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.inject.Provider;
import javax.lang.model.type.TypeMirror;

public interface NameManager {
    String GET = "get";
    String PROVIDER = "Provider";
    String INSTANCE = "instance";
    String MODULE = "module";
    String INJECT = "inject";
    String INJECTOR = "Injector";

    default String getPackage(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    default String getPackage(TypeName name) {
        return getPackage(name.toString());
    }

    default String fieldName(TypeName name) {
        return fieldName(name.toString());
    }

    default String fieldName(String name) {
        String simpleName = simpleName(name);
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }

    default ParameterizedTypeName provider(TypeMirror type) {
        return provider(name(type));
    }

    default ParameterizedTypeName provider(TypeName type) {
        return ParameterizedTypeName.get(ClassName.get(Provider.class), type);
    }

    default String providerName(TypeName name) {
        return fieldName(name) + PROVIDER;
    }

    default String providerName(String name) {
        return fieldName(name) + PROVIDER;
    }

    default String providerName(TypeMirror name) {
        return providerName(name(name));
    }

    default String simpleName(TypeName type) {
        return simpleName(type.toString());
    }

    default String simpleName(TypeMirror type) {
        return simpleName(name(type));
    }

    default String simpleName(String type) {
        if (type.contains("<")) {
            int first = type.indexOf("<");
            int last = type.lastIndexOf(">");
            String typeName = type.substring(0, first);
            String paramName = type.substring(first + 1, last);
            typeName = simpleName(typeName);
            paramName = simpleName(paramName);
            return typeName + paramName;
        }

        String[] a = type.split("\\.");
        if (a.length == 0)
            return type;
        else
            return a[a.length - 1];
    }

    default TypeName name(TypeMirror type) {
        TypeName name = TypeName.get(type);
        if (name.isPrimitive())
            return name.box();
        else
            return TypeName.get(type);
    }
}
