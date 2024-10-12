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
import java.util.List;

import javax.script.ScriptException;

import org.lara.interpreter.exception.EvaluationException;
import org.lara.interpreter.generator.stmt.ImportProcessor;
import org.lara.interpreter.joptions.config.interpreter.LaraIDataStore;
import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.profile.ReportField;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.utils.MessageConstants;


import larac.utils.output.Output;
import larai.LaraI;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.tools.lara.trace.CallStackTrace;
import pt.up.fe.specs.util.SpecsCheck;
import pt.up.fe.specs.util.SpecsIo;

public class Interpreter {

    public static final String ARGS_PREFIX = "";
    public static final String ATTRIBUTES = "attributes";
    public static final String TOOLS_CONTEXT = "tools";
    // public static final String PROPERTY_GETTER = "laraGetter";

    public static final String JPUTILS_NAME = "__jpUtils";
    private final LaraI laraInterp;
    private final LaraIDataStore options;
    private Output out = new Output();
    private final JsEngine engine;
    private final ImportProcessor importProcessor;
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
        // this(laraInt, engine, true);
        // }
        //
        // public Interpreter(LaraI laraInt, JsEngine engine, boolean importScripts) {
        laraInterp = laraInt;
        options = laraInterp.getOptions();
        out = laraInterp.out;
        this.engine = engine;
        importProcessor = ImportProcessor.newInstance(this);
        if (options.useStackTrace()) {
            stackStrace = new CallStackTrace();
        }

        if (options.getWeaverArgs().get(LaraiKeys.API_AUTOLOAD)) {
            importProcessor.importScriptsAndClasses();
            importProcessor.importAndInitialize();
        }
    }

    public JsEngine getEngine() {
        return engine;
    }

    public Object executeMainAspect(final StringBuilder mainCall) {
        long start = setupStage();

        String code = mainCall.toString();
        final Object result = evaluate(code, "main_aspect");// cx.evaluateString(scope, code, "<js>", 1, null);

        completeStage(start);
        return result;
    }

    public Object executeMainAspect(File mainFile) {
        long start = setupStage();

        final Object result = evaluate(mainFile);

        completeStage(start);
        return result;
    }

    public Object executeMainAspect(String code, JsFileType type, String source) {
        long start = setupStage();

        final Object result = evaluate(code, type, source);

        completeStage(start);
        return result;
    }

    private long setupStage() {
        out.println(MessageConstants.getHeaderMessage(MessageConstants.order++, "Executing Main Aspect"));
        long begin = LaraI.getCurrentTime();
        laraInterp.getWeaver().setInitialTime(begin);
        return begin;
    }

    private void completeStage(long begin) {
        long end = LaraI.getCurrentTime() - begin;
        laraInterp.getWeavingProfile().report(ReportField.WEAVING_TIME, (int) end);
        // exportMetrics();
        out.println(MessageConstants.getElapsedTimeMessage(end));
    }

    /**
     * Export the weaver metrics to the given file
     */
    public void exportMetrics() {
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
    public Object evaluate(String code, String source) {
        return evaluate(code, JsFileType.NORMAL, source);
        // return evaluate(code, JsFileType.MODULE, source);
    }

    public Object evaluate(String code, JsFileType type, String source) {

        try {
            return engine.eval(code, type, source);
        } catch (Exception e) {
            throw new EvaluationException(e);
        }

    }

    public Object evaluate(File jsFile) {
        return engine.evalFile(jsFile);
    }

    private int oldDepth;


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
     * Sets the specified value with the specified key in the ENGINE_SCOPE Bindings of the protected context field.
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        // Get function
        // var setterFunction = engine.eval("setGlobalThis");
        // var setterFunction = engine.eval("function setGlobalThis(key, value) {\n"
        // + " globalThis[key] = value;\n"
        // + "}\n"
        // + "setGlobalThis;");
        //
        // engine.call(setterFunction, key, value);
        // System.out.println("STREAM: " + value);
        engine.put(key, value);

    }

    public int getOldDepth() {
        return oldDepth;
    }

    public void setOldDepth(int oldDepth) {
        this.oldDepth = oldDepth;
    }

    public ImportProcessor getImportProcessor() {
        return importProcessor;
    }

    public CallStackTrace getStackStrace() {
        return stackStrace;
    }

}
