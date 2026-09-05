/*
 * Copyright 2013 SPeCS.
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
package org.lara.interpreter.weaver.defaultweaver.joinpoints;

import org.lara.interpreter.weaver.defaultweaver.DWWeaver;
import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AFunction;
import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AJoinpoint;
import org.lara.interpreter.weaver.interf.enums.InsertPosition;

public class DWFunction<Self extends DWFunction<Self>> extends AFunction<Self> {

    public DWFunction(String element, DWWeaver weaver) {
        super(element, weaver);
    }

    @Override
    public String getNodeImpl() {
        return (String) super.getNodeImpl();
    }

    // @Override
    // public boolean same(JoinPoint iJoinPoint) {
    // if (!(iJoinPoint instanceof DWFunction))
    // return false;
    // DWFunction function = (DWFunction) iJoinPoint;
    // return this.name.equals(function.name);
    // }

    @Override
    public AJoinpoint<?>[] insertImpl(InsertPosition position, String code) {
        System.out.println("#########INSERTING#########");
        System.out.println(
                "Action not available. But would insert " + position.getDisplay() + " function " + this.getNodeImpl() + ": " + code.trim());//
        System.out.println("###########################");

        return null;
    }

    // @Override
    // public void reportImpl() {
    // System.out.println("Function Report");
    // System.out.println("\tname: " + name);
    // }

    @Override
    public String getNameImpl() {
        return this.getNodeImpl();
    }

    @Override
    public Self getUsesThisImpl(Self[] param1) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
