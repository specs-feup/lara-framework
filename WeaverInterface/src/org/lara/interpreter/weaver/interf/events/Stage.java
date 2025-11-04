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

package org.lara.interpreter.weaver.interf.events;

public enum Stage {

    BEGIN,
    DURING,
    END,;

    /**
     * Retrieves the name of the stage, in lower case
     * 
     * @return the name of the stage, in lower case
     */
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Returns a string representation of the stage, such as 'Stage.BEGIN'
     *
     */
    public String toCode() {
        return Stage.class.getSimpleName() + "." + name();
    }
}