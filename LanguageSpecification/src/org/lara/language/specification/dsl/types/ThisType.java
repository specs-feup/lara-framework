/**
 * Copyright 2026 SPeCS.
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

package org.lara.language.specification.dsl.types;

/**
 * Represents the late-bound 'this' type that refers to the current join point class.
 * This type is never resolved within this project; resolution is deferred to downstream
 * consumers (e.g., WeaverGenerator) where the owning join point context is known.
 * 
 * <p>When used in attribute/action return types or parameters, 'this' provides polymorphic
 * behavior across the join point hierarchy: if B extends A and foo() is defined in A with
 * return type 'this', then A.foo() returns A and B.foo() returns B.</p>
 * 
 * <p>ThisType can appear standalone or as a type argument within generics
 * (e.g., {@code List<this>}, {@code Map<String, this>}).</p>
 */
public final class ThisType implements IType {

    /** The keyword used in type specifications to represent the self type. */
    public static final String THIS_KEYWORD = "this";

    /** Singleton instance since ThisType has no state. */
    private static final ThisType INSTANCE = new ThisType();

    private ThisType() {
        // Private constructor to enforce singleton pattern
    }

    /**
     * Returns the singleton ThisType instance.
     * 
     * @return the ThisType instance
     */
    public static ThisType getInstance() {
        return INSTANCE;
    }

    @Override
    public String type() {
        return THIS_KEYWORD;
    }

    @Override
    public String toString() {
        return THIS_KEYWORD;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ThisType;
    }

    @Override
    public int hashCode() {
        return THIS_KEYWORD.hashCode();
    }
}
