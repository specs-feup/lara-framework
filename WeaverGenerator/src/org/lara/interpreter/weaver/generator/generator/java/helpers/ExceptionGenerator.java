/**
 * Copyright 2016 SPeCS.
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

import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.enums.Privacy;
import org.specs.generators.java.members.Constructor;
import org.specs.generators.java.members.Field;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.types.Primitive;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class ExceptionGenerator extends GeneratorHelper {

    protected ExceptionGenerator(JavaAbstractsGenerator javaGenerator) {
        super(javaGenerator);
    }

    /**
     * Generate an Exception with the name of the Weaver
     * 
     * @param javaGenerator
     * @return
     */
    public static JavaClass generate(JavaAbstractsGenerator javaGenerator) {
        return new ExceptionGenerator(javaGenerator).generate();
    }

    @Override
    public JavaClass generate() {
        JavaClass eC = new JavaClass(javaGenerator.getWeaverName() + "Exception",
                javaGenerator.getOutPackage() + ".exceptions");
        eC.setSuperClass(JavaTypeFactory.convert(BaseException.class));
        eC.appendComment(
                "This class can be used as the exception of this weaver in order to follow the message pretty print used by the interpreter");
        generateSerialIDField(eC);
        Field eventF = generateEventField(eC);
        generateConstructors(eC, eventF);
        generateSimpleMessageMethod(eC);
        generateMessageMethod(eC);
        return eC;
    }

    private static void generateConstructors(JavaClass eC, Field eventF) {

        Constructor eventConstr = new Constructor(eC);
        eventConstr.addArgument(eventF);
        eventConstr.appendCode("this(" + eventF.getName() + ",null);" + ln());
        eventConstr.appendComment("Create a new exception with a message");
        eventConstr.addJavaDocTag(JDocTag.PARAM, "event the exception message");

        Constructor completeConstr = new Constructor(eC);
        completeConstr.addArgument(eventF);
        completeConstr.addArgument(JavaTypeFactory.convert(Throwable.class), "cause");
        completeConstr.appendCode("super(cause);" + ln());
        completeConstr.appendCode("this." + eventF.getName() + " = " + eventF.getName() + ";" + ln());
        completeConstr.appendComment("Create a new exception with the cause and the triggering event");
        completeConstr.addJavaDocTag(JDocTag.PARAM, "event the event that caused the exception");
        completeConstr.addJavaDocTag(JDocTag.PARAM, "cause the cause of this exception");

    }

    private static Method generateMessageMethod(JavaClass eC) {
        Method m = new Method(JavaTypeFactory.getStringType(), "generateMessage");
        m.add(Annotation.OVERRIDE);
        m.setPrivacy(Privacy.PROTECTED);
        m.addJavaDocTag(JDocTag.SEE, BaseException.class.getName() + "#generateMessage()");
        m.appendCode("return \"Exception in \"+this.generateSimpleMessage();");
        eC.add(m);
        return m;
    }

    /**
     * Generate the following Method:
     * 
     * <pre>
     * 
     * &#64;Override
     * protected String generateSimpleMessage() {
     *     return "when building interpreter";
     * }
     * </pre>
     * 
     * @see pt.up.fe.specs.tools.lara.exception.BaseException#generateSimpleMessage()
     * @param eC
     */
    private Method generateSimpleMessageMethod(JavaClass eC) {
        Method m = new Method(JavaTypeFactory.getStringType(), "generateSimpleMessage");
        m.add(Annotation.OVERRIDE);
        m.setPrivacy(Privacy.PROTECTED);
        m.addJavaDocTag(JDocTag.SEE, BaseException.class.getName() + "#generateSimpleMessage()");
        String exceptionMessage = "\" [" + javaGenerator.getWeaverName() + "] \" +this.event";
        m.appendCode("return " + exceptionMessage + ";");
        eC.add(m);
        return m;
    }

    /**
     * Generate a field that can be used by the weaver developer to inform in which event the exception occurred
     * 
     * @param eC
     * @return
     */
    private static Field generateEventField(JavaClass eC) {
        Field eventF = new Field(JavaTypeFactory.getStringType(), "event");
        // serialID.addModifier();
        eventF.addModifier(Modifier.FINAL);
        eC.add(eventF);
        return eventF;
    }

    /**
     * Generate the following field:
     * 
     * <pre>
     * private static final long serialVersionUID = 1L;
     * </pre>
     * 
     * @param eC
     */
    private static void generateSerialIDField(JavaClass eC) {
        Field serialID = new Field(JavaTypeFactory.getPrimitiveType(Primitive.LONG), "serialVersionUID");
        // serialID.addModifier();
        serialID.addModifier(Modifier.STATIC);
        serialID.addModifier(Modifier.FINAL);
        serialID.setInitializer(e -> new StringBuilder("1L"));
        eC.add(serialID);
    }
}
