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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.Primitive;
import org.lara.language.specification.dsl.types.PrimitiveClasses;

/**
 * A basic class that contains a type and a name
 * 
 * @author tiago
 *
 */
public class Action extends BaseNode implements Comparable<Action> {

    private static final Action INSERT;
    private static final Action DEF;

    static {
        INSERT = new Action(Primitive.VOID, "insert");
        Action.INSERT.addParameter(new LiteralEnum("", "{before,after,replace}"), "position");
        Action.INSERT.addParameter(PrimitiveClasses.STRING, "code");
        DEF = new Action(Primitive.VOID, "def");
        Action.DEF.addParameter(PrimitiveClasses.STRING, "attribute");
        Action.DEF.addParameter(PrimitiveClasses.OBJECT, "value");
    }

    private final Declaration declaration;
    private List<Parameter> parameters;

    public Action(IType returnType, String name) {
        this(returnType, name, new ArrayList<>());
    }

    public Action(IType returnType, String name, List<Parameter> parameters) {
        declaration = new Declaration(returnType, name);
        this.parameters = parameters;
    }

    public void addParameter(IType type, String name) {
        addParameter(type, name, "");
    }

    public void addParameter(IType type, String name, String defaultValue) {
        parameters.add(new Parameter(type, name, defaultValue));
    }

    public String getName() {
        return declaration.getName();
    }

    public void setName(String name) {
        declaration.setName(name);
    }

    public IType getType() {
        return declaration.getType();
    }

    public void setType(IType type) {
        declaration.setType(type);
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        String parametersStr = parameters.stream().map(Parameter::toString)
                .collect(Collectors.joining(", ", "(", ")"));
        return getType().toString() + " " + getName() + parametersStr;
    }

    public static Action getInsertAction() {
        return Action.INSERT;
    }

    public static Action getDefAction() {
        return Action.DEF;
    }

    public Declaration getDeclaration() {
        return declaration;
    }

    @Override
    public int compareTo(Action o) {
        return getName().compareTo(o.getName());
    }
}
