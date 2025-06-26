/**
 * Copyright 2025 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pt.up.fe.specs.jsengine.node;

/**
 * Singleton class representing an undefined value in JavaScript engine integration.
 * <p>
 * This class is used to represent JavaScript's "undefined" in Java. It is primarily utilized by
 * NodeJsEngine as a return value when JavaScript code evaluates to "undefined".
 */
public class UndefinedValue {

    /**
     * A single instance of UndefinedValue to ensure singleton behavior.
     */
    private static final UndefinedValue UNDEFINED = new UndefinedValue();

    /**
     * Retrieves the singleton instance of UndefinedValue.
     *
     * @return the singleton instance of UndefinedValue
     */
    public static UndefinedValue getUndefined() {
        return UNDEFINED;
    }
}
