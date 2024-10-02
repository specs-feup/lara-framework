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
package utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.exception.ToolExecutionException;
import org.lara.interpreter.utils.LaraIUtils;
import org.lara.interpreter.utils.Tools;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import larac.utils.output.ErrorMsg;
import larac.utils.output.WarningMsg;
import larai.LaraI;
import pt.up.fe.specs.lang.SpecsPlatforms;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.exceptions.NotImplementedException;
import pt.up.fe.specs.util.system.ProcessOutputAsString;

public class LARASystem {
    public static final String LARA_SYSTEM_NAME = "LARA_SYSTEM";
    public static final String LARAPATH = "$LARAI";
    public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    static int auxCount = 0;
    private static final String execFileName = "CMD.log";
    private static final File execFile = new File(LARASystem.execFileName);
    // private static ProcessBuilder builder = new ProcessBuilder();
    private final HashMap<String, String> ENVIRONMET_VARIABLES = new HashMap<>();
    private final LaraI larai;
    public boolean showCommand = false;
    private File workingDir;

    public LARASystem(LaraI larai) {
        this.larai = larai;
        workingDir = SpecsIo.getCanonicalFile(SpecsIo.getWorkingDir());
        if (larai.getOptions().isDebug()) {
            showCommand = true;
        }
    }

    public void setWorkingDir(String dirStr) {
        setWorkingDir(new File(dirStr));
    }

    public void setWorkingDir(File dir) {
        workingDir = dir;
    }

    /**
     *
     * @param app
     * @param arguments
     * @param jpname
     * @param name
     * @param verbose
     * @param pipe
     * @param argsStr
     * @return
     * @throws IOException
     */
    public Object run(String app, Object arguments[], String jpname, String name, int verbose, Object pipe,
            String argsStr) throws IOException {
        // List<String> testArgs = new ArrayList<>();
        // stringifyArgs(arguments, LaraIUtils.getJarFoldername(), testArgs);
        // System.out.println("ARGS:" + testArgs);

        // System.out.println("PIPE: "+pipe+" class: "+(pipe !=
        // null?pipe.getClass():"none"));
        // System.out.println("UNDEFINED: "+Undefined.instance+" class:
        // "+Undefined.class);

        final Tools tools = larai.getTools();
        if (tools == null) {
            throw new RuntimeException(
                    "Tools specification file was not found. Please specify the location of the file 'tools.xml' before using run.");
            // throw new
            // LaraException("Tools specification file was not found. Please
            // specify the location of the file 'tools.xml' before using run.");
        }

        if (!tools.contains(app)) {
            throw new LaraIException("Tool '" + app + "' does not exist on the tools' xml specification");
        }

        final Element toolEl = tools.getToolElementProperties(app);
        if (toolEl.hasAttribute("type")) {
            if (toolEl.getAttribute("type").equals("eval")) {
                return evalJavaScript(toolEl, argsStr);
            }
            if (toolEl.getAttribute("type").equals("java")) {
                return runJava(app, arguments, toolEl);
            }
        }

        String toolExec = toolEl.getAttribute("run");

        // String jarLoc = LaraI.jarPath.substring(0,
        // LaraI.jarPath.lastIndexOf("/"));
        final String jarLoc = LaraIUtils.getJarFoldername();
        toolExec = toolExec.replace(LARASystem.LARAPATH, jarLoc);

        final ArrayList<String> commandArgs = new ArrayList<>();
        final ArrayList<String> after = new ArrayList<>();
        final NodeList extraArgs = toolEl.getElementsByTagName("argument");

        commandArgs.add(toolExec);
        for (int i = 0; i < extraArgs.getLength(); i++) {
            final Element argument = (Element) extraArgs.item(i);
            final String extra = argument.getAttribute("value").replace(LARASystem.LARAPATH, jarLoc);
            if (argument.hasAttribute("position") && argument.getAttribute("position").equals("before")) {
                commandArgs.add(extra);
            } else {
                after.add(extra);
            }
        }

        stringifyArgs(arguments, jarLoc, commandArgs);

        commandArgs.addAll(after);

        executeRun(toolExec, commandArgs, verbose);
        final NodeList reportModeJava = toolEl.getElementsByTagName("java");
        final NodeList reportModeJavascript = toolEl.getElementsByTagName("javascript");
        final int total = reportModeJava.getLength() + reportModeJavascript.getLength();
        if (total == 0) {
            return "";
        }
        if (total > 1) {
            WarningMsg
                    .say("Tool '"
                            + app
                            + "' contains more than 1 specification for the report.\n\tFirst javascript specification(or java if no javascript availabe) will be used");
        }
        if (reportModeJavascript.getLength() > 0) {
            final Element javascript = (Element) reportModeJavascript.item(0);
            final String report_name = javascript.getAttribute("report_name");
            final File report = new File(report_name);
            if (report.exists()) {
                // return parseReport(report_name);
                return SpecsIo.read(new File(report_name));
            }

        } else {
            final Element java = (Element) reportModeJava.item(0);
            final String reportClass = java.getAttribute("class");
            final String reportMethod = java.getAttribute("method");
            try {
                final Class<?> toolReportClass = Class.forName(reportClass);
                final Method toolReportMethod = toolReportClass.getMethod(reportMethod, String.class, String.class);
                final String ret = (String) toolReportMethod.invoke(null, jpname, name);
                return ret;
            } catch (final Exception e) {
                throw new LaraIException("When invoking java tool '" + reportClass + "." + reportMethod + "()'", e);
            }
        }

        return "{\r\n"
                + "    \"glossary\": {\r\n"
                + "        \"title\": \"example glossary\",\r\n"
                + "		\"GlossDiv\": {\r\n"
                + "            \"title\": \"S\",\r\n"
                + "			\"GlossList\": {\r\n"
                + "                \"GlossEntry\": {\r\n"
                + "                    \"ID\": \"SGML\",\r\n"
                + "					\"SortAs\": \"SGML\",\r\n"
                + "					\"GlossTerm\": \"Standard Generalized Markup Language\",\r\n"
                + "					\"Acronym\": \"SGML\",\r\n"
                + "					\"Abbrev\": \"ISO 8879:1986\",\r\n"
                + "					\"GlossDef\": {\r\n"
                + "                        \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\r\n"
                + "						\"GlossSeeAlso\": [\"GML\", \"XML\"]\r\n"
                + "                    },\r\n"
                + "					\"GlossSee\": \"markup\"\r\n" + "                }\r\n"
                + "            }\r\n"
                + "        }\r\n" + "    }\r\n" + "}";

    }

