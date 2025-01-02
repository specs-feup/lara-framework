/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.weaver.generator.generator.java;

import org.lara.interpreter.weaver.generator.generator.BaseGenerator;
import org.lara.interpreter.weaver.generator.generator.java.helpers.*;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.classtypes.JavaEnum;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.types.JavaType;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaAbstractsGenerator extends BaseGenerator {
    private static final String INTERFACE_NAME = GenConstants.interfaceName();
    private static final String ABSTRACT_PREFIX = GenConstants.abstractPrefix();
    private static final String abstractJoinPointClassName = JavaAbstractsGenerator.ABSTRACT_PREFIX
            + JavaAbstractsGenerator.INTERFACE_NAME;

    private String joinPointPackage;

    private JavaType aJoinPointType;
    private JavaType aUserJoinPointType;
    private JavaType nodeJavaType;
    private String weaverPackage;
    private String abstractWeaverPackage;
    private String abstractUserJoinPointPackage;
    private String entitiesPackage;
    private String literalEnumsPackage;
    private String enumsPackage;
    private JavaType superClass;

    /**
     * These fields will contain the generated Java code for the given language specification
     */
    private JavaClass aJoinPointClass;
    private JavaClass userClass;
    private List<JavaEnum> enums;
    private List<JavaClass> abstractJoinPoints;

    private List<String> definedObjects;
    private JavaClass weaverAbstractClass;
    private JavaClass weaverImplClass;
    private JavaClass weaverExceptionClass;

    public JavaAbstractsGenerator(BaseGenerator baseGenerator) {
        super(baseGenerator);

        enums = new ArrayList<>();
        abstractJoinPoints = new ArrayList<>();
    }

    /**
     * Create the default JavaGenerator.<br>
     * <b>NOTE:</b> Please define the language specification before using {@link JavaAbstractsGenerator#generate()},
     * otherwise it will not work!
     */
    public JavaAbstractsGenerator() {
        this((BaseGenerator) null);
    }

    /**
     * Create a JavaGenerator with the given language specification
     *
     * @param langSpec the language specification
     */
    /*
    public JavaAbstractsGenerator(LanguageSpecification langSpec) {
        this();
        this.languageSpec(langSpec);
    }
     */

    /**
     * Create a JavaGenerator with the given language specification folder
     *
     * @param langSpec the folder location of the language specification
     */
    public JavaAbstractsGenerator(File langSpec) {
        this();
        this.languageSpec(langSpec);
    }

    /**
     * Create a JavaGenerator with the given language specification folder
     *
     * @param langSpec the name of the folder containing the language specification
     */
    public JavaAbstractsGenerator(String langSpec) {
        this();
        this.languageSpec(langSpec);
    }

    @Override
    protected void generateCode() {

        setPackages(); // Assign required package names
        setJavaTypes(); // Assign required basic java types

        // Generate the Weaver abstraction class
        weaverAbstractClass = WeaverAbstractGenerator.generate(this); // partially

        // Generate the Weaver implementation
        weaverImplClass = WeaverImplGenerator.generate(this);

        // Generate an exception class to be used by the weaver developer
        weaverExceptionClass = ExceptionGenerator.generate(this);

        // Generate the user defined objects that can be used as complex
        // attributes in LARA
        definedObjects = generateUserDefinedEntities(); // partially done
        // (stays here or move to new class?)

        // Abstract class containing all the global elements
        aJoinPointClass = SuperAbstractJoinPointGenerator.generate(this); // done

        // superClass = JavaTypeFactory.convert(aJoinPointClass);

        // Create a class which can be defined by the user
        userClass = UserAbstractJPClassGenerator.generate(this); // done
        // userClass.addImport(joinPointPackage + "." +
        // aJoinPointClass.getName());
        setSuperClass(JavaTypeFactory.convert(userClass));

        abstractJoinPoints = generateClasses();

        for (final JavaClass javaC : abstractJoinPoints) {
            if (javaC.getSuperClass().toString().equals(userClass.getName())) {
                javaC.addImport(abstractUserJoinPointPackage + "." + userClass.getName());
            }
            for (final Field f : javaC.getFields()) {
                if (definedObjects.contains(f.getType().toString())) {
                    javaC.addImport(getOutPackage() + "." + GenConstants.entity() + "." + f.getType());
                }
            }
        }

    }

    private void setJavaTypes() {
        final String aUserJPName = GenConstants.abstractPrefix() + getWeaverName() + GenConstants.interfaceName();
        final JavaType aUserJoinPointType = new JavaType(aUserJPName, getAbstractUserJoinPointClassPackage());
        setAUserJoinPointType(aUserJoinPointType);
        setAJoinPointType(new JavaType(JavaAbstractsGenerator.abstractJoinPointClassName, joinPointPackage));
        setNodeJavaType(new JavaType(getNodeType()));
    }

    /**
     * Prepare the used packages
     */
    private void setPackages() {
        final String basePackageName = getOutPackage().isEmpty() ? "" : getOutPackage() + ".";
        setWeaverPackage(getOutPackage());

        setAbstractWeaverPackage(basePackageName + "abstracts.weaver");
        setAbstractUserJoinPointPackage(weaverPackage + ".abstracts");
        setJoinPointPackage(basePackageName + "abstracts.joinpoints");
        setEntitiesPackage(basePackageName + GenConstants.entity());
        setEnumsPackage(basePackageName + GenConstants.enums());
        setLiteralEnumsPackage(joinPointPackage + ".enums");

        /**
         * return javaC.getClassPackage() + ".enums"
         */
    }

    // protected void printCode() {
    // generateFiles();
    // /*this.definedObjects, this.aJoinPointClass, this.userClass, this.abstractJoinPoints,
    // this.weaverAbstractClass,
    // this.weaverImplClass, this.weaverExceptionClass);*/
    // }
    // * @param definedObjects
    // * @param abstrJPClass
    // * @param userClass
    // * @param classes
    // * @param weaverAbstractClass
    // * @param weaverImplClass

    /**
     * Write the java class files in the defined output directory
     */
    @Override
    public void printCode() {// List<String> definedObjects, JavaClass abstrJPClass, JavaClass userClass,
        SpecsLogs.info(
                "Make sure to make this project import the following projects in order to work: jOptions, LanguageSpecification, LaraFramework, LARAI, SpecsUtils and WeaverInterface");

        // List<JavaClass> classes, JavaClass weaverAbstractClass, JavaClass weaverImplClass) {
        final File outDir = getOutDir();

        Utils.generateToFile(outDir, weaverAbstractClass, true);
        final boolean generatedWeaverImpl = Utils.generateToFile(outDir, weaverImplClass, false);
        if (!generatedWeaverImpl) {
            System.out.println("Note: java class for the weaver '" + getWeaverName()
                    + "' was not created because the file already exist on the path!");
        }
        Utils.generateToFile(outDir, aJoinPointClass, true);
        Utils.generateToFile(outDir, userClass, false);
        for (final JavaClass javaC : abstractJoinPoints) {
            Utils.generateToFile(outDir, javaC, true);
        }
        for (final JavaEnum javaE : enums) {
            Utils.generateToFile(outDir, javaE, true);
        }
        Utils.generateToFile(outDir, weaverExceptionClass, false);

    }

    /**
     * @param weaverName
     * @param outPackage
     * @return
     */
    public static String getWeaverText(String weaverName, JavaType aJoinPointType) {
        String text = SpecsIo.getResource(GenConstants.weaverTextHeaderLocation());
        text = text.replace(GenConstants.weaverNameTag(), weaverName);
        text = text.replace(GenConstants.linkTag(), aJoinPointType.getCanonicalName());
        return text;
    }


    protected List<JavaClass> generateClasses() {

        final List<JavaClass> joinPointClasses = new ArrayList<>();


        for (var joinPoint : getLanguageSpecification().getDeclaredJoinPoints()) {
            var jClass = AbstractJoinPointClassGenerator.generate(this, joinPoint);

            joinPointClasses.add(jClass);
        }

        return joinPointClasses;
    }

    /**
     * Generate Java Classes defined by the user in the artifacts model, such as <object name="Symbol">
     * <attribute name="name" type="String"/> </object>
     *
     * @param newObjects the map containing the objects mapped to the field elements
     * @param outPackage the class package (will append '.entities')
     */
    private List<String> generateUserDefinedEntities() {
        final List<String> userDefinedClasses = new ArrayList<>();

        for (var newObject : getLanguageSpecification().getTypeDefs().values()) {
            final JavaClass uDClass = UserEntitiesGenerator.generate(this, newObject);
            userDefinedClasses.add(newObject.getName());
            Utils.generateToFile(getOutDir(), uDClass, true);
        }

        for (var newEnum : getLanguageSpecification().getEnumDefs().values()) {
            final JavaEnum userEnum = UserEnumsGenerator.generate(this, newEnum);
            userDefinedClasses.add(newEnum.getName());
            Utils.generateToFile(getOutDir(), userEnum, true);
        }

        // System.out.println("USER DEFINED CLASSES: " + userDefinedClasses);
        return userDefinedClasses;
    }

    public List<JavaEnum> getEnums() {
        return enums;
    }

    /**
     * This package will contain the abstract join points, including the super type AJoinPoint
     *
     * @return
     */
    public String getJoinPointClassPackage() {
        return joinPointPackage;
    }

    /**
     * This is the package for the Weaver implementation
     *
     * @return
     */
    public String getWeaverPackage() {
        return weaverPackage;
    }

    /**
     * This is the package for the abstract representation of the weaver
     *
     * @return
     */
    public String getAbstractWeaverPackage() {
        return abstractWeaverPackage;
    }

    /**
     * This package is the one containing the abstract class that can be edited by the use
     *
     * @return
     */
    public String getAbstractUserJoinPointClassPackage() {
        return abstractUserJoinPointPackage;
    }

    /**
     * This is the name of the (uneditable) abstract join point
     *
     * @return
     */
    public static String getAbstractJoinPointClassName() {

        return JavaAbstractsGenerator.abstractJoinPointClassName;
    }

    public JavaType getSuperClass() {

        return superClass;
    }

    public JavaType getaJoinPointType() {
        return aJoinPointType;
    }

    public void setAJoinPointType(JavaType aJoinPointType) {
        this.aJoinPointType = aJoinPointType;
    }

    /**
     * This package will contain the user defined entities
     *
     * @return
     */
    public String getEntitiesPackage() {
        return entitiesPackage;
    }

    private void setEntitiesPackage(String entitiesPackage) {
        this.entitiesPackage = entitiesPackage;
    }

    private void setJoinPointPackage(String joinPointClassPackage) {
        joinPointPackage = joinPointClassPackage;
    }

    private void setWeaverPackage(String weaverPackage) {
        this.weaverPackage = weaverPackage;
    }

    private void setAbstractWeaverPackage(String abstractWeaverPackage) {
        this.abstractWeaverPackage = abstractWeaverPackage;
    }

    private void setAbstractUserJoinPointPackage(String abstractUserJoinPointClassPackage) {
        abstractUserJoinPointPackage = abstractUserJoinPointClassPackage;
    }

    private void setSuperClass(JavaType superClass) {
        this.superClass = superClass;
    }

    public JavaType getAUserJoinPointType() {
        return aUserJoinPointType;
    }

    public void setAUserJoinPointType(JavaType aUserJoinPointType) {
        this.aUserJoinPointType = aUserJoinPointType;
    }

    public String getLiteralEnumsPackage() {
        return literalEnumsPackage;
    }

    public void setLiteralEnumsPackage(String enumsPackage) {
        this.literalEnumsPackage = enumsPackage;
    }

    public JavaType getNodeJavaType() {
        return nodeJavaType;
    }

    public void setNodeJavaType(JavaType nodeType) {
        nodeJavaType = nodeType;
    }

    public JavaClass getAJoinPointClass() {
        return aJoinPointClass;
    }

    public JavaClass getUserClass() {
        return userClass;
    }

    public List<JavaClass> getAbstractJoinPoints() {
        return abstractJoinPoints;
    }

    public List<String> getDefinedObjects() {
        return definedObjects;
    }

    public JavaClass getWeaverAbstractClass() {
        return weaverAbstractClass;
    }

    public JavaClass getWeaverImplClass() {
        return weaverImplClass;
    }

    public String getEnumsPackage() {
        return enumsPackage;
    }

    public void setEnumsPackage(String enumsPackage) {
        this.enumsPackage = enumsPackage;
    }

}
