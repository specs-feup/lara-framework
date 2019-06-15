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
 * specific language governing permissions and limitations under the License. under the License.
 */
package org.lara.interpreter;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.lara.interpreter.exception.EvaluationException;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.generator.js.ExpressionProcessor;
import org.lara.interpreter.generator.stmt.AspectClassProcessor;
import org.lara.interpreter.generator.stmt.ImportProcessor;
import org.lara.interpreter.generator.stmt.StatementProcessor;
import org.lara.interpreter.generator.stmt.WeaverStatementProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraIDataStore;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.utils.Coordinates;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.LaraIUtils.Operators;
import org.lara.interpreter.utils.MessageConstants;
import org.lara.interpreter.weaver.events.EventTriggerGenerator;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.js.JsEngine;

import com.google.common.base.Preconditions;

import larac.objects.Enums.Types;
import larac.utils.output.Output;
import larac.utils.xml.Pair;
import larai.LaraI;
import pt.up.fe.specs.lara.aspectir.Argument;
import pt.up.fe.specs.lara.aspectir.Aspect;
import pt.up.fe.specs.lara.aspectir.Aspects;
import pt.up.fe.specs.lara.aspectir.Code;
import pt.up.fe.specs.lara.aspectir.ExprBody;
import pt.up.fe.specs.lara.aspectir.ExprCall;
import pt.up.fe.specs.lara.aspectir.ExprId;
import pt.up.fe.specs.lara.aspectir.ExprKey;
import pt.up.fe.specs.lara.aspectir.ExprLiteral;
import pt.up.fe.specs.lara.aspectir.ExprOp;
import pt.up.fe.specs.lara.aspectir.Expression;
import pt.up.fe.specs.lara.aspectir.IElement;
import pt.up.fe.specs.lara.aspectir.Parameter;
import pt.up.fe.specs.lara.aspectir.ParameterList;
import pt.up.fe.specs.lara.aspectir.ParameterSection;
import pt.up.fe.specs.lara.aspectir.Statement;
import pt.up.fe.specs.tools.lara.trace.CallStackTrace;
import pt.up.fe.specs.util.SpecsIo;

public class Interpreter {

    public static final String ARGS_PREFIX = "";
    public static final String ATTRIBUTES = "attributes";
    public static final String TOOLS_CONTEXT = "tools";

    public static final String JPUTILS_NAME = "__jpUtils";
    private final LaraI laraInterp;
    private final LaraIDataStore options;
    private Output out = new Output();
    private final JsEngine engine;
    private final WeaverStatementProcessor wStmtProcessor;
    private final AspectClassProcessor aspectProcessor;
    private final ImportProcessor importProcessor;
    private final StatementProcessor statementProcessor;
    private CallStackTrace stackStrace;

    // private List<Integer> js2LARALines;

    /**
     * Create a new interpreter based on a given context and scope
     *
     * @param laraInt
     *            The larai instance using this interpreter
     * @param cx
     *            the javascript context for this interpreter
     * @param scope
     *            the scope for this interpreter
     */
    public Interpreter(LaraI laraInt, JsEngine engine) {
        laraInterp = laraInt;
        options = laraInterp.getOptions();
        out = laraInterp.out;
        this.engine = engine;
        wStmtProcessor = WeaverStatementProcessor.newInstance(this);
        aspectProcessor = AspectClassProcessor.newInstance(this);
        importProcessor = ImportProcessor.newInstance(this);
        statementProcessor = StatementProcessor.newInstance(this);
        if (options.useStackTrace()) {
            stackStrace = new CallStackTrace();
        }
        importProcessor.importScriptsAndClasses(); // this order is important so the output stream is set
        if (options.isJavaScriptStream()) { // AFTER it is initialized
            setprintStream(out.getOutStream());
        }

    }

    public JsEngine getEngine() {
        return engine;
    }

    /**
     * Interpret a list of aspects and run the main aspect.
     *
     * @param asps
     *            The Input Aspects
     * @param cx
     *            The javascript context
     * @param scope
     *            The javascript scope
     * @return the main aspect after its execution
     * @throws ScriptException
     */
    public Object interpret(Aspects asps) {

        importProcessor.importAndInitialize();
        final StringBuilder mainCall = aspectProcessor.generateJavaScript(asps);

        return executeMainAspect(mainCall);
    }

