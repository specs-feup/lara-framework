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

public class EsprimaDataParsers {

    public static Class<? extends LaraNode> getLaraNodeClass(JsonObject node) {
        // Get class name
        var className = node.get("type");

        if (className == null) {
            throw new RuntimeException("Expected attribute 'type' in node: " + node);
        }

        String location = null;

        var locationObject = node.get("loc");
        if (locationObject != null) {
            location = locationObject.toString();
        }

        return ConverterUtils.getLaraNodeClass(className.getAsString(), location);
    }

    public static DataStore newLaraDataStore(Class<? extends LaraNode> nodeClass) {
        StoreDefinition nodeKeys = StoreDefinitions.fromInterface(nodeClass);

        // If unimplemented node, can store any key
        boolean isClosedNode = nodeClass.isAssignableFrom(UnimplementedNode.class) ? false : true;

        // Create node data
        return DataStore.newInstance(nodeKeys, isClosedNode);
    }

    public static DataStore parseNodeData(JsonObject node, EsprimaConverterData data) {
        // Get class name
        // var className = node.get("type");
        //
        // if (className == null) {
        // throw new RuntimeException("Expected attribute 'type' in node: " + node);
        // }
        //
        // Class<? extends LaraNode> nodeClass = null;
        // try {
        // nodeClass = LaraNodeClassesService.getNodeClass(className.getAsString());
        // } catch (Exception e) {
        // String message = "Problems while parsing code";
        // var location = node.get("loc");
        // if (location != null) {
        // message += "at location '" + location.toString() + "'";
        // }
        // throw new RuntimeException(message, e);
        // }

        var nodeClass = getLaraNodeClass(node);

        // StoreDefinition nodeKeys = StoreDefinitions.fromInterface(nodeClass);
        //
        // // Create node data
        // DataStore nodeData = DataStore.newInstance(nodeKeys, true);

        DataStore nodeData = newLaraDataStore(nodeClass);

        // Populate node data
        nodeData.add(LaraNode.CONTEXT, data.get(EsprimaConverterData.LARA_CONTEXT));

        return nodeData;
    }

    public static DataStore parseProgramData(JsonObject node, EsprimaConverterData data) {
        DataStore nodeData = parseNodeData(node, data);

        // Extract program specific information

        return nodeData;
    }

}
