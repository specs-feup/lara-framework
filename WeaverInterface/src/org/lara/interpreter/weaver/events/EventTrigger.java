/**
 * Copyright 2014 SPeCS Research Group.
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

package org.lara.interpreter.weaver.events;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.script.Bindings;

import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.LaraIEvent;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyIterationEvent;
import org.lara.interpreter.weaver.interf.events.data.AspectEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;
import org.lara.interpreter.weaver.interf.events.data.SelectEvent;
import org.lara.interpreter.weaver.interf.events.data.WeaverEvent;
import org.lara.interpreter.weaver.joinpoint.LaraJoinPoint;
import org.lara.interpreter.weaver.utils.FilterExpression;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.util.events.Event;
import pt.up.fe.specs.util.events.EventController;
import pt.up.fe.specs.util.events.SimpleEvent;

public class EventTrigger {
    private final EventController eventController;

    public EventTrigger() {

        eventController = new EventController();
    }

    public void registerReceiver(AGear gear) {
        eventController.registerReceiver(gear);
    }

    public void registerReceivers(List<AGear> gears) {

        gears.forEach(this::registerReceiver);
        // for (final AGear gear : gears) {
        // eventController.registerReceiver(gear);
        // }
    }

    public static final String TRIGGER_WEAVER_EVENT = "triggerWeaver";

    /**
     * Trigger a weaver event
     * 
     * @param stage
     * @param args
     * @param sources
     */
    public void triggerWeaver(Stage stage, DataStore args, List<File> sources, String mainAspect,
            String aspectFile) {
        final WeaverEvent data = new WeaverEvent(stage, args, sources, mainAspect,
                aspectFile);
        final Event event = new SimpleEvent(LaraIEvent.OnWeaver, data);
        eventController.notifyEvent(event);
    }

    public static final String TRIGGER_ASPECT_EVENT = "triggerAspect";

    /**
     * Trigger an aspect event
     * 
     * @param stage
     * @param aspect_name
     * @param called_by
     * @param params
     * @param objects
     */
    public void triggerAspect(Stage stage, String aspect_name, String called_by, String[] params, String[] objects,
            Object exception) {

        final AspectEvent data = new AspectEvent(stage, aspect_name, called_by, params, objects, exception);
        final Event event = new SimpleEvent(LaraIEvent.OnAspect, data);
        eventController.notifyEvent(event);
    }

    public static final String TRIGGER_ACTION_EVENT = "triggerAction";

    /**
     * Trigger an action event
     * 
     * @param stage
     * @param name
     * @param target
     * @param params
     */
    public void triggerAction(Stage stage, String name, JoinPoint target, List<Object> params,
            Optional<Object> result) {

        final ActionEvent data = new ActionEvent(stage, name, target, params, result);
        final Event event = new SimpleEvent(LaraIEvent.OnAction, data);
        eventController.notifyEvent(event);
    }

    public void triggerAction(Stage stage, String name, JoinPoint target, Optional<Object> result, Object... params) {

        final ActionEvent data = new ActionEvent(stage, name, target, Arrays.asList(params), result);
        final Event event = new SimpleEvent(LaraIEvent.OnAction, data);
        eventController.notifyEvent(event);
    }

    public static final String TRIGGER_APPLY_EVENT = "triggerApply";

    /**
     * Trigger an apply event
     * 
     * @param stage
     * @param name
     * @param target
     * @param params
     */
    public void triggerApply(Stage stage, String aspect_name, String label, String select_label,
            Bindings select) {
        Optional<LaraJoinPoint> root;
        if (select == null || select.get("isEmpty").equals(true)) {
            root = Optional.empty();
        } else {
            final LaraJoinPoint laraRoot = (LaraJoinPoint) select.get("laraJoinPoint");
            LaraJoinPoint weaverRoot = laraRoot.getChild(0); // Master root only has one child (weaver root);
            root = Optional.of(weaverRoot);
        }

        final ApplyEvent data = new ApplyEvent(stage, aspect_name, label, select_label, root);
        final Event event = new SimpleEvent(LaraIEvent.OnApply, data);
        eventController.notifyEvent(event);

    }

    public void triggerApply(Stage stage, String aspect_name, String label, String select_label,
            JoinPoint[] pointcutChain) {
        final ApplyIterationEvent data = new ApplyIterationEvent(stage, aspect_name, label, select_label,
                Arrays.asList(pointcutChain));
        final Event event = new SimpleEvent(LaraIEvent.OnApply, data);
        eventController.notifyEvent(event);
    }

    /**
     * Trigger a select event
     * 
     * @param stage
     * @param aspect_name
     * @param selectLable
     * @param pointcutChain
     * @param aliases
     * @param filters
     * @param pointcut
     */
    public void triggerSelect(Stage stage, String aspect_name, String selectLable, String[] pointcutChain,
            String[] aliases, FilterExpression[][] filters, Optional<LaraJoinPoint> pointcut) {

        final SelectEvent data = new SelectEvent(stage, aspect_name, selectLable, pointcutChain, aliases, filters,
                pointcut);
        final Event event = new SimpleEvent(LaraIEvent.OnSelect, data);
        eventController.notifyEvent(event);
    }

    /**
     * Trigger an attribute access event
     * 
     * @param stage
     * @param target
     * @param name
     * @param args
     */
    public void triggerAttribute(Stage stage, JoinPoint target, String name, Optional<Object> result, Object... args) {
        triggerAttribute(stage, target, name, Arrays.asList(args), result);
    }

    public void triggerAttribute(Stage stage, JoinPoint target, String name, List<Object> args,
            Optional<Object> result) {

        final AttributeEvent data = new AttributeEvent(stage, target, name, args, result);
        final Event event = new SimpleEvent(LaraIEvent.OnAttribute, data);
        eventController.notifyEvent(event);
    }

    public boolean hasListeners() {
        return eventController.hasListeners();
    }
}