    private Object executeMainAspect(final StringBuilder mainCall) {

        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Executing Main Aspect"));
        String code = mainCall.toString();
        long begin = LaraI.getCurrentTime();
        laraInterp.getWeaver().setInitialTime(begin);
        final Object result = evaluate(code);// cx.evaluateString(scope, code, "<js>", 1, null);
        long end = LaraI.getCurrentTime() - begin;

        exportMetrics();
        out.println(MessageConstants.getElapsedTimeMessage(end));
        return result;
    }

    /**
     * Export the weaver metrics to the given file
     */
    private void exportMetrics() {
        OptionalFile reportFile = getOptions().getMetricsFile();
        if (!reportFile.isUsed()) {
            return;
        }

        File file = reportFile.getFile();
        WeaverProfiler weavingProfile = laraInterp.getWeavingProfile();
        SpecsIo.write(file, weavingProfile.buildJsonReport());
    }

    /**
     * Standard method for evaluating a string
     *
     * @param importer
     * @return
     * @throws ScriptException
     */
    public Object evaluate(String code) {

        try {
            return engine.getEngine().eval(code);
        } catch (ScriptException e) {
            throw new EvaluationException(e);
        }

    }

    /**
     * Evaluate a set of lines of code
     *
     * @param lines
     */
    // private void evaluateStrings(String... lines) {
    // for (final String line : lines) {
    // evaluate(line);
    // }
    // }

    // ================================================================================//
    // ParameterList //
    // ================================================================================//
    public StringBuilder getJavascriptString(ParameterList paramList, int depth) {
        final StringBuilder ret = new StringBuilder();
        for (final Parameter param : paramList.parameters) {
            ret.append(LaraIUtils.getSpace(depth) + "this.");
            ret.append(getJavascriptString(param, 0));
            ret.append(";\n");
        }
        return ret;
    }

    // ================================================================================//
    // Parameter //
    // ================================================================================//
    public StringBuilder getJavascriptString(Parameter param, int depth) {
        final StringBuilder ret = new StringBuilder(LaraIUtils.getSpace(depth) + "this." + param.name + " = ("
                + Interpreter.ARGS_PREFIX + param.name + "!=undefined)?" + Interpreter.ARGS_PREFIX + param.name + ":");
        if (!param.exprs.isEmpty()) {
            ret.append(getJavascriptString(param.exprs.get(0), 0));
        } else {
            ret.append("undefined");
        }
        return ret;
    }

    // ================================================================================//
    // Argument //
    // ================================================================================//
    public StringBuilder getJavascriptString(Argument arg, int depth) {
        final StringBuilder ret = new StringBuilder(LaraIUtils.getSpace(depth));
        if (arg.exprs.isEmpty()) {
            return ret;
        }
        if (arg.name != null) {
            ret.append(arg.name + ": ");
        }
        for (final Expression expr : arg.exprs) {
            ret.append(getJavascriptString(expr, depth));
            ret.append(", ");
        }
        ret.delete(ret.length() - 2, ret.length());
        return ret;
    }

    // ================================================================================//
    // Expression //
    // ================================================================================//
    public StringBuilder getJavascriptString(Expression exp, int depth) {

        // In case of another type of expression
        if (exp instanceof ExprCall) {
            return getJavascriptString((ExprCall) exp, depth);
        }
        if (exp instanceof ExprBody) {
            return getJavascriptString((ExprBody) exp, depth);
        }
        if (exp instanceof ExprId) {
            return ExpressionProcessor.getJavascriptString((ExprId) exp, depth);
        }
        if (exp instanceof ExprLiteral) {
            return getJavascriptString((ExprLiteral) exp, depth);
        }
        if (exp instanceof ExprOp) {
            return getJavascriptString((ExprOp) exp, depth);
        }
        // if(exp instanceof ExprProperty)
        // return getJavascriptString((ExprProperty)exp,depth);
        if (exp instanceof ExprKey) {
            return getJavascriptString((ExprKey) exp, depth);
        }
        // Else
        if (exp.xmltag.equals("property")) {
            final StringBuilder ret = new StringBuilder(LaraIUtils.getSpace(depth));
            ret.append(getJavascriptString(exp.exprs.get(0), -1));
            // String isAttribute = seeIfIsAttributeAccess(exp.exprs.get(0),
            // exp.parent);
            if (exp.exprs.size() > 1) {
                ret.append("[");
                ret.append(getJavascriptString(exp.exprs.get(1), 0));
                ret.append("]");
                // ret.append(isAttribute);
                return ret;
            }
            return ret;
        }
        final StringBuilder ret = new StringBuilder();
        if (exp.exprs.isEmpty()) {
            return ret;
        }
        for (final Expression expr : exp.exprs) {
            ret.append(getJavascriptString(expr, depth));
            ret.append(", ");
        }
        ret.delete(ret.length() - 2, ret.length());
        return ret;
    }

