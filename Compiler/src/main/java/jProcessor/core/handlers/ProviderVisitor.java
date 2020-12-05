package jProcessor.core.handlers;

import com.google.common.collect.ImmutableList;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner8;

import static jProcessor.core.IllegalAccessException.checkAccessible;

public class ProviderVisitor extends ElementScanner8<Void, Void> {
    private final ImmutableList.Builder<ExecutableElement> providers = new ImmutableList.Builder<>();

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        if (e.getKind() == ElementKind.METHOD) {
            checkAccessible(e);
            providers.add(e);
        }
        return super.visitExecutable(e, aVoid);
    }

    public ImmutableList<ExecutableElement> providers() {
        return providers.build();
    }
}
