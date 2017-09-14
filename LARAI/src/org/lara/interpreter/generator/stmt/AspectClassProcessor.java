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
import org.lara.interpreter.aspectir.Aspect;
import org.lara.interpreter.aspectir.Aspects;
import org.lara.interpreter.aspectir.ExprOp;
import org.lara.interpreter.aspectir.Expression;
import org.lara.interpreter.aspectir.Parameter;
import org.lara.interpreter.aspectir.Statement;
import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.utils.Coordinates;
import org.lara.interpreter.utils.ExceptionUtils;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.MessageConstants;
import org.lara.interpreter.weaver.MasterWeaver;
import org.lara.interpreter.weaver.events.EventTriggerGenerator;

import pt.up.fe.specs.util.SpecsIo;

/**
 * A processor that deals with Aspect related constructions
 * 
 * @author Tiago
 *
 */
public class AspectClassProcessor {

    private static final String MAIN_PREFIX = "_main_";

    private final Interpreter interpreter;
    private String currentAspect;

    public static AspectClassProcessor newInstance(Interpreter interpreter) {
        return new AspectClassProcessor(interpreter);
    }

    private AspectClassProcessor(Interpreter interpreter) {
        this.interpreter = interpreter;

    }

    // ================================================================================//
    // ================================== Aspects =====================================//
    // ================================================================================//
    /**
     * Convert all aspects into javascript code
     * 
     * @param asps
     * @return
     */
    public StringBuilder generateJavaScript(Aspects asps) {
        currentAspect = "";
        interpreter.out().println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Interpreting Aspects"));
        long begin = System.currentTimeMillis();
        final StringBuilder code;
        if (asps.aspects.isEmpty()) {
            interpreter.out().warnln("No aspects to execute!");
            code = new StringBuilder();
        } else {
            code = generateAspects(asps);
        }

        code.append(MasterWeaver.WEAVER_NAME);
        code.append(".close();\n");

        interpreter.getLaraI().appendJs(code);

        if (interpreter.getOptions().isDebug()) {

            interpreter.out()
                    .println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Generated JavaScript"));
            interpreter.out().println(interpreter.getLaraI().getJs().toString());
        }

        long end = System.currentTimeMillis() - begin;
        interpreter.out().println(MessageConstants.getElapsedTimeMessage(end));

        return code;
    }

    private StringBuilder generateAspects(Aspects asps) {
        // first the aspects declaration
        for (final Aspect asp : asps.aspects.values()) {
            setCurrentAspect(asp.name);
            try {
                final StringBuilder aspectConstructor = getAspectJavascript(asp);
                aspectConstructor.trimToSize();
                interpreter.getLaraI().appendJs(aspectConstructor);
                interpreter.evaluate(aspectConstructor.toString());
            } catch (Exception e) {
                throw new LaraIException(SpecsIo.getCanonicalPath(interpreter.getOptions().getLaraFile()),
                        "generating aspects", e);
            }
        }
        // then the global variables declaration
        for (final Statement stmt : asps.declarations) {
            try {
                final StringBuilder globalsConstructor = interpreter.getJavascriptString(stmt, 0);
                globalsConstructor.trimToSize();
                interpreter.getLaraI().appendJs(globalsConstructor);
                interpreter.evaluate(globalsConstructor.toString());
            } catch (Exception e) {
                throw new LaraIException(SpecsIo.getCanonicalPath(interpreter.getOptions().getLaraFile()),
                        "generating global variables", e);
            }
        }

        String mainName = MAIN_PREFIX;
        String main = asps.main;
        String mainAspect = interpreter.getOptions().getMainAspect();
        if (mainAspect != null) {
            mainAspect = mainAspect.trim();
            if (!mainAspect.isEmpty()) {
                main = mainAspect;
            }
        }
        mainName += main;
        final StringBuilder code = new StringBuilder();
        code.append("var " + mainName + " = new " + main + "();\n");

        setArgumentsAndCallMain(mainName, code);
        code.append(";\n");

        createOutputReport(code, mainName, asps.aspects.get(main),
                interpreter.getOptions().getReportFile());

        code.append(LaraIUtils.getSpace(0));

        // code.append(".close('");
        // final String separator = java.lang.System.getProperty("file.separator");
        // code.append(IoUtils.getCanonicalPath(interpreter.getOptions().getOutputDir()).replace(separator,
        // separator + separator));
        // code.append("');\n");
        // code.append("');\n}\n");
        code.trimToSize();
        return code;
    }

