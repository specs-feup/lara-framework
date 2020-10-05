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

package org.lara.interpreter.weaver.interf;

/**
 * Represents the basic interface that the AST nodes must support.
 * 
 * @author Joao Bispo
 *
 */
public interface AstMethods<T> {

    /**
     * Transforms the given AST node into a join point.
     * 
     * @param node
     * @return the join point corresponding to this node
     */
    JoinPoint toJoinPoint(T node);

    /**
     * 
     * @return an array with the children of this AST node
     */
    T[] getChildren(T node);

    /**
     * 
     * @return the number of children
     */
    default int numChildren(T node) {
        return getChildren(node).length;
    }

}
