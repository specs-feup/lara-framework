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

import java.util.Arrays;
import java.util.Collection;

import org.suikasoft.jOptions.treenode.ClassesService;

import pt.up.fe.specs.lara.ast.EcmaNode;
import pt.up.fe.specs.lara.ast.UnimplementedNode;

public class LaraNodeClassesService extends ClassesService<EcmaNode> {

    private static final Collection<String> LARA_AST_PACKAGES = Arrays.asList("pt.up.fe.specs.lara.ast.exprs",
            "pt.up.fe.specs.lara.ast.stmts", "pt.up.fe.specs.lara.ast.scripts");

    // private static final Map<String, Function<DataStore, Class<? extends LaraNode>>> CUSTOM_MAPS;
    // static {
    // CUSTOM_MAPS = new HashMap<>();
    // CUSTOM_MAPS.put("Program", value)
    // }

    private static final LaraNodeClassesService STATIC_INSTANCE = new LaraNodeClassesService();

    public LaraNodeClassesService() {
        super(EcmaNode.class, LARA_AST_PACKAGES);
        setDefaultClass(UnimplementedNode.class);
    }

    public static Class<? extends EcmaNode> getNodeClass(String classname) {
        return STATIC_INSTANCE.getClass(classname);
    }

}
