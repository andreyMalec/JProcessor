package jProcessor.core.validation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import jProcessor.core.IllegalAccessException;

public class AccessibleValidator implements Validator<Element> {
    @Override
    public void validate(Element target) throws RuntimeException {
        if (!target.getModifiers().contains(Modifier.PUBLIC))
            if (target instanceof VariableElement)
                throw new IllegalAccessException((VariableElement) target);
            else if (target instanceof ExecutableElement)
                throw new IllegalAccessException((ExecutableElement) target);
            else
                throw new IllegalAccessException();

        if (target instanceof VariableElement && target.getModifiers().contains(Modifier.FINAL))
            throw new UnsupportedOperationException(String.format(
                    "Inject annotated field may not be final. %s.%s",
                    target.getEnclosingElement().getSimpleName(),
                    target.getSimpleName()
            ));
    }
}
