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
/**
 * Copyright 2012 SPeCS Research Group.
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

package org.lara.interpreter.weaver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.exception.PointcutExprException;
import org.lara.interpreter.exception.SelectException;
import org.lara.interpreter.exception.WeaverEngineException;
import org.lara.interpreter.weaver.defaultweaver.DefaultWeaver;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;
import org.lara.interpreter.weaver.utils.FilterExpression;

import larai.LaraI;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.tools.lara.exception.BaseException;
import pt.up.fe.specs.tools.lara.logging.LaraLog;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsLogs;

/**
 * Weaver that controls all the instances of Weavers created for controlling a specific file
 *
 * @author Tiago
 *
 */
public class MasterWeaver {
    // public class MasterWeaver implements AutoCloseable {

    public static final String WEAVER_NAME = "__weaver";
    public static final String GET_EVENT_TRIGGER = MasterWeaver.WEAVER_NAME + ".eventTrigger()";
    public static final String JPWEAVER_NAME = "__myWeaver";
    public static final String LANGUAGE_SPECIFICATION_NAME = "Weaver";

    // private Class<? extends WeaverEngine> weaverClass;
    // private final Map<File, WeaverEngine> weavers;
    private String root;
    private List<String> actions;
    private JoinpointUtils jpUtils;
    // private boolean handlesApplicationFolder;
    private final LaraI larai;
    private final EventTrigger eventTrigger;
    private final WeaverEngine weaverEngine;
    private long initialTime;

    /**
     * Creates a new MasterWeaver depending on the weaver class to use
     *
     * @param larai
     *           the LARA Interpreter (TODO: Should be replaced by a data object)
     * @param weaverEngine
     *            the weaver class to create by reflection
     * @param engine
     *            the current JavaScript engine
     */
    public MasterWeaver(LaraI larai, WeaverEngine weaverEngine, JsEngine engine) {
        eventTrigger = new EventTrigger();
        weaverEngine.setEventTrigger(eventTrigger);

        this.larai = larai;
        jpUtils = new JoinpointUtils(engine);

        this.weaverEngine = weaverEngine;
    }

    public static Class<? extends WeaverEngine> getWeaverClass(String className) throws ClassNotFoundException {
        final Class<? extends WeaverEngine> theWeaver = Class
                .forName(className, true, Thread.currentThread().getContextClassLoader())
                .asSubclass(WeaverEngine.class);

        return theWeaver;
    }

    public void addGears(List<AGear> gears) {
        if (larai.getOptions().getMetricsFile().isUsed()) {
            eventTrigger.registerReceiver(larai.getWeavingProfile());
        }

        if (gears == null) {
            return;
        }
        eventTrigger.registerReceivers(gears);

    }

