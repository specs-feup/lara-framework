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

package org.lara.interpreter.weaver.generator.generator.java2cpp.helpers;

import java.util.Collections;
import java.util.Optional;

import org.lara.interpreter.exception.SelectException;
import org.lara.interpreter.weaver.generator.generator.java2cpp.JavaImplGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.artifactsmodel.schema.Artifact;
import org.lara.language.specification.artifactsmodel.schema.Attribute;
import org.lara.language.specification.joinpointmodel.constructor.JoinPointModelConstructor;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.lara.language.specification.joinpointmodel.schema.Select;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.enums.Annotation;
import org.specs.generators.java.enums.JDocTag;
import org.specs.generators.java.enums.Modifier;
import org.specs.generators.java.members.Method;
import org.specs.generators.java.utils.Utils;

import tdrc.utils.StringUtils;

public class JoinPointImplClassGenerator {

    private final JoinPointType joinPoint;
    private final JavaImplGenerator javaGenerator;
    private final JavaClass abstractClass;

    protected JoinPointImplClassGenerator(JavaImplGenerator javaGenerator, JoinPointType joinPoint,
	    JavaClass abstractClass) {
	this.joinPoint = joinPoint;
	this.javaGenerator = javaGenerator;
	this.abstractClass = abstractClass;
    }

    /**
     * Generate the Join Point abstract class for the given join point type
     * 
     * @param javaGenerator
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    public static JavaClass generate(JavaImplGenerator javaGenerator, JoinPointType joinPoint,
	    JavaClass abstractClass) {
	final JoinPointImplClassGenerator gen = new JoinPointImplClassGenerator(javaGenerator, joinPoint,
		abstractClass);
	return gen.generate();
    }

    /**
     * Generate the Join Point abstract class for the given join point type
     * 
     * @param sanitizedOutPackage
     * @param enums
     * @return
     */
    public JavaClass generate() {
	final String className = Utils.firstCharToUpper(joinPoint.getClazz());
	final JavaClass javaC = new JavaClass(javaGenerator.getJoinPointPrefix() + className,
		javaGenerator.getImplPackage());
	javaC.appendComment("Auto-Generated class for join point " + javaC.getName());
	javaC.appendComment("\nThis class is overwritten by the Weaver Generator.");
	javaC.add(JDocTag.AUTHOR, GenConstants.getAUTHOR());

	addFieldsAndConstructors(javaC);
	// addSelects(javaC);
	// addActions(javaC);

	return javaC;
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
		String methodName = attribute.getName();
		if (attribute.getParameter().isEmpty()) {
		    methodName = "get" + StringUtils.firstCharToUpper(methodName);
		}
		final String getterName = methodName;

		Optional<Method> abstrGetter = abstractClass.getMethods().stream()
			.filter(m -> m.getName().equals(getterName)).findFirst();
		Method getterImpl = abstrGetter.get().clone();
		getterImpl.remove(Modifier.ABSTRACT);
		getterImpl.add(Annotation.OVERRIDE);
		getterImpl.appendCode("//THIS WILL CONTAIN THE CODE using reference and attribute: "
			+ attribute.getType() + " " + attribute.getName());
		javaC.add(getterImpl);
		// GeneratorUtils.generateAttribute(attribute, javaC, javaGenerator);
	    }
	}
    }

    /**
     * Add selects for each join point child
     * 
     * @param joinPoint
     * @param javaC
     */
    void addSelects(JavaClass javaC) {

	// if (!joinPoint.getSelect().isEmpty())
	// javaC.addImport("java.util.List");

	for (final Select sel : joinPoint.getSelect()) {
	    final String selectName = sel.getAlias();
	    final String type = JoinPointModelConstructor.getJoinPointClass(sel);
	    addSelect(selectName, type, javaC);
	}

    }

    /**
     * Add selects for each join point child
     * 
     * @param joinPoint
     * @param javaC
     */
    void addActions(JavaClass javaC) {

	// final List<Action> actions = javaGenerator.getLanguageSpecification().getActionModel()
	// .getJoinPointOwnActions(joinPoint.getClazz());
	// for (final Action action : actions) {

	// final Method m = GeneratorUtils.generateActionMethod(action, javaGenerator);
	// javaC.add(m);
	//
	// Method cloned = GeneratorUtils.generateActionImplMethod(m, action.getName(), javaC);
	// javaC.add(cloned);
	// }

    }

    /**
     * Create a new select method for a joinpoint
     * 
     * @param selectName
     * @param type
     * @param javaC
     */
    private void addSelect(String selectName, String type, JavaClass javaC) {

	// final String joinPointPackage = javaGenerator.getJoinPointClassPackage();
	// final Method selectMethod = GeneratorUtils.generateSelectMethod(selectName, type, joinPointPackage, true);
	// javaC.add(selectMethod);

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

}
