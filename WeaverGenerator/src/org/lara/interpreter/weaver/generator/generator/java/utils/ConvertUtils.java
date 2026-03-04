/**
 * Copyright 2015 SPeCS Research Group.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.GenericType;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.ParameterizedType;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.lara.language.specification.dsl.types.ThisType;
import org.lara.language.specification.dsl.types.TypeDef;
import org.lara.language.specification.dsl.types.WildcardType;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.types.Primitive;
import org.specs.generators.java.utils.Utils;

import tdrc.utils.Pair;
import tdrc.utils.StringUtils;

public class ConvertUtils {

    private enum PrimitiveConversionStrategy {
        STANDARD {
            @Override
            JavaType convertPrimitive(Primitive primitive, int arrayDimension) {
                JavaType primitiveType = JavaTypeFactory.getPrimitiveType(primitive);
                primitiveType.setArrayDimension(arrayDimension);
                return primitiveType;
            }
        },
        ATTRIBUTE_RETURN {
            @Override
            JavaType convertPrimitive(Primitive primitive, int arrayDimension) {
                if (arrayDimension == 0) {
                    JavaType wrapperType = JavaTypeFactory.getPrimitiveWrapper(primitive);
                    wrapperType.setArrayDimension(arrayDimension);
                    return wrapperType;
                }

                JavaType primitiveType = JavaTypeFactory.getPrimitiveType(primitive);
                primitiveType.setArrayDimension(arrayDimension);
                return primitiveType;
            }
        };

        abstract JavaType convertPrimitive(Primitive primitive, int arrayDimension);
    }

    private static final String JoinPointClassTypeName = "Joinpoint";
    private static final String JoinPointInterfaceClassTypeName = "JoinpointInterface";
    private static final Map<String, JavaType> InterpreterTypes;
    /**
     * Standard Java types that can be used in type specifications.
     * These don't require full qualification and are resolved to their proper
     * java.util classes.
     */
    private static final Map<String, JavaType> StandardJavaTypes;

    static {
        InterpreterTypes = new HashMap<>();

        ConvertUtils.InterpreterTypes.put("String", JavaTypeFactory.getStringType());

        final JavaType objectType = JavaTypeFactory.getObjectType();
        ConvertUtils.InterpreterTypes.put("Object", objectType);

        final JavaType mapType = new JavaType(Map.class);
        mapType.addGeneric(new JavaGenericType(JavaTypeFactory.getWildCardType()));
        mapType.addGeneric(new JavaGenericType(JavaTypeFactory.getWildCardType()));
        ConvertUtils.InterpreterTypes.put("Map", mapType);
        ConvertUtils.InterpreterTypes.put("Template", JavaTypeFactory.getStringType());

        // Standard Java types that can be used in generic contexts
        StandardJavaTypes = new HashMap<>();
        StandardJavaTypes.put("List", new JavaType(List.class));
        StandardJavaTypes.put("Set", new JavaType(Set.class));
        StandardJavaTypes.put("Collection", new JavaType(Collection.class));
        StandardJavaTypes.put("Optional", new JavaType(Optional.class));
        StandardJavaTypes.put("Map", new JavaType(Map.class));

    }

    private static JavaType getConvertedTypeAux(String type, JavaAbstractsGenerator generator,
            final int arrayDimension) {
        String keyType = StringUtils.firstCharToUpper(type);

        if (generator.getLanguageSpecification().hasEnumDef(type)) {
            keyType = "String";
        }

        // if it is a primitive type of the interpreter
        if (ConvertUtils.InterpreterTypes.containsKey(keyType)) {
            final JavaType clone = ConvertUtils.InterpreterTypes.get(keyType).clone();
            clone.setArrayDimension(arrayDimension);
            return clone;
        }

        // if it is the base joinpoint type (case-insensitive match)
        if (keyType.equalsIgnoreCase(ConvertUtils.JoinPointClassTypeName)) {
            final JavaType clone = generator.getaJoinPointType().clone();
            clone.setArrayDimension(arrayDimension);
            return clone;
        }

        // if it is the joinpoint interface type (case-insensitive match)
        if (keyType.equalsIgnoreCase(ConvertUtils.JoinPointInterfaceClassTypeName)) {
            final JavaType clone = GenConstants.getJoinPointInterfaceType().clone();
            clone.setArrayDimension(arrayDimension);
            return clone;
        }

        if (generator.getLanguageSpecification().hasTypeDef(type)) {
            return new JavaType(type, generator.getEntitiesPackage(), arrayDimension);
        }

        // if it is a join point class
        if (generator.getLanguageSpecification().hasJoinPoint(type)) {
            final String jpName = GenConstants.abstractPrefix() + StringUtils.firstCharToUpper(type);
            return new JavaType(jpName, generator.getJoinPointClassPackage(), arrayDimension);
        }

        // If it does not exist, throw an exception with the error message and
        // the possible types that can be used
        final StringBuilder message = new StringBuilder(
                "Could not convert type '" + type + "'. Available types in the Language Specification: ");

        final StringBuilder availableTypes = reportAvailableTypes(generator.getLanguageSpecification());
        message.append(availableTypes);

        throw new RuntimeException(message.toString());
    }

    private static StringBuilder reportAvailableTypes(LanguageSpecification langSpec) {
        final StringBuilder message = new StringBuilder(ln() + "\t Primitives: ");
        String join = StringUtils.join(Arrays.asList(Primitive.values()), Enum::name, ", ")
                + ", Object, Array, Map, Template, Joinpoint";
        message.append(join);

        var objects = langSpec.getTypeDefs().values();
        if (!objects.isEmpty()) {

            message.append(ln() + "\t Defined types: ");
            final String objectsString = StringUtils.join(objects, TypeDef::getName, ", ");
            message.append(objectsString);
        }

        var joinpoints = langSpec.getDeclaredJoinPoints();
        if (!joinpoints.isEmpty()) {

            message.append(ln() + "\t Join point types: ");
            final String jpsString = StringUtils.join(joinpoints, JoinPointClass::getName, ", ");
            message.append(jpsString);
        }
        return message;
    }

    private static String normalizeReferenceType(String rawType) {
        String trimmed = rawType == null ? "" : rawType.trim();
        if (trimmed.isEmpty()) {
            return trimmed;
        }

        if (trimmed.startsWith("{")) {
            return normalizeWrappedReference(rawType, trimmed, '}', "{", "}");
        }

        if (trimmed.startsWith("[") && trimmed.indexOf('|') < 0) {
            return normalizeWrappedReference(rawType, trimmed, ']', "[", "]");
        }

        return trimmed;
    }

    private static String normalizeWrappedReference(String rawType, String trimmed, char closingChar, String openToken,
            String closeToken) {
        int closingIdx = trimmed.indexOf(closingChar);
        if (closingIdx < 0) {
            throw new RuntimeException("Malformed object reference type '" + rawType + "': missing closing '"
                    + closeToken + "' character");
        }

        String referenceName = trimmed.substring(1, closingIdx).trim();
        if (referenceName.isEmpty()) {
            throw new RuntimeException("Malformed object reference type '" + rawType
                    + "': expected a type name inside '" + openToken + "..." + closeToken + "'");
        }

        String remainder = trimmed.substring(closingIdx + 1);
        String normalizedRemainder = remainder.replaceAll("\\s+", "");
        if (!normalizedRemainder.isEmpty() && !normalizedRemainder.matches("(\\[\\])+$")) {
            throw new RuntimeException("Malformed object reference type '" + rawType
                    + "': only array suffixes are permitted after the closing '" + closeToken + "'");
        }

        return referenceName + normalizedRemainder;
    }

    public static String ln() {
        return Utils.ln();
    }

    // ========== IType-aware type conversion ==========

    /**
     * Converts an IType to a JavaType, resolving ThisType to the current join point
     * type.
     * This method handles all IType subtypes including ParameterizedType,
     * ArrayType, WildcardType, etc.
     *
     * @param type          the IType to convert (must not be null). String-based
     *                      conversion is no longer supported.
     * @param generator     the generator context for resolving type references
     * @param currentJpType the JavaType representing the current join point
     *                      abstract being generated; ThisType will resolve to this
     *                      type. Must not be null if the type contains ThisType
     *                      (directly or nested in generic arguments).
     * @return the converted JavaType
     * @throws IllegalArgumentException if type is null
     * @throws IllegalStateException    if ThisType is encountered but currentJpType
     *                                  is null (ThisType is not supported in
     *                                  contexts like TypeDef fields)
     */
    public static JavaType getConvertedType(IType type, JavaAbstractsGenerator generator, JavaType currentJpType) {
        ensureThisTypeContext(type, currentJpType);
        return convert(type, generator, currentJpType, PrimitiveConversionStrategy.STANDARD);
    }

    /**
     * Converts an IType to a JavaType for use as an attribute return type.
     * Similar to {@link #getConvertedType(IType, JavaAbstractsGenerator, JavaType)}
     * but wraps
     * primitives in their wrapper classes for use as return types.
     *
     * @param type          the IType to convert (must not be null). String-based
     *                      conversion is no longer supported.
     * @param generator     the generator context for resolving type references
     * @param currentJpType the JavaType representing the current join point
     *                      abstract; ThisType will resolve to this type. Must not
     *                      be null if the type contains ThisType (directly or
     *                      nested in generic arguments).
     * @return the converted JavaType with primitives wrapped
     * @throws IllegalArgumentException if type is null
     * @throws IllegalStateException    if ThisType is encountered but currentJpType
     *                                  is null (ThisType is not supported in
     *                                  contexts like TypeDef fields)
     */
    public static JavaType getAttributeConvertedType(IType type, JavaAbstractsGenerator generator,
            JavaType currentJpType) {
        ensureThisTypeContext(type, currentJpType);
        return convert(type, generator, currentJpType, PrimitiveConversionStrategy.ATTRIBUTE_RETURN);
    }

    private static void ensureThisTypeContext(IType type, JavaType currentJpType) {
        if (type == null) {
            return;
        }

        if (currentJpType != null) {
            return;
        }

        if (TypeTraversalUtils.containsThisType(type)) {
            throw new IllegalStateException(
                    "ThisType found but no currentJpType context provided. ThisType is not supported in this context (e.g., TypeDef fields).");
        }
    }

    private static JavaType convert(IType type, JavaAbstractsGenerator generator, JavaType currentJpType,
            PrimitiveConversionStrategy strategy) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }

        if (type instanceof org.lara.language.specification.dsl.types.Primitive primitiveType) {
            Primitive primitive = Primitive.getPrimitive(primitiveType.type());
            return strategy.convertPrimitive(primitive, 0);
        }

        // Handle ThisType - resolve to current join point type
        if (type instanceof ThisType) {
            return currentJpType.clone();
        }

        // Handle ArrayType - recursively convert base type and set dimension
        if (type instanceof ArrayType arrayType) {
            // For arrays, use the base conversion to ensure primitives are wrapped at the
            // element level
            JavaType baseJavaType = convert(arrayType.getBaseType(), generator, currentJpType, strategy);
            baseJavaType.setArrayDimension(baseJavaType.getArrayDimension() + arrayType.getDimension());
            return baseJavaType;
        }

        // Handle ParameterizedType - convert base and type arguments
        if (type instanceof ParameterizedType paramType) {
            JavaType baseJavaType = getRawBaseType(paramType.getBaseType(), generator, currentJpType);

            for (IType typeArg : paramType.getTypeArguments()) {
                JavaType argJavaType = convert(typeArg, generator, currentJpType, strategy);
                baseJavaType.addGeneric(new JavaGenericType(argJavaType));
            }

            return baseJavaType;
        }

        // Handle WildcardType
        if (type instanceof WildcardType wildcardType) {
            return convertWildcardType(wildcardType, generator, currentJpType);
        }

        // Handle JPType (join point reference)
        if (type instanceof JPType jpType) {
            String jpClassName = jpType.getJointPoint().getName();
            // Check if this is the global join point (named "joinpoint")
            // If so, use the pre-configured AJoinPoint type from the generator
            if (jpClassName.equalsIgnoreCase(JoinPointClassTypeName)) {
                return generator.getaJoinPointType().clone();
            }
            // For regular join points, construct the abstract class name
            String jpName = GenConstants.abstractPrefix() + StringUtils.firstCharToUpper(jpClassName);
            return new JavaType(jpName, generator.getJoinPointClassPackage());
        }

        // Handle GenericType - check standard Java types first, then fall back to
        // string-based conversion
        if (type instanceof GenericType genericType) {
            String typeName = genericType.type();
            // Check standard Java types (List, Set, Optional, etc.)
            if (StandardJavaTypes.containsKey(typeName)) {
                JavaType result = StandardJavaTypes.get(typeName).clone();
                if (genericType.isArray()) {
                    result.setArrayDimension(1);
                }
                return result;
            }

            // GenericType carries array information separately from its name.
            // Preserve it for non-standard types by appending the suffix before
            // simple-name conversion.
            if (genericType.isArray()) {
                return convertSimpleTypeName(typeName + "[]", generator, strategy);
            }
        }

        // Fall back to string-based conversion for other simple types
        return convertSimpleTypeName(type.type(), generator, strategy);
    }

    private static JavaType convertSimpleTypeName(String typeName, JavaAbstractsGenerator generator,
            PrimitiveConversionStrategy strategy) {
        String normalizedType = normalizeReferenceType(typeName);

        final Pair<String, Integer> splitType = JavaTypeFactory.splitTypeFromArrayDimension(normalizedType);
        final String baseType = splitType.left();
        final int arrayDimension = splitType.right();

        if (JavaTypeFactory.isPrimitive(baseType)) {
            Primitive primitive = Primitive.getPrimitive(baseType);
            return strategy.convertPrimitive(primitive, arrayDimension);
        }

        if (JavaTypeFactory.isPrimitiveWrapper(baseType)) {
            final JavaType primitiveWrapper = JavaTypeFactory.getPrimitiveWrapper(baseType);
            primitiveWrapper.setArrayDimension(arrayDimension);
            return primitiveWrapper;
        }

        return getConvertedTypeAux(baseType, generator, arrayDimension);
    }

    /**
     * Gets the raw base type for use in ParameterizedType conversion.
     * This method ensures that when resolving generic container types like Map,
     * List, etc.,
     * we get a clean type without any pre-populated generics.
     * 
     * <p>
     * The InterpreterTypes map contains some types with pre-populated wildcards for
     * backward compatibility (e.g., Map<?, ?>). When we're building a
     * ParameterizedType with explicit type arguments, we need the raw type without
     * these wildcards.
     * </p>
     *
     * @param baseType      the base type of a ParameterizedType
     * @param generator     the generator context
     * @param currentJpType the current join point type for ThisType resolution
     * @return a JavaType representing the raw base type without pre-populated
     *         generics
     */
    private static JavaType getRawBaseType(IType baseType, JavaAbstractsGenerator generator, JavaType currentJpType) {
        // For GenericType, check if it's a standard Java type
        if (baseType instanceof GenericType genericType) {
            String typeName = genericType.type();
            // Use StandardJavaTypes which have clean types without pre-populated generics
            if (StandardJavaTypes.containsKey(typeName)) {
                return StandardJavaTypes.get(typeName).clone();
            }
        }

        // Handle PrimitiveClasses that are also in StandardJavaTypes (e.g., MAP)
        // PrimitiveClasses.MAP has pre-populated wildcards in InterpreterTypes,
        // so we need to use the clean version from StandardJavaTypes
        if (baseType instanceof PrimitiveClasses primitiveClass) {
            String typeName = primitiveClass.type(); // e.g., "Map"
            if (StandardJavaTypes.containsKey(typeName)) {
                return StandardJavaTypes.get(typeName).clone();
            }
        }

        // For other types, convert normally.
        // Note: Only collection types in InterpreterTypes have pre-populated generics,
        // and those are handled above via StandardJavaTypes. Other types (String,
        // Object, etc.) don't have pre-populated generics that would interfere.
        return convert(baseType, generator, currentJpType, PrimitiveConversionStrategy.STANDARD);
    }

    /**
     * Converts a WildcardType to a JavaType representing a wildcard.
     * Note: Wildcards in Java generics are special - they can only appear as type
     * arguments, not as standalone types. This method returns a JavaType that can
     * be used with addGeneric().
     * For unbounded wildcards, returns the wildcard type directly.
     * For bounded wildcards, we create the bound type and represent the wildcard
     * accordingly.
     */
    private static JavaType convertWildcardType(WildcardType wildcardType, JavaAbstractsGenerator generator,
            JavaType currentJpType) {
        switch (wildcardType.getKind()) {
            case UNBOUNDED:
                return JavaTypeFactory.getWildCardType();

            case EXTENDS:
                // For ? extends T, we need to properly represent the bounded wildcard
                JavaType extendsBound = convert(wildcardType.getBound(), generator, currentJpType,
                        PrimitiveConversionStrategy.STANDARD);
                // Create a type that represents ? extends T
                // Use the full type representation including package if needed
                String extendsTypeName = extendsBound.getPackage().isEmpty()
                        ? extendsBound.getSimpleType()
                        : extendsBound.getPackage() + "." + extendsBound.getSimpleType();
                JavaType extendsWildcard = new JavaType("? extends " + extendsTypeName);
                return extendsWildcard;

            case SUPER:
                JavaType superBound = convert(wildcardType.getBound(), generator, currentJpType,
                        PrimitiveConversionStrategy.STANDARD);
                // Create a type that represents ? super T
                String superTypeName = superBound.getPackage().isEmpty()
                        ? superBound.getSimpleType()
                        : superBound.getPackage() + "." + superBound.getSimpleType();
                JavaType superWildcard = new JavaType("? super " + superTypeName);
                return superWildcard;

            default:
                throw new IllegalArgumentException("Unknown wildcard kind: " + wildcardType.getKind());
        }
    }
}
