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
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.interpreter.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;

import com.google.common.base.Preconditions;

/**
 * Class to help the implementation of the 'def' action.
 * 
 * @author JoaoBispo
 *
 */
public class DefMap<JP> {

    // private final Map<DataKey<?>, BiConsumer<JP, ?>> defMap;
    private final Class<JP> joinpointClass;
    private final Map<String, BiConsumer<JP, ? extends Object>> consumers;
    private final Map<String, DataKey<?>> keys;

    public DefMap(Class<JP> joinpointClass) {
        this.joinpointClass = joinpointClass;
        this.consumers = new HashMap<>();
        keys = new HashMap<>();
    }

    public Set<String> keys() {
        return keys.keySet();
    }

    public void addBool(String attribute, BiConsumer<JP, Boolean> def) {
        add(KeyFactory.bool(attribute), def);
    }

    public void addInteger(String attribute, BiConsumer<JP, Integer> def) {
        // We are not using DataStores, default value should not be relevant
        add(KeyFactory.integer(attribute, 0), def);
    }

    public <T> void add(DataKey<T> attribute, BiConsumer<JP, ? extends T> def) {
        consumers.put(attribute.getName(), def);
        keys.put(attribute.getName(), attribute);
    }

    public boolean hasAttribute(String attribute) {
        return consumers.get(attribute) != null;
    }

    public void apply(String attribute, Object joinpoint, Object value) {
        BiConsumer<JP, ? extends Object> rawConsumer = consumers.get(attribute);
        if (rawConsumer == null) {
            throw new RuntimeException("Could not find attribute '" + attribute + "':" + consumers);
        }

        // Check that we are on the correct joinpoint
        Preconditions.checkArgument(joinpointClass.isInstance(joinpoint),
                "Expected to be called with a joinpoint of type '" + joinpointClass.getSimpleName()
                        + "' instead received a " + joinpoint.getClass().getSimpleName());

        // All elements on the map are guaranteed to be BiConsumer<?, ?>
        @SuppressWarnings("unchecked")
        BiConsumer<Object, Object> consumer = (BiConsumer<Object, Object>) rawConsumer;
        DataKey<?> key = keys.get(attribute);

        Object parsedValue = parseValue(key, value);

        consumer.accept(joinpoint, parsedValue);
    }

    private Object parseValue(DataKey<?> key, Object value) {
        Class<?> valueClass = key.getValueClass();

        // Check if object is an instance of the required class
        if (valueClass.isInstance(value)) {
            return value;
        }

        // If value is a String, decode it
        if (value instanceof String) {
            return key.getDecoder().get().decode((String) value);
        }

        throw new RuntimeException(
                "Attribute expects a value of type '" + valueClass.getSimpleName() + "', got a '"
                        + value.getClass().getSimpleName() + "'");
    }
}
