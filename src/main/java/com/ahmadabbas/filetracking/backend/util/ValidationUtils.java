package com.ahmadabbas.filetracking.backend.util;

import com.ahmadabbas.filetracking.backend.util.validation.BeanDeserializerModifierWithValidation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

public class ValidationUtils {
    public static ObjectMapper getObjectMapperWithValidation() {
        SimpleModule validationModule = new SimpleModule();
        validationModule.setDeserializerModifier(new BeanDeserializerModifierWithValidation());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
        mapper.registerModule(validationModule);
        return mapper;
    }
}