    private static void stringifyArgs(Object[] arguments, String jarLoc, List<String> args) {

        for (final Object value : arguments) {// nativeArray.getArray().asObjectArray()) {
            // final Object value = nativeArray.get(obj);

            Object json;
            if (!(value instanceof String)) {
                // json = NativeJSON.stringify(arguments, value, null, null);
                json = WeaverEngine.getThreadLocalWeaver().getScriptEngine().stringify(value);
            } else {
                json = value;
            }
            final String arg = json.toString().replace(LARASystem.LARAPATH, jarLoc);
            args.add(arg);
        }
    }

    public Object report(String app, Object arguments, String argsStr, int verbose) throws IOException {
        try {
            return reportAux(app, arguments, argsStr, verbose);
        } catch (Exception e) {
            throw new ToolExecutionException(app, new String[] { argsStr }, e);
        }
    }

    public Object reportAux(String app, Object arguments, String argsStr, int verbose) throws IOException {

        final Tools tools = larai.getTools();
        if (tools == null) {
            throw new RuntimeException(
                    "Tools specification file was not found. Please specify the location of the file 'tools.xml' before using report.");
            // throw new
            // LaraException("Tools specification file was not found. Please
            // specify the location of the file 'tools.xml' before using
            // report.");
        }

        if (!tools.contains(app)) {
            throw new LaraIException("Report tool " + app + "' does not exist on the tools' xml specification");
        }
        final Element toolEl = tools.getToolElementProperties(app);
        if (toolEl.hasAttribute("type")) {
            if (toolEl.getAttribute("type").equals("eval")) {
                return evalJavaScript(toolEl, argsStr);
            }

            if (toolEl.getAttribute("type").equals("java")) {
                return runJava(app, arguments, toolEl);
            }
        }

        String toolExec = toolEl.getAttribute("run");
        // String jarLoc = LaraI.jarPath.substring(0,
        // LaraI.jarPath.lastIndexOf("/"));
        final String jarLoc = LaraIUtils.getJarFoldername();
        toolExec = toolExec.replace(LARASystem.LARAPATH, jarLoc);

        final ArrayList<String> args = new ArrayList<>();
        final ArrayList<String> after = new ArrayList<>();
        final NodeList extraArgs = toolEl.getElementsByTagName("argument");

        args.add(toolExec);
        for (int i = 0; i < extraArgs.getLength(); i++) {
            final Element argument = (Element) extraArgs.item(i);
            final String extra = argument.getAttribute("value").replace(LARASystem.LARAPATH, jarLoc);

            if (argument.hasAttribute("position") && argument.getAttribute("position").equals("before")) {
                args.add(extra);
            } else {
                after.add(extra);
            }
        }

        if (!larai.getScriptEngine().isArray(arguments)) {
            ErrorMsg.say("The arguments must be inside an Array");
            return "";
        }

        for (final Object obj : larai.getScriptEngine().getValues(arguments)) {
            args.add(obj.toString());
        }
        /*
        for (final Object obj : ((NativeArray) arguments).getArray().asObjectArray()) {
            args.add(((NativeArray) arguments).get(obj).toString());
        }
        */
        args.addAll(after);

        executeRun(toolExec, args, verbose);
        final NodeList reportModeJava = toolEl.getElementsByTagName("java");
        final NodeList reportModeJavascript = toolEl.getElementsByTagName("javascript");
        final int total = reportModeJava.getLength() + reportModeJavascript.getLength();
        if (total == 0) {
            return "";
        }
        if (total > 1) {
            WarningMsg
                    .say("Tool '"
                            + app
                            + "' contains more than 1 specification for the report.\n\tFirst javascript specification(or java if no javascript availabe) will be used");
        }
        if (reportModeJavascript.getLength() > 0) {
            final Element javascript = (Element) reportModeJavascript.item(0);
            final String report_name = javascript.getAttribute("report_name");
            final File report = new File(report_name);
            if (report.exists()) {
                // parseReport(report_name);
                return SpecsIo.read(new File(report_name));
            }
        } else {
            final Element java = (Element) reportModeJava.item(0);
            final String reportClass = java.getAttribute("class");
            final String reportMethod = java.getAttribute("method");
            try {
                final Class<?> toolReportClass = Class.forName(reportClass);
                final Method toolReportMethod = toolReportClass.getMethod(reportMethod);
                final String ret = (String) toolReportMethod.invoke(null);
                return ret;
            } catch (final Exception e) {
                throw new LaraIException(
                        "When invoking java report method '" + reportClass + "." + reportMethod + "()",
                        e);
            }
        }

        return "";

    }

