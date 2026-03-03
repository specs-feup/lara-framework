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

import java.util.function.Predicate;

import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.ParameterizedType;
import org.lara.language.specification.dsl.types.ThisType;
import org.lara.language.specification.dsl.types.WildcardType;

public final class TypeTraversalUtils {

    private TypeTraversalUtils() {
    }

    public static boolean containsType(IType type, Predicate<IType> matcher) {
        if (type == null) {
            return false;
        }
        if (matcher.test(type)) {
            return true;
        }
        if (type instanceof ArrayType arrayType) {
            return containsType(arrayType.getBaseType(), matcher);
        }
        if (type instanceof ParameterizedType paramType) {
            if (containsType(paramType.getBaseType(), matcher)) {
                return true;
            }
            for (IType typeArg : paramType.getTypeArguments()) {
                if (containsType(typeArg, matcher)) {
                    return true;
                }
            }
            return false;
        }
        if (type instanceof WildcardType wildcardType) {
            return containsType(wildcardType.getBound(), matcher);
        }
        return false;
    }

    public static boolean containsThisType(IType type) {
        return containsType(type, candidate -> candidate instanceof ThisType);
    }
}
