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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.exception.SelectException;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java.utils.GeneratorUtils;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.interpreter.weaver.interf.SelectOp;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.actionsmodel.schema.Action;
import org.lara.language.specification.artifactsmodel.schema.Artifact;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.joinpointmodel.JoinPointModel;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.Select;
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

/**
 * Generate the Join Point abstract class for a given join point type
 *
 */
public class AbstractJoinPointClassGenerator extends GeneratorHelper {

    private final JoinPointType joinPoint;

    protected AbstractJoinPointClassGenerator(JavaAbstractsGenerator javaGenerator, JoinPointType joinPoint) {
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
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator, JoinPointType joinPoint) {
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
        // generateClass(JoinPointType joinPoint, String superClass, String
        // outPackage,
        // LanguageSpecification langSpec, List<JavaEnum> enums, boolean
        // abstractGetters)
        final LanguageSpecification langSpec = javaGenerator.getLanguageSpecification();
        final String className = Utils.firstCharToUpper(joinPoint.getClazz());
        final JavaClass javaC = new JavaClass(GenConstants.abstractPrefix() + className,
                javaGenerator.getJoinPointClassPackage());
        javaC.add(Modifier.ABSTRACT);
        javaC.appendComment("Auto-Generated class for join point " + javaC.getName());
        javaC.appendComment("\nThis class is overwritten by the Weaver Generator.\n\n");
        String comment = joinPoint.getTooltip();
        if (comment != null) {
            javaC.appendComment(comment);
        }
        javaC.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());

        addFieldsAndConstructors(javaC);
        addSelects(javaC);
        addActions(javaC);

        String superTypeName = null;
        if (!joinPoint.equals(joinPoint.getExtends())) {
            final JoinPointType superType = (JoinPointType) joinPoint.getExtends();

            String superClass = Utils.firstCharToUpper(superType.getClazz());
            superClass = GenConstants.abstractPrefix() + superClass;

            superTypeName = addSuperMethods(javaC);

            javaC.setSuperClass(new JavaType(superClass, javaGenerator.getJoinPointClassPackage()));

        } else {
            // System.out.println("CODE :" + javaC.generateCode(0));

            javaC.setSuperClass(javaGenerator.getSuperClass());
        }

        boolean isFinal = !langSpec.getJpModel().isSuper(joinPoint);

        GeneratorUtils.createSelectByName(javaC, joinPoint, superTypeName, isFinal, langSpec.getJpModel());
        if (javaGenerator.hasDefs()) {
            List<Attribute> allDefinableAttributes = langSpec.getArtifacts()
                    .getAllDefinableAttributes(joinPoint.getClazz());

            GeneratorUtils.createDefImpl(javaC, isFinal, allDefinableAttributes, javaGenerator);
        }
        GeneratorUtils.createListOfAvailableAttributes(javaC, langSpec, joinPoint, superTypeName, isFinal);
        GeneratorUtils.createListOfAvailableSelects(javaC, joinPoint, superTypeName, isFinal);
        GeneratorUtils.createListOfAvailableActions(javaC, joinPoint, superTypeName, langSpec, isFinal);

        generateGet_Class(javaC, isFinal);

        if (superTypeName != null) {
            GeneratorUtils.generateInstanceOf(javaC, "this." + superTypeName, isFinal);
        }

        AttributesEnumGenerator.generate(javaC, joinPoint, langSpec);

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
        clazzMethod.appendCode("return \"" + joinPoint.getClazz() + "\";");
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
    }

    /**
     * Add selects for each join point child
     *
     * @param joinPoint
     * @param javaC
     */
    private void addSelects(JavaClass javaC) {

        // if (!joinPoint.getSelect().isEmpty())
        // javaC.addImport("java.util.List");

        for (final Select sel : joinPoint.getSelect()) {

            addSelect(sel, javaC);
        }

    }

    /**
     * Adds actions for the join point
     *
     * @param javaC
     */
    private void addActions(JavaClass javaC) {

        final List<Action> actions = javaGenerator.getLanguageSpecification().getActionModel()
                .getJoinPointOwnActions(joinPoint.getClazz());

        for (final Action action : actions) {

            final Method m = GeneratorUtils.generateActionMethod(action, javaGenerator);
            javaC.add(m);

            Method cloned = GeneratorUtils.generateActionImplMethod(m, action.getName(), action.getReturn(), javaC,
                    javaGenerator.hasEvents());
            javaC.add(cloned);
        }

    }

    /**
     * Create a new select method for a joinpoint
     *
     * @param selectName
     * @param type
     * @param javaC
     */
    private void addSelect(Select sel, JavaClass javaC) {

        final String joinPointPackage = javaGenerator.getJoinPointClassPackage();
        // final Method selectMethod = GeneratorUtils.generateSelectMethod(sel, joinPointPackage, true);
        final Method selectMethod = GeneratorUtils.generateSelectMethodGeneric(sel, joinPointPackage);

        javaC.add(selectMethod);
        javaC.addImport(SelectOp.class);

        // addSelectWithTryCatch(selectName, javaC, selectMethod);
    }

    /**
     * Create the select method with try catch
     *
     * @param selectName
     * @param javaC
     * @param selectMethod
     */
    void addSelectWithTryCatch(String selectName, JavaClass javaC, final Method selectMethod) {
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
    }

    /**
     * Add code that calls to the super methods
     *
     * @param joinPoint
     * @param langSpec
     * @param javaC
     */
    private String addSuperMethods(JavaClass javaC) {

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
    /*
    private void addSuperWeaverEngineSetter(JavaClass javaC, String fieldName) {
        String SET_WEAVER_ENGINE_METHOD_NAME = "setWeaverEngine";
        final Method setWeaverEngineMethod = new Method(JavaTypeFactory.getVoidType(), SET_WEAVER_ENGINE_METHOD_NAME);
        setWeaverEngineMethod.add(Annotation.OVERRIDE);
    
        setWeaverEngineMethod.appendCodeln("this." + fieldName + "." + SET_WEAVER_ENGINE_METHOD_NAME + "(engine);");
        setWeaverEngineMethod.appendCodeln("super." + SET_WEAVER_ENGINE_METHOD_NAME + "(engine);");
        Method setWeaverEngineMethod2 = setWeaverEngineMethod.clone();
        setWeaverEngineMethod.addArgument(WeaverEngine.class, "engine");
        setWeaverEngineMethod2.addArgument(JoinPoint.class, "engine");
    
        javaC.add(setWeaverEngineMethod);
        javaC.add(setWeaverEngineMethod2);
    
        //
        // @Override
        // public void setWeaverEngine(WeaverEngine engine) {
        // aExpression.setWeaverEngine(engine);
        // super.setWeaverEngine(engine);
        // }
        //
        // @Override
        // public void setWeaverEngine(JoinPoint reference) {
        // aExpression.setWeaverEngine(reference);
        // super.setWeaverEngine(reference);
        // }
    }
    */
}
