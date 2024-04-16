package com.ahmadabbas.filetracking.backend.util.validation.validator;

import com.ahmadabbas.filetracking.backend.util.validation.constraint.ValueOfEnum;
import jakarta.validation.*;

import java.util.List;
import java.util.stream.*;

@Deprecated
public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;

    @Override
    public void initialize(ValueOfEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) return false;

        return acceptedValues.contains(value.toString());
    }
}