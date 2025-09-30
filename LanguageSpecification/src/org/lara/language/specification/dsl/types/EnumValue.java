/**
 * Copyright 2017 SPeCS.
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

import org.lara.language.specification.dsl.IdentifierValidator;

public class EnumValue implements Comparable<EnumValue> {

    private String value;
    private String string;

    public EnumValue(String value, String string) {
        this.setValue(value);
        this.setString(string);
    }

    public EnumValue() {
        this(null, null);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        IdentifierValidator.requireValid(value, "enum value");
        this.value = value;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return value + "(" + string + ")";
    }

    @Override
    public int compareTo(EnumValue o) {
        return getValue().compareTo(o.getValue());
    }
}
