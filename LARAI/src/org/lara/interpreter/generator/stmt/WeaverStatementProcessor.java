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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dojo.jsl.parser.ast.utils.LARACConstantPool;
import org.lara.interpreter.Interpreter;
import org.lara.interpreter.generator.js.ExpressionProcessor;
import org.lara.interpreter.utils.Coordinates;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.LaraIUtils.Operators;
import org.lara.interpreter.utils.SelectUtils;
import org.lara.interpreter.weaver.JoinpointUtils;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.events.EventTriggerGenerator;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;

import larac.objects.Enums;
import larac.objects.Enums.JoinOperator;
import larai.LaraI;
import pt.up.fe.specs.lara.aspectir.Code;
import pt.up.fe.specs.lara.aspectir.CodeElem;
import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.Statement;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

public class WeaverStatementProcessor {

    private static final String APPLY_PREFIX = "_apply_counter_";
    private static final String CARDINAL = "__CARDINAL__";
    private static final String WRAP_JOINPOINT = "wrapJoinPoint";
    private static final String UNWRAP_JOINPOINT = "unwrapJoinPoint";

    private final Interpreter interpreter;
    private final Map<String, List<String>> selects;
    private int laraApplyCounter;
    private String lastInChain;

    /**
     * A processor that deals with statements related to the weaving process, such as selects, applies and actions
     *
     * @param interpreter
     */
    public static WeaverStatementProcessor newInstance(Interpreter interpreter) {
        return new WeaverStatementProcessor(interpreter);
    }

    private WeaverStatementProcessor(Interpreter interpreter) {
        this.interpreter = interpreter;
        selects = new HashMap<>();
        laraApplyCounter = 0;
    }

    /**
     * Process a given statement that portrays a select
     *
     * @param stat
     * @param depth
     * @param ret
     */
    public void processSelect(Statement stat, int depth, final StringBuilder ret) {
        ret.append(LaraIUtils.getSpace(depth) + "var " + stat.label + " = ");
        ret.append(generateSelectArguments(stat));

        selects.put(stat.label, getAliasList(stat));
    }

    /**
     * Process a given statement that portrays an apply
     *
     * @param stat
     * @param depth
     * @param ret
     */
    public void processApply(Statement stat, int depth, final StringBuilder ret) {

        if (stat.dynamic) {
            processDynamicApply(stat, depth, ret);
        } else {
            processStaticApply(stat, depth, ret);
        }

    }

    private static void processDynamicApply(Statement stat, int depth, final StringBuilder ret) {
        throw new NotImplementedException(
                "Dynamic applies are not supported in current interpreter version (" + LaraI.LARA_VERSION + ")");
    }

    private void processStaticApply(Statement stat, int depth, final StringBuilder ret) {
        final Expression conditionExpr = (Expression) stat.components.get(1);
        final Code beforeExpr = (Code) stat.components.get(2);
        final Code codeExpr = (Code) stat.components.get(3);
        final Code afterExpr = (Code) stat.components.get(4);

        StringBuilder selectLabel = null;
        final Expression selectExpr = (Expression) stat.components.get(0);
        if (!(selectExpr.exprs.get(0) instanceof ExprId)) {
            selectLabel = new StringBuilder("__LARAI_SELECT_" + getApplyCounter());
            ret.append(LaraIUtils.getSpace(depth) + "var " + selectLabel + " = ");
            ret.append(generateSelectArguments(stat));

            selects.put(selectLabel.toString(), getAliasList(stat));

            // LaraI.die("On larai, the expression for 'apply to' can only
            // be a list of identifiers, no operators are available.");

        } else {
            selectLabel = interpreter.getJavascriptString(selectExpr, 0);
        }
        ret.append(LaraIUtils.getSpace(depth) + "//Before: apply to " + selectLabel + "\n");
        final List<String> select = selects.get(selectLabel.toString());

        // TRIGGER APPLY BEGIN EVENT
        if (interpreter.hasEvents()) {
            EventTriggerGenerator.triggerApply(Stage.BEGIN, interpreter.getCurrentAspect(), stat.label,
                    selectLabel.toString(),
                    depth, ret, select.get(0));
        }
        ret.append(interpreter.getJavascriptString(beforeExpr, depth));

        final StringBuilder after = interpreter.getJavascriptString(afterExpr, depth);

        // out.println(select);
        ret.append(LaraIUtils.getSpace(depth) + "//Apply to " + selectLabel + "\n");

        ret.append(getLoopForSelect(selectLabel.toString(), stat.label, select, codeExpr, conditionExpr, depth + 1));

        ret.append(LaraIUtils.getSpace(depth) + "//After: apply to " + selectLabel + "\n");
        ret.append(after);
        // TRIGGER APPLY END EVENT
        if (interpreter.hasEvents()) {
            EventTriggerGenerator.triggerApply(Stage.END, interpreter.getCurrentAspect(), stat.label,
                    selectLabel.toString(),
                    depth,
                    ret, select.get(0));
        }
    }