    /*
     * private String seeIfIsAttributeAccess(Expression expr, IElement parent) {
     *
     * if (expr instanceof ExprId) {
     *
     * boolean startWithDollarSign = ((ExprId) expr).name.startsWith("$"); if
     * (startWithDollarSign) { if (parent instanceof Expression) { Expression
     * exprParent = (Expression) parent; if (exprParent.xmltag == null ||
     * !exprParent.xmltag.equals("method")) return "()"; } else return "()"; } }
     * return ""; }
     */
    // ================================================================================//
    // Code //
    // ================================================================================//
    private boolean brackets = true;
    private boolean newInstance = false;

    public StringBuilder getJavascriptString(Code c, int depth) {
        final boolean myBrackets = brackets;

        brackets = true;
        final StringBuilder stats = new StringBuilder();
        if (depth < 0) {
            depth = -depth;
        } else {
            stats.append(LaraIUtils.getSpace(depth));
        }
        if (myBrackets) {
            stats.append("{\n");
        }
        for (final Statement stat : c.statements) {
            if (!myBrackets) {
                stats.append(getJavascriptString(stat, 0));
                stats.append(",");
            } else {
                stats.append(getJavascriptString(stat, depth + 1));
            }
        }
        final StringBuilder forBrackets = new StringBuilder(
                (stats.length() == 0) ? ";\n" : stats.substring(0, stats.length() - 1));
        return ((!myBrackets) ? forBrackets : (stats.append(LaraIUtils.getSpace(depth) + "}\n")));
    }

    // ================================================================================//
    // ParameterList //
    // ================================================================================//
    public StringBuilder getJavascriptString(ExprBody exprBody, int depth) {
        return getJavascriptString(exprBody.code, depth);
    }

    // ================================================================================//
    // ExprCall //
    // ================================================================================//
    public StringBuilder getJavascriptString(ExprCall exprCall, int depth) {
        boolean newInstance = this.newInstance;
        // if (newInstance) {
        // System.out.println("EXPR CALL:");
        // exprCall.print(System.out, 0);
        // }
        this.newInstance = false;
        final StringBuilder encapsule = new StringBuilder();
        StringBuilder call = getJavascriptString(exprCall.method, depth);

        final Pair<String, String> methodName = getMethodName(exprCall, call.toString());

        // out.println("for call "+call+" CALLING: " + methodName.getRight() + "
        // from " + methodName.getLeft());

        final String methodNameRight = methodName.getRight();
        final boolean isAction = wStmtProcessor.verifyAction(methodNameRight);

        // out.warnln("IS " + call + " ACTION? " + isAction);
        // out.println("call to " + methodName.getLeft() + " " +
        // methodName.getRight());

        if (methodNameRight.equals("call")) {

            // ENCAPSULE method to TRIGGER ASPECT CALL EVENTS
            if (hasEvents() && methodName.getLeft() != null && !methodName.getLeft().isEmpty()) {
                encapsule.append(" ((" + methodName.getLeft() + "." + EventTriggerGenerator.IS_ASPECT + "?");
                encapsule.append(methodName.getLeft() + "." + EventTriggerGenerator.ASPECT_CALLER_PROPERTY_NAME + "= '"
                        + aspectProcessor.getCurrentAspect());
                encapsule.append("':undefined), ");
            }
        }

        if (isAction) {
            String lastInChain = wStmtProcessor.getLastInChain();
            if (methodName.getLeft().isEmpty()) {
                encapsule.append("");
                call = new StringBuilder(lastInChain + "['" + call + "']");
                methodName.setLeft(lastInChain);
            }
            // String jpName = methodName.getLeft();
            // if (jpName.isEmpty()) {
            // jpName = lastInChain;
            // }
            // ENCAPSULE method to TRIGGER ACTION EVENTS
            if (methodName.getLeft() != null && !methodName.getLeft().isEmpty()
                    && !laraInterp.getWeaver().getEngine().implementsEvents() && hasEvents()) {
                encapsule.append(
                        "( (" + methodName.getLeft() + " instanceof " + JoinPoint.class.getSimpleName() + ")?(");
            }
            // encapsule.append(", ");
        }

        if (exprCall.arguments.isEmpty()) {
            call.append("()");
            return returnCall(encapsule, call, newInstance, isAction, methodName, exprCall.arguments);
        }
        if (exprCall.arguments.get(0).name != null) {
            String context = "";

            if (exprCall.method.exprs.get(0).exprs.isEmpty()) {
                context = "null";
            } else {
                context = call.substring(0, call.lastIndexOf("["));
            }
            call = new StringBuilder("invoke(" + context + "," + call + ",{");
            for (final Argument arg : exprCall.arguments) {
                call.append(getJavascriptString(arg, 0) + ",");
            }
            call.deleteCharAt(call.length() - 1);
            call.append("})");
            return returnCall(encapsule, call, newInstance, isAction, methodName, exprCall.arguments);
        }
        call.append("(");
        for (int i = 0; i < exprCall.arguments.size() - 1; i++) {
            call.append(getJavascriptString(exprCall.arguments.get(i), 0));
            call.append(",");
        }
        call.append(getJavascriptString(exprCall.arguments.get(exprCall.arguments.size() - 1), 0));
        call.append(")");
        return returnCall(encapsule, call, newInstance, isAction, methodName, exprCall.arguments);
    }

