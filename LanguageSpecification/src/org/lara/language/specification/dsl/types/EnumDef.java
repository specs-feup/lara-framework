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

package org.lara.language.specification.dsl.types;

import org.lara.language.specification.dsl.BaseNode;
import org.lara.language.specification.dsl.IdentifierValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnumDef extends BaseNode implements IType {

    private String name;
    private List<EnumValue> values;

    public EnumDef(String name) {
        this(name, new ArrayList<>());
    }

    public EnumDef(String name, List<EnumValue> values) {
        setName(name);
        this.setValues(values);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        IdentifierValidator.requireValid(name, "enum name");
        this.name = name;
    }

    public void add(String value, String string) {
        getValues().add(new EnumValue(value, string));
    }

    @Override
    public String type() {
        return getName();
    }

    @Override
    public String toString() {
        return getName();
    }

    public String toDSLString() {

        String string = "enumdef " + getName() + "{";
        string += values.stream().map(EnumValue::toString).collect(Collectors.joining("\n\t", "\n\t", "\n"));

        return string + "}";
    }

    public List<EnumValue> getValues() {
        return values;
    }

    public void setValues(List<EnumValue> values) {
        this.values = values;
    }
}
