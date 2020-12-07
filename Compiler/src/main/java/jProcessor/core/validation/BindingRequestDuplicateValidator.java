package jProcessor.core.validation;

import java.util.List;

import jProcessor.core.data.BindingRequest;

import static jProcessor.util.Ext.findDuplicate;

public class BindingRequestDuplicateValidator implements DuplicateValidator<BindingRequest> {
    @Override
    public void validate(List<BindingRequest> requests) throws RuntimeException {
        BindingRequest firstDuplicateRequest = findDuplicate(requests);
        if (firstDuplicateRequest != null)
            throw new IllegalStateException(
                    firstDuplicateRequest.targetType + " constructor is injected multiple times");
    }
}
