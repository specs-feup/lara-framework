package org.lara.weavergen2.emit;

import java.util.List;
import java.util.Optional;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.model.Parameter;
import org.lara.langspec2.types.JpDataType.ArrayType;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;
import org.lara.weavergen2.model.JoinPointMember;

public final class PublicWrapperEmitter {

    private final JoinPointTypeRenderer types;

    public PublicWrapperEmitter(JoinPointTypeRenderer types) {
        this.types = types;
    }

    public void emit(JavaSourceBuilder sb, JoinPointMember member) {
        var wrapperReturnType = types.mapPublicReturnType(member.type());
        var methodClass = member.kind().eventName();
        var params = member.parameters();

        sb.line("@Deprecated");
        if (params.isEmpty()) {
            sb.openBlock("public final " + wrapperReturnType + " " + member.wrapperName() + "()");
        } else {
            var wrapperParams = types.formatPublicParams(params);
            sb.openBlock("public final " + wrapperReturnType + " " + member.wrapperName() + "("
                    + String.join(", ", wrapperParams) + ")");
        }

        sb.openBlock("try");

        var eventTriggerArgsString = buildEventTriggerArgsString(params);

        sb.openBlock("if(getWeaverEngine().hasListeners())");
        sb.line("getWeaverEngine().getEventTrigger().trigger" + methodClass + "(Stage.BEGIN, this, \"" + member.name()
                + "\", Optional.empty()" + eventTriggerArgsString + ");");
        sb.closeBlock();

        var implCall = new StringBuilder();
        implCall.append("this.").append(member.implementationName()).append("(");
        if (!params.isEmpty()) {
            var args = params.stream()
                    .map(p -> {
                        var paramName = TypeMapper.sanitizeJavaIdentifier(p.name());
                        return types.toImplArgument(p.type(), paramName);
                    })
                    .toList();
            implCall.append(String.join(", ", args));
        }
        implCall.append(")");

        if (member.type() == WeaverSpec.VOID) {
            sb.line(implCall + ";");
        } else {
            if (types.isEnumLike(member.type())) {
                sb.line(wrapperReturnType + " result = " + types.toPublicEnumReturnExpression(member.type(), implCall)
                        + ";");
            } else {
                var javaRetType = types.javaType(member.type());
                sb.line(javaRetType + " result = " + implCall + ";");
            }
        }

        sb.openBlock("if(getWeaverEngine().hasListeners())");
        sb.line("getWeaverEngine().getEventTrigger().trigger" + methodClass + "(Stage.END, this, \"" + member.name()
                + "\", "
                + ((member.type() == WeaverSpec.VOID) ? "Optional.empty()" : "Optional.ofNullable(result)")
                + eventTriggerArgsString + ");");
        sb.closeBlock();

        if (member.type() == WeaverSpec.VOID) {
            sb.line("return;");
        } else {
            sb.line("return result;");
        }

        sb.closeBlockNoNewline();
        sb.append(" catch (Exception e) {\n");
        sb.indent();
        sb.line("throw new " + methodClass + "Exception(get_class(), \"" + member.name() + "\", e);");
        sb.closeBlock();
        sb.closeBlock();
        sb.line();
    }

    private String buildEventTriggerArgsString(List<Parameter> params) {
        if (params.isEmpty()) {
            return "";
        }
        return ", " + String.join(", ",
                params.stream()
                        .map(p -> (p.type() instanceof ArrayType ? "(Object) " : "")
                                + TypeMapper.sanitizeJavaIdentifier(p.name()))
                        .toList());
    }
}
