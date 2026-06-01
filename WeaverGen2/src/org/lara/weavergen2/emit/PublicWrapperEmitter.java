package org.lara.weavergen2.emit;

import java.util.List;
import java.util.Optional;

import org.lara.langspec2.dsl.WeaverSpec;
import org.lara.langspec2.model.Parameter;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.ArrayType;
import org.lara.weavergen2.java.JavaSourceBuilder;
import org.lara.weavergen2.java.TypeMapper;

public final class PublicWrapperEmitter {

    private final JoinPointTypeRenderer types;

    public PublicWrapperEmitter(JoinPointTypeRenderer types) {
        this.types = types;
    }

    public void emit(JavaSourceBuilder sb, String attrName, String methodName,
            JpDataType type, List<Parameter> params, boolean isAttribute) {
        var wrapperReturnType = types.mapPublicReturnType(type);
        var wrapperName = TypeMapper.sanitizeJavaIdentifier(attrName);
        var methodClass = isAttribute ? "Attribute" : "Action";

        sb.line("@Deprecated");
        if (params.isEmpty()) {
            sb.openBlock("public final " + wrapperReturnType + " " + wrapperName + "()");
        } else {
            var wrapperParams = types.formatPublicParams(params);
            sb.openBlock("public final " + wrapperReturnType + " " + wrapperName + "("
                    + String.join(", ", wrapperParams) + ")");
        }

        sb.openBlock("try");

        var eventTriggerArgsString = buildEventTriggerArgsString(params);

        sb.openBlock("if(getWeaverEngine().hasListeners())");
        sb.line("getWeaverEngine().getEventTrigger().trigger" + methodClass + "(Stage.BEGIN, this, \"" + attrName
                + "\", Optional.empty()" + eventTriggerArgsString + ");");
        sb.closeBlock();

        var implCall = new StringBuilder();
        implCall.append("this.").append(methodName).append("Impl(");
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

        if (type == WeaverSpec.VOID) {
            sb.line(implCall + ";");
        } else {
            if (types.isEnumLike(type)) {
                sb.line(wrapperReturnType + " result = " + types.toPublicEnumReturnExpression(type, implCall) + ";");
            } else {
                var javaRetType = types.mapReturnType(type);
                sb.line(javaRetType + " result = " + implCall + ";");
            }
        }

        sb.openBlock("if(getWeaverEngine().hasListeners())");
        sb.line("getWeaverEngine().getEventTrigger().trigger" + methodClass + "(Stage.END, this, \"" + attrName + "\", "
                + ((type == WeaverSpec.VOID) ? "Optional.empty()" : "Optional.ofNullable(result)")
                + eventTriggerArgsString + ");");
        sb.closeBlock();

        if (type == WeaverSpec.VOID) {
            sb.line("return;");
        } else {
            sb.line("return result;");
        }

        sb.closeBlockNoNewline();
        sb.append(" catch (Exception e) {\n");
        sb.indent();
        sb.line("throw new " + methodClass + "Exception(get_class(), \"" + attrName + "\", e);");
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
