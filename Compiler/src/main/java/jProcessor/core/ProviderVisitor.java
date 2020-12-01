package jProcessor.core;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.ElementScanner8;

public class ProviderVisitor extends ElementScanner8<Void, Void> {
    List<ExecutableElement> providers = new ArrayList<>();

    @Override
    public Void visitExecutable(ExecutableElement e, Void aVoid) {
        if (e.getKind() == ElementKind.METHOD)
            providers.add(e);

        return super.visitExecutable(e, aVoid);
    }

    public List<ExecutableElement> providers() {
        return providers;
    }
}
