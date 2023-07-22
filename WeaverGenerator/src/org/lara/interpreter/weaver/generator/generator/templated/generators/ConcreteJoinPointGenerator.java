/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.templated.generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.lara.interpreter.weaver.generator.generator.templated.GeneratedFile;
import org.lara.interpreter.weaver.generator.generator.templated.TemplatedGenerator;
import org.lara.language.specification.joinpointmodel.schema.JoinPointType;

import pt.up.fe.specs.util.utilities.Replacer;

public class ConcreteJoinPointGenerator {

    private final TemplatedGenerator baseGenerator;

    public ConcreteJoinPointGenerator(TemplatedGenerator baseGenerator) {
        this.baseGenerator = baseGenerator;
    }

    public String getClassname(JoinPointType jpType) {
        var classname = jpType.getClazz();

        // Add prefix and capitalize first letter
        return baseGenerator.getConcreteClassesPrefix() + classname.substring(0, 1).toUpperCase()
                + classname.substring(1);
    }

    public String getPackage() {
        var weaverPackage = baseGenerator.getWeaverPackage();
        var sep = weaverPackage.isBlank() ? "" : ".";
        return baseGenerator.getWeaverPackage() + sep + "joinpoints";
    }

    public String getQualifiedClassname(JoinPointType jpType) {
        return getPackage() + "." + getClassname(jpType);
    }

    public GeneratedFile generate(JoinPointType joinPoint) {

        var year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
        var classname = getClassname(joinPoint);

        var template = new Replacer(GeneratorResource.CONCRETE_JOINPOINT_TEMPLATE);
        template
                .replace("<YEAR>", year)
                .replace("<PACKAGE>", getPackage())
                .replace("<BASE_NODE>", baseGenerator.getNodeType())
                .replace("<BASE_NODE_NAME>", baseGenerator.getNodeName())
                .replace("<ABSTRACT_QUALIFIED_NAME>",
                        baseGenerator.getAbstractGenerator().getQualifiedClassname(joinPoint))
                .replace("<CONCRETE_NAME>", getClassname(joinPoint))
                .replace("<ABSTRACT_NAME>", baseGenerator.getAbstractGenerator().getClassname(joinPoint))
                .replace("<JP_NAME>", joinPoint.getClazz() + "Node");

        var filename = classname + ".java";

        // var abstractName = abstractClass.getName();
        // SpecsCheck.checkArgument(abstractName.startsWith("A"),
        // () -> "Expected abstract class name to start with A: " + abstractName);

        // return getConcreteClassesPrefix() + abstractName.substring(1);
        return new GeneratedFile(filename, template.toString(), Arrays.asList(getPackage().split("\\.")));
    }

    public List<GeneratedFile> generate() {
        var langSpec = baseGenerator.getLanguageSpecification();
        var jpModel = langSpec.getJpModel();

        var classes = new ArrayList<GeneratedFile>();

        for (var joinPoint : jpModel.getJoinPointList().getJoinpoint()) {
            classes.add(generate(joinPoint));
        }

        return classes;
    }
}
