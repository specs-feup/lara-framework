/**
 * Copyright 2015 SPeCS.
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

import tdrc.utils.StringUtils;

/**
 * Enumeration of the existing primitives in Java
 *
 * @author tiago
 */
public enum PrimitiveClasses implements IType {

    VOID,
    BYTE,
    SHORT,
    INTEGER,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    CHAR,
    STRING,
    OBJECT,
    MAP,
    JOINPOINT_INTERFACE;

    public static PrimitiveClasses get(String name) {
        name = name.toUpperCase();
        for (final PrimitiveClasses primitive : values()) {

            // String primName = StringUtils.firstCharToUpper(primitive.name());
            if (primitive.name().equals(name)) {
                return primitive;
            }
        }
        throw new RuntimeException("The type '" + name + "' is not a primitive.");
    }

    public static boolean contains(String name) {
        name = name.toUpperCase();
        for (final PrimitiveClasses primitive : values()) {

            // String primName = StringUtils.firstCharToUpper(primitive.name());
            if (primitive.name().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        if (this == JOINPOINT_INTERFACE) {
            return "JoinpointInterface";
        }
        return StringUtils.firstCharToUpper(name().toLowerCase());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return getType();
    }
}