    private String evalJavaScript(Element toolEl, String argsSource) {
        final StringBuffer fileData = new StringBuffer("eval(" + toolEl.getTextContent());
        fileData.append("(");
        if (toolEl.getAttribute("needsArgs").equals("true")) {
            fileData.append(argsSource);
        }
        fileData.append("))");
        if (showCommand) {
            java.lang.System.out.println("Evaluating: " + fileData);
        }
        return fileData.toString();
    }

    /*
     * private static String runJavaScript(Element toolEl, String argsSource) {
     * String source = toolEl.getAttribute("source"); StringBuffer fileData =
     * new StringBuffer("tools.exec = "); BufferedReader reader; try { reader =
     * new BufferedReader( new FileReader(source)); char[] buf = new char[1024];
     * int numRead=0; while((numRead=reader.read(buf)) != -1)
     * fileData.append(String.valueOf(buf, 0, numRead));
     * fileData.append("\n"+"eval(("); fileData.append(argsSource);
     * fileData.append("));");
     *
     * return fileData.toString(); } catch (FileNotFoundException e) { throw new
     * LaraException(e.getLocalizedMessage()); } catch (IOException e) { throw
     * new LaraException(e.getLocalizedMessage()); } return ""; }
     */

    private Object runJava(String app, Object arguments, Element toolEl) {
        final NodeList javaRunnables = toolEl.getElementsByTagName("java");
        if (javaRunnables.getLength() == 0) {
            throw new LaraIException(
                    "Running " + app + " must have a java method specified in the tool specification file");
        }
        final Element javaEl = (Element) javaRunnables.item(0);
        final String javaClass = javaEl.getAttribute("class");
        final String javaMethod = javaEl.getAttribute("method");

        Class<?> toolClass;
        try {
            toolClass = Class.forName(javaClass, true, LARASystem.classLoader);

            Method toolReportMethod = null;
            Object ret = "";

            if (larai.getScriptEngine().isArray(arguments)) {

                final ArrayList<Class<?>> classTypes = new ArrayList<>();
                final ArrayList<Object> objects = new ArrayList<>();
                final Object[] args = larai.getScriptEngine().convert(arguments, Object[].class);

                for (final Object arg : args) {
                    classTypes.add(arg.getClass());
                    objects.add(arg);
                }
                toolReportMethod = toolClass.getMethod(javaMethod, classTypes.toArray(new Class<?>[] {}));
                ret = toolReportMethod.invoke(null, objects.toArray(new Object[] {}));
            } else {
                throw new NotImplementedException(
                        "For Java 9 it is necessary to replace the use of NativeObject.class, however currently it seems tools need to receive a NativeObject as interface? Check this.");
                // TODO: Replace NativeObject
                // toolReportMethod = toolClass.getMethod(javaMethod, NativeObject.class);
                // ret = toolReportMethod.invoke(null, arguments);
            }
            return ret;
        } catch (final Exception e) {
            throw new LaraIException("Problems when executing the java tool " + app, e);
        }
    }