    /**
     * Sets the weaver or weavers, depending if the weaver can handle an application folder or a single file at a time
     *
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public boolean begin() {
        try {
            weaverEngine.setWeaver();

            setActions(weaverEngine.getActions());
            setRoot(weaverEngine.getRoot());
            weaverEngine.getWeaverProfiler().reset();
            
            final List<AGear> gears = weaverEngine.getGears();
            addGears(gears);

            // TRIGGER WEAVER BEGIN EVENT
            if (eventTrigger.hasListeners()) {

                String main = larai.getOptions().getMainAspect();
                larai.getWeavingProfile().reportLaraNumTokens(larai.getNumMainLaraTokens());
                eventTrigger().triggerWeaver(Stage.BEGIN, larai.getWeaverArgs(), main,
                        larai.getOptions().getLaraFile().getName());
            }

            final boolean weaverIsWorking = weaverEngine.run(larai.getWeaverArgs());

            if (!weaverIsWorking) {
                larai.out.warnln("Application inputs '" + getSources()
                        + "' could not be used by '" + weaverEngine.getClass().getName() + "'");

                return false;
            }

            return true;

        } catch (Exception e) {
            throw new WeaverEngineException("initializing", e);
        }
    }

    public void simpleBegin() {
        weaverEngine.setWeaver();

        setActions(weaverEngine.getActions());
        setRoot(weaverEngine.getRoot());
    }

    private final static String PATH_MODEL_BEGIN = "(path())==('";
    private final static String PATH_MODEL_END = "')";

    // public LaraJoinPoint select(String selectName, String[] jpChain, String[] aliasChain,
    // FilterExpression[][] filterChain,
    // String aspect_name, Bindings localScope, int lineNumber) throws IOException {
    // return selectPrivate(selectName, jpChain, aliasChain, filterChain, aspect_name, localScope, lineNumber);
    // }

    // public LaraJoinPoint select(String selectName, String[] jpChain, String[] aliasChain,
    // FilterExpression[][] filterChain,
    // String aspect_name, Value localScope, int lineNumber) throws IOException {
    // return selectPrivate(selectName, jpChain, aliasChain, filterChain, aspect_name, localScope, lineNumber);
    // }

    /**
     * Select method that invokes the weaver(s) to get the desired pointcuts
     *
     * @param jpChain
     *            the join point chain
     * @param aliasChain
     *            the alias for each member in the join point chain
     * @param filterChain
     *            the filter for each join point
     * @return a javascript variable with the pointcut
     * @throws IOException
     */
    public LaraJoinPoint select(String selectName, String[] jpChain, String[] aliasChain,
            FilterExpression[][] filterChain,
            String aspect_name, Object localScope, int lineNumber) throws IOException {

        // System.out.println("SELECT 2");

        // localScope comes from JS, convert first to compatible Bindings
        // localScope = getEngine().getScriptEngine().asBindings(localScope);

        // TRIGGER SELECT BEGIN EVENT
        if (eventTrigger.hasListeners()) {
            eventTrigger.triggerSelect(Stage.BEGIN, aspect_name, selectName, jpChain, aliasChain, filterChain,
                    Optional.empty());
        }

        try {
            final LaraJoinPoint root = LaraJoinPoint.createRoot();

            if (weaverEngine instanceof DefaultWeaver) {
                selectWithDefaultWeaver(jpChain, aliasChain, filterChain, localScope, root);
            } else {
                selectWithWeaver(jpChain, aliasChain, filterChain, localScope, root, aspect_name,
                        selectName);
            }

            // if (handlesApplicationFolder) {
            // }
            // } else {
            // for (final WeaverEngine currentWeaver : weavers.values()) {
            // selectByWeaver(currentWeaver, jpChain, aliasChain, filterChain, localScope, root);
            // }
            // }

            // TRIGGER SELECT END EVENT
            if (eventTrigger.hasListeners()) {

                Optional<LaraJoinPoint> pointcut = root.getChildren().isEmpty() ? Optional.empty()
                        : Optional.of(root.getChild(0));
                eventTrigger.triggerSelect(Stage.END, aspect_name, selectName, jpChain, aliasChain, filterChain,
                        pointcut);
            }
            LaraLog.printMemory("before converting to javascript");
            // final Bindings javascriptObject = jpUtils.toJavaScript(root);
            LaraLog.printMemory("after converting to javascript");
            // System.out.println("LOCAL SCOPE: " + localScope);
            // System.out.println("ROOT: " + root);

            return root;
        } catch (Exception e) {
            throw processSelectException(selectName, jpChain, e, lineNumber);
        }
    }

    private static PointcutExprException processSelectException(String selectName, String[] jpChain, Exception e,
            int lineNumber) {

        Throwable cause = e.getCause();
        if (cause != null) {
            if (cause instanceof BaseException) {
                e = (Exception) cause;
            }
        }

        return new PointcutExprException(selectName, jpChain, lineNumber, e);
    }

