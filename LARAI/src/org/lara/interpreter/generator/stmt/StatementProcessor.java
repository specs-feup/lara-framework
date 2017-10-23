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

package org.lara.interpreter.generator.stmt;

import org.lara.interpreter.Interpreter;
import org.lara.interpreter.utils.Coordinates;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.LaraIUtils.Statements;

import larac.objects.Enums.LoopTypes;
import pt.up.fe.specs.lara.aspectir.Code;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.Statement;

public class StatementProcessor {
    private static final String CARDINAL = "__CARDINAL__";

    private final Interpreter interpreter;
    private final StringBuilder afterCall = new StringBuilder();

    public static StatementProcessor newInstance(Interpreter interpreter) {
        return new StatementProcessor(interpreter);
    }

    private StatementProcessor(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public StringBuilder processStatement(Statement stat, String prefix, int depth, String sufix) {
        final StringBuilder ret = new StringBuilder();
        LaraIUtils.appendComment(stat, ret);
        switch (Statements.valueOf(stat.name.toUpperCase())) {
        case SELECT:
            interpreter.getWeaverStmtProcessor().processSelect(stat, depth, ret);
            break;
        case APPLY:
            interpreter.getWeaverStmtProcessor().processApply(stat, depth, ret);
            break;
        case VARDECL:
            declareVariables(stat, prefix, depth, sufix, ret);
            break;
        case FNDECL:
            functionDeclaration(stat, depth, sufix, ret);
            break;
        case RETURN:
        case EXIT:
            returnStmt(stat, depth, sufix, ret);
            break;
        case EXPR:
            exprStmt(stat, depth, sufix, ret);
            break;
        case LOOP:
            loopStmt(stat, depth, sufix, ret);
            break;
        case IF:
            ifStmt(stat, depth, ret);
            break;
        case TRY:
            tryStmt(stat, depth, ret);
            break;
        case WITH:
            withStmt(stat, depth, ret);
            break;
        case SWITCH:
            switchStmt(stat, depth, ret);
            break;
        case BLOCK:
            blockStmt(stat, depth, ret);
            break;
        case BREAK:
        case CONTINUE:
            jumpStmt(stat, depth, sufix, ret);
            break;
        case THROW:
            throwStmt(stat, depth, sufix, ret);
            break;
        default:
            ret.append(LaraIUtils.getSpace(depth) + "throw 'Aspect-IR statement type " + stat.name
                    + " is not implemented. Please contact the developers for further information.'" + sufix);
        }
        return ret;
    }

    private void throwStmt(Statement stat, int depth, String sufix, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + "throw ");
        int line = (new Coordinates(stat.coord)).getLineBegin();
        ret.append("new UserException(");
        ret.append(interpreter.getJavascriptString(stat.components.get(0), 0));
        ret.append("," + line + ")");
        ret.append(sufix);
    }

