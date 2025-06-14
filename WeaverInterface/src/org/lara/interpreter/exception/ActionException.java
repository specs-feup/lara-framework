/**
 * Copyright 2015 SPeCS.
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

package org.lara.interpreter.exception;

import pt.up.fe.specs.tools.lara.exception.BaseException;

public class ActionException extends BaseException {

    private static final long serialVersionUID = 1L;
    private String joinPointName;
    private String actionName;

    public ActionException(String joinPointName, String actionName, Throwable e) {
        super(e);
        setJoinPointName(joinPointName);
        setActionName(actionName);
    }

    @Override
    protected String generateMessage() {
        return "Exception " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        return "in action " + joinPointName + "." + actionName + "";
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName the actionName to set
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return the joinPointName
     */
    public String getJoinPointName() {
        return joinPointName;
    }

    /**
     * @param joinPointName the joinPointName to set
     */
    public void setJoinPointName(String joinPointName) {
        this.joinPointName = joinPointName;
    }
}