    private StringBuilder returnCall(StringBuilder encapsule, StringBuilder call, boolean newInstance, boolean isAction,
            Pair<String, String> methodName, List<Argument> arguments) {
        if (newInstance) {
            StringBuilder temp = call;
            call = new StringBuilder("new ");
            call.append(temp);
            // call = new StringBuilder("(new ");
            // call.append(temp);
            // call.append(")");
        }
        if (encapsule.length() != 0) {
            // This code is more feasible if done at weaver-side
            WeaverEngine weaverEngine = laraInterp.getEngine();
            if (!weaverEngine.implementsEvents()

                    && isAction && hasEvents()) {
                encapsule.append(" /*IF JOIN POINT*/ ");

                // TRIGGER ACTION BEGIN EVENT
                EventTriggerGenerator.triggerAction(Stage.BEGIN,
                        methodName.getRight(),
                        methodName.getLeft(), arguments, encapsule, 0, this);
                // encapsule.append(", ");
                encapsule.append(", ____temp___ = ");
                encapsule.append(call);
                encapsule.append(", ");
                // TRIGGER ACTION END EVENT
                EventTriggerGenerator.triggerAction(Stage.END,
                        methodName.getRight(),
                        methodName.getLeft(), arguments, encapsule, 0, this);
                encapsule.append(", ____temp___ ");
                encapsule.append("): /* ELSE */ ");

            }

            encapsule.append(call);
            encapsule.append(")");
            return encapsuleCall(encapsule, methodName, newInstance);
        }
        return encapsuleCall(call, methodName, newInstance);

    }

    private StringBuilder encapsuleCall(StringBuilder callCode, Pair<String, String> methodName, boolean newInstance) {
        // if (newInstance) {
        // System.out.println("ENCAPSULATE CALL CODE:" + callCode);
        // }

        if (options.useStackTrace()) {
            // Ignore if newInstance
            // if (newInstance) {
            // return callCode;
            // }

            String called = methodName.getRight();
            if (methodName.getLeft() != null && !methodName.getLeft().isEmpty()) {
                called = methodName.getLeft() + "." + called;
            }
            if (newInstance) {
                called = "new " + called;
                // called = "(new " + called + ")";
            }
            called = StringEscapeUtils.escapeEcmaScript(called);
            String position = "unknown";
            if (currentStatement != null) {
                position = extractFileAndLineFromCoords(currentStatement.coord);
            }
            String pushToStack = ExpressionProcessor.pushToStack(called, position);
            String popFromStack = ExpressionProcessor.popFromStack(called, position);
            return new StringBuilder("( ")
                    .append(pushToStack)
                    .append(", ")
                    .append(CallStackTrace.RETURN_FOR_STACK_STRACE)
                    .append(" = ")
                    .append(callCode)
                    .append(", ")
                    .append(popFromStack)
                    .append(", ")
                    .append(CallStackTrace.RETURN_FOR_STACK_STRACE)
                    .append(" )");
        }
        return callCode;
    }