    // ================================================================================//
    // ================================== Aspect ======================================//
    // ================================================================================//
    /**
     * Generate the javascript code for an Aspect
     * 
     * @param asp
     *            The Aspect to be interpreted
     * @param cx
     *            The javascript context
     */
    public StringBuilder getAspectJavascript(Aspect asp) {
        final StringBuilder aspectConstructor = new StringBuilder();
        LaraIUtils.appendComment(asp, aspectConstructor);
        aspectConstructor.append("function " + asp.name + "(");
        final StringBuilder arguments = new StringBuilder();
        final StringBuilder callArgs = new StringBuilder();
        final StringBuilder inputInit = new StringBuilder();
        String coord = asp.coord;
        Coordinates coordinates = new Coordinates(coord);
        int line = coordinates.getLineBegin();
        inputInit.append(LaraIUtils.getSpace(1) + "if(!(this instanceof " + asp.name + "))\n");
        inputInit.append(LaraIUtils.getSpace(2)
                + "throw new UserException('error: function [aspect()] must be used as constructor!'," + line
                + ");\n\n");
        inputInit.append(LaraIUtils.getSpace(1) + "with(this){\n");
        final StringBuilder outputInit = new StringBuilder();
        final boolean hasInput = (asp.parameters != null) && (asp.parameters.input != null);
        final boolean hasOutput = (asp.parameters != null) && (asp.parameters.output != null);

        if (hasInput) {

            for (final Parameter param : asp.parameters.input.parameters) {
                arguments.append(Interpreter.ARGS_PREFIX + param.name + ",");
                callArgs.append(
                        LaraIUtils.getSpace(1) + "if(" + Interpreter.ARGS_PREFIX + param.name + " != undefined)\n");
                callArgs.append(LaraIUtils.getSpace(2) + "this." + param.name + " = " + Interpreter.ARGS_PREFIX
                        + param.name + ";\n");
                inputInit.append(interpreter.getJavascriptString(param, 2));
                inputInit.append(";\n");
            }
            if (!asp.parameters.input.parameters.isEmpty()) {
                arguments.delete(arguments.length() - 1, arguments.length());
            }
        }
        if (hasOutput) {
            outputInit.append(LaraIUtils.getSpace(1) + "with(this) {");
            for (final Parameter param : asp.parameters.output.parameters) {

                outputInit.append(LaraIUtils.getSpace(2) + "this." + param.name + " = ");
                if (!param.exprs.isEmpty()) {
                    outputInit.append(interpreter.getJavascriptString(param.exprs.get(0), 0));
                } else {
                    outputInit.append("undefined");
                }
                outputInit.append(";\n");

            }
            outputInit.append(LaraIUtils.getSpace(1) + "}");
        }
        inputInit.append(LaraIUtils.getSpace(1) + "}\n");
        final StringBuilder staticFields = new StringBuilder();
        if (asp.staticBlock != null) {
            for (final Statement stat : asp.staticBlock.statements) {
                if (stat.name.equals("fndecl")) {
                    final Expression exp = (Expression) stat.components.get(0);
                    final ExprOp op = (ExprOp) exp.exprs.get(0);
                    StringBuilder funcName = interpreter.getJavascriptString(op.exprs.get(0), 0);
                    funcName = new StringBuilder(funcName.toString().replaceAll("'", ""));
                    staticFields.append(generateDefineProperty(asp.name, funcName, 1));
                } else {
                    for (int i = 0; i < stat.components.size(); i += 2) {
                        final Expression leftExpr = (Expression) stat.components.get(i);
                        final StringBuilder varName = interpreter.getJavascriptString(leftExpr, -1);
                        staticFields.append(generateDefineProperty(asp.name, varName, 1));
                    }
                }
            }
        }

        aspectConstructor.append(arguments);
        aspectConstructor.append("){\n\n");
        aspectConstructor.append(staticFields);
        aspectConstructor.append("\n");
        aspectConstructor.append(inputInit);
        aspectConstructor.append("\n");
        aspectConstructor.append(outputInit);
        aspectConstructor.append("\n");

        aspectConstructor.append(LaraIUtils.getSpace(1) + "this.checked = undefined;\n");
        // Add empty string to called_by parameters, since the main is not
        // invoked by another parameter
        aspectConstructor.append(
                LaraIUtils.getSpace(1) + "this." + EventTriggerGenerator.ASPECT_CALLER_PROPERTY_NAME + " = '';\n");
        aspectConstructor.append(LaraIUtils.getSpace(1) + "this." + EventTriggerGenerator.IS_ASPECT + " = true;\n");
        aspectConstructor.append(LaraIUtils.getSpace(1) + "this.exception = undefined;\n\n");
        aspectConstructor.append("}\n");

        aspectConstructor.append(asp.name + ".prototype.call = function (");
        aspectConstructor.append(arguments);
        aspectConstructor.append("){\n");
        aspectConstructor.append(callArgs);
        aspectConstructor.append(outputInit);
        aspectConstructor.append("\n");
        aspectConstructor.append(LaraIUtils.getSpace(1) + "this.checked = undefined;\n");
        aspectConstructor.append(LaraIUtils.getSpace(1) + "this.exception = null;\n");
        aspectConstructor.append(LaraIUtils.getSpace(1) + "__init_section = false;\n");

        // TRIGGER ASPECT BEGIN EVENT
        if (interpreter.hasEvents()) {

            EventTriggerGenerator.triggerAspectBegin(asp, aspectConstructor, hasInput, 1);
        }
        aspectConstructor.append(LaraIUtils.getSpace(1) + "with (this){\n");
        // generateFilterMethod(aspectConstructor, 2);
        aspectConstructor.append(LaraIUtils.getSpace(2) + "try{\n");

        if (asp.initialize != null) {
            for (final Statement stat : asp.initialize.statements) {
                aspectConstructor.append(interpreter.getJavascriptString(stat, 3));
            }
        }
        aspectConstructor.append(LaraIUtils.getSpace(3) + "__init_section = true;\n");
        if (asp.check != null) {
            aspectConstructor.append(LaraIUtils.getSpace(3) + "if(");
            aspectConstructor.append(interpreter.getJavascriptString(asp.check, 0));
            aspectConstructor.append("){\n");
        } else {
            aspectConstructor.append(LaraIUtils.getSpace(3) + "if(true){\n");
        }

        // Map<String, Integer> lines = new HashMap<>();

        for (final Statement stat : asp.statements) {
            int statLineBegin = new Coordinates(stat.coord).getLineBegin();
            aspectConstructor.append(LaraIUtils.getSpace(4) + "this.__currentLine__= " + statLineBegin + ";\n");
            StringBuilder statementString = interpreter.getJavascriptString(stat, 4);
            aspectConstructor.append(statementString);
            // int naiveCount = StringUtils.naiveCounter(statementString, '\n');
            // for (int i = 0; i < naiveCount; i++) {
            // lines.put("" + (startLine + i), statLineBegin);
            // }
            // startLine += naiveCount;
        }

        aspectConstructor.append(LaraIUtils.getSpace(3) + "} else this.checked = false;\n");
        aspectConstructor.append(LaraIUtils.getSpace(2) + "} catch (e){\n");
        aspectConstructor.append(LaraIUtils.getSpace(3) + "this.exception = e;\n");
        aspectConstructor.append(LaraIUtils.getSpace(3) + "e = e == undefined?'undefined exception':e;\n");
        aspectConstructor.append(LaraIUtils.getSpace(3)
                + "this.__currentLine__ = this.__currentLine__ == undefined?1:this.__currentLine__;\n");
        // Gson gson = new Gson();
        // String json = gson.toJson(lines);
        String aspectCoords = coord.replace("\\", "\\\\");
        aspectConstructor.append(LaraIUtils.getSpace(3) + ExceptionUtils.class.getSimpleName()
                + ".throwAspectException(e, '" + asp.name + "','" + aspectCoords + "',this.__currentLine__);\n");
        // + ".throwAspectException(e, e.rhinoException, '" + asp.name +
        // "'," + json + ");\n");
        // aspectConstructor.append(LaraIUtils.getSpace(3) + "var
        // laraNumber = 0;var jsLineNumber =0;\n");
        // aspectConstructor.append(LaraIUtils.getSpace(3) +
        // "println(e);\n");
        // aspectConstructor.append(LaraIUtils.getSpace(3) + "throw new
        // " + AspectDefException.class.getSimpleName());
        // aspectConstructor.append("('" + asp.name +
        // "',laraNumber,jsLineNumber,new
        // java.lang.RuntimeException(e));\n");
        // aspectConstructor.append(LaraIUtils.getSpace(3) +
        // "println('Exception on call " + asp.name
        // + " :\\n\\t'+ this.exception);\n");
        // if (this.options.isDebug()) {
        // aspectConstructor.append(LaraIUtils.getSpace(3) +
        // "this.exception.rhinoException.printStackTrace();\n");
        // // aspectConstructor.append("for(prop in
        // this.exception.rhinoException) println(prop);\n");
        // }

        // aspectConstructor.append(LaraIUtils.getSpace(3) + "this.exception =
        // '';\n");
        aspectConstructor.append(LaraIUtils.getSpace(2) + "} finally {\n");
        if (asp.finalize != null) {
            aspectConstructor.append(LaraIUtils.getSpace(3) + "if (__init_section)");
            aspectConstructor.append(interpreter.getJavascriptString(asp.finalize, -3));
        }

        // TRIGGER ASPECT END EVENT
        if (interpreter.hasEvents()) {

            EventTriggerGenerator.triggerAspectEnd(asp, aspectConstructor, hasOutput, 3);
        }

        aspectConstructor.append(LaraIUtils.getSpace(2) + "}\n");

        aspectConstructor.append(LaraIUtils.getSpace(1) + "}\n");

        aspectConstructor.append("}\n");

        if (asp.staticBlock != null) {
            aspectConstructor.append("with(" + asp.name + "){\n");
            for (final Statement stat : asp.staticBlock.statements) {
                final String sufix = ";\n";
                if (stat.name.equals("fndecl")) {
                    final Expression exp = (Expression) stat.components.get(0);
                    final ExprOp op = (ExprOp) exp.exprs.get(0);
                    final StringBuilder funcName = interpreter.getJavascriptString(op.exprs.get(0), 0);

                    aspectConstructor
                            .append(asp.name + "." + funcName.toString().replaceAll("'", "") + " = function (");
                    final StringBuilder args = new StringBuilder();
                    for (int i = 1; i < op.exprs.size() - 1; i++) {
                        args.append(interpreter.getJavascriptString(op.exprs.get(i), 0));
                        args.append(i == op.exprs.size() - 2 ? "" : ",");
                    }
                    aspectConstructor.append(args.toString().replaceAll("'", ""));
                    aspectConstructor.append(")");
                    aspectConstructor.append(interpreter.getJavascriptString(op.exprs.get(op.exprs.size() - 1), 0)); // body
                } else {
                    aspectConstructor.append(interpreter.processStatement(stat, asp.name + ".", 1, sufix));
                }
            }
            aspectConstructor.append("}\n");
        }
        // if (this.options.isDebug()) {
        // this.out.println(aspectConstructor.toString());
        // }
        return aspectConstructor;
    }

