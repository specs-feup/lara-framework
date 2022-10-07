/**
 * Copyright 2022 SPeCS.
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

package org.lara.interpreter.utils;

import org.lara.interpreter.weaver.interf.AGear;
import org.lara.interpreter.weaver.interf.WeaverEngine;
import org.lara.interpreter.weaver.interf.events.data.ActionEvent;

public class JsGear extends AGear {

    private Object jsOnAction;

    public JsGear() {
        this.jsOnAction = null;
    }

    /**
     * 
     * @param jsOnAction
     * @return true if the given object is a function and could be set, false otherwise
     */
    public boolean setJsOnAction(Object jsOnAction) {
        var engine = WeaverEngine.getThreadLocalWeaver().getScriptEngine();

        if (!engine.isFunction(jsOnAction)) {
            return false;
        }

        this.jsOnAction = jsOnAction;
        return true;
    }

    @Override
    public void onAction(ActionEvent data) {
        if (jsOnAction == null) {
            super.onAction(data);
            return;
        }

        var engine = WeaverEngine.getThreadLocalWeaver().getScriptEngine();
        engine.call(jsOnAction, data);
    }
}
