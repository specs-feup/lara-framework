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

package pt.up.fe.specs.lara.doc.jsdoc.data;

import java.util.Optional;

import pt.up.fe.specs.util.SpecsLogs;

public class ParamData {
    private final String name;
    private final boolean isOptional;
    private final String defaultValue;

    public ParamData(String name, boolean isOptional, String defaultValue) {
        this.name = name;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public Optional<String> getDefaultValue() {
        return Optional.ofNullable(defaultValue);
    }

    public static ParamData parseParam(String param) {
        String workString = param.trim();
        if (!workString.startsWith("[")) {
            return new ParamData(workString, false, null);
        }

        // Remove []
        if (!workString.endsWith("]")) {
            SpecsLogs.msgInfo("Missing closing square bracket: " + param);
            return new ParamData(workString, false, null);
        }

        boolean isOptional = true;
        workString = workString.substring(1, workString.length() - 1);

        // Find default value
        int eqSignIndex = workString.indexOf('=');

        String name = eqSignIndex == -1 ? workString.trim() : workString.substring(0, eqSignIndex).trim();
        String defaultValue = eqSignIndex == -1 ? null : workString.substring(eqSignIndex + 1).trim();

        return new ParamData(name, isOptional, defaultValue);
    }

}
