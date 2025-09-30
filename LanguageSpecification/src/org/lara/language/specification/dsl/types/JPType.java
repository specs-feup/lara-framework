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
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.language.specification.dsl.types;

import org.lara.language.specification.dsl.JoinPointClass;

public class JPType implements IType {

    private JoinPointClass joinPoint;

    public JPType(JoinPointClass jointPoint) {
        this.setJointPoint(jointPoint);
    }

    public static JPType of(JoinPointClass jointPoint) {
        return new JPType(jointPoint);
    }

    @Override
    public String type() {
        return joinPoint.getName();
    }

    public JoinPointClass getJointPoint() {
        return joinPoint;
    }

    public void setJointPoint(JoinPointClass jointPoint) {
        joinPoint = jointPoint;
    }

    @Override
    public String toString() {
        return type();
    }

}
