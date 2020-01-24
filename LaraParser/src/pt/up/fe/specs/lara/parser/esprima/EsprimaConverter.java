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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.treenode.converter.NodeDataParser;

import com.google.gson.JsonObject;

import pt.up.fe.specs.lara.ast.LaraContext;
import pt.up.fe.specs.lara.ast.LaraNode;
import pt.up.fe.specs.lara.ast.UnimplementedNode;
import pt.up.fe.specs.lara.parser.esprima.parsers.GeneralParsers;
import pt.up.fe.specs.util.SpecsSystem;

/***
 * Converts the Esprima AST to the LARA AST.
 * 
 * @author JoaoBispo
 *
 */
public class EsprimaConverter {

    private final LaraContext laraContext;
    private final NodeDataParser dataParser;

    public EsprimaConverter(LaraContext laraContext) {
        this.laraContext = laraContext;

        // Initialize data parsers
        Method defaultMethod = SpecsSystem.findMethod(GeneralParsers.class, "parseNodeData", JsonObject.class,
                EsprimaConverterData.class);

        this.dataParser = new NodeDataParser(defaultMethod, Arrays.asList(GeneralParsers.class));
    }

    private EsprimaConverterData newParserData() {

        var parserData = new EsprimaConverterData();

        // Set context
        parserData.set(EsprimaConverterData.LARA_CONTEXT, laraContext);

        // Initialize children
        parserData.set(EsprimaConverterData.FOUND_CHILDREN, new ArrayList<>());

        return parserData;
    }

    public LaraNode parse(JsonObject node) {
        // Get DataStore
        // Arg1: JsonObject node
        // Arg2: EsprimaConverterData data

        var nodeType = EsprimaUtils.getType(node);

        var parserData = newParserData();

        // Return: DataStore of the LaraNode
        DataStore nodeData = (DataStore) dataParser.parse(nodeType, node, parserData);

        // Get children
        var esprimaChildren = parserData.get(EsprimaConverterData.FOUND_CHILDREN);

        // var esprimaChildren = EsprimaUtils.getChildren(node);
        // System.out.println("CHILDREN: " + esprimaChildren);
        // Convert each children
        var laraChildren = esprimaChildren.stream()
                .map(this::parse)
                .collect(Collectors.toList());

        // Invoke constructor to build node and return it
        var nodeClass = ConverterUtils.getLaraNodeClass(node);
        // System.out.println("NODE CLASS:" + nodeClass);
        var laraNode = SpecsSystem.newInstance(nodeClass, nodeData, laraChildren);

        // If node is UnimplementedNode, add field 'type'
        if (laraNode instanceof UnimplementedNode) {
            laraNode.set(UnimplementedNode.TYPE, nodeType);
        }

        // System.out.println("LARA NODE: " + laraNode);

        return laraNode;
    }

}
