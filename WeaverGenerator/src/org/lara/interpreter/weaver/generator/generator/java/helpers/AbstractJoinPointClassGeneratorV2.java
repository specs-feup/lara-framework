/**
 * Copyright 2015 SPeCS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.helpers;

import java.util.Optional;

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.dsl.JoinPointClass;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaGenericType;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;

/**
 * Generate the Join Point abstract class for a given join point type
 *
 */
public class AbstractJoinPointClassGeneratorV2 extends GeneratorHelper {

    private final JoinPointClass joinPoint;

    protected AbstractJoinPointClassGeneratorV2(JavaAbstractsGenerator javaGenerator, JoinPointClass joinPoint) {
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
        final AbstractJoinPointClassGeneratorV2 gen = new AbstractJoinPointClassGeneratorV2(javaGenerator, joinPoint);
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
        var langSpec = javaGenerator.getLanguageSpecificationV2();
        final String className = Utils.firstCharToUpper(joinPoint.getName());
        final JavaClass javaC = new JavaClass(GenConstants.abstractPrefix() + className,
                javaGenerator.getJoinPointClassPackage());
        javaC.add(Modifier.ABSTRACT);
        javaC.appendComment("Auto-Generated class for join point " + javaC.getName());
        javaC.appendComment(ln() + "This class is overwritten by the Weaver Generator." + ln() + ln());
        joinPoint.getToolTip().ifPresent(javaC::appendComment);

        javaC.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());

        addFieldsAndConstructors(javaC);
        addSelects(javaC);
        addActions(javaC);

        String superTypeName = null;
        var superType = joinPoint.getExtend().orElse(null);
        // if (!joinPoint.equals(joinPoint.getExtends())) {
        if (superType != null) {
            // final JoinPointType superType = (JoinPointType) joinPoint.getExtends();

            String superClass = Utils.firstCharToUpper(superType.getName());
            superClass = GenConstants.abstractPrefix() + superClass;

            superTypeName = addSuperMethods(javaC);

            javaC.setSuperClass(new JavaType(superClass, javaGenerator.getJoinPointClassPackage()));

        } else {
            // System.out.println("CODE :" + javaC.generateCode(0));

            javaC.setSuperClass(javaGenerator.getSuperClass());
        }

        boolean isFinal = !langSpec.isSuper(joinPoint);

        GeneratorUtils.createSelectByNameV2(javaC, joinPoint, superTypeName, isFinal);
        if (javaGenerator.hasDefs()) {
            var allDefinableAttributes = joinPoint.getAttributes();
            // List<Attribute> allDefinableAttributes = langSpec.getArtifacts()
            // .getAllDefinableAttributes(joinPoint.getClazz());

            GeneratorUtils.createDefImplV2(javaC, isFinal, allDefinableAttributes, javaGenerator);
        }

