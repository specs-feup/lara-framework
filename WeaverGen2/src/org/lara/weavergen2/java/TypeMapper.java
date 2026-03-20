package org.lara.weavergen2.java;

import org.lara.langspec2.types.JpDataType;
import org.lara.langspec2.types.JpDataType.*;

import javax.lang.model.SourceVersion;

/**
 * Utilities for mapping LangSpec2 types to Java type strings.
 */
public final class TypeMapper {

    private TypeMapper() {}

    /**
     * Converts a JpDataType to its Java type representation.
     *
     * @param type             the spec type
     * @param selfType         the Java type string to use for SelfType (e.g., "Self")
     * @param jpRefMapper      how to resolve JP reference names to Java types
     * @param typeDefRefMapper how to resolve TypeDef reference names to Java types
     * @param enumRefMapper    how to resolve EnumDef reference names to Java types
     */
    public static String toJavaType(JpDataType type,
                                    String selfType,
                                    java.util.function.Function<String, String> jpRefMapper,
                                    java.util.function.Function<String, String> typeDefRefMapper,
                                    java.util.function.Function<String, String> enumRefMapper) {
        if (type instanceof PrimitiveType p) {
            return p.name();
        } else if (type instanceof SelfType) {
            return selfType;
        } else if (type instanceof JpRefType ref) {
            return jpRefMapper.apply(ref.jpName());
        } else if (type instanceof TypeDefRefType ref) {
            return typeDefRefMapper.apply(ref.typeDefName());
        } else if (type instanceof EnumRefType ref) {
            return enumRefMapper.apply(ref.enumName());
        } else if (type instanceof ArrayType arr) {
            return toJavaType(arr.element(), selfType, jpRefMapper, typeDefRefMapper, enumRefMapper) + "[]";
        } else if (type instanceof ParameterizedType pt) {
            var base = toJavaType(pt.base(), selfType, jpRefMapper, typeDefRefMapper, enumRefMapper);
            var args = pt.args().stream()
                    .map(a -> toJavaType(a, selfType, jpRefMapper, typeDefRefMapper, enumRefMapper))
                    .toList();
            return base + "<" + String.join(", ", args) + ">";
        } else if (type instanceof WildcardType wt) {
            return switch (wt.kind()) {
                case UNBOUNDED -> "?";
                case EXTENDS -> "? extends " + toJavaType(wt.bound(), selfType, jpRefMapper, typeDefRefMapper, enumRefMapper);
                case SUPER -> "? super " + toJavaType(wt.bound(), selfType, jpRefMapper, typeDefRefMapper, enumRefMapper);
            };
        }
        throw new IllegalArgumentException("Unknown JpDataType: " + type);
    }

    /**
     * Returns the Java type for an impl method return type.
     * For Self types, returns "Self". For JP refs, returns the abstract class name.
     */
    public static String toImplReturnType(JpDataType type, String selfType,
                                          java.util.function.Function<String, String> jpRefMapper,
                                          java.util.function.Function<String, String> typeDefRefMapper,
                                          java.util.function.Function<String, String> enumRefMapper) {
        return toJavaType(type, selfType, jpRefMapper, typeDefRefMapper, enumRefMapper);
    }

    /**
     * Returns the boxed version of a primitive type name.
     */
    public static String boxed(String type) {
        return switch (type) {
            case "int" -> "Integer";
            case "long" -> "Long";
            case "double" -> "Double";
            case "float" -> "Float";
            case "boolean" -> "Boolean";
            case "byte" -> "Byte";
            case "short" -> "Short";
            case "char" -> "Character";
            case "void" -> "Void";
            default -> type;
        };
    }

    /**
     * Returns true if the type is a Java primitive.
     */
    public static boolean isPrimitive(String type) {
        return switch (type) {
            case "int", "long", "double", "float", "boolean", "byte", "short", "char", "void" -> true;
            default -> false;
        };
    }

    /**
     * Capitalizes the first letter of a string.
     */
    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Returns the abstract class name for a join point (e.g., "statement" -> "AStatement").
     */
    public static String abstractClassName(String jpName) {
        return "A" + capitalize(jpName);
    }

    /**
     * Returns the provider definition interface name for a join point.
     */
    public static String providerDefName(String jpName) {
        return capitalize(jpName) + "ProviderDef";
    }

    /**
     * Returns a Java-safe identifier by prefixing reserved words with an underscore.
     */
    public static String sanitizeJavaIdentifier(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        if (SourceVersion.isKeyword(name)) {
            return "_" + name;
        }

        return name;
    }
}
