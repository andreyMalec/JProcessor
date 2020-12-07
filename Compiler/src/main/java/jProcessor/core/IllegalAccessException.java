package jProcessor.core;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class IllegalAccessException extends RuntimeException {
    public IllegalAccessException(ExecutableElement element) {
        super(String.format(
                "Provides annotated method must be public. %s.%s",
                element.getEnclosingElement().getSimpleName(),
                element.getSimpleName()
        ));
    }

    public IllegalAccessException(VariableElement element) {
        super(String.format(
                "Inject annotated field must be public. %s.%s",
                element.getEnclosingElement().getSimpleName(),
                element.getSimpleName()
        ));
    }

    public IllegalAccessException() { }
}