    private StringBuilder getLoopForSelect(String selectLabel, String applyLabel, List<String> select, Code codeExpr,
            Expression conditionExpr,
            int depth) {

        String before = selectLabel;
        final List<String> pointcutChainNames = new ArrayList<>();
        final StringBuilder ret = new StringBuilder(LaraIUtils.getSpace(depth));
        // TODO: change select.get(0) with "children"

        ret.append(LaraIUtils.getSpace(depth) + "if (" + selectLabel + " != null && " + selectLabel
                + ".hasChildren()){\n");
        final int oldDepth = depth++;
        String finalCode = "";
        for (String jpAlias : select) {

            final String simpleJPName = jpAlias.substring(1, jpAlias.length() - 1);
            final String jpArrayName = "__" + simpleJPName + "List__";
            final String assignment = before + ".getChildren(); // get " + simpleJPName + " join points"; // TODO:
                                                                                                          // change
            // jpAlias with
            // "children"
            ret.append(LaraIUtils.getSpace(depth) + "var " + jpArrayName + " = " + assignment + ";\n");
            final String counterName = getApplyCounter();
            ret.append(LaraIUtils.getSpace(depth));
            ret.append(WeaverStatementProcessor.CARDINAL + simpleJPName + ": ");
            ret.append("for( var " + counterName + "= 0; ");
            ret.append(counterName + " < " + jpArrayName + ".size(); ");
            ret.append(counterName + "++){\n");

            finalCode = LaraIUtils.getSpace(depth) + "}\n" + finalCode;
            depth++;
            String jpWrapper = "__" + simpleJPName + "Wrapper_";
            jpAlias = '$' + simpleJPName;
            // var __appWrapper_ = __appList__.get(_apply_counter_0); //foreach app
            ret.append(LaraIUtils.getSpace(depth) + "var " + jpWrapper + " = " + jpArrayName + ".get(" + counterName
                    + ");\n");
            ret.append(LaraIUtils.getSpace(depth) + "if(!" + jpWrapper + "." + JoinpointUtils.getAliasProperty()
                    + ".equals('" + simpleJPName + "')) {\n");
            ret.append(LaraIUtils.getSpace(depth + 1) + "continue;\n");
            ret.append(LaraIUtils.getSpace(depth) + "}\n");
            ret.append(LaraIUtils.getSpace(depth) + "var " + jpAlias + " = "
                    + WRAP_JOINPOINT + "(" + jpWrapper + "."
                    + JoinpointUtils.getReferenceProperty() + ");\n");
            // ret.append(LaraIUtils.getSpace(depth) + "println('current wrapper: '+" + jpWrapper + ");\n");
            // ret.append(LaraIUtils.getSpace(depth) + "println('current ref: '+" + jpWrapper + "."
            // + JoinpointUtils.getReferenceProperty() + ");\n");
            // ret.append(LaraIUtils.getSpace(depth) + "println('current join point: '+" + jpAlias +
            // ".joinPointType);\n");
            pointcutChainNames.add(jpAlias);
            before = jpWrapper;// jpArrayName + "[" + counterName + "]";
        }
        if (!conditionExpr.exprs.isEmpty()) {
            ret.append(LaraIUtils.getSpace(depth) + "if(!(");
            ret.append(interpreter.getJavascriptString(conditionExpr, 0));
            ret.append("))\n");
            ret.append(LaraIUtils.getSpace(depth + 1) + "continue;\n");
        }
        lastInChain = select.get(select.size() - 1);
        lastInChain = '$' + lastInChain.replace("'", "");
        if (interpreter.hasEvents()) {

            EventTriggerGenerator.triggerApply(Stage.BEGIN, interpreter.getCurrentAspect(), applyLabel,
                    selectLabel,
                    depth,
                    ret,
                    pointcutChainNames);
        }
        ret.append(interpreter.getJavascriptString(codeExpr, depth));

        lastInChain = null;
        ret.append(finalCode);
        ret.append(LaraIUtils.getSpace(oldDepth) + "}\n");
        return ret;
    }

