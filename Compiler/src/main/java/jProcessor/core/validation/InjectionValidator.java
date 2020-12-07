package jProcessor.core.validation;

import jProcessor.core.data.Injection;

public class InjectionValidator implements Validator<Injection> {
    private final BindingDuplicateValidator bindingDuplicateValidator;
    private final BindingRequestDuplicateValidator bindingRequestDuplicateValidator;

    public InjectionValidator(
            BindingDuplicateValidator bindingDuplicateValidator,
            BindingRequestDuplicateValidator bindingRequestDuplicateValidator
    ) {
        this.bindingDuplicateValidator = bindingDuplicateValidator;
        this.bindingRequestDuplicateValidator = bindingRequestDuplicateValidator;
    }

    @Override
    public void validate(Injection target) throws RuntimeException {
        bindingDuplicateValidator.validate(target.bindings);
        bindingRequestDuplicateValidator.validate(target.requests);
    }
}
