/**
 * Copyright 2026 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.generator.generator.java.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lara.interpreter.weaver.generator.generator.java.JavaAbstractsGenerator;
import org.lara.language.specification.dsl.Action;
import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.Parameter;
import org.lara.language.specification.dsl.types.Primitive;
import org.lara.language.specification.dsl.types.PrimitiveClasses;
import org.specs.generators.java.classtypes.JavaClass;
import org.specs.generators.java.members.Argument;
import org.specs.generators.java.members.Method;

@DisplayName("GeneratorUtils")
class GeneratorUtilsTest {

    @Test
    @DisplayName("action generation should produce canonical signature for primitive types")
    void actionGenerationSignatureForPrimitiveTypes() {
        Action action = new Action(Primitive.INT, "sum", java.util.List.of(
                new Parameter(Primitive.INT, "left"),
                new Parameter(Primitive.INT, "right")));

        Method generatedMethod = GeneratorUtils.generateActionMethod(action, null, null);
        assertThat(methodSignature(generatedMethod)).isEqualTo("sum(left:int, right:int):int");
    }

    @Test
    @DisplayName("attribute generation should produce canonical signature for primitive types")
    void attributeGenerationSignatureForPrimitiveTypes() {
        Attribute attribute = new Attribute(Primitive.INT, "counter");
        JavaAbstractsGenerator generator = new JavaAbstractsGenerator();
        JavaClass targetClass = new JavaClass("CanonicalClass", "pt.up.fe");

        Method generatedMethod = GeneratorUtils.generateAttribute(attribute, targetClass, generator, null);
        assertThat(methodSignature(generatedMethod)).isEqualTo("getCounter():Integer");
    }

    @Test
    @DisplayName("base JoinPoint analysis should detect corrected return type and final wrapper skip")
    void analyzeJoinPointBaseAction() {
        Action action = new Action(PrimitiveClasses.STRING, "insert", List.of(
                new Parameter(PrimitiveClasses.STRING, "position"),
                new Parameter(PrimitiveClasses.STRING, "code")));

        GeneratorUtils.JoinPointBaseActionInfo info = GeneratorUtils.analyzeJoinPointBaseAction(action);

        assertThat(info.skipWrapper()).isTrue();
        assertThat(info.correctedReturnType()).contains("joinpoint[]");
    }

    @Test
    @DisplayName("adding same action twice should keep one signature pair")
    void addActionAndWrapperDeduplicates() {
        JavaAbstractsGenerator generator = new JavaAbstractsGenerator();
        JavaClass targetClass = new JavaClass("ActionHost", "pt.up.fe");
        Action action = new Action(Primitive.INT, "sum", List.of(
                new Parameter(Primitive.INT, "left"),
                new Parameter(Primitive.INT, "right")));

        GeneratorUtils.addActionAndWrapper(targetClass, action, generator, null, false,
                "duplicate action %s %s", "duplicate wrapper %s");
        int methodCountAfterFirst = targetClass.getMethods().size();

        GeneratorUtils.addActionAndWrapper(targetClass, action, generator, null, false,
                "duplicate action %s %s", "duplicate wrapper %s");

        assertThat(methodCountAfterFirst).isEqualTo(2);
        assertThat(targetClass.getMethods()).hasSize(2);
    }

    private static String methodSignature(Method method) {
        String params = method.getParams().stream()
                .map(GeneratorUtilsTest::argumentSignature)
                .collect(Collectors.joining(", "));

        return method.getName() + "(" + params + "):" + method.getReturnType().getSimpleType();
    }

    private static String argumentSignature(Argument argument) {
        return argument.getName() + ":" + argument.getClassType().getSimpleType();
    }
}
