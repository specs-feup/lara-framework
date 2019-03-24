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

package pt.up.fe.specs.lara.loc.visitors;

import pt.up.fe.specs.lara.aspectir.Argument;
import pt.up.fe.specs.lara.aspectir.Aspect;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.aspectir.Base;
import pt.up.fe.specs.lara.aspectir.Code;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprBody;
import pt.up.fe.specs.lara.aspectir.ExprCall;
import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.lara.aspectir.ExprKey;
import pt.up.fe.specs.lara.aspectir.ExprLiteral;
import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.Parameter;
import pt.up.fe.specs.lara.aspectir.ParameterList;
import pt.up.fe.specs.lara.aspectir.ParameterSection;
import pt.up.fe.specs.lara.aspectir.Statement;
import pt.up.fe.specs.lara.aspectir.Visitor;
import pt.up.fe.specs.util.SpecsStrings;

public class CommentVisitor implements Visitor {

    private int commentLines;

    public CommentVisitor() {
        this.commentLines = 0;
    }

    public int getCommentLines() {
        return commentLines;
    }

    @Override
    public void visit(Base base) {
        commentLines += SpecsStrings.countLines(base.comment);
    }

    @Override
    public void visit(CodeElem codeElem) {
        commentLines += SpecsStrings.countLines(codeElem.comment);
    }

    @Override
    public void visit(Expression expression) {
        commentLines += SpecsStrings.countLines(expression.comment);

    }

    @Override
    public void visit(Argument argument) {
        commentLines += SpecsStrings.countLines(argument.comment);

    }

    @Override
    public void visit(ExprCall exprCall) {
        commentLines += SpecsStrings.countLines(exprCall.comment);

    }

    @Override
    public void visit(ExprId exprId) {
        commentLines += SpecsStrings.countLines(exprId.comment);

    }

    @Override
    public void visit(ExprKey exprKey) {
        commentLines += SpecsStrings.countLines(exprKey.comment);

    }

    @Override
    public void visit(ExprLiteral exprLiteral) {
        commentLines += SpecsStrings.countLines(exprLiteral.comment);
    }

    @Override
    public void visit(ExprOp exprOp) {
        commentLines += SpecsStrings.countLines(exprOp.comment);
    }

    @Override
    public void visit(Parameter parameter) {
        commentLines += SpecsStrings.countLines(parameter.comment);

    }

    @Override
    public void visit(ParameterList parameterList) {
        commentLines += SpecsStrings.countLines(parameterList.comment);

    }

    @Override
    public void visit(ParameterSection parameterSection) {
        commentLines += SpecsStrings.countLines(parameterSection.comment);

    }

    @Override
    public void visit(Statement statement) {
        commentLines += SpecsStrings.countLines(statement.comment);

    }

    @Override
    public void visit(Code code) {
        commentLines += SpecsStrings.countLines(code.comment);

    }

    @Override
    public void visit(Aspect aspect) {
        commentLines += SpecsStrings.countLines(aspect.comment);
    }

    @Override
    public void visit(Aspects aspects) {
        commentLines += SpecsStrings.countLines(aspects.comment);

    }

    @Override
    public void visit(ExprBody exprBody) {
        commentLines += SpecsStrings.countLines(exprBody.comment);

    }

}
