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

import pt.up.fe.specs.util.providers.ResourceProvider;

public enum GeneratorResource implements ResourceProvider {
    ABSTRACT_JOINPOINT_TEMPLATE("AbstractClassTemplate.txt"),
    CONCRETE_JOINPOINT_TEMPLATE("ConcreteClassTemplate.txt");

    private final String resource;

    private GeneratorResource(String filename) {
        this.resource = "pt/up/fe/specs/lara/weaver/generator/" + filename;
    }

    @Override
    public String getResource() {
        return resource;
    }

}
