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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.LaraIEvent;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;

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
    }

    /**
     * Trigger an action event
     *
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

    /**
     * Trigger an attribute access event
     *
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
