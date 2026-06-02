package org.lara.weavergen2.model;

import java.util.List;

import org.lara.langspec2.model.Parameter;
import org.lara.langspec2.types.JpDataType;

public record MemberSignature(String name, List<JpDataType> parameterTypes) {

    public MemberSignature {
        parameterTypes = List.copyOf(parameterTypes);
    }

    public static MemberSignature of(String name, List<Parameter> parameters) {
        return new MemberSignature(name, parameters.stream()
                .map(Parameter::type)
                .toList());
    }

    public String asText() {
        var parameters = parameterTypes.stream()
                .map(Object::toString)
                .toList();
        return name + "(" + String.join(", ", parameters) + ")";
    }
}