    // private void selectByWeaver(WeaverEngine currentWeaver, String[] jpChain, String[] aliasChain,
    private void selectWithWeaver(String[] jpChain, String[] aliasChain,
            FilterExpression[][] filterChain,
            Object localScope, LaraJoinPoint root, String aspect_name, String selectName)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final JoinPoint rootSelect = weaverEngine.select();
        // TRIGGER SELECT BEGIN EVENT
        if (eventTrigger.hasListeners()) {

            eventTrigger.triggerJoinPoint(Stage.BEGIN, selectName, weaverEngine.getRoot(), filterChain[0], rootSelect,
                    true);
        }
        // rootSelect.setWeaverEngine(weaverEngine);
        final MWRoot mwRoot = new MWRoot();
        root.setReference(mwRoot);
        if (jpUtils.evalFilter(rootSelect, filterChain[0], localScope)) {
            final LaraJoinPoint wRoot = new LaraJoinPoint(rootSelect);
            wRoot.setClassAlias(aliasChain[0]);
            generateSelect(wRoot, jpChain, aliasChain, filterChain, 1, localScope);

            if (!wRoot.isLeaf() && wRoot.getChildren().isEmpty()) {
                return;
            }
            wRoot.setParent(root);
            root.addChild(wRoot);
        }
    }

    private void selectWithDefaultWeaver(String[] jpChain, String[] aliasChain, FilterExpression[][] filterChain,
            Object localScope,
            LaraJoinPoint root)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DefaultWeaver defWeaver = (DefaultWeaver) weaverEngine;
        if (!filterChain[0][0].isEmpty()) {
            String path = (String) filterChain[0][0].getExpected();
            path = path.substring(MasterWeaver.PATH_MODEL_BEGIN.length(),
                    path.length() - MasterWeaver.PATH_MODEL_END.length());
            path = "";
            // System.out.println(path);
            final File appFolder = new File(path).getCanonicalFile();
            if (!appFolder.isDirectory()) {
                throw new LaraIException("Path used in 'folder' must be a directory!");
            }
            defWeaver.ensureThatContains(appFolder);
            // if (!defWeaver.contains(appFolder)) {
            // WeaverEngine newWeaver;
            // try {
            // newWeaver = weaverClass.newInstance();
            // newWeaver.begin(appFolder, larai.getOptions().getOutputDir(),
            // larai.getWeaverArgs());
            // weavers.put(appFolder, newWeaver);
            // } catch (final InstantiationException | IllegalAccessException e) {
            // throw new LaraIException("Could instanciate selected weaver", e);
            // }
            // }
            // currentWeaver = weavers.get(appFolder);

        } else {
            // currentWeaver = weavers.get(getApplicationFolder());
        }
        root.setReference(defWeaver.select());
        generateSelect(root, jpChain, aliasChain, filterChain, 0, localScope);
    }

    /**
     * Print a weaver exception
     *
     * @param selectName
     * @param e
     */
    @Deprecated
    static void printException(String selectName, Exception e) {
        final String message = e.getMessage();

        Throwable throwable = e;
        if (message == null) {
            throwable = e.getCause();
        }
        SpecsLogs.warn("Problem during select " + selectName + ": " + throwable.getMessage(), throwable);
    }

    /**
     * Select method that invokes the weaver(s) to get the desired pointcuts, starting in the first(s)
     * joinPointReference(s)
     *
     * @param joinPointReferences
     * @param jpChain
     * @param aliasChain
     * @param filterChain
     * @return
     * @throws IOException
     */
    public LaraJoinPoint select(Object joinPointReferences, String selectName, String[] jpChain, String[] aliasChain,
            FilterExpression[][] filterChain, String aspect_name, Object localScope, int lineNumber)
            throws IOException {

        // System.out.println("SELECT 1");

        final LaraJoinPoint root = LaraJoinPoint.createRoot();
        root.setReference(null);
        // TRIGGER SELECT BEGIN EVENT
        if (eventTrigger.hasListeners()) {
            eventTrigger.triggerSelect(Stage.BEGIN, aspect_name, selectName, jpChain, aliasChain, filterChain,
                    Optional.empty());
        }
        try {

            boolean isArray = weaverEngine.getScriptEngine().isArray(joinPointReferences);
            if (isArray) {

                // final ScriptObjectMirror jpReferences = (ScriptObjectMirror) joinPointReferences;
                var jpReferences = weaverEngine.getScriptEngine().getValues(joinPointReferences);

                int counter = 0;
                // for (int i = 0; i < jpReferences.size(); i++) {
                for (var reference : jpReferences) {
                    // final Object reference = jpReferences.get("" + i);
                    if (!(reference instanceof JoinPoint)) {
                        // String errorMsg = "Array element " + i + " of " + jpChain[0] + " is not supported ("
                        String errorMsg = "Array element " + counter + " of " + jpChain[0] + " is not supported ("
                                + reference.getClass().getSimpleName() + ").\n";
                        errorMsg += "\tThe select supports only join point references and JavaScript arrays";
                        throw new RuntimeException(errorMsg);
                    }
                    final JoinPoint jpReference = (JoinPoint) reference;
                    // System.out.println(jpReference.getWeavingEngine());
                    generateSelectFromArbitraryIJoinPoint(jpReference, jpChain, aliasChain, filterChain, root,
                            localScope);

                    counter++;
                }

                // throw new RuntimeException("T O D O SELECT FROM REFERENCE -
                // NATIVE ARRAY");

            } else if (joinPointReferences instanceof JoinPoint) {
                final JoinPoint jpReference = (JoinPoint) joinPointReferences;
                generateSelectFromArbitraryIJoinPoint(jpReference, jpChain, aliasChain, filterChain, root, localScope);

            } else {

                String errorMsg = "Select from variable " + jpChain[0] + " is not supported: variable is ";
                // if (joinPointReferences instanceof Undefined) {
                if (weaverEngine.getScriptEngine().isUndefined(joinPointReferences)) {
                    errorMsg += "undefined.";
                } else {
                    errorMsg += " variable is of type " + joinPointReferences.getClass().getSimpleName() + ".";
                }

                errorMsg += "\n\tThe select statement supports only join point references and JavaScript arrays";
                throw new RuntimeException(errorMsg);
            }

            // final Bindings javascriptObject = jpUtils.toJavaScript(root);
            if (eventTrigger.hasListeners()) {

                Optional<LaraJoinPoint> pointcut = root.getChildren().isEmpty() ? Optional.empty()
                        : Optional.of(root.getChild(0));
                eventTrigger.triggerSelect(Stage.END, aspect_name, selectName, jpChain, aliasChain, filterChain,
                        pointcut);
            }

            return root;
        } catch (Exception e) {
            throw processSelectException(selectName, jpChain, e, lineNumber);
        }
    }

    private void generateSelectFromArbitraryIJoinPoint(JoinPoint joinPointReferences, String[] jpChain,
            String[] aliasChain, FilterExpression[][] filterChain, LaraJoinPoint root, Object localScope)
            throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        final JoinPoint ijp = joinPointReferences;
        final LaraJoinPoint ljp = new LaraJoinPoint(ijp);
        ljp.setClassAlias(aliasChain[0]);
        generateSelect(ljp, jpChain, aliasChain, filterChain, 1, localScope);
        // System.out.println(ljp);
        if (ljp.isLeaf() || ljp.getChildren().size() != 0) {
            root.addChild(ljp);
        }
    }

    /**
     * Generate a select for a certain weaver
     *
     * @param current
     *            the weaver to use
     * @param jpChain
     *            the pointcut chain
     * @param aliasChain
     *            the join points alias
     * @param filterChain
     *            the filters to use
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void generateSelect(LaraJoinPoint current, String[] jpChain, String[] aliasChain,
            FilterExpression[][] filterChain,
            int pos, Object localScope) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {

        if (pos == jpChain.length) {
            current.setLeaf(true);
            return;
        }
        final JoinPoint lastJP = current.getReference();
        String selectName = jpChain[pos];

        try {
            // ret = jpMethod.invoke(lastJP);
            LaraLog.printMemory("before joinpoint selection of " + selectName);

            // WeaverEngine weavingEngine = lastJP.getWeaverEngine();
            List<? extends JoinPoint> joinPointList = lastJP.select(selectName);
            LaraLog.printMemory("after joinpoint selection of " + selectName);
            if (joinPointList == null) {
                throw new NullPointerException(
                        "Select '" + selectName + "' from '" + lastJP.get_class() + "' returned null");
            }
            LaraLog.printMemory("when converting to LaraJoinPoint");

            FilterExpression[] filter = filterChain[pos];
            String alias = aliasChain[pos];
            for (final JoinPoint joinPoint : joinPointList) {
                // joinPoint.setWeaverEngine(weavingEngine);
                // if(!true)
                if (eventTrigger.hasListeners()) {

                    eventTrigger.triggerJoinPoint(Stage.BEGIN, selectName, alias, filter, joinPoint, true);

                }
                boolean approvedByFilter = jpUtils.evalFilter(joinPoint, filterChain[pos], localScope);
                if (!approvedByFilter) {
                    continue;
                }
                final LaraJoinPoint child = new LaraJoinPoint(joinPoint);
                generateSelect(child, jpChain, aliasChain, filterChain, pos + 1, localScope);

                if (!child.isLeaf() && child.getChildren().isEmpty()) {
                    continue;
                }
                current.addChild(child);
                child.setParent(current);
                child.setClassAlias(aliasChain[pos]);
            }

        } catch (Exception e) {
            throw new SelectException(lastJP.get_class(), selectName, e);
        }

    }

    public LaraJoinPoint defaultOfJoin() {
        return LaraJoinPoint.createRoot();
        // return jpUtils.toJavaScript(LaraJoinPoint.createRoot());
    }

    /**
     * @return the jpUtils
     */
    public JoinpointUtils getJpUtils() {
        return jpUtils;
    }

    /**
     * @param jpUtils
     *            the jpUtils to set
     */
    public void setJpUtils(JoinpointUtils jpUtils) {
        this.jpUtils = jpUtils;
    }

    /**
     * @return the actions
     */
    public List<String> getActions() {
        return actions;
    }

    /**
     * @param actions
     *            the actions to set
     */
    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    /**
     * @return the applicationFolder
     */
    public List<File> getSources() {
        return larai.getOptions().getWorkingDir().getFiles();
    }

    /**
     *
     * @param joinpointReference
     * @param method
     * @param args
     */
    public void action(Object joinpointReference, String method, Object... args) {

    }

    /**
     * @return
     */
    public EventTrigger eventTrigger() {

        return eventTrigger;
    }

    public boolean close() {
        try {
            weaverEngine.close();
            // for (final WeaverEngine weaver : weavers.values()) {
            // weaver.close();
            // }
        } catch (Exception e) {
            throw new WeaverEngineException("closing", e);
        } finally {
            // Remove if it is set
            if (WeaverEngine.isWeaverSet()) {
                WeaverEngine.removeWeaver();
            }

            // Delete weaver folder, if created
            if (weaverEngine.hasTemporaryWeaverFolder()) {
                SpecsIo.deleteFolder(weaverEngine.getTemporaryWeaverFolder());
            }
        }
        // System.out.println("Done");
        return true;
    }

    /**
     * @return the root name
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root
     *            the root to set
     */
    public void setRoot(String root) {
        this.root = root;
    }

    public WeaverEngine getEngine() {
        return weaverEngine;
    }

    public void removeWeaverEngine() {
        if (WeaverEngine.isWeaverSet()) {
            WeaverEngine.removeWeaver();
        }
    }

    public long currentTime() {
        return LaraI.getCurrentTime() - initialTime;
    }

    public void setInitialTime(long begin) {
        initialTime = begin;
    }
}