    public StringBuilder generateSelectArguments(Statement stat) {

        final ArrayList<CodeElem> statComponents = stat.components;
        final Expression firstExpr = ((Expression) statComponents.get(0));
        final Expression firstExprOp = firstExpr.exprs.get(0);
        if (firstExprOp instanceof ExprOp) {
            final StringBuilder joinSelect = generateJoin((ExprOp) firstExprOp);
            joinSelect.append(";\n");
            return joinSelect;
        }
        final StringBuilder jpChain = new StringBuilder("[");
        final StringBuilder aliasChain = new StringBuilder("[");
        final StringBuilder filterChain = new StringBuilder("[");
        String userDeclaredVariable = null;
        for (int i = 0; i < statComponents.size(); i += 3) {

            final Expression jpClass = (Expression) statComponents.get(i);
            if (jpClass.exprs.get(0) instanceof ExprId) {
                userDeclaredVariable = interpreter.getJavascriptString(jpClass, 0).toString();
                jpChain.append("'");
                jpChain.append(interpreter.getJavascriptString(jpClass, 0));
                jpChain.append("'");
            } else {
                jpChain.append(interpreter.getJavascriptString(jpClass, 0));
            }
            jpChain.append(", ");
            final Expression jpAlias = (Expression) statComponents.get(i + 1);
            aliasChain.append(interpreter.getJavascriptString(jpAlias, 0));
            aliasChain.append(", ");
            final Expression jpFilter = (Expression) statComponents.get(i + 2);
            filterChain.append('[');
            if (jpFilter.exprs.isEmpty()) {
                filterChain.append("FilterExpression.newEmpty()");
            } else {
                StringBuilder filterExpressionsStr = convert2FilterExpression(jpFilter.exprs.get(0));
                // isFilter = true;
                filterChain.append(filterExpressionsStr);
            }
            // filterChain.append(getJavascriptString(jpFilter, 0));
            // isFilter = false;
            filterChain.append(']');
            filterChain.append(", ");
            // convert2FilterExpression(statComponents, filterChain, i);
        }
        jpChain.append("], ");
        aliasChain.append("], ");
        filterChain.append("]");
        final StringBuilder ret = new StringBuilder(MasterWeaver.WEAVER_NAME);
        ret.append(".select( ");
        if (userDeclaredVariable != null) {
            ret.append(UNWRAP_JOINPOINT + "(" + userDeclaredVariable + "), ");
        }
        ret.append("'");
        ret.append(stat.label); // Name of the select
        ret.append("'");
        ret.append(", ");
        ret.append(jpChain);
        ret.append(aliasChain);
        ret.append(filterChain);
        ret.append(",'" + interpreter.getCurrentAspect() + "'");
        // if (interpreter.getEngine().supportsModifyingThis()) {
        ret.append(",this");
        // }
        ret.append(",");
        ret.append(new Coordinates(stat.coord).getLineBegin());
        ret.append(");\n");
        // out.println("---> "+ret);
        // java.lang.System.exit(-1);
        return ret;
    }

    /**
     * Creates a select for a join expression
     *
     * @param exprOp
     * @return
     */
    private StringBuilder generateJoin(ExprOp exprOp) {

        final StringBuilder ret = new StringBuilder(MasterWeaver.WEAVER_NAME);
        final String joinFunctionName = exprOp.name.toLowerCase();
        try {
            MasterWeaver.class.getMethod(joinFunctionName, String.class, LaraJoinPoint.class, String.class,
                    LaraJoinPoint.class);
        } catch (final NoSuchMethodException e) {
            final JoinOperator op = JoinOperator.valueOf(joinFunctionName.toUpperCase());
            throw new RuntimeException(
                    "The join operator " + joinFunctionName + "(" + op.getOp() + ")" + " is not implemented!");
        } catch (final SecurityException e) {
            throw new RuntimeException("No permissions to use " + joinFunctionName + "!");
        }

        ret.append("." + joinFunctionName + "(");

        final StringBuilder left = generateJoinAux(exprOp.exprs.get(0));
        final String leftName = getLiteralJoin(exprOp.exprs.get(0));

        ret.append("'" + leftName + "'");
        ret.append(",");
        ret.append(left);
        ret.append(",");

        final StringBuilder right = generateJoinAux(exprOp.exprs.get(1));
        final String rightName = getLiteralJoin(exprOp.exprs.get(1));
        ret.append("'" + rightName + "'");
        ret.append(",");
        ret.append(right);
        ret.append(")");

        return ret;
    }

    private StringBuilder generateJoinAux(Expression expression) {

        if (expression instanceof ExprId) {

            final StringBuilder ret = ExpressionProcessor.getJavascriptString((ExprId) expression, 0);
            // ret.append(".laraJoinPoint");
            return ret;
        }

        final StringBuilder ret = generateJoin((ExprOp) expression);
        // ret.append(".laraJoinPoint");
        return ret;

    }

