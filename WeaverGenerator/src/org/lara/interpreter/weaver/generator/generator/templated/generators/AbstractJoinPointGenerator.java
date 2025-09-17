/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter.weaver.generator.generator.templated.generators;

import org.lara.interpreter.weaver.generator.generator.templated.TemplatedGenerator;
import org.lara.language.specification.dsl.JoinPointClass;

public class AbstractJoinPointGenerator {

    private final TemplatedGenerator baseGenerator;

    public AbstractJoinPointGenerator(TemplatedGenerator baseGenerator) {
        this.baseGenerator = baseGenerator;
    }

    public String getClassname(JoinPointClass jpType) {
        var classname = jpType.getName();

        // Add A as a prefix and capitalize first letter
        return "A" + classname.substring(0, 1).toUpperCase()
                + classname.substring(1);
    }

    public String getPackage() {
        var weaverPackage = baseGenerator.getWeaverPackage();
        var sep = weaverPackage.isBlank() ? "" : ".";
        return baseGenerator.getWeaverPackage() + sep + "abstracts.joinpoints";
    }

    public String getQualifiedClassname(JoinPointClass jpType) {
        return getPackage() + "." + getClassname(jpType);
    }
}
