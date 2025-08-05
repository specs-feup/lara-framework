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

package org.lara.interpreter.weaver.interf.events.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Args {

    private final Map<String, Object> arguments;

    public Args() {
        arguments = new LinkedHashMap<>();
    }

    public Args(String[] params, Object[] args) {
        arguments = new LinkedHashMap<>();
        allAll(params, args);
    }

    /**
     * Add all input arguments
     * 
     * @param params
     *               the parameter associated to the name
     * @param args
     *               the arguments related to the parameters
     * @return true if successfully added, false if the number of parameters is
     *         less than the number of arguments
     */
    private boolean allAll(String[] params, Object[] args) {
        if (params.length < params.length) {
            return false;
        }

        for (int i = 0; i < params.length; i++) {
            final String param = params[i];
            Object arg = null;
            if (i < args.length) {
                arg = args[i];
            }
            arguments.put(param, arg);
        }
        return true;
    }

    /**
     * Returns the list of
     * 
     * @param param
     * @return
     */
    public Object getArgument(String param) {
        return arguments.get(param);
    }

    public Object[] getArguments() {
        return arguments.values().toArray();
    }

    public String[] getParameters() {
        final Set<String> keySet = arguments.keySet();
        final String[] params = new String[keySet.size()];
        return keySet.toArray(params);
    }

    public boolean isEmpty() {
        return arguments.isEmpty();
    }
}