    /**
     *
     * @param toolPath
     * @param arguments
     * @param verbose
     * @param pipe
     * @return the return code of the process, or -1 if there was an exception
     * @throws IOException
     */
    public int execute(String toolPath, Object[] arguments, int verbose, Object pipe) throws IOException {
        // System.out.println("PIPE : "+pipe+" class: "+(pipe !=
        // null?pipe.getClass():"none"));
        // System.out.println("UNDEFINED: "+Undefined.instance+" class:
        // "+Undefined.class);
        final List<String> args = new ArrayList<>();
        args.add(toolPath);
        stringifyArgs(arguments, "", args);
        // for (final Object obj : arguments) {
        // args.add(obj.toString());
        // }
        return executeRun(toolPath, args, verbose);
    }

    public void export(String toExport) {
        final String[] splitted = toExport.split("=");
        if (splitted.length != 2) {
            ErrorMsg.say("invalid argument for export: " + toExport);
            return;
        }
        export(splitted[0], splitted[1]);
    }

    public void export(String varName, String value) {
        ENVIRONMET_VARIABLES.put(varName, value);
        // builder.environment().put(varName, value);
    }

    /**
     *
     * @param toolPath
     * @param args
     * @param verbose
     * @return the return code of the process, or -1 if there was an exception
     * @throws IOException
     */
    // private static Runtime rt = Runtime.getRuntime();
    private int executeRun(String toolPath, List<String> args, int verbose) throws IOException {

        final OutputStream out = java.lang.System.out;

        final String[] cmdArgs = args.toArray(new String[] {});
        try {

            final boolean toConsole = verbose == 2;
            final boolean toFile = verbose == 1;
            final StringBuilder builderStr = new StringBuilder();
            final String start = "[LARAI] Running: " + args + "\n";

            if (showCommand) {
                out.write(start.getBytes());
            }

            final ProcessBuilder builder = new ProcessBuilder(Arrays.asList(cmdArgs));
            // File workingDir = new File("." + File.separator);
            builder.directory(workingDir);
            final ProcessOutputAsString status = SpecsSystem.runProcess(builder, toFile, toConsole);

            // ProcessOutput status =
            // ProcessUtils.runProcess(Arrays.asList(cmdArgs), "." +
            // File.separator, toFile,
            // toConsole, builder);
            final String end = "[LARAI] Exit value: " + status.getReturnValue() + "\n";

            if (showCommand) {
                out.write((end).getBytes());
            }
            if (toFile) {
                builderStr.append(status.getStdOut());
                builderStr.append("\n");
                builderStr.append(status.getStdErr());

                SpecsIo.append(LARASystem.execFile, builderStr.toString());
                if (!status.getStdErr().isEmpty()) {
                    ErrorMsg.say(status.getStdErr());
                }
            }

            return status.getReturnValue();
        } catch (final Exception e) {
            out.write(("[LARAI] Run finished with problems\n").getBytes());
        }

        return -1;
    }

