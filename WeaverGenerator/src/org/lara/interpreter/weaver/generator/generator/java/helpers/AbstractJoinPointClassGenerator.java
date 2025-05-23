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
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.JoinPointClass;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;

import java.util.Optional;

/**
 * Generate the Join Point abstract class for a given join point type
 */
public class AbstractJoinPointClassGenerator extends GeneratorHelper {

    private final JoinPointClass joinPoint;

    protected AbstractJoinPointClassGenerator(JavaAbstractsGenerator javaGenerator, JoinPointClass joinPoint) {
        super(javaGenerator);
        this.joinPoint = joinPoint;
    }

    /**
     * Generate the Join Point abstract class for the given join point type
     *
     * @param javaGenerator
     * @param joinPoint
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator, JoinPointClass joinPoint) {
        final AbstractJoinPointClassGenerator gen = new AbstractJoinPointClassGenerator(javaGenerator, joinPoint);
        return gen.generate();
    }

    /**
     * Generate the Join Point abstract class for the given join point type
     *
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    @Override
    public JavaClass generate() {
        final String className = Utils.firstCharToUpper(joinPoint.getName());
        final JavaClass javaC = new JavaClass(GenConstants.abstractPrefix() + className,
                javaGenerator.getJoinPointClassPackage());
        javaC.add(Modifier.ABSTRACT);
        javaC.appendComment("Auto-Generated class for join point " + javaC.getName());
        javaC.appendComment(ln() + "This class is overwritten by the Weaver Generator." + ln() + ln());
        String comment = joinPoint.getToolTip().orElse(null);
        if (comment != null) {
            javaC.appendComment(comment);
        }
        javaC.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());

        addFieldsAndConstructors(javaC);
        addActions(javaC);

        String superTypeName = null;

        // Returns itself if join point does extend anything
        // This is testing if the node

        // If explicitly extends a join point
        if (joinPoint.getExtendExplicit().isPresent()) {
            var superType = joinPoint.getExtendExplicit().get();

            String superClass = Utils.firstCharToUpper(superType.getName());
            superClass = GenConstants.abstractPrefix() + superClass;

            superTypeName = addSuperMethods(javaC);

            javaC.setSuperClass(new JavaType(superClass, javaGenerator.getJoinPointClassPackage()));

        } else {
            javaC.setSuperClass(javaGenerator.getSuperClass());
        }

        // If join point is not extended by any other join point, it is final
        boolean isFinal = !javaGenerator.getLanguageSpecification().isSuper(joinPoint);

        generateGet_Class(javaC, isFinal);

        if (superTypeName != null) {
            GeneratorUtils.generateInstanceOf(javaC, "this." + superTypeName, isFinal);
        }

        AttributesEnumGenerator.generate(javaC, joinPoint);

        return javaC;
    }

    private void generateGet_Class(JavaClass javaC, boolean isFinal) {
        final Method clazzMethod = new Method(JavaTypeFactory.getStringType(), GenConstants.getClassName());
        clazzMethod.add(Annotation.OVERRIDE);
        if (isFinal) {
            clazzMethod.add(Modifier.FINAL);
        }
        clazzMethod.appendComment("Returns the join point type of this class");
        clazzMethod.addJavaDocTag(JDocTag.RETURN, "The join point type");
        clazzMethod.appendCode("return \"" + joinPoint.getName() + "\";");
        javaC.add(clazzMethod);
    }

    /**
     * Add fields and constructors
     *
     * @param defaultAttribute
     * @param attributes
     * @param javaC
     * @param enums
     * @param abstractGetters
     */
    private void addFieldsAndConstructors(JavaClass javaC) {

        for (var attribute : joinPoint.getAttributesSelf()) {
            Method generateAttribute = GeneratorUtils.generateAttribute(attribute, javaC, javaGenerator);
            Method generateAttributeImpl = GeneratorUtils.generateAttributeImpl(generateAttribute, attribute,
                    javaC, javaGenerator);
            javaC.add(generateAttributeImpl);
        }

    }

    /**
     * Adds actions for the join point
     *
     * @param javaC
     */
    private void addActions(JavaClass javaC) {

        for (var action : joinPoint.getActionsSelf()) {
            final Method m = GeneratorUtils.generateActionMethod(action, javaGenerator);
            javaC.add(m);

            Method cloned = GeneratorUtils.generateActionImplMethod(m, action, javaC,
                    javaGenerator);

            javaC.add(cloned);
        }

    }
    
    /**
     * Add code that calls to the super methods
     *
     * @param joinPoint
     * @param langSpec
     * @param javaC
     */
    private String addSuperMethods(JavaClass javaC) {

        var superType = joinPoint.getExtendExplicit().orElseThrow(() -> new RuntimeException("Expected join point to explicitly extend another join point"));

        final String superClassName = GenConstants.abstractPrefix() + Utils.firstCharToUpper(superType.getName());
        final String fieldName = GenConstants.abstractPrefix().toLowerCase()
                + Utils.firstCharToUpper(superType.getName());
        final JavaType joinPointType = new JavaType(superClassName, javaGenerator.getJoinPointClassPackage());
        javaC.add(new Field(joinPointType, fieldName, Privacy.PROTECTED));

        final Constructor constructor = new Constructor(javaC);
        constructor.addArgument(joinPointType, fieldName);
        if (superType.getExtendExplicit().isPresent()) {
            constructor.appendCode("super(" + fieldName + ");" + ln());
        }
        constructor.appendCode("this." + fieldName + " = " + fieldName + ";");
        // System.out.println("addSuperMethods: " + joinPoint.getClazz());
        GeneratorUtils.addSuperMethods(javaC, fieldName, javaGenerator, joinPoint);

        // Add global methods for global attributes
        var globalAttributes = javaGenerator.getLanguageSpecification().getGlobal().getAttributesSelf();

        GeneratorUtils.addSuperGetters(javaC, fieldName, javaGenerator, globalAttributes);
        // GeneratorUtils.addSuperMethods(javaC, fieldName, javaGenerator, superType.getClazz());
        // addSuperGetters(javaC, fieldName, javaGenerator, parent);
        // System.out.println("ABS JP: " + superType.getClazz());
        // Update also here for actionImpl
//        GeneratorUtils.addSuperActions(javaGenerator, javaC, superType.getName(), fieldName);
        GeneratorUtils.addSuperActions(javaGenerator, javaC, superType, fieldName);
        // GeneratorUtils.addSuperToString(javaC, fieldName); // Do not add toString(), JoinPoint already implements it,
        // and this one has bugs (e.g., param shows 'decl')
        // addSuperWeaverEngineSetter(javaC, fieldName);
        addGetSuperMethod(javaC, joinPointType, fieldName);
        return fieldName;
    }

    /**
     * E.g.: Optional&lt;? extends AExpression&gt;
     *
     * @param javaC
     * @param joinPointType
     * @param fieldName
     */
    private void addGetSuperMethod(JavaClass javaC, JavaType joinPointType, String fieldName) {
        String GET_SUPER_NAME = "getSuper";
        javaC.addImport(Optional.class);
        JavaGenericType wildExtendsType = JavaTypeFactory.getWildExtendsType(joinPointType);
        JavaType optionalOfSuperType = JavaTypeFactory.convert(Optional.class);
        optionalOfSuperType.addGeneric(wildExtendsType);
        final Method getSuperMethod = new Method(optionalOfSuperType, GET_SUPER_NAME);
        getSuperMethod.add(Annotation.OVERRIDE);

        getSuperMethod.appendCodeln("return Optional.of(this." + fieldName + ");");
        javaC.add(getSuperMethod);
    }

}
