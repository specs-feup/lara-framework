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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LiteralEnum implements IType {

    private String name;
    private List<String> values;

    public LiteralEnum(String name) {
        this(name, new ArrayList<>());
    }

    public LiteralEnum(String name, String values) {
        this(name, parseValues(values));
    }

    private static List<String> parseValues(String values2) {
        List<String> values = new ArrayList<>();
        final String[] items = values2.replace("{", "").replace("}", "").split(",");
        for (String value : items) {
            value = value.trim();
            values.add(value);
        }
        return values;
    }

    public LiteralEnum(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public static LiteralEnum of(String name, String... values) {
        return new LiteralEnum(name, Arrays.asList(values));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addValue(String value) {
        values.add(value);
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String getType() {
        return toString();
    }

    @Override
    public String toString() {
        return values.stream().collect(Collectors.joining("| ", "[", "]"));
    }
}
