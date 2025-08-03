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

import org.lara.interpreter.weaver.interf.events.LaraIEvent;
import org.lara.interpreter.weaver.interf.events.Stage;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;
import org.lara.interpreter.weaver.interf.events.data.AttributeEvent;

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

        actionsMap.putAction(LaraIEvent.OnAction, event -> actionEvent(event.getData()));
        actionsMap.putAction(LaraIEvent.OnAttribute, event -> attributeEvent(event.getData()));

        return actionsMap;
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
     * Method to execute when an action is executed ({@link Stage#BEGIN} and
     * {@link Stage#AFTER})
     * 
     * @param data
     *             the event data
     */
    public void onAction(ActionEvent data) {
        // Do nothing as default
    }

    /**
     * Method to execute when an attribute is accessed ({@link Stage#BEGIN} and
     * {@link Stage#AFTER})
     * 
     * @param data
     *             the event data
     */
    public void onAttribute(AttributeEvent data) {
        // Do nothing as default
    }

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
     * This method will be invoked every time {@link WeaverEngine#run} is invoked.
     * By default it does nothing.
     */
    public void reset() {

    }
}
