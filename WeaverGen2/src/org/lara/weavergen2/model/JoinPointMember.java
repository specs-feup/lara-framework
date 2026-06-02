package org.lara.weavergen2.model;

import java.util.List;

import org.lara.langspec2.model.Action;
import org.lara.langspec2.model.Attribute;
import org.lara.langspec2.model.Parameter;
import org.lara.langspec2.types.JpDataType;
import org.lara.weavergen2.java.TypeMapper;

public record JoinPointMember(
        JoinPointMemberKind kind,
        String name,
        JpDataType type,
        List<Parameter> parameters) {

    public JoinPointMember {
        parameters = List.copyOf(parameters);
    }

    public static JoinPointMember attribute(Attribute attribute) {
        return new JoinPointMember(JoinPointMemberKind.ATTRIBUTE, attribute.name(), attribute.type(),
                attribute.parameters());
    }

    public static JoinPointMember action(Action action) {
        return new JoinPointMember(JoinPointMemberKind.ACTION, action.name(), action.returnType(),
                action.parameters());
    }

    public String wrapperName() {
        return TypeMapper.sanitizeJavaIdentifier(name);
    }

    public String implementationName() {
        return implementationBaseName() + "Impl";
    }

    private String implementationBaseName() {
        if (kind == JoinPointMemberKind.ATTRIBUTE) {
            return "get" + TypeMapper.capitalize(name);
        }

        return name;
    }

    public MemberSignature signature() {
        return MemberSignature.of(name, parameters);
    }
}
