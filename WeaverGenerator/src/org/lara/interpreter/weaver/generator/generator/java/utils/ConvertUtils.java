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
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.utils;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.LanguageSpecification;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.types.Primitive;
import org.specs.generators.java.utils.Utils;
import tdrc.utils.Pair;
import tdrc.utils.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConvertUtils {

    private static final String JoinPointClassTypeName = "Joinpoint";
    private static final String JoinPointInterfaceClassTypeName = "JoinpointInterface";
    private static final Map<String, JavaType> InterpreterTypes;

    static {
        InterpreterTypes = new HashMap<>();

        ConvertUtils.InterpreterTypes.put("String", JavaTypeFactory.getStringType());

        final JavaType objectType = JavaTypeFactory.getObjectType();
        ConvertUtils.InterpreterTypes.put("Object", objectType);

        // objectType = JavaTypeFactory.getObjectType();
        // objectType.setArrayDimension(1);
        // InterpreterTypes.put("Array", objectType);

        final JavaType mapType = new JavaType(Map.class);
        mapType.addGeneric(new JavaGenericType(JavaTypeFactory.getWildCardType()));
        mapType.addGeneric(new JavaGenericType(JavaTypeFactory.getWildCardType()));
        ConvertUtils.InterpreterTypes.put("Map", mapType);
        ConvertUtils.InterpreterTypes.put("Template", JavaTypeFactory.getStringType());

    }

    /**
     * Get the correct type for the given string, according to:
     * <p>
     * 1st the primitives, 2nd the declared objects and 3rd the declared join points
     *
     * @param type
     * @param ls
     * @return
     * @throws RuntimeException if the type cannot be found.
     */
    public static JavaType getConvertedType(String type, JavaAbstractsGenerator generator) {
        // String original = type;
        // First remove array dimension
        final Pair<String, Integer> splittedType = JavaTypeFactory.splitTypeFromArrayDimension(type);
        type = splittedType.getLeft();
        final int arrayDimension = splittedType.getRight();
        // if the type is a primitive (e.g. int) or a primitive wrapper (e.g.
        // Integer)
        if (JavaTypeFactory.isPrimitive(type)) {

            final JavaType primitiveType = JavaTypeFactory.getPrimitiveType(Primitive.getPrimitive(type));
            primitiveType.setArrayDimension(arrayDimension);
            return primitiveType;
        }
        if (JavaTypeFactory.isPrimitiveWrapper(type)) {

            final JavaType primitiveWrapper = JavaTypeFactory.getPrimitiveWrapper(type);
            primitiveWrapper.setArrayDimension(arrayDimension);
            return primitiveWrapper;
        }

        return getConvertedTypeAux(type, generator, arrayDimension);
    }

    /**
     * Get the correct type for the return of an attribute. This method converts a primitive type into its wrapper
     * <p>
     * <p>
     * 1st the primitives, 2nd the declared objects and 3rd the declared join points
     *
     * @param type
     * @param ls
     * @return
     * @throws RuntimeException if the type cannot be found.
     */
    public static JavaType getAttributeConvertedType(String type, JavaAbstractsGenerator generator) {
        // String original = type;
        // First remove array dimension
        final Pair<String, Integer> splittedType = JavaTypeFactory.splitTypeFromArrayDimension(type);
        type = splittedType.getLeft();
        final int arrayDimension = splittedType.getRight();
        // if the type is a primitive (e.g. int) or a primitive wrapper (e.g.
        // Integer)
        if (JavaTypeFactory.isPrimitive(type)) {

            Primitive primitive = Primitive.getPrimitive(type);
            if (arrayDimension == 0) {
                final JavaType primitiveType = JavaTypeFactory.getPrimitiveWrapper(primitive);
                primitiveType.setArrayDimension(arrayDimension);
                return primitiveType;
            }
            final JavaType primitiveType = JavaTypeFactory.getPrimitiveType(Primitive.getPrimitive(type));
            primitiveType.setArrayDimension(arrayDimension);
            return primitiveType;
        }
        if (JavaTypeFactory.isPrimitiveWrapper(type)) {

            final JavaType primitiveWrapper = JavaTypeFactory.getPrimitiveWrapper(type);
            primitiveWrapper.setArrayDimension(arrayDimension);
            return primitiveWrapper;
        }

        return getConvertedTypeAux(type, generator, arrayDimension);
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

        // if it is the base joinpoint type
        if (keyType.equals(ConvertUtils.JoinPointClassTypeName)) {
            final JavaType clone = generator.getaJoinPointType().clone();
            clone.setArrayDimension(arrayDimension);
            return clone;
        }

        // if it is the joinpoint interface type
        if (keyType.equals(ConvertUtils.JoinPointInterfaceClassTypeName)) {
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
            final JavaType jpType = new JavaType(jpName, generator.getJoinPointClassPackage(), arrayDimension);
            return jpType;
        }

        // If it does not exist, throw an exception with the error message and
        // the possible
        // types that can be used
        final StringBuilder message = new StringBuilder(
                "Could not convert type '" + type + "'. Available types in the Language Specification: ");

        final StringBuilder availableTypes = reportAvailableTypes(generator.getLanguageSpecification());
        message.append(availableTypes);

        throw new RuntimeException(message.toString());
    }

    private static StringBuilder reportAvailableTypes(LanguageSpecification langSpec) {
        final StringBuilder message = new StringBuilder(ln() + "\t Primitives: ");
        String join = StringUtils.join(Arrays.asList(Primitive.values()), p -> p.name(), ", ")
                + ", Object, Array, Map, Template, Joinpoint";
        message.append(join);

        var objects = langSpec.getTypeDefs().values();
        if (!objects.isEmpty()) {

            message.append(ln() + "\t Defined types: ");
            final String objectsString = StringUtils.join(objects, t -> t.getName(), ", ");
            message.append(objectsString);
        }

        var joinpoints = langSpec.getDeclaredJoinPoints();
        if (!joinpoints.isEmpty()) {

            message.append(ln() + "\t Join point types: ");
            final String jpsString = StringUtils.join(joinpoints, j -> j.getName(), ", ");
            message.append(jpsString);
        }
        return message;
    }

    public static String ln() {
        return Utils.ln();
        //return "\n";
        //return SpecsIo.getNewline();
    }
}
