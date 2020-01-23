/**
 * Copyright 2020 SPeCS.
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

package pt.up.fe.specs.lara.parser.esprima;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;

public class EsprimaUtils {

    /**
     * Set of common fields that are known not to represent children that can be safely ignored
     */
    private static final Set<String> NOT_CHILDREN_FIELDS = Set.of("type", "range", "loc");

    public static String getType(JsonObject node) {

        // Get class name
        var className = node.get("type");

        if (className == null) {
            throw new RuntimeException("Expected attribute 'type' in node: " + node);
        }

        return className.getAsString();
    }

    /**
     * Returns the children of the given node.
     * 
     * @deprecated Not working, instead acquiring children during node parsing
     * @param node
     * @return
     */
    @Deprecated
    public static List<JsonObject> getChildren(JsonObject node) {
        // Iterates over all values of the node, looking for objects or arrays that contain objects with the field
        // 'type'

        List<JsonObject> children = new ArrayList<>();

        for (var entry : node.entrySet()) {
            if (NOT_CHILDREN_FIELDS.contains(entry.getKey())) {
                continue;
            }

            var value = entry.getValue();

            // JsonObject
            if (value.isJsonObject()) {
                var jsonObject = value.getAsJsonObject();
                if (isEsprimaNode(jsonObject)) {
                    children.add(jsonObject);

                    if (jsonObject.get("type").getAsString() == "Line") {
                        System.out.println("SINGLE: ");
                    }
                }

                continue;

                // // Check if object has a 'type' field
                // var type = value.getAsJsonObject().get("type");
                //
                // // If type is present, has single child
                // if (type != null) {
                // return Arrays.asList(value.getAsJsonObject());
                // }
                //
                // // Otherwise, continue
                // continue;
            }

            // Array
            if (value.isJsonArray()) {
                var jsonArray = value.getAsJsonArray();

                for (var element : jsonArray) {
                    var jsonValue = element.getAsJsonObject();
                    if (!EsprimaUtils.isEsprimaNode(jsonValue)) {
                        continue;
                    }
                    if (jsonValue.get("type").getAsString() == "Line") {
                        System.out.println("ARRAY: ASDASDASD");
                    }
                    children.add(jsonValue);
                }

                /*
                if (jsonArray.size() == 0) {
                    continue;
                }
                
                var firstElement = jsonArray.get(0);
                
                if (!firstElement.isJsonObject()) {
                    continue;
                }
                
                var jsonValue = firstElement.getAsJsonObject();
                
                if (!EsprimaUtils.isEsprimaNode(jsonValue)) {
                    continue;
                }
                
                // Found array of children
                for (var element : jsonArray) {
                    children.add(element.getAsJsonObject());
                }
                */

                continue;
            }
        }

        // Return found children
        return children;

    }

    public static boolean isEsprimaNode(JsonObject value) {
        // Check if object has a 'type' field
        var type = value.get("type");

        return type != null;
    }

}