    private String getLiteralJoin(Expression expression) {

        if (expression instanceof ExprId) {

            final StringBuilder ret = ExpressionProcessor.getJavascriptString((ExprId) expression, 0);
            return ret.toString();
        }

        final ExprOp exprOp = (ExprOp) expression;
        final String op = Enums.JoinOperator.valueOf(exprOp.name.toUpperCase()).getOp();
        final String left = getLiteralJoin(exprOp.exprs.get(0));
        final String right = getLiteralJoin(exprOp.exprs.get(1));

        return left + op + right;

    }

    private List<String> getJoinChain(ExprOp exprOp) {
        List<String> chain = null;
        final Expression left = exprOp.exprs.get(0);
        final Expression right = exprOp.exprs.get(1);
        List<String> leftChain = null;
        List<String> rightChain = null;
        if (left instanceof ExprOp) {
            leftChain = getJoinChain((ExprOp) left);
        } else {
            final String leftLabel = ExpressionProcessor.getJavascriptString((ExprId) left, 0).toString();
            // out.println(leftLabel);
            leftChain = selects.get(leftLabel);
        }
        if (right instanceof ExprOp) {
            rightChain = getJoinChain((ExprOp) right);
        } else {
            final String rightLabel = ExpressionProcessor.getJavascriptString((ExprId) right, 0).toString();
            // out.println(rightLabel);
            rightChain = selects.get(rightLabel);
        }
        chain = SelectUtils.getJoinChain(leftChain, rightChain);
        final Set<String> set = new HashSet<>(chain);

        if (set.size() < chain.size()) {
            String error = "The resulting join point chain of the join operation contains duplicate join point names: \n\t";
            for (int i = 0; i < chain.size() - 1; i++) {

                final String joinPoint = chain.get(i);
                if (joinPoint.contains(LARACConstantPool.HIDDEN_TAG)) {
                    error += " a hidden " + joinPoint.substring(0, joinPoint.indexOf(LARACConstantPool.HIDDEN_TAG))
                            + "'";
                } else {
                    error += joinPoint;
                }
                error += " -> ";
            }
            error += chain.get(chain.size() - 1);
            error += "\nNote: Do not forget to use the same join point names at the beginning of the chain!";
            throw new RuntimeException(error);
        }
        // out.println(chain);
        return chain;
    }

    private StringBuilder convert2FilterExpression(Expression jpFilter) {
        // jpFilter must always be a binary expression!
        if (jpFilter.exprs.isEmpty()) {
            return new StringBuilder("FilterExpression.newEmpty()");
        }
        if (!(jpFilter instanceof ExprOp) || jpFilter.exprs.size() < 2) {
            throw new RuntimeException("Filter must be defined as binary expressions");
        }
        ExprOp op = (ExprOp) jpFilter;
        final String operator = Operators.getOpString(op.name);

        if (op.name.equals("OR") || op.name.equals("AND")) {
            StringBuilder left = convert2FilterExpression(op.exprs.get(0));
            for (int i = 1; i < op.exprs.size(); i++) {
                left.append(", FilterExpression.newComparator('" + operator + "'),");
                StringBuilder right = convert2FilterExpression(op.exprs.get(i));
                left.append(right);
            }
            return left;

        }
        final StringBuilder left = interpreter.getJavascriptString(op.exprs.get(0), 0);
        StringBuilder right = interpreter.getJavascriptString(op.exprs.get(1), 0);
        if (op.name.equals("MATCH")) {
            return new StringBuilder("FilterExpression.newMatch('" + left + "'," + right + ",\"" + right + "\")");
        }

        return new StringBuilder(
                "FilterExpression.newInstance('" + left + "','" + operator + "'," + right + ",\"" + right + "\")");

    }

    private List<String> getAliasList(Statement stat) {
        final ArrayList<CodeElem> statComponents = stat.components;
        final Expression firstExpr = ((Expression) statComponents.get(0));
        final Expression firstExprOp = firstExpr.exprs.get(0);
        if (firstExprOp instanceof ExprOp) {
            return getJoinChain((ExprOp) firstExprOp);
        }
        final List<String> aliasChain = new ArrayList<>();
        for (int i = 1; i < stat.components.size(); i += 3) {
            final Expression jpAlias = (Expression) stat.components.get(i);
            aliasChain.add(interpreter.getJavascriptString(jpAlias, 0).toString());
        }
        return aliasChain;
    }

    public boolean verifyAction(String methodName) {
        if (methodName.equals("insert") || methodName.equals("def")) {
            return true;
        }

        boolean contains = interpreter.getActions().contains(methodName);
        if (contains) {
            return true;
        }
        return false;
    }

    public String getApplyCounter() {
        return WeaverStatementProcessor.APPLY_PREFIX + (laraApplyCounter++);
    }

    public String getLastInChain() {
        return lastInChain;
    }

    public void setLastInChain(String lastInChain) {
        this.lastInChain = lastInChain;
    }
}
