package jProcessor.core.handlers;

import com.google.common.collect.ImmutableList;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner8;

import jProcessor.core.validation.AccessibleValidator;

public class ProviderVisitor extends ElementScanner8<Void, Void> {
    private final ImmutableList.Builder<ExecutableElement> providers = new ImmutableList.Builder<>();
    private final AccessibleValidator accessibleValidator = new AccessibleValidator();

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        if (e.getKind() == ElementKind.METHOD) {
            accessibleValidator.validate(e);
            providers.add(e);
        }
        return super.visitExecutable(e, aVoid);
    }

    public ImmutableList<ExecutableElement> providers() {
        return providers.build();
    }
}
