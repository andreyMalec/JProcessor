package jProcessor.core.validation;

public interface Validator<T> {
    void validate(T target) throws RuntimeException;
}

