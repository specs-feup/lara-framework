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

package org.lara.interpreter.weaver.ast;

/**
 * Represents the basic interface that the AST nodes must support.
 * 
 * @author Joao Bispo
 *
 */
public interface AstMethods {

    /**
     * Transforms the given AST node into a join point.
     * 
     * @param node
     * @return the join point corresponding to this node
     */
    Object toJavaJoinPoint(Object node);

    /**
     * Maps an AST node to the corresponding Common Language Specification join point name.
     * 
     * @param node
     * @return
     */
    Object getJoinPointName(Object node);

    /**
     * 
     * @return a JavaScript array with the children of this AST node
     */
    Object getChildren(Object node);

    /**
     * 
     * @return the number of children
     */
    Object getNumChildren(Object node);

    /**
     * 
     * @return a JavaScript array with the children inside the scope declared by this AST node (e.g., body of a loop),
     *         or empty array if node does not have a scope
     */
    Object getScopeChildren(Object node);

    /**
     * 
     * @return the parent of this AST node
     */
    Object getParent(Object node);

    /**
     *
     * @param node
     * @return a JavaScript array with the descendants of this AST node
     */
    Object getDescendants(Object node);
}
