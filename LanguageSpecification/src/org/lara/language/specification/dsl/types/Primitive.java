/**
 * Copyright 2015 SPeCS.
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

/**
 * Enumeration of the existing primitives in Java
 * 
 * @author tiago
 *
 */
public enum Primitive implements IType {

    VOID,
    BYTE,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    CHAR;

    public static Primitive get(String name) {

        for (final Primitive primitive : values()) {

            if (primitive.name().toLowerCase().equals(name)) {
                return primitive;
            }
        }
        throw new RuntimeException("The type '" + name + "' is not a primitive.");
    }

    public static boolean contains(String name) {

        for (final Primitive primitive : values()) {

            if (primitive.name().toLowerCase().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getType() {
        return name().toLowerCase();
    }

    @Override
    public String toString() {
        return getType();
    }
}
