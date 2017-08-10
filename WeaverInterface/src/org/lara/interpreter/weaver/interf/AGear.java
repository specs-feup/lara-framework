/**
 * Copyright 2013 SPeCS Research Group.
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

package org.lara.interpreter.weaver.interf;

import org.lara.interpreter.exception.LaraIException;
import org.lara.interpreter.weaver.interf.events.LaraIEvent;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyEvent;
import org.lara.interpreter.weaver.interf.events.data.ApplyIterationEvent;
import org.lara.interpreter.weaver.interf.events.data.AspectEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;
import org.lara.interpreter.weaver.interf.events.data.JoinPointEvent;
import org.lara.interpreter.weaver.interf.events.data.SelectEvent;
import org.lara.interpreter.weaver.interf.events.data.WeaverEvent;

import pt.up.fe.specs.util.events.ActionsMap;
import pt.up.fe.specs.util.events.EventReceiverTemplate;

/**
 * Abstract base Class for implementing a new Gear for LARAI.
 * 
 * @author Joao Bispo
 * 
 */
public abstract class AGear extends EventReceiverTemplate {

    private final ActionsMap actionsMap;

    /*
     * Allows (de)activation of the gear
     */
    private boolean active;

    public AGear() {
        setActive(true);
        actionsMap = newActionsMap();
    }

    /**
     * 
     */
    private ActionsMap newActionsMap() {
        final ActionsMap actionsMap = new ActionsMap();

        // actionsMap.putAction(LaraIEvent.OnWeaver, newOnWeaver());
        // actionsMap.putAction(LaraIEvent.OnWeaver, this::performAction);
        actionsMap.putAction(LaraIEvent.OnWeaver, event -> weaverEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnAspect, event -> aspectEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnSelect, event -> selectEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnJoinPoint, event -> joinPointEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnApply, event -> applyEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnAction, event -> actionEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnAttribute, event -> attributeEvent(event.getData()));

        return actionsMap;
    }

    private void weaverEvent(Object data) {
        if (active) {
            onWeaver((WeaverEvent) data);
        }
    }

    private void aspectEvent(Object data) {
        if (active) {
            onAspect((AspectEvent) data);
        }
    }

    private void selectEvent(Object data) {
        if (active) {
            onSelect((SelectEvent) data);
        }
    }

    private void joinPointEvent(Object data) {
        if (active) {
            onJoinPoint((JoinPointEvent) data);
        }
    }

    private void applyEvent(Object data) {
        if (active) {
            if (data instanceof ApplyEvent) {

                onApply((ApplyEvent) data);
            } else if (data instanceof ApplyIterationEvent) {

                onApply((ApplyIterationEvent) data);
            } else {

                throw new LaraIException("Wrong type of data used in an apply event. Used " + data.getClass()
                        + ", expected: " + ApplyEvent.class + " or " + ApplyIterationEvent.class + ".");
            }
        }
    }

    private void actionEvent(Object data) {
        if (active) {
            onAction((ActionEvent) data);
        }
    }

    private void attributeEvent(Object data) {
        if (active) {
            onAttribute((AttributeEvent) data);
        }
    }

    /**
     * Handler to execute when the weaver is initialized, and its execution is finalized ({@link Stage#BEGIN} and
     * {@link Stage#AFTER})
     * 
     * @param data
     *            the event data
     */
    public void onWeaver(WeaverEvent data) {
        // Do nothing as default
    }

    /**
     * Handler to execute when an aspect is called ({@link Stage#BEGIN}, {@link Stage#AFTER} and {@link Stage#EXCEPTION}
     * )
     * 
     * @param data
     *            the event data
     */
    public void onAspect(AspectEvent data) {
        // Do nothing as default
    }

    /**
     * Handler to execute when a select occurs({@link Stage#BEGIN} and {@link Stage#AFTER})
     * 
     * @param data
     *            the event data
     */
    public void onSelect(SelectEvent data) {
        // Do nothing as default
    }

    /**
     * Method to execute when a join point is created({@link Stage#BEGIN} and {@link Stage#AFTER})
     * 
     * @param data
     *            the event data
     */
    public void onJoinPoint(JoinPointEvent data) {
        // Do nothing as default
    }

    /**
     * Method to execute when an apply occurs({@link Stage#BEGIN}, and {@link Stage#AFTER})
     * 
     * @param data
     *            the event data
     */
    public void onApply(ApplyEvent data) {
        // Do nothing as default
    }

    /**
     * Method to execute when an iteration of an apply occurs( {@link Stage#BEGIN_ITERATION} and
     * {@link Stage#END_ITERATION} )
     * 
     * @param data
     *            the event data
     */
    public void onApply(ApplyIterationEvent data) {
        // Do nothing as default
    }

    /**
     * Method to execute when an action is executed ({@link Stage#BEGIN} and {@link Stage#AFTER})
     * 
     * @param data
     *            the event data
     */
    public void onAction(ActionEvent data) {
        // Do nothing as default
    }

    /**
     * Method to execute when an attribute is accessed ({@link Stage#BEGIN} and {@link Stage#AFTER})
     * 
     * @param data
     *            the event data
     */
    public void onAttribute(AttributeEvent data) {
        // Do nothing as default
    }

    // private void performAction(Event event) {
    // onWeaver((WeaverEvent) event.getData());
    // }

    // private EventAction newOnWeaver() {
    // return event -> onWeaver((WeaverEvent) event.getData());
    //
    // return new EventAction() {
    //
    // @Override
    // public void performAction(Event event) {
    // WeaverEvent data = (WeaverEvent) event.getData();
    // onWeaver(data);
    // }
    // };
    //
    // }

    // private void performAction(Event event) {
    // onWeaver((WeaverEvent) event.getData());
    // }
    //
    // private EventAction newOnAspect() {
    // return new EventAction() {
    //
    // @Override
    // public void performAction(Event event) {
    // AspectEvent data = (AspectEvent) event.getData();
    // onAspect(data);
    // }
    // };
    // }
    //
    // private EventAction newOnSelect() {
    // return new EventAction() {
    //
    // @Override
    // public void performAction(Event event) {
    // SelectEvent data = (SelectEvent) event.getData();
    // onSelect(data);
    // }
    // };
    // }
    //
    // private EventAction newOnJoinPoint() {
    // return new EventAction() {
    //
    // @Override
    // public void performAction(Event event) {
    // JoinPointEvent data = (JoinPointEvent) event.getData();
    // onJoinPoint(data);
    // }
    // };
    // }
    //
    // private EventAction newOnApply() {
    // return new EventAction() {
    //
    // @Override
    // public void performAction(Event event) {
    // ApplyEvent data = (ApplyEvent) event.getData();
    // onApply(data);
    // }
    // };
    // }
    //
    // private EventAction newOnAction() {
    // return new EventAction() {
    //
    // @Override
    // public void performAction(Event event) {
    // ActionEvent data = (ActionEvent) event.getData();
    // onAction(data);
    // }
    // };
    // }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.up.fe.specs.util.Events.EventReceiverTemplate#getActionsMap()
     */
    @Override
    protected ActionsMap getActionsMap() {
        return actionsMap;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This method will be invoked every time {@link WeaverEngine#begin} is invoked. By default it does nothing.
     */
    public void reset() {

    }
}