    public static String extractFileAndLineFromCoords(String coordsStr) {
        Coordinates coords = new Coordinates(coordsStr);
        if (!coords.isWellParsed()) {
            return "unknown";
        }
        return coords.fileAndLineString();
    }

    /**
     * Returns a pair in which the left is the target object (if any) and the right side is the actual method invoked
     *
     * @param exprCall
     * @param call
     * @return
     */
    private static Pair<String, String> getMethodName(ExprCall exprCall, String call) {

        final Expression callExpr = exprCall.method.exprs.get(0);
        if (!(callExpr instanceof ExprId || callExpr instanceof ExprLiteral)) {
            if (callExpr.xmltag.equals("property")) {
                String methodName = call.substring(call.lastIndexOf("[") + 1, call.lastIndexOf("]"));
                methodName = methodName.replace("'", "");
                final String objName = call.substring(0, call.lastIndexOf("["));
                return new Pair<>(objName, methodName);
            }
        }
        return new Pair<>("", call.replaceAll("'", ""));

    }

    // ================================================================================//
    // ExprLiteral //
    // ================================================================================//
    public StringBuilder getJavascriptString(ExprLiteral literal, int depth) {
        StringBuilder ret = new StringBuilder(LaraIUtils.getSpace(depth));
        String value = literal.value;
        if (literal.type != null) {
            // System.out.println("DEBUG: "+literal.value+" "+literal.type+"
            // "+depth+literal.parent);

            if (literal.type.equals(Types.String.toString().toLowerCase()) && depth > -1) {
                // occurrences of ' must be escaped!
                value = escapeApostrophe(value);
                return new StringBuilder(ret + "'" + value + "'");
            }
            if (literal.type.equals(Types.RegEx.toString().toLowerCase())) {
                return new StringBuilder(ret + value);
            }
            if (literal.type.equals(Types.Array.toString())) {
                int i = 0;
                if (!literal.exprs.isEmpty()) {
                    ret.append("[ ");
                    for (final Expression expr : literal.exprs) {
                        final ExprKey prop = (ExprKey) expr;
                        final int propPos = Integer.parseInt(prop.name);
                        while (propPos > i) {
                            ret.append(", ");
                            i++;
                        }
                        ret.append(getJavascriptString(expr.exprs.get(0), 0));
                        ret.append(", ");
                        i++;
                    }
                    ret.delete(ret.length() - 2, ret.length());
                    ret.append("]");
                    return ret;

                } else if (value != null) {
                    return ret.append(value);
                }

                return ret.append("[]");

            }
            if (literal.type.equals(Types.Object.toString())) {
                if (value != null && value.equals("null")) {
                    ret.append(value);
                    return ret;
                }
                final boolean isCodeTemplate = literal.desc != null && literal.desc.equals("code");
                ret.append("{ ");
                if (!literal.exprs.isEmpty()) {
                    for (final Expression expr : literal.exprs) {
                        final ExprKey prop = (ExprKey) expr;
                        ret.append(LaraIUtils.getSpace(depth) + "'" + prop.name + "': ");

                        ret.append(getJavascriptString(expr, 0));

                        if (isCodeTemplate) {
                            ret.append("+''");
                        }
                        ret.append(", \n");
                    }
                    ret.delete(ret.length() - 2, ret.length());
                }
                ret.append(LaraIUtils.getSpace(depth) + "}");

                if (isCodeTemplate) {
                    final StringBuilder newRet = new StringBuilder("LARASystem.createCodeTemplate(");
                    newRet.append(ret);
                    newRet.append(")");
                    ret = newRet;
                }
                return ret;
            }
            if (literal.type.equals(Types.Code.toString().toLowerCase())) {
                return new StringBuilder("LARASystem.decode('" + value + "','" + Types.Base64 + "')");
                // return new StringBuilder("'" + literal.value + "'");
            }
            if (literal.type.equals(Types.Base64.toString())) {
                return new StringBuilder("LARASystem.decode('" + value + "','" + Types.Base64 + "')");
            }
        }
        ret.append(value);
        return ret;
    }

