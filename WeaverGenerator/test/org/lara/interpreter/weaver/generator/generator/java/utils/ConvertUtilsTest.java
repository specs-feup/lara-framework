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

package org.lara.interpreter.weaver.generator.generator.java.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.GenericType;
import org.lara.language.specification.dsl.types.ParameterizedType;
import org.lara.language.specification.dsl.types.Primitive;
import org.lara.language.specification.dsl.types.ThisType;

/**
 * Unit tests for {@link ConvertUtils}.
 */
@DisplayName("ConvertUtils")
class ConvertUtilsTest {

    @Test
    @DisplayName("primitive conversion behavior should differ between standard and attribute conversion")
    void primitiveConversionBehavior() {
        var regularPrimitive = ConvertUtils.getConvertedType(Primitive.INT, null, null);
        var attributePrimitive = ConvertUtils.getAttributeConvertedType(Primitive.INT, null, null);
        var attributePrimitiveArray = ConvertUtils.getAttributeConvertedType(new ArrayType(Primitive.INT, 1), null,
                null);

        assertThat(regularPrimitive.isPrimitive()).isTrue();
        assertThat(attributePrimitive.isPrimitive()).isFalse();
        assertThat(attributePrimitiveArray.isArray()).isTrue();
    }

    @Nested
    @DisplayName("getConvertedType(IType, generator, currentJpType)")
    class GetConvertedTypeITypeTests {

        @Test
        @DisplayName("should throw IllegalArgumentException when type is null")
        void shouldThrowWhenTypeIsNull() {
            assertThatThrownBy(() -> ConvertUtils.getConvertedType(null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Type cannot be null");
        }

        @Test
        @DisplayName("should throw IllegalStateException when ThisType used without currentJpType context")
        void shouldThrowWhenThisTypeWithoutContext() {
            assertThatThrownBy(() -> ConvertUtils.getConvertedType(ThisType.getInstance(), null, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ThisType found but no currentJpType context provided")
                    .hasMessageContaining("ThisType is not supported in this context");
        }

        @Test
        @DisplayName("should throw IllegalStateException when nested ThisType used without context")
        void shouldThrowWhenNestedThisTypeWithoutContext() {
            var nestedType = ParameterizedType.of(new GenericType("List", false), ThisType.getInstance());

            assertThatThrownBy(() -> ConvertUtils.getConvertedType(nestedType, null, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ThisType found but no currentJpType context provided");
        }
    }

    @Nested
    @DisplayName("getAttributeConvertedType(IType, generator, currentJpType)")
    class GetAttributeConvertedTypeITypeTests {

        @Test
        @DisplayName("should throw IllegalArgumentException when type is null")
        void shouldThrowWhenTypeIsNull() {
            assertThatThrownBy(() -> ConvertUtils.getAttributeConvertedType(null, null, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Type cannot be null");
        }

        @Test
        @DisplayName("should throw IllegalStateException when ThisType used without currentJpType context")
        void shouldThrowWhenThisTypeWithoutContext() {
            assertThatThrownBy(() -> ConvertUtils.getAttributeConvertedType(ThisType.getInstance(), null, null))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ThisType found but no currentJpType context provided")
                    .hasMessageContaining("ThisType is not supported in this context");
        }
    }
}
