package jProcessor.core;

import com.squareup.javapoet.TypeName;

import javax.lang.model.type.TypeMirror;

public class ProviderNotFoundException extends RuntimeException {
    private final static String message = "No provider found for class ";

    public ProviderNotFoundException(TypeMirror type) {
        super(message + type);
    }

    public ProviderNotFoundException(TypeName type) {
        super(message + type);
    }

    public ProviderNotFoundException(Object type) {
        super(message + type);
    }
}