    /*
     * private static String parseReport(String report) { String attributes =
     * "";
     *
     * attributes = readFile(report);
     *
     * return attributes; }
     */

    public static void selectFile(String dirName, String patt, ArrayList<String> files) {

        final File dir = new File(dirName);
        final String path = dir.getPath() + File.separator;

        for (final String fileName : dir.list()) {
            final File f = new File(path + fileName);

            if (f.isDirectory()) {
                selectFile(path + fileName, patt, files);
            } else if (fileName.matches(patt)) {
                files.add(fileName);
            }
        }
    }

    /*
     * private static String readFile(String fileName) { File file = new
     * File(fileName); return IoUtils.read(file); }
     */

    // public static String createCodeTemplate(JSObject obj) {
    // String code = obj.getMember("code").toString();
    // // String code = IoUtils.read(new File(codeFileName));
    // obj.removeMember("code");
    // for (final String key : obj.keySet()) {
    // final Object property = obj.getMember(key);
    // code = code.replace(key.toString(), property.toString());
    // }
    // return code;
    // }

    /**
     * Creates a code template.
     * 
     * Function for LARA.
     * 
     * @param obj
     * @return
     */
    // public static String createCodeTemplate(Bindings obj) {
    public static String createCodeTemplate(Object obj) {
        // For compatibility
        // obj = WeaverEngine.getThreadLocalWeaver().getScriptEngine().asBindings(obj);

        var engine = WeaverEngine.getThreadLocalWeaver().getScriptEngine();

        // String code = obj.get("code").toString();
        String code = engine.get(obj, "code").toString();

        // obj.remove("code");
        engine.remove(obj, "code");

        // for (final String key : obj.keySet()) {
        for (final String key : engine.keySet(obj)) {
            final Object property = engine.get(obj, key);
            code = code.replace(key.toString(), property.toString());
        }
        return code;

    }

    /**
     * @deprecated
     * @param arr
     * @return
     */
    @Deprecated
    public static Object[] toJavaArray(Object arr) {
        throw new RuntimeException("Deprecated method, is this necessary?");

        // if (!NashornUtils.isJSArray(arr)) {
        // return new Object[] { arr };
        // }
        //
        // return NashornUtils.getValues(arr).toArray(new Object[0]);
    }

    /**
     * Simply return class {@link Class}&lt;{@link JoinPoint}&gt;
     *
     * @return
     */
    public static Class<JoinPoint> getJoinPointClass() {
        return JoinPoint.class;
    }

    /**
     * Simply return class {@link Class}&lt;{@link JoinPoint}&gt;
     *
     * @return
     */
    public static Class<?> getJavaClass(Object object) {
        return object.getClass();
    }

    /**
     * Helper method which receives a String.
     *
     * @param filename
     * @return
     */
    public static String prepareExe(String filename) {
        return prepareExe(new File(filename));
    }

    /**
     * Prepares a file to be executed by a "cmd".
     *
     * @param file
     * @return
     */
    public static String prepareExe(File file) {
        // Just the name of the executable
        String name = file.getName();

        // If on Linux, add ./
        if (SpecsPlatforms.isLinux()) {
            name = "./" + name;
        }

        return name;
    }
}
