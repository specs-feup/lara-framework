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
import javax.script.ScriptException;

import org.lara.interpreter.exception.EvaluationException;
import org.lara.interpreter.joptions.config.interpreter.LaraIDataStore;
import org.lara.interpreter.joptions.keys.OptionalFile;
import org.lara.interpreter.profile.ReportField;
import org.lara.interpreter.profile.WeaverProfiler;
import org.lara.interpreter.utils.MessageConstants;

import larac.utils.output.Output;
import larai.LaraI;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.jsengine.JsFileType;
import pt.up.fe.specs.util.SpecsIo;

public class Interpreter {

    public static final String ARGS_PREFIX = "";
    public static final String ATTRIBUTES = "attributes";
    public static final String TOOLS_CONTEXT = "tools";

    private final LaraI laraInterp;
    private final LaraIDataStore options;
    private Output out = new Output();
    private final JsEngine engine;


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
    }

    public Object executeMainAspect(File mainFile) {
        long start = setupStage();

        final Object result = evaluate(mainFile);

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
        OptionalFile reportFile = options.getMetricsFile();
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
        return engine.evalFile(jsFile, JsFileType.MODULE);
    }
}
