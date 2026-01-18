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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.interf.events.data;

import java.util.List;
import java.util.Optional;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.events.Stage;

import tdrc.utils.StringUtils;

public class ActionEvent extends BaseEvent {

    private String name;
    private JoinPoint joinPoint;
    private List<Object> arguments;
    private Optional<Object> result;

    public ActionEvent(Stage stage, String actionName, JoinPoint joinPoint, List<Object> arguments,
            Optional<Object> result) {
        super(stage);
        setName(actionName);
        setJoinPoint(joinPoint);
        this.arguments = arguments;
        this.result = result;
    }

    @Override
    public String toString() {
        String ret = super.toString();
        ret += ", action " + name;
        ret += ", in join point " + joinPoint.get_class();
        ret += ", with arguments: (" + StringUtils.join(arguments, ",") + ")";
        if (result.isPresent()) {
            ret += ", result: " + result.get();
        }
        return ret;
    }

    /**
     * @return the arguments
     */
    public List<Object> getArguments() {
        return arguments;
    }

    /**
     * @param arguments the arguments to set
     */
    protected void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }

    /**
     * @return the joinPoint
     */
    public JoinPoint getJoinPoint() {
        return joinPoint;
    }

    /**
     * @param joinPoint the joinPoint to set
     */
    protected void setJoinPoint(JoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return name;
    }

    /**
     * @param actionName the actionName to set
     */
    protected void setName(String actionName) {
        name = actionName;
    }

}
