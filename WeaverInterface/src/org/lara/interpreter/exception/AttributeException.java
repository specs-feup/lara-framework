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

public class AttributeException extends BaseException {

    private static final long serialVersionUID = 1L;
    private String joinPointName;
    private String attribute;

    public AttributeException(String joinPointName, String attribute, Throwable e) {
        super(e);
        setJoinPointName(joinPointName);
        setAttributeName(attribute);
    }

    @Override
    protected String generateMessage() {
        return "Exception " + generateSimpleMessage();
    }

    @Override
    protected String generateSimpleMessage() {
        return "in attribute " + joinPointName + "." + attribute + "";
    }

    /**
     * @return the actionName
     */
    public String getAttributeName() {
        return attribute;
    }

    /**
     * @param actionName the actionName to set
     */
    public void setAttributeName(String attribute) {
        this.attribute = attribute;
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
