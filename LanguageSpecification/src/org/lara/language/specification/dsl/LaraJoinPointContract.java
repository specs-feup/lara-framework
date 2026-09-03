/**
 * Copyright 2026 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.language.specification.dsl;

import org.lara.language.specification.dsl.types.ArrayType;
import org.lara.language.specification.dsl.types.JPType;
import org.lara.language.specification.dsl.types.LiteralEnum;
import org.lara.language.specification.dsl.types.Primitive;
import org.lara.language.specification.dsl.types.PrimitiveClasses;

public final class LaraJoinPointContract {

    private LaraJoinPointContract() {
    }

    public static JoinPointClass build(String className) {
        var joinPoint = new JoinPointClass(className);
        var selfType = JPType.of(joinPoint);
        var selfArrayType = ArrayType.of(selfType);
        var insertPosition = LiteralEnum.of("Position", "before", "after", "replace");

        joinPoint.addAttribute(PrimitiveClasses.STRING, "dump");
        joinPoint.addAttribute(PrimitiveClasses.STRING, "joinPointType");
        joinPoint.addAttribute(PrimitiveClasses.OBJECT, "node");
        joinPoint.addAttribute(selfType, "self");
        joinPoint.addAttribute(selfType, "super");
        joinPoint.addAttribute(selfArrayType, "children");
        joinPoint.addAttribute(selfArrayType, "descendants");
        joinPoint.addAttribute(selfArrayType, "scopeNodes");

        joinPoint.addAction(selfArrayType, "insert",
                new Parameter(insertPosition, "position"),
                new Parameter(PrimitiveClasses.STRING, "code"));
        joinPoint.addAction(selfArrayType, "insert",
                new Parameter(insertPosition, "position"),
                new Parameter(selfType, "joinpoint"));
        joinPoint.addAction(PrimitiveClasses.STRING, "toString");
        joinPoint.addAction(Primitive.BOOLEAN, "equals",
                new Parameter(selfType, "jp"));
        joinPoint.addAction(Primitive.BOOLEAN, "instanceOf",
                new Parameter(PrimitiveClasses.STRING, "name"));
        joinPoint.addAction(Primitive.BOOLEAN, "instanceOf",
                new Parameter(ArrayType.of(PrimitiveClasses.STRING), "names"));

        return joinPoint;
    }
}