    private void jumpStmt(Statement stat, int depth, String sufix, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + stat.name.toLowerCase());
        final Expression exprJumpLabel = (Expression) stat.components.get(0);
        if (!exprJumpLabel.exprs.isEmpty()) {

            String label = interpreter.getJavascriptString(exprJumpLabel.exprs.get(0), 0).toString();
            label = label.replace("#", CARDINAL);
            ret.append(" " + label);
        }
        ret.append(sufix);
    }

    private void blockStmt(Statement stat, int depth, final StringBuilder ret) {
        ret.append(interpreter.getJavascriptString(stat.components.get(0), depth));
    }

    private void switchStmt(Statement stat, int depth, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + "switch(");
        ret.append(interpreter.getJavascriptString(stat.components.get(0), 0));
        ret.append("){\n");
        for (int i = 1; i < stat.components.size(); i++) {
            final CodeElem elem = stat.components.get(i);
            if (elem instanceof Code && elem.desc.equals("default")) { // default
                ret.append(LaraIUtils.getSpace(depth + 1) + "default:");
                ret.append(interpreter.getJavascriptString(elem, -(depth + 1)));
            } else {
                ret.append(LaraIUtils.getSpace(depth + 1) + "case ");
                ret.append(interpreter.getJavascriptString(elem, 0));
                ret.append(":");
                i++;
                ret.append(interpreter.getJavascriptString(stat.components.get(i), -(depth + 1)));
            }
        }
        ret.append(LaraIUtils.getSpace(depth) + "}\n");
    }

    private void withStmt(Statement stat, int depth, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + "with(");
        ret.append(interpreter.getJavascriptString(stat.components.get(0), 0));
        ret.append(")");
        ret.append(interpreter.getJavascriptString(stat.components.get(1), -depth));
    }

    private void tryStmt(Statement stat, int depth, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + "try");
        ret.append(interpreter.getJavascriptString(stat.components.get(0), -depth));
        int pos = 1;
        if (stat.components.get(1) instanceof Expression) { // Catch
            ret.append(LaraIUtils.getSpace(depth) + "catch(");
            ret.append(interpreter.getJavascriptString(stat.components.get(1), 0));
            ret.append(")");
            ret.append(interpreter.getJavascriptString(stat.components.get(2), -depth));
            pos = 3;
        }
        if (pos < stat.components.size()) { // finally
            ret.append(LaraIUtils.getSpace(depth) + "finally");
            ret.append(interpreter.getJavascriptString(stat.components.get(pos), -depth));
            pos++;
        }
    }

    private void ifStmt(Statement stat, int depth, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + "if(");
        ret.append(interpreter.getJavascriptString(stat.components.get(0), 0));
        ret.append(")"); // Condition
        final Code then = (Code) stat.components.get(1);
        final Code elseC = (Code) stat.components.get(2);
        if (then.statements.isEmpty()) {
            ret.append("\n" + LaraIUtils.getSpace(depth + 1) + ";\n"); // Then
        } else {
            ret.append(interpreter.getJavascriptString(then, -depth));
        }
        if (!elseC.statements.isEmpty()) {
            ret.append(LaraIUtils.getSpace(depth) + "else ");
            ret.append(interpreter.getJavascriptString(elseC, -depth)); // Else
        }
    }

    private void loopStmt(Statement stat, int depth, String sufix, final StringBuilder ret) {
        if (stat.components.get(0) instanceof Code) { // For|do-While
            // statement
            final Code c = (Code) stat.components.get(0);
            if (c.desc.equals("init")) { // For [var|in] statement
                ret.append(LaraIUtils.getSpace(depth) + "for ");
                if (stat.desc != null && stat.desc.equals(LoopTypes.FOREACH.toString())) {
                    ret.append("each ");
                }
                ret.append("(");
                if (stat.components.get(1).desc.equals("container")) { // For
                    // in
                    // statement
                    ret.append("var ");

                    final Expression leftExpr = (Expression) c.statements.get(0).components.get(0);
                    ret.append(interpreter.getJavascriptString(leftExpr, -1));
                    ret.append(" in ");

                    ret.append(interpreter.getJavascriptString(stat.components.get(1), 0));
                } else { // For [var] statement
                    if (!c.statements.isEmpty()) {
                        interpreter.setBrackets(false);

                        if (c.statements.get(0).name.equals("vardecl")) {
                            ret.append("var ");
                        }
                        for (final Statement varStat : c.statements) {
                            ret.append(processStatement(varStat, "", 0, ", "));
                        }
                        ret.delete(ret.length() - 2, ret.length());
                    }
                    ret.append(";");
                    ret.append(interpreter.getJavascriptString(stat.components.get(1), 0) + ";");
                    ret.append(interpreter.getJavascriptString(stat.components.get(3), 0));

                }
                ret.append(")");
                interpreter.setBrackets(true);
                ret.append(interpreter.getJavascriptString(stat.components.get(2), -depth));
            } else { // do-while statement
                ret.append(LaraIUtils.getSpace(depth) + "do");
                ret.append(interpreter.getJavascriptString(stat.components.get(0), -depth)); // d

                if ('\n' == ret.charAt(ret.length() - 1)) {// equals(ret.charAt(ret.length() - 1))) {
                    ret.deleteCharAt(ret.length() - 1);
                }
                ret.append("while(");
                ret.append(interpreter.getJavascriptString(stat.components.get(1), 0));
                ret.append(")" + sufix); // while
            }
        } else { // while statement
            ret.append(LaraIUtils.getSpace(depth) + "while(");
            ret.append(interpreter.getJavascriptString(stat.components.get(0), 0));
            ret.append(")"); // Condition
            ret.append(interpreter.getJavascriptString(stat.components.get(1), -depth)); // do
        }
    }

    private void exprStmt(Statement stat, int depth, String sufix, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth));
        interpreter.setOldDepth(depth);
        ret.append(interpreter.getJavascriptString(stat.components.get(0), 0));
        interpreter.setOldDepth(0);
        ret.append(sufix);

        if (afterCall.length() != 0) {
            interpreter.out().warnln("AFTER CALL: " + afterCall);
            afterCall.delete(0, afterCall.length());
        }
    }

    private void returnStmt(Statement stat, int depth, String sufix, final StringBuilder ret) {
        StringBuilder value = new StringBuilder();
        if (!stat.components.isEmpty()) {
            value = interpreter.getJavascriptString(stat.components.get(0), 0);
        }
        ret.append(LaraIUtils.getSpace(depth) + "return " + value + sufix);
    }

    private void declareVariables(Statement stat, String prefix, int depth, String sufix, final StringBuilder ret) {
        for (int i = 0; i < stat.components.size(); i += 2) {
            final Expression leftExpr = (Expression) stat.components.get(i);
            final Expression rightExpr = (Expression) stat.components.get(i + 1);
            ret.append(LaraIUtils.getSpace(depth) + prefix
                    + interpreter.getJavascriptString(leftExpr, -1));
            ret.append(" = ");
            if (rightExpr.exprs.isEmpty()) {
                ret.append("undefined");
            } else {
                interpreter.setOldDepth(depth);
            }
            ret.append(interpreter.getJavascriptString(rightExpr, 0));
            interpreter.setOldDepth(0);
            ret.append(sufix);
        }
    }

    private void functionDeclaration(Statement stat, int depth, String sufix, final StringBuilder ret) {
        final Expression exp = (Expression) stat.components.get(0);
        final ExprOp op = (ExprOp) exp.exprs.get(0);
        // get functionName
        String funcName = interpreter.getJavascriptString(op.exprs.get(0), 0).toString();
        funcName = funcName.replaceAll("'", "");
        ret.append(LaraIUtils.getSpace(depth) + "function " + funcName + "(");
        final StringBuilder args = new StringBuilder();
        for (int i = 1; i < op.exprs.size() - 1; i++) {
            args.append(
                    interpreter.getJavascriptString(op.exprs.get(i), 0) + (i == op.exprs.size() - 2 ? "" : ","));
        }
        ret.append(args.toString().replaceAll("'", ""));
        ret.append(")");
        Expression body = op.exprs.get(op.exprs.size() - 1);
        ret.append(interpreter.getJavascriptString(body, -depth)); // body
        ret.append(sufix);
    }
}
