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

import java.util.Objects;

/**
 * Represents a wildcard type used in generic type arguments.
 * 
 * <p>Supports three kinds of wildcards:
 * <ul>
 *   <li>{@link Kind#UNBOUNDED} - {@code ?} - matches any type</li>
 *   <li>{@link Kind#EXTENDS} - {@code ? extends T} - upper bounded wildcard</li>
 *   <li>{@link Kind#SUPER} - {@code ? super T} - lower bounded wildcard</li>
 * </ul>
 * </p>
 * 
 * <p>Examples:
 * <ul>
 *   <li>{@code List<?>} - list of unknown type</li>
 *   <li>{@code List<? extends Number>} - list of Number or subtype</li>
 *   <li>{@code List<? super Integer>} - list of Integer or supertype</li>
 *   <li>{@code Map<String, ? extends this>} - wildcard with ThisType bound</li>
 * </ul>
 * </p>
 */
public class WildcardType implements IType {

    /** The wildcard symbol. */
    public static final String WILDCARD_SYMBOL = "?";

    /**
     * Represents the kind of wildcard bound.
     */
    public enum Kind {
        /** Unbounded wildcard: {@code ?} */
        UNBOUNDED(""),
        /** Upper bounded wildcard: {@code ? extends T} */
        EXTENDS("extends"),
        /** Lower bounded wildcard: {@code ? super T} */
        SUPER("super");

        private final String keyword;

        Kind(String keyword) {
            this.keyword = keyword;
        }

        /**
         * Returns the keyword used in the type syntax (empty for unbounded).
         * 
         * @return the keyword
         */
        public String getKeyword() {
            return keyword;
        }
    }

    /** Singleton instance for unbounded wildcard. */
    private static final WildcardType UNBOUNDED_INSTANCE = new WildcardType(Kind.UNBOUNDED, null);

    private final Kind kind;
    private final IType bound;

    /**
     * Creates a wildcard type with the specified kind and bound.
     * 
     * @param kind the wildcard kind
     * @param bound the bound type (must be null for UNBOUNDED, non-null for EXTENDS/SUPER)
     * @throws NullPointerException if kind is null
     * @throws IllegalArgumentException if bound is inconsistent with the kind
     */
    public WildcardType(Kind kind, IType bound) {
        Objects.requireNonNull(kind, "Wildcard kind cannot be null");

        if (kind == Kind.UNBOUNDED && bound != null) {
            throw new IllegalArgumentException("Unbounded wildcard cannot have a bound type");
        }
        if (kind != Kind.UNBOUNDED && bound == null) {
            throw new IllegalArgumentException("Bounded wildcard (" + kind + ") requires a bound type");
        }

        this.kind = kind;
        this.bound = bound;
    }

    /**
     * Returns an unbounded wildcard ({@code ?}).
     * 
     * @return the unbounded wildcard singleton
     */
    public static WildcardType unbounded() {
        return UNBOUNDED_INSTANCE;
    }

    /**
     * Creates an upper bounded wildcard ({@code ? extends T}).
     * 
     * @param bound the upper bound type
     * @return the wildcard type
     * @throws NullPointerException if bound is null
     */
    public static WildcardType extendsType(IType bound) {
        Objects.requireNonNull(bound, "Bound type cannot be null for extends wildcard");
        return new WildcardType(Kind.EXTENDS, bound);
    }

    /**
     * Creates a lower bounded wildcard ({@code ? super T}).
     * 
     * @param bound the lower bound type
     * @return the wildcard type
     * @throws NullPointerException if bound is null
     */
    public static WildcardType superType(IType bound) {
        Objects.requireNonNull(bound, "Bound type cannot be null for super wildcard");
        return new WildcardType(Kind.SUPER, bound);
    }

    /**
     * Returns the wildcard kind.
     * 
     * @return the kind (UNBOUNDED, EXTENDS, or SUPER)
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * Returns the bound type, if any.
     * 
     * @return the bound type, or null for unbounded wildcards
     */
    public IType getBound() {
        return bound;
    }

    /**
     * Checks if this is an unbounded wildcard.
     * 
     * @return true if unbounded
     */
    public boolean isUnbounded() {
        return kind == Kind.UNBOUNDED;
    }

    /**
     * Checks if this is an upper bounded wildcard (extends).
     * 
     * @return true if upper bounded
     */
    public boolean isUpperBounded() {
        return kind == Kind.EXTENDS;
    }

    /**
     * Checks if this is a lower bounded wildcard (super).
     * 
     * @return true if lower bounded
     */
    public boolean isLowerBounded() {
        return kind == Kind.SUPER;
    }

    @Override
    public String type() {
        if (kind == Kind.UNBOUNDED) {
            return WILDCARD_SYMBOL;
        }
        return WILDCARD_SYMBOL + " " + kind.getKeyword() + " " + bound.toString();
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
        if (!(obj instanceof WildcardType other)) {
            return false;
        }
        return kind == other.kind && Objects.equals(bound, other.bound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, bound);
    }
}