    // public static void main(String[] args) {
    //
    // List<String> a = Arrays.asList("", "'", "aa", "aa'aa", "'aaaa", "aaaa'", "'aa'aa'", "\\'", "\\\\'");
    // for (String string : a) {
    // System.out.print("checking \"" + string + "\": ");
    // String[] result = consistentSplit(string, "'");
    //
    // String escapedString = Arrays.asList(result).stream()
    // .map(Interpreter::parseSplittedApostrofe)
    // // .reduce("", ())
    // .collect(Collectors.joining("'"));
    //
    // System.out.println("Escaped String:" + escapedString);
    // System.out.println(Arrays.toString(result));
    // }

    // }

    // private static String parseSplittedApostrofe(String element) {
    // if (!element.endsWith("\\")) {
    // return element;// + "\\";
    // }
    //
    // return requiresEscape(element) ? element + "\\" : element;
    // }
    //
    // String[] split = value.split("'");
    // StringBuilder newString = new StringBuilder();
    // for (int i = 0; i < split.length - 1; i++) {
    // String string = split[i];
    // newString.append(string);
    // if (!string.endsWith("\\")) {
    // newString.append('\\');
    // } else {
    // if (requiresEscape(string)) { // requires escape!
    // newString.append('\\');
    // }
    // }
    // newString.append('\'');
    // }
    // String str = split[split.length - 1];
    // newString.append(str);
    // if (value.endsWith("'")) {
    // if (requiresEscape(str)) { // requires escape!
    // newString.append('\\');
    // }
    // }
    // newString.append('\'');
    // return newString.toString();

    private static String escapeApostrophe(String value) { // change to split + join(\')

        if (!value.contains("'")) {
            return value;
        }
        if (value.equals("'")) {
            return "\\'";
        }

        StringBuilder newString = new StringBuilder();
        int countSeparators = 0;
        for (int i = 0; i < value.length(); i++) {
            char charAt = value.charAt(i);
            if (charAt == '\\') {
                countSeparators++;
            } else {
                if (charAt == '\'') {

                    if (countSeparators % 2 == 0) { // requires escape!
                        newString.append('\\');
                    }
                }
                countSeparators = 0;
            }
            newString.append(charAt);
        }
        // StringBuilder newString = new StringBuilder();
        // for (int i = 0; i < value.length(); i++) {
        // char charAt = value.charAt(i);
        // if (charAt == '\'') {
        // int sum = 0;
        // int pos = i - 1;
        // while (pos >= 0 && value.charAt(pos) == '\\') {
        // sum++;
        // pos--;
        // }
        // if (sum % 2 == 0) { // requires escape!
        // newString.append('\\');
        // }
        // }
        // newString.append(charAt);
        // }
        return newString.toString();
    }

    // private static boolean requiresEscape(String string) {
    // int sum = 0;
    // int pos = string.length() - 1;
    // while (pos >= 0 && string.charAt(pos) == '\\') {
    // sum++;
    // pos--;
    // }
    // return sum % 2 == 0;
    // }

    public static String[] consistentSplit(String string, String pattern) {
        if (string.isEmpty()) {
            return new String[0];
        }
        if (string.equals(pattern)) {
            return new String[] { "", "" };
        }

        String[] splittedString = string.split(pattern);

        // If last character is different than original String, adds empty string to the end
        if (splittedString.length != 0) {
            String lastSplit = splittedString[splittedString.length - 1];
            Preconditions.checkArgument(!lastSplit.isEmpty(),
                    "Should not be empty last element? " + Arrays.toString(splittedString));

            if (lastSplit.charAt(lastSplit.length() - 1) != string.charAt(string.length() - 1)) {
                String[] concatenatedString = Arrays.copyOf(splittedString, splittedString.length + 1);
                concatenatedString[splittedString.length] = "";
                return concatenatedString;
            }

        }

        return splittedString;
    }