    /**
     * Add the arguments given with the larai parameter "-av" and call the main argument
     * 
     * @param mainName
     *            the main aspect name
     * @param code
     */
    private void setArgumentsAndCallMain(String mainName, StringBuilder code) {
        String aspectArgumentsStr = interpreter.getOptions().getAspectArgumentsStr();
        if (aspectArgumentsStr == null || aspectArgumentsStr.isEmpty()) {
            code.append(mainName + ".call()");
        } else {
            // String aspectArguments = StringUtils.join(this.options.getAspectArgumentsStr(), ", ");
            String aspectArguments = aspectArgumentsStr.trim();

            /*
            	final List<String> aspectArgumentsList = options.getAspectArg();
            	String aspectArguments = "";
            	for (int i = 0; i < aspectArgumentsList.size() - 1; i++) {
            		final String arg = aspectArgumentsList.get(i);
            		aspectArguments += arg + ", ";
            	}
            	final String arg = aspectArgumentsList.get(aspectArgumentsList.size() - 1);
            	aspectArguments += arg;
            */

            if (aspectArguments.startsWith("{") && aspectArguments.endsWith("}")) {
                // Assume that arguments are given in a JSON format
                code.append("invoke(" + mainName + "," + mainName + ".call," + aspectArguments + ")");
            } else if (aspectArguments.startsWith("(") && aspectArguments.endsWith(")")) {
                // Assume that arguments already have opening and closing brackets
                code.append(mainName + ".call" + aspectArguments);
            } else {
                code.append(mainName + ".call(" + aspectArguments + ")");
            }
        }
    }

