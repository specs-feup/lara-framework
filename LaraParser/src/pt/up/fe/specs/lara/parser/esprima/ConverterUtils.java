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

import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.storedefinition.StoreDefinition;
import org.suikasoft.jOptions.storedefinition.StoreDefinitions;

import com.google.gson.JsonObject;

import pt.up.fe.specs.lara.ast.LaraNode;
import pt.up.fe.specs.lara.ast.UnimplementedNode;
import pt.up.fe.specs.lara.ast.utils.LaraNodeClassesService;

public class ConverterUtils {

    public static Class<? extends LaraNode> getLaraNodeClass(String classname, String location) {
        try {
            return LaraNodeClassesService.getNodeClass(classname);
        } catch (Exception e) {
            String message = "Problems while parsing code";
            if (location != null) {
                message += "at location '" + location.toString() + "'";
            }
            throw new RuntimeException(message, e);
        }

    }

    public static DataStore newLaraDataStore(Class<? extends LaraNode> nodeClass) {
        StoreDefinition nodeKeys = StoreDefinitions.fromInterface(nodeClass);

        // If unimplemented node, can store any key
        boolean isClosedNode = nodeClass.isAssignableFrom(UnimplementedNode.class) ? false : true;

        // Create node data
        return DataStore.newInstance(nodeKeys, isClosedNode);
    }

    public static Class<? extends LaraNode> getLaraNodeClass(JsonObject node) {
        // Get class name
        var className = EsprimaUtils.getType(node);
        // var className = node.get("type");

        if (className == null) {
            throw new RuntimeException("Expected attribute 'type' in node: " + node);
        }

        String location = null;

        var locationObject = node.get("loc");
        if (locationObject != null) {
            location = locationObject.toString();
        }

        return getLaraNodeClass(className, location);
    }

}
