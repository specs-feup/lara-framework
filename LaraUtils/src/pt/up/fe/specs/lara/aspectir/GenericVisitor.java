/**
 * Copyright 2019 SPeCS.
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

package pt.up.fe.specs.lara.aspectir;

public interface GenericVisitor extends Visitor {
    @Override
    default void visit(Argument argument) {

    }

    @Override
    default void visit(Aspect aspect) {

    }

    @Override
    default void visit(Aspects aspects) {

    }

    @Override
    default void visit(Base base) {

    }

    @Override
    default void visit(Code code) {

    }

    @Override
    default void visit(CodeElem codeElem) {

    }

    @Override
    default void visit(ExprBody exprBody) {

    }

    @Override
    default void visit(ExprCall exprCall) {

    }

    @Override
    default void visit(Expression expression) {

    }

    @Override
    default void visit(ExprId exprId) {

    }

    @Override
    default void visit(ExprKey exprKey) {

    }

    @Override
    default void visit(ExprLiteral exprLiteral) {

    }

    @Override
    default void visit(ExprOp exprOp) {

    }

    @Override
    default void visit(Parameter parameter) {

    }

    @Override
    default void visit(ParameterList parameterList) {

    }

    @Override
    default void visit(ParameterSection parameterSection) {

    }

    @Override
    default void visit(Statement statement) {

    }
}
