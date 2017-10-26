/**
 * Copyright 2017 SPeCS.
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

package pt.up.fe.specs.lara.doc.aspectir;

import java.util.stream.Stream;

import pt.up.fe.specs.lara.aspectir.Base;
import pt.up.fe.specs.lara.aspectir.Code;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprBody;
import pt.up.fe.specs.lara.aspectir.ExprCall;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.Statement;
import pt.up.fe.specs.util.classmap.FunctionClassMap;

public class BaseVisitor {

    private final static FunctionClassMap<Base, Stream<Base>> BASE_TO_STREAM;
    static {
        BASE_TO_STREAM = new FunctionClassMap<>();

        BASE_TO_STREAM.put(ExprCall.class, BaseVisitor::toElemStream);
        BASE_TO_STREAM.put(ExprBody.class, exprBody -> BaseVisitor.toElemStream(exprBody, exprBody.code));
        BASE_TO_STREAM.put(Expression.class, BaseVisitor::getDescendantsAndSelf);
        BASE_TO_STREAM.put(Code.class, BaseVisitor::toElemStream);
        BASE_TO_STREAM.put(CodeElem.class, BaseVisitor::toElemStream);
        BASE_TO_STREAM.put(Statement.class, BaseVisitor::toElemStream);
    }

    public static FunctionClassMap<Base, Stream<Base>> getBaseToStream() {
        return BASE_TO_STREAM;
    }

    private static Stream<Base> toElemStream(CodeElem codeElem) {
        return BASE_TO_STREAM.apply(codeElem);
    }

    private static Stream<Base> toElemStream(Code code) {
        if (code.statements == null) {
            return Stream.empty();
        }

        return code.statements.stream().flatMap(CodeElems::toElemStream);
    }

    private static Stream<Base> toElemStream(Statement statement) {
        if (statement.components == null) {
            return Stream.empty();
        }

        return statement.components.stream().flatMap(BASE_TO_STREAM::apply);
    }

    private static Stream<Base> getDescendantsAndSelf(Expression expression) {
        Stream<Base> descendants = getDescendants(expression);

        return Stream.concat(Stream.of(expression), descendants);
    }

    private static Stream<Base> getDescendants(Expression expression) {
        return expression.exprs != null ? expression.exprs.stream().flatMap(BASE_TO_STREAM::apply)
                : Stream.empty();
    }

    private static Stream<Base> toElemStream(Expression expression, Code code) {
        Stream<Base> descendants = getDescendants(expression);

        return Stream.concat(descendants, toElemStream(code));
    }

    private static Stream<Base> toElemStream(ExprCall exprCall) {
        Stream<Base> arguments = exprCall.arguments.stream().flatMap(BaseVisitor::getDescendants);
        Stream<Base> method = BaseVisitor.getDescendants(exprCall.method);

        return Stream.concat(arguments, method);
    }

}