    /**
     * Generates the output report, which contains the outputs of the main aspect
     * 
     * @param code
     * @param mainName
     * @param aspect
     * @param optionalFile
     */
    private static void createOutputReport(StringBuilder code, String mainName, Aspect aspect,
            OptionalFile optionalFile) {

        if (!optionalFile.isUsed()) {
            return;
        }
        final boolean hasOutput = (aspect.parameters != null) && (aspect.parameters.output != null);
        if (hasOutput) {
            final String outputObjName = mainName + "_output";
            code.append("var " + outputObjName + " = {};\n");
            for (final Parameter param : aspect.parameters.output.parameters) {

                code.append(outputObjName + "." + param.name + " = " + mainName + "." + param.name);
                code.append(";\n");
            }
            // code.append("printObject("+outputObjName+");\n");
            String canonicalPath = SpecsIo.getCanonicalPath(optionalFile.getFile());
            canonicalPath = SpecsIo.normalizePath(canonicalPath);
            code.append(
                    "JSONtoFile('" + canonicalPath + "'," + outputObjName + ");\n");
        }

    }

    /**
     * Defines a static variable as a property of the Aspect "class"
     * 
     * @param aspect
     * @param var
     * @param depth
     * @return
     */
    private static StringBuilder generateDefineProperty(String aspect, StringBuilder var, int depth) {
        final StringBuilder ret = new StringBuilder(
                LaraIUtils.getSpace(depth) + "Object.defineProperty(this, '" + var + "',\n");
        ret.append(LaraIUtils.getSpace(depth + 1) + "{get: function() { return " + aspect + "." + var + "; },\n");
        ret.append(LaraIUtils.getSpace(depth + 1) + " set: function(val) { " + aspect + "." + var + " = val; },\n");
        ret.append(LaraIUtils.getSpace(depth + 1) + " enumerable: true,\n");
        ret.append(LaraIUtils.getSpace(depth + 1) + " configurable: false\n");
        ret.append(LaraIUtils.getSpace(depth + 1) + "});\n");
        return ret;
    }

    public String getCurrentAspect() {
        return currentAspect;
    }

    public void setCurrentAspect(String currentAspect) {
        this.currentAspect = currentAspect;
    }

}
