/**
 * Copyright 2017 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.interf.events.data;

import java.util.List;
import java.util.Optional;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

public class AttributeEvent extends BaseEvent {
    private JoinPoint target;
    private String attribute;

    private List<Object> arguments;
    private Optional<Object> result;

    public AttributeEvent(Stage stage, JoinPoint target, String attribute, List<Object> arguments,
            Optional<Object> result) {
        super(stage);
        this.target = target;
        this.attribute = attribute;
        this.arguments = arguments;
        this.result = result;
    }

    /**
     * @return the target
     */
    public JoinPoint getTarget() {
        return target;
    }

    /**
     * @return the attribute
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * @return the arguments
     */
    public List<Object> getArguments() {
        return arguments;
    }

    public Optional<Object> getResult() {
        return result;
    }

}
