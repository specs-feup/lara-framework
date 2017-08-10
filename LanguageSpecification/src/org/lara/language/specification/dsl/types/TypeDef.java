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

package org.lara.language.specification.dsl.types;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lara.language.specification.dsl.Attribute;
import org.lara.language.specification.dsl.BaseNode;

public class TypeDef extends BaseNode implements IType {

    private String name;
    private List<Attribute> fields;

    public TypeDef(String name) {
        this(name, new ArrayList<>());
    }

    public TypeDef(String name, List<Attribute> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void add(IType type, String name) {
        fields.add(new Attribute(type, name));
    }

    public List<Attribute> getFields() {
        return fields;
    }

    public void setFields(List<Attribute> fields) {
        this.fields = fields;
    }

    @Override
    public String getType() {
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    public String toDSLString() {

        String string = "typedef " + getName() + "{";
        string += fields.stream().map(Attribute::toString).collect(Collectors.joining("\n\t", "\n\t", "\n"));

        return string + "}";
    }
}
