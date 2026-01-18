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

import org.lara.interpreter.weaver.defaultweaver.abstracts.joinpoints.AFunction;
import org.lara.interpreter.weaver.interf.JoinPoint;

public class DWFunction extends AFunction {

    private final String name;

    public DWFunction(String element) {
        name = element;
    }

    @Override
    public Object getNode() {
        return name;
    }

    // @Override
    // public boolean same(JoinPoint iJoinPoint) {
    // if (!(iJoinPoint instanceof DWFunction))
    // return false;
    // DWFunction function = (DWFunction) iJoinPoint;
    // return this.name.equals(function.name);
    // }

    @Override
    public JoinPoint[] insertImpl(String position, String code) {
        System.out.println("#########INSERTING#########");
        System.out.println(
                "Action not available. But would insert " + position + " function " + name + ": " + code.trim());//
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
        return name;
    }
}
