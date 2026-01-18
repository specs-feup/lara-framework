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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.interf;

import java.util.Arrays;

import org.lara.interpreter.exception.LaraIException;

import pt.up.fe.specs.util.providers.StringProvider;
import tdrc.utils.StringUtils;

public interface NamedEnum extends StringProvider {

    public String getName();

    @Override
    default String getString() {
        return getName();
    }

    public static <T extends Enum<T> & NamedEnum> T fromString(Class<T> clazz, String name, String sourceName) {

        for (T item : clazz.getEnumConstants()) {

            if (item.getName().equals(name)) {
                return item;
            }
        }

        throw new LaraIException("Unknown value for " + sourceName + ": " + name + ". Expected one of: "
                + StringUtils.join(Arrays.asList(clazz.getEnumConstants()), NamedEnum::getName, ", "));
    }
}