        // TODO:
        /*
        GeneratorUtils.createListOfAvailableAttributes(javaC, langSpec, joinPoint, superTypeName, isFinal);
        GeneratorUtils.createListOfAvailableSelects(javaC, joinPoint, superTypeName, isFinal);
        GeneratorUtils.createListOfAvailableActions(javaC, joinPoint, superTypeName, langSpec, isFinal);
        
        generateGet_Class(javaC, isFinal);
        
        if (superTypeName != null) {
            GeneratorUtils.generateInstanceOf(javaC, "this." + superTypeName, isFinal);
        }
        
        AttributesEnumGenerator.generate(javaC, joinPoint, langSpec);
        */
        return javaC;
    }

    private void generateGet_Class(JavaClass javaC, boolean isFinal) {
        // TODO:
        /*
        final Method clazzMethod = new Method(JavaTypeFactory.getStringType(), GenConstants.getClassName());
        clazzMethod.add(Annotation.OVERRIDE);
        if (isFinal) {
            clazzMethod.add(Modifier.FINAL);
        }
        clazzMethod.appendComment("Returns the join point type of this class");
        clazzMethod.addJavaDocTag(JDocTag.RETURN, "The join point type");
        clazzMethod.appendCode("return \"" + joinPoint.getClazz() + "\";");
        javaC.add(clazzMethod);
        */
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
        // TODO:
        /*
        final Artifact artifact = javaGenerator.getLanguageSpecification().getArtifacts()
                .getArtifact(joinPoint.getClazz());
        if (artifact != null) {
            for (final Attribute attribute : artifact.getAttribute()) {
                Method generateAttribute = GeneratorUtils.generateAttribute(attribute, javaC, javaGenerator);
                Method generateAttributeImpl = GeneratorUtils.generateAttributeImpl(generateAttribute, attribute,
                        javaC, javaGenerator);
                javaC.add(generateAttributeImpl);
                GeneratorUtils.generateDefMethods(attribute, generateAttribute.getReturnType(), javaC,
                        javaGenerator);
                // if(!def.isEmpty())
            }
        }
        */
    }

    /**
     * Add selects for each join point child
     *
     * @param joinPoint
     * @param javaC
     */
    private void addSelects(JavaClass javaC) {
        // TODO:
        /*
        // if (!joinPoint.getSelect().isEmpty())
        // javaC.addImport("java.util.List");
        
        for (final Select sel : joinPoint.getSelect()) {
        
            addSelect(sel, javaC);
        }
        */
    }

    /**
     * Adds actions for the join point
     *
     * @param javaC
     */
    private void addActions(JavaClass javaC) {
        // TODO:
        /*
         * final List<Action> actions = javaGenerator.getLanguageSpecification().getActionModel()
         * .getJoinPointOwnActions(joinPoint.getClazz());
         * 
         * for (final Action action : actions) {
         * 
         * final Method m = GeneratorUtils.generateActionMethod(action, javaGenerator); javaC.add(m);
         * 
         * Method cloned = GeneratorUtils.generateActionImplMethod(m, action.getName(), action.getReturn(), javaC,
         * javaGenerator.hasEvents()); javaC.add(cloned); }
         */
    }

    /**
     * Create a new select method for a joinpoint
     *
     * @param selectName
     * @param type
     * @param javaC
     */
    private void addSelect(Select sel, JavaClass javaC) {
        // TODO:
        /*
        final String joinPointPackage = javaGenerator.getJoinPointClassPackage();
        // final Method selectMethod = GeneratorUtils.generateSelectMethod(sel, joinPointPackage, true);
        final Method selectMethod = GeneratorUtils.generateSelectMethodGeneric(sel, joinPointPackage);
        
        javaC.add(selectMethod);
        javaC.addImport(SelectOp.class);
        
        // addSelectWithTryCatch(selectName, javaC, selectMethod);
         
         */
    }

    /**
     * Create the select method with try catch
     *
     * @param selectName
     * @param javaC
     * @param selectMethod
     */
    void addSelectWithTryCatch(String selectName, JavaClass javaC, final Method selectMethod) {
        // TODO:
        /*
        // Add the method used to encapsulate the output with an Optional, or
        // encapsulate a thrown exception
        final Method selectWithTry = new Method(selectMethod.getReturnType(),
                selectMethod.getName() + GenConstants.getWithTryPrefix());
        selectWithTry.appendCodeln("try{");
        
        String tab = "   ";
        selectWithTry.appendCode(tab + selectMethod.getReturnType().getSimpleType());
        String listName = selectName + "List";
        selectWithTry.appendCodeln(" " + listName + " = " + selectMethod.getName() + "();");
        selectWithTry.appendCodeln(tab + "return " + listName + "!=null?" + listName + ":Collections.emptyList();");
        selectWithTry.appendCodeln("} catch(Exception e) {");
        selectWithTry.appendCodeln(tab + "throw new " + SelectException.class.getSimpleName() + "(\""
                + joinPoint.getClazz() + "\",\"" + selectName + "\",e);");
        selectWithTry.appendCodeln("}");
        
        javaC.add(selectWithTry);
        javaC.addImport(Collections.class);
        javaC.addImport(SelectException.class);
        */
    }

    /**
     * Add code that calls to the super methods
     *
     * @param joinPoint
     * @param langSpec
     * @param javaC
     */
    private String addSuperMethods(JavaClass javaC) {
        // TODO:
        /*
        final JoinPointType superType = JoinPointModel.toJoinPointType(joinPoint.getExtends());
        final JoinPointType superSuperType = JoinPointModel.toJoinPointType(superType.getExtends());
        final String superClassName = GenConstants.abstractPrefix() + Utils.firstCharToUpper(superType.getClazz());
        final String fieldName = GenConstants.abstractPrefix().toLowerCase()
                + Utils.firstCharToUpper(superType.getClazz());
        final JavaType joinPointType = new JavaType(superClassName, javaGenerator.getJoinPointClassPackage());
        javaC.add(new Field(joinPointType, fieldName, Privacy.PROTECTED));
        
        final Constructor constructor = new Constructor(javaC);
        constructor.addArgument(joinPointType, fieldName);
        if (!superType.equals(superSuperType)) {
            constructor.appendCode("super(" + fieldName + ");\n");
        }
        constructor.appendCode("this." + fieldName + " = " + fieldName + ";");
        
        GeneratorUtils.addSuperMethods(javaC, fieldName, javaGenerator, joinPoint);
        
        // Update also here for actionImpl
        GeneratorUtils.addSuperActions(javaGenerator, javaC, superType.getClazz(), fieldName);
        GeneratorUtils.addSuperToString(javaC, fieldName);
        // addSuperWeaverEngineSetter(javaC, fieldName);
        addGetSuperMethod(javaC, joinPointType, fieldName);
        return fieldName;
        */

        return null;
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
