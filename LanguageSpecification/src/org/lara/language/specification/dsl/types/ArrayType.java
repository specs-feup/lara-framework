/**
 * Copyright 2016 SPeCS.
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

public class ArrayType implements IType {

    IType baseType;
    int dimension;

    public ArrayType(IType baseType) {
        this(baseType, 1);
    }

    public ArrayType(IType baseType, int dimension) {
        this.baseType = baseType;
        this.dimension = dimension;
    }

    public static ArrayType of(IType baseType) {
        return new ArrayType(baseType);
    }

    @Override
    public String type() {
        return baseType.type() + "[]".repeat(dimension);
    }

    @Override
    public boolean isArray() {
        return true;
    }

    public IType getBaseType() {
        return baseType;
    }

    public void setBaseType(IType baseType) {
        this.baseType = baseType;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public String toString() {
        return type();
    }

}