    // ================================================================================//
    // ExprOp //
    // ================================================================================//
    public StringBuilder getJavascriptString(ExprOp op, int depth) {

        StringBuilder exprString = getJavascriptString(op.exprs.get(0), 0);
        if (op.name.equals("COND")) {
            // Condition
            final StringBuilder cond = new StringBuilder(LaraIUtils.getSpace(depth));
            cond.append("(");
            cond.append(exprString);
            cond.append(")?(");
            // True

            cond.append(getJavascriptString(op.exprs.get(1), 0));
            cond.append("):(");
            // False
            cond.append(getJavascriptString(op.exprs.get(2), 0));
            cond.append(")");
            return cond;
        }
        if (op.name.equals("NEW")) {
            final StringBuilder newOp = new StringBuilder(LaraIUtils.getSpace(depth));
            newInstance = true;
            newOp.append(getJavascriptString(op.exprs.get(0), depth));
            // newOp.deleteCharAt(0);
            // newOp.deleteCharAt(newOp.length()-1);
            return newOp;
        }
        if (op.exprs.size() == 1) {
            final StringBuilder inc = new StringBuilder(LaraIUtils.getSpace(depth));
            if (op.name.equals("INCS")) {
                inc.append(exprString);
                inc.append("++");
                return inc;
            }
            if (op.name.equals("DECS")) {
                inc.append(exprString);
                inc.append("--");
                return inc;
            }
            // INCP and DECP are in this "else"
            inc.append(Operators.getOpString(op.name));
            inc.append("(" + exprString + ")");
            return inc;
        }

        final StringBuilder left = exprString;
        // if (isFilter && (op.exprs.get(0) instanceof ExprId))
        // left.append("()");
        StringBuilder right = getJavascriptString(op.exprs.get(1), 0);

        if (op.name.equals("ASSIGN")) {
            if (left.indexOf("(" + Interpreter.ATTRIBUTES + ".") == 0) {
                return getAttributesMerge(op.exprs.get(0), right, depth);
            }
        }

        if (op.name.equals("MATCH")) {
            final StringBuilder match = new StringBuilder(LaraIUtils.getSpace(depth) + "String(");
            match.append(left);
            match.append(").match(new RegExp(");
            match.append(right);
            match.append(")) != null");
            return match;
        }
        if (op.name.equals("FN")) {

            final StringBuilder ret = generateFunctionExpression(op, depth);
            return ret;
        }
        if (op.name.equals("COMMA")) { // this type of expression is always binary, even if multiple commas are used
            final StringBuilder commaExpr = new StringBuilder(LaraIUtils.getSpace(depth) + "(");
            commaExpr.append(left);
            commaExpr.append(",");
            commaExpr.append(right);
            commaExpr.append(")");
            return commaExpr;
        }
        final String operator = Operators.getOpString(op.name);
        final StringBuilder opBuff = new StringBuilder("(" + LaraIUtils.getSpace(depth));
        opBuff.append(left);
        opBuff.append(")" + operator + "(");
        opBuff.append(right);
        opBuff.append(")");
        for (int i = 2; i < op.exprs.size(); i++) {
            right = getJavascriptString(op.exprs.get(i), 0);
            opBuff.append(operator + "(");
            opBuff.append(right);
            opBuff.append(")");
        }
        return opBuff;

    }

    /**
     * Generate a function expression with the given expression element
     *
     * @param depth
     *
     * @param op
     *            the FN element containing the function expression
     * @return
     */
    private StringBuilder generateFunctionExpression(ExprOp fnOp, int depth) {
        final List<Expression> exprs = fnOp.exprs;
        final StringBuilder ret = new StringBuilder("function ");
        final StringBuilder funcName = getJavascriptString(exprs.get(0), -1); // can be empty name
        ret.append(funcName);
        ret.append("(");

        if (exprs.size() > 2) {
            StringBuilder argName = getJavascriptString(exprs.get(1), -1);
            ret.append(argName);
            for (int pos = 2; pos < exprs.size() - 1; pos++) {
                argName = getJavascriptString(exprs.get(pos), -1);
                ret.append(",");
                ret.append(argName);
            }
        }
        ret.append(")");
        if (oldDepth != 0) {
            depth = oldDepth;
        }
        final StringBuilder funcBody = getJavascriptString(exprs.get(exprs.size() - 1), -depth);
        ret.append(funcBody.subSequence(0, funcBody.length() - 1));
        return ret;
    }

    private StringBuilder getAttributesMerge(Expression expression, StringBuilder right, int depth) {
        final StringBuilder merging = new StringBuilder(
                LaraIUtils.getSpace(depth) + Interpreter.ATTRIBUTES + ".set( {" + getAMObject(expression));
        amBrackets++;
        merging.append(right);
        merging.append(" ");
        while (amBrackets > 0) {
            merging.append("}");
            amBrackets--;
        }
        merging.append(")");
        return merging;
    }

