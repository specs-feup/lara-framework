/**
 * Copyright 2023 SPeCS.
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

package org.lara.interpreter.weaver.utils;

import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.util.SpecsCheck;

public class JoinPointsUtils {

    public static JoinPoint toJavaJoinPoint(Object jsJoinPoint, String joinPointType) {
        SpecsCheck.checkArgument(jsJoinPoint instanceof JoinPoint,
                () -> "localVariable must be a join point, it is a " + jsJoinPoint.getClass().getSimpleName());

        JoinPoint javaJoinPoint = (JoinPoint) jsJoinPoint;

        SpecsCheck.checkArgument(javaJoinPoint.instanceOf(joinPointType),
                () -> "Given join point must be of type '" + joinPointType + "', is "
                        + javaJoinPoint.getJoinPointType());

        return javaJoinPoint;
    }

    public static <T extends JoinPoint> T toJavaJoinPoint(Object jsJoinPoint, String joinPointType,
            Class<T> javaJpClass) {

        var javaJp = toJavaJoinPoint(jsJoinPoint, joinPointType);

        return javaJpClass.cast(javaJp);
    }

}
