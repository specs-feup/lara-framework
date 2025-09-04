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

package org.lara.language.specification.dsl;

import org.lara.language.specification.dsl.types.IType;

public class Parameter {

    private Declaration declaration;
    private String defaultValue;

    public Parameter(IType returnType, String name) {
        this(returnType, name, "");
    }

    public Parameter(IType returnType, String name, String defaultValue) {
        this(new Declaration(returnType, name), defaultValue);
    }

    public Parameter(Declaration declaration, String defaultValue) {
        this.declaration = declaration;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        String initStr = defaultValue.isEmpty() ? "" : (" = " + defaultValue);
        return declaration.toString() + initStr;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    public String getName() {
        return getDeclaration().getName();
    }

    public String getType() {
        return getDeclaration().getType().type();
    }
}
