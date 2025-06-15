/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.ConvertUtils;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.language.specification.dsl.types.TypeDef;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import tdrc.utils.Pair;
import tdrc.utils.StringUtils;

public class UserEntitiesGenerator extends GeneratorHelper {
    private final TypeDef entity;

    /**
     * Generate an entity based on the NewObject instance
     *
     * @param newObject the new Object to generate
     * @return
     */
    protected UserEntitiesGenerator(JavaAbstractsGenerator javaGenerator, TypeDef object) {
        super(javaGenerator);
        entity = object;
    }

    /**
     * Generate an entity based on the NewObject instance
     *
     * @param newObject the new Object to generate
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator, TypeDef object) {
        final UserEntitiesGenerator gen = new UserEntitiesGenerator(javaGenerator, object);
        return gen.generate();
    }

    /**
     * Generate an entity based on the NewObject instance
     *
     * @return
     */
    @Override
    public JavaClass generate() {
        final JavaClass uDClass = new JavaClass(entity.getName(), javaGenerator.getEntitiesPackage());
        uDClass.appendComment(ln());
        uDClass.add(JDocTag.AUTHOR, "Lara C.");

        for (var attribute : entity.getFields()) {

            final String fieldName = attribute.getName();
            final String classType = attribute.getReturnType();

            final String sanitizedName = StringUtils.getSanitizedName(fieldName);
            final JavaType jType = ConvertUtils.getConvertedType(classType, javaGenerator);
            final Field field = new Field(jType, sanitizedName, Privacy.PRIVATE);
            final Pair<Method, Method> getSet = GeneratorUtils.createGetterAndSetter(field, fieldName, false);
            uDClass.add(field);
            uDClass.add(getSet.left());
            uDClass.add(getSet.right());
        }
        uDClass.createOrGetEmptyConstructor().clearCode();
        uDClass.createFullConstructor();
        generateToString(uDClass);
        return uDClass;
    }

    /**
     * Generate the toString method based on a json format
     *
     * @param uDClass
     */
    private static void generateToString(JavaClass uDClass) {
        final Method toString = new Method(JavaTypeFactory.getStringType(), "toString");
        toString.add(Annotation.OVERRIDE);
        // default method
        // toString.appendCode("return super.toString();");
        final StringBuffer buff = new StringBuffer("String json = \"{\\n\";" + ln());
        for (final Field f : uDClass.getFields()) {
            final String name = f.getName();
            buff.append("json += \" ");
            buff.append(name);
            buff.append(": \"+get" + StringUtils.firstCharToUpper(name));
            buff.append("() + \",\\n\";" + ln());
        }
        buff.append("json+=\"}\";" + ln());
        buff.append("return json;");
        toString.setMethodBody(buff);
        uDClass.add(toString);
    }
}
