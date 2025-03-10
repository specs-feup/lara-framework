/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.dsl;

import org.lara.language.specification.dsl.types.IType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A basic class that contains a type and a name
 *
 * @author tiago
 */
public class Attribute extends BaseNode implements Comparable<Attribute> {

    private Declaration declaration;
    private List<Parameter> parameters;
    private List<Def> defs;

    public Attribute(IType type, String name) {
        this(type, name, new ArrayList<>());
    }

    public Attribute(IType type, String name, List<Parameter> parameters) {
        declaration = new Declaration(type, name);
        this.parameters = parameters;
        this.defs = new ArrayList<>();
    }

    public void addParameter(IType type, String name) {
        addParameter(type, name, "");
    }

    public void addParameter(IType type, String name, String defaultValue) {
        parameters.add(new Parameter(type, name, defaultValue));
    }

    public void addDef(Def def) {
        defs.add(def);
    }

    public List<Def> getDefs() {
        return defs;
    }

    public IType getType() {
        return declaration.getType();
    }

    public void setType(IType type) {
        declaration.setType(type);
    }

    public String getReturnType() {
        return getType().getType();
    }

    public String getName() {
        return declaration.getName();
    }

    public void setName(String name) {
        declaration.setName(name);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        String parametersStr = parameters.stream().map(p -> p.toString()).collect(Collectors.joining(", "));
        return getType() + " " + getName() + (parametersStr.isEmpty() ? "" : "(" + parametersStr + ")");
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(Declaration declaration) {
        this.declaration = declaration;
    }

    @Override
    public int compareTo(Attribute o) {
        return getName().compareTo(o.getName());
    }
}
