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

package org.lara.interpreter.weaver.generator.generator.java2cpp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.weaver.generator.generator.BaseGenerator;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.interpreter.weaver.generator.generator.java2cpp.helpers.JoinPointImplClassGenerator;
import org.lara.interpreter.weaver.generator.generator.utils.GenConstants;
import org.lara.language.specification.LanguageSpecification;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.types.JavaTypeFactory;
import org.specs.generators.java.utils.Utils;

import tdrc.utils.StringUtils;

public class JavaImplGenerator extends BaseGenerator {

    private JavaAbstractsGenerator javaAGenerator;
    private String joinPointPrefix;
    private String implPackage;
    private List<JavaClass> javaImplClasses;

    /**
     * Create a JavaGenerator with the given language specification
     * 
     * @param langSpec
     *            the language specification
     * @param joinPointPrefix
     *            the prefix to use for the name of the join point implementations
     */
    public JavaImplGenerator(LanguageSpecification langSpec, String joinPointPrefix) {
        super();
        this.languageSpec(langSpec);
        setJoinPointPrefix(joinPointPrefix);

    }

    public JavaImplGenerator(File langSpecDir) {
        super();
        this.languageSpec(langSpecDir);
        setImplPackage("todo");
        javaImplClasses = new ArrayList<>();
        setJoinPointPrefix("C");
    }

    @Override
    protected void generateCode() {
        setAbstractGetters(true);
        javaAGenerator = new JavaAbstractsGenerator(this);
        javaAGenerator.generate();
        // After generating the abstract methods then we should generate the java specific classes
        List<JavaClass> abstJoinPoints = javaAGenerator.getAbstractJoinPoints();
        for (JavaClass abstJP : abstJoinPoints) {
            String joinPointName = abstJP.getName().substring(GenConstants.abstractPrefix().length());
            joinPointName = StringUtils.firstCharToLower(joinPointName);

            // Replace the abstract prefix "A" with the given prefix
            // String implName = joinPointPrefix + joinPointName;
            // JavaClass jpImpl = new JavaClass(implName, implPackage);

            JoinPointType jPType = getLanguageSpecification().getJpModel().getJoinPoint(joinPointName);
            // Create the implementation of the abstract version

            JavaClass jpImpl = JoinPointImplClassGenerator.generate(this, jPType, abstJP);
            jpImpl.setSuperClass(JavaTypeFactory.convert(abstJP));

            javaImplClasses.add(jpImpl);
            // Generate the select methods (only abstract selects)
            /*
            List<Method> selects = abstJP.getMethods().stream()
            .filter(m -> m.getModifiers().contains(Modifier.ABSTRACT) && m.getName().startsWith("select"))
            .collect(Collectors.toList());
            */

        }
    }

    @Override
    protected void printCode() {
        javaAGenerator.print();
        File outputDir = getOutDir();
        javaImplClasses.forEach(jic -> Utils.generateToFile(outputDir, jic, true));
    }

    public String getJoinPointPrefix() {
        return joinPointPrefix;
    }

    public void setJoinPointPrefix(String joinPointPrefix) {
        this.joinPointPrefix = joinPointPrefix;
    }

    public String getImplPackage() {
        return implPackage;
    }

    public void setImplPackage(String implPackage) {
        this.implPackage = implPackage;
    }

}
