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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a parameterized (generic) type with a base type and type arguments.
 * 
 * <p>Examples:
 * <ul>
 *   <li>{@code List<String>} - base type "List" with one type argument (String)</li>
 *   <li>{@code Map<String, this>} - base type "Map" with two type arguments</li>
 *   <li>{@code List<Map<String, Integer>>} - nested parameterized types</li>
 * </ul>
 * </p>
 * 
 * <p>Type arguments can be any IType, including ThisType, other ParameterizedTypes,
 * primitives, join point types, etc.</p>
 */
public class ParameterizedType implements IType {

    private final IType baseType;
    private final List<IType> typeArguments;

    /**
     * Creates a parameterized type with the given base type and type arguments.
     * 
     * @param baseType the base type (e.g., "List", "Map")
     * @param typeArguments the list of type arguments; must not be null or empty
     * @throws NullPointerException if baseType or typeArguments is null
     * @throws IllegalArgumentException if typeArguments is empty
     */
    public ParameterizedType(IType baseType, List<IType> typeArguments) {
        Objects.requireNonNull(baseType, "Base type cannot be null");
        Objects.requireNonNull(typeArguments, "Type arguments cannot be null");
        if (typeArguments.isEmpty()) {
            throw new IllegalArgumentException("Type arguments cannot be empty for a parameterized type");
        }

        this.baseType = baseType;
        this.typeArguments = new ArrayList<>(typeArguments);
    }

    /**
     * Creates a parameterized type with a single type argument.
     * 
     * @param baseType the base type
     * @param typeArgument the single type argument
     * @return the parameterized type
     */
    public static ParameterizedType of(IType baseType, IType typeArgument) {
        return new ParameterizedType(baseType, List.of(typeArgument));
    }

    /**
     * Creates a parameterized type with multiple type arguments.
     * 
     * @param baseType the base type
     * @param typeArguments the type arguments
     * @return the parameterized type
     */
    public static ParameterizedType of(IType baseType, IType... typeArguments) {
        return new ParameterizedType(baseType, List.of(typeArguments));
    }

    /**
     * Returns the base type (e.g., for {@code List<String>}, returns the type for "List").
     * 
     * @return the base type
     */
    public IType getBaseType() {
        return baseType;
    }

    /**
     * Returns an unmodifiable view of the type arguments.
     * 
     * @return the list of type arguments
     */
    public List<IType> getTypeArguments() {
        return Collections.unmodifiableList(typeArguments);
    }

    @Override
    public String type() {
        String args = typeArguments.stream()
                .map(IType::toString)
                .collect(Collectors.joining(", "));
        return baseType.type() + "<" + args + ">";
    }

    @Override
    public String toString() {
        return type();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ParameterizedType other)) {
            return false;
        }
        return baseType.equals(other.baseType) && typeArguments.equals(other.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseType, typeArguments);
    }
}
