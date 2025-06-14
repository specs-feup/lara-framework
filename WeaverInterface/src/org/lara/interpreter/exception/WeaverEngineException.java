/**
 * Copyright 2016 SPeCS.
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

/**
 * @author tiago
 *
 */
public class WeaverEngineException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String situation;

    /**
     * 
     * @param situation the situation in which the exception was throwned. e.g.:
     *                  initializing, closing
     * @param e the cause of this exception
     */
    public WeaverEngineException(String situation, Throwable e) {
        super(e);
        this.situation = situation;
    }

    /**
     * @see pt.up.fe.specs.tools.lara.exception.BaseException#generateMessage()
     */
    @Override
    protected String generateMessage() {
        return "exception " + generateSimpleMessage();
    }

    /**
     * @see pt.up.fe.specs.tools.lara.exception.BaseException#generateSimpleMessage()
     */
    @Override
    protected String generateSimpleMessage() {
        return "when " + this.situation + " the weaver";
    }

}
