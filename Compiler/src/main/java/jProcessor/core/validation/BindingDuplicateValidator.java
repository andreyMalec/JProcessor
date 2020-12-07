package jProcessor.core.validation;

import java.util.List;

import jProcessor.core.data.Binding;

import static jProcessor.util.Ext.findDuplicate;

public class BindingDuplicateValidator implements DuplicateValidator<Binding> {
    @Override
    public void validate(List<Binding> bindings) throws RuntimeException {
        Binding firstDuplicate = findDuplicate(bindings);
        if (firstDuplicate != null)
            throw new IllegalStateException(firstDuplicate.provider.type + " is bound multiple times in " +
                    firstDuplicate.factory.split("_")[0]);
    }
}
