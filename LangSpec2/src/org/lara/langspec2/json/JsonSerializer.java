package org.lara.langspec2.json;

import org.lara.langspec2.model.*;
import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.*;

import java.util.*;

/**
 * Serializes a {@link WeaverModel} to JSON format compatible with the TypeScript wrapper generator.
 * <p>
 * The output format matches the legacy {@code LangSpecNode.toJson()} structure for backward compatibility.
 */
public final class JsonSerializer {

    private JsonSerializer() {}

    /**
     * Serializes the model to a JSON string.
     */
    public static String toJson(WeaverModel model) {
        var sb = new StringBuilder();
        sb.append("{\n");

        // Root
        var root = model.getRoot().map(JpClass::getName).orElse("joinpoint");
        appendKv(sb, "\t", "root", root, true);
        appendKv(sb, "\t", "rootAlias", root, true);

        // Children: all JP classes
        sb.append("\t\"children\": [\n");
        var allClasses = model.getAllJpClasses();
        for (int i = 0; i < allClasses.size(); i++) {
            var jp = allClasses.get(i);
            writeJpClass(sb, jp, model, "\t\t");
            if (i < allClasses.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        // TypeDefs and EnumDefs
        var typeDefs = new ArrayList<>(model.getTypeDefs().values());
        var enumDefs = new ArrayList<>(model.getEnumDefs().values());
        if (!typeDefs.isEmpty() || !enumDefs.isEmpty()) {
            sb.append(",\n");
            for (int i = 0; i < typeDefs.size(); i++) {
                writeTypeDef(sb, typeDefs.get(i), "\t\t");
                if (i < typeDefs.size() - 1 || !enumDefs.isEmpty()) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            for (int i = 0; i < enumDefs.size(); i++) {
                writeEnumDef(sb, enumDefs.get(i), "\t\t");
                if (i < enumDefs.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
        }

        sb.append("\t]\n");
        sb.append("}");
        return sb.toString();
    }

    private static void writeJpClass(StringBuilder sb, JpClass jp, WeaverModel model, String indent) {
        sb.append(indent).append("{\n");
        var innerIndent = indent + "\t";

        appendKv(sb, innerIndent, "type", "joinpoint", true);
        appendKv(sb, innerIndent, "name", jp.getName(), true);

        // extends
        var extendsName = jp.getParent().map(JpClass::getName).orElse("");
        sb.append(innerIndent).append("\"extends\": \"").append(extendsName).append("\"");

        // default attribute
        jp.getDefaultAttribute().ifPresent(da -> {
            sb.append(",\n");
            sb.append(innerIndent).append("\"defaultAttr\": \"").append(da).append("\"");
        });

        // tooltip
        jp.getTooltip().ifPresent(tt -> {
            sb.append(",\n");
            sb.append(innerIndent).append("\"tooltip\": ").append(jsonString(tt));
        });

        // Children: all attributes and actions (own + inherited for this JP)
        var allAttrs = jp.getAllAttributes();
        var allActions = jp.getAllActions();

        if (!allAttrs.isEmpty() || !allActions.isEmpty()) {
            sb.append(",\n");
            sb.append(innerIndent).append("\"children\": [\n");
            var childIndent = innerIndent + "\t";

            int total = allAttrs.size() + allActions.size();
            int idx = 0;

            for (var attr : allAttrs) {
                writeAttribute(sb, attr, childIndent);
                idx++;
                if (idx < total) sb.append(",");
                sb.append("\n");
            }

            for (var action : allActions) {
                writeAction(sb, action, childIndent);
                idx++;
                if (idx < total) sb.append(",");
                sb.append("\n");
            }

            sb.append(innerIndent).append("]");
        }

        sb.append("\n").append(indent).append("}");
    }

    private static void writeAttribute(StringBuilder sb, Attribute attr, String indent) {
        sb.append(indent).append("{\n");
        var inner = indent + "\t";

        sb.append(inner).append("\"type\": \"attribute\"");

        if (attr.tooltip() != null) {
            sb.append(",\n").append(inner).append("\"tooltip\": ").append(jsonString(attr.tooltip()));
        }

        // Children: [return type/name, params...]
        sb.append(",\n").append(inner).append("\"children\": [\n");
        var paramIndent = inner + "\t";

        // First child is the return type + name
        sb.append(paramIndent).append("{\n");
        sb.append(paramIndent).append("\t\"type\": ").append(jsonString(typeToString(attr.type()))).append(",\n");
        sb.append(paramIndent).append("\t\"name\": ").append(jsonString(attr.name())).append("\n");
        sb.append(paramIndent).append("}");

        // Parameters
        for (var param : attr.parameters()) {
            sb.append(",\n");
            writeParameter(sb, param, paramIndent);
        }

        sb.append("]\n");
        sb.append(indent).append("}");
    }

    private static void writeAction(StringBuilder sb, Action action, String indent) {
        sb.append(indent).append("{\n");
        var inner = indent + "\t";

        sb.append(inner).append("\"type\": \"action\"");

        if (action.tooltip() != null) {
            sb.append(",\n").append(inner).append("\"tooltip\": ").append(jsonString(action.tooltip()));
        }

        // Children: [return type/name, params...]
        sb.append(",\n").append(inner).append("\"children\": [\n");
        var paramIndent = inner + "\t";

        // First child is the return type + name
        sb.append(paramIndent).append("{\n");
        sb.append(paramIndent).append("\t\"type\": ").append(jsonString(typeToString(action.returnType()))).append(",\n");
        sb.append(paramIndent).append("\t\"name\": ").append(jsonString(action.name())).append("\n");
        sb.append(paramIndent).append("}");

        // Parameters
        for (var param : action.parameters()) {
            sb.append(",\n");
            writeParameter(sb, param, paramIndent);
        }

        sb.append("]\n");
        sb.append(indent).append("}");
    }

    private static void writeParameter(StringBuilder sb, Parameter param, String indent) {
        sb.append(indent).append("{\n");
        sb.append(indent).append("\t\"type\": ").append(jsonString(typeToString(param.type()))).append(",\n");
        sb.append(indent).append("\t\"name\": ").append(jsonString(param.name()));
        if (param.defaultValue() != null) {
            sb.append(",\n").append(indent).append("\t\"defaultValue\": ").append(jsonString(param.defaultValue()));
        }
        sb.append("\n");
        sb.append(indent).append("}");
    }

    private static void writeTypeDef(StringBuilder sb, TypeDef td, String indent) {
        sb.append(indent).append("{\n");
        var inner = indent + "\t";
        sb.append(inner).append("\"type\": \"typedef\",\n");
        sb.append(inner).append("\"name\": ").append(jsonString(td.name()));

        if (td.tooltip() != null) {
            sb.append(",\n").append(inner).append("\"tooltip\": ").append(jsonString(td.tooltip()));
        }

        if (!td.fields().isEmpty()) {
            sb.append(",\n").append(inner).append("\"children\": [\n");
            var fieldIndent = inner + "\t";
            for (int i = 0; i < td.fields().size(); i++) {
                var field = td.fields().get(i);
                sb.append(fieldIndent).append("{\n");
                sb.append(fieldIndent).append("\t\"type\": ").append(jsonString(typeToString(field.type()))).append(",\n");
                sb.append(fieldIndent).append("\t\"name\": ").append(jsonString(field.name())).append("\n");
                sb.append(fieldIndent).append("}");
                if (i < td.fields().size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append(inner).append("]");
        }

        sb.append("\n").append(indent).append("}");
    }

    private static void writeEnumDef(StringBuilder sb, EnumDef ed, String indent) {
        sb.append(indent).append("{\n");
        var inner = indent + "\t";
        sb.append(inner).append("\"type\": \"enum\",\n");
        sb.append(inner).append("\"name\": ").append(jsonString(ed.name()));

        if (ed.tooltip() != null) {
            sb.append(",\n").append(inner).append("\"tooltip\": ").append(jsonString(ed.tooltip()));
        }

        sb.append(",\n").append(inner).append("\"children\": [\n");
        var valIndent = inner + "\t";
        for (int i = 0; i < ed.values().size(); i++) {
            var v = ed.values().get(i);
            sb.append(valIndent).append("{\n");
            sb.append(valIndent).append("\t\"value\": ").append(jsonString(v.value()));
            if (v.display() != null) {
                sb.append(",\n").append(valIndent).append("\t\"display\": ").append(jsonString(v.display()));
            }
            sb.append("\n").append(valIndent).append("}");
            if (i < ed.values().size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append(inner).append("]");

        sb.append("\n").append(indent).append("}");
    }

    /**
     * Converts a JpDataType to its string representation for JSON output.
     */
    static String typeToString(JpDataType type) {
        if (type instanceof PrimitiveType p) {
            return p.name();
        } else if (type instanceof SelfType) {
            return "this";
        } else if (type instanceof JpRefType ref) {
            return ref.jpName();
        } else if (type instanceof TypeDefRefType ref) {
            return ref.typeDefName();
        } else if (type instanceof EnumRefType ref) {
            return ref.enumName();
        } else if (type instanceof ArrayType arr) {
            return typeToString(arr.element()) + "[]";
        } else if (type instanceof ParameterizedType pt) {
            var base = typeToString(pt.base());
            var args = pt.args().stream()
                    .map(JsonSerializer::typeToString)
                    .toList();
            return base + "<" + String.join(", ", args) + ">";
        } else if (type instanceof WildcardType wt) {
            return switch (wt.kind()) {
                case UNBOUNDED -> "?";
                case EXTENDS -> "? extends " + typeToString(wt.bound());
                case SUPER -> "? super " + typeToString(wt.bound());
            };
        }
        throw new IllegalArgumentException("Unknown JpDataType: " + type);
    }

    // ----- Helpers -----

    private static void appendKv(StringBuilder sb, String indent, String key, String value, boolean comma) {
        sb.append(indent).append("\"").append(key).append("\": \"").append(escapeJson(value)).append("\"");
        if (comma) sb.append(",");
        sb.append("\n");
    }

    private static String jsonString(String s) {
        return "\"" + escapeJson(s) + "\"";
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
