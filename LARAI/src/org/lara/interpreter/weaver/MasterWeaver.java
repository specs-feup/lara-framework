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
import java.util.List;
import org.lara.interpreter.exception.WeaverEngineException;
import org.lara.interpreter.weaver.events.EventTrigger;
import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.Stage;
import larai.LaraI;
import pt.up.fe.specs.jsengine.JsEngine;
import pt.up.fe.specs.util.SpecsIo;

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