    private int amBrackets = 0;

    private StringBuilder getAMObject(Expression exp) {

        if (exp instanceof ExprId) {
            return new StringBuilder(((ExprId) exp).name.substring(1) + ": ");
        }
        if (exp instanceof ExprLiteral) {
            return new StringBuilder("" + ((ExprLiteral) exp).value + ": ");
        }
        if (exp.xmltag.equals("property")) {
            final StringBuilder obj = getAMObject(exp.exprs.get(0));
            obj.append("{ ");
            obj.append(getAMObject(exp.exprs.get(1)));
            amBrackets++;
            return obj;
        }
        return null;
    }

    // ================================================================================//
    // =================================== ExprKey ====================================//
    // ================================================================================//
    public StringBuilder getJavascriptString(ExprKey prop, int depth) {
        final StringBuilder ret = new StringBuilder(LaraIUtils.getSpace(depth));
        ret.append(getJavascriptString(prop.exprs.get(0), 0));
        if (prop.exprs.size() > 1) {
            ret.append("[");
            ret.append(getJavascriptString(prop.exprs.get(1), 0));
            ret.append("]");
            return ret;
        }
        return ret;
    }

    private Statement currentStatement;

    // ================================================================================//
    // ================================== Statement ===================================//
    // ================================================================================//
    public StringBuilder getJavascriptString(Statement stat, int depth) {
        currentStatement = stat;
        return processStatement(stat, "var ", depth, ";\n");
    }

    public StringBuilder processStatement(Statement stat, String varStr, int depth, String sufix) {

        return statementProcessor.processStatement(stat, varStr, depth, sufix);
    }

    private int oldDepth;

    // ================================================================================//
    // IElement //
    // ================================================================================//
    // private IElement lastIElement = null;
    public StringBuilder getJavascriptString(IElement el, int depth) {
        if (el instanceof Expression) {
            return getJavascriptString((Expression) el, depth);
        }
        if (el instanceof Statement) {
            return getJavascriptString((Statement) el, depth);
        }
        if (el instanceof Parameter) {
            return getJavascriptString((Parameter) el, depth);
        }
        if (el instanceof ParameterList) {
            return getJavascriptString((ParameterList) el, depth);
        }
        if (el instanceof ParameterSection) {
            return getJavascriptString(el, depth);
        }
        if (el instanceof Argument) {
            return getJavascriptString((Argument) el, depth);
        }
        if (el instanceof Aspect) {
            return getJavascriptString(el, depth);
        }
        if (el instanceof Aspects) {
            return getJavascriptString(el, depth);
        }
        if (el instanceof Code) {
            return getJavascriptString((Code) el, depth);
        }
        if (el instanceof ExprBody) {
            return getJavascriptString((ExprBody) el, depth);
        }

        throw new LaraIException(
                "Interpreter.getJavaScriptString(" + el.getClass().getSimpleName() + ",int): not implemented!");

    }

    public String getCurrentAspect() {
        return aspectProcessor.getCurrentAspect();
    }

    public boolean hasEvents() {
        return laraInterp.getWeaver().eventTrigger().hasListeners();
    }

    public List<String> getActions() {
        return laraInterp.getWeaver().getActions();
    }

    public Output out() {
        return out;
    }

    public LaraI getLaraI() {
        return laraInterp;
    }

    public LaraIDataStore getOptions() {
        return options;
    }

    /**
     * Change the print stream where the javascript output is going
     */
    private void setprintStream(PrintStream printStream) {
        // private void setprintStream(OutputStream printStream) {
        engine.getEngine().put("outputStream", printStream);
        engine.getEngine().put("errorStream", printStream);
    }

    /**
     * Sets the specified value with the specified key in the ENGINE_SCOPE Bindings of the protected context field.
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        engine.getEngine().put(key, value);

    }

    public WeaverStatementProcessor getWeaverStmtProcessor() {
        return wStmtProcessor;
    }

    public int getOldDepth() {
        return oldDepth;
    }

    public void setOldDepth(int oldDepth) {
        this.oldDepth = oldDepth;
    }

    public boolean useBrackets() {
        return brackets;
    }

    public void setBrackets(boolean brackets) {
        this.brackets = brackets;
    }

    public ImportProcessor getImportProcessor() {

        return importProcessor;
    }

    public CallStackTrace getStackStrace() {
        return stackStrace;
    }

}
