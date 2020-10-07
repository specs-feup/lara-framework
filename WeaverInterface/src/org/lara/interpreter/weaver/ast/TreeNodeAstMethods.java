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

import java.util.function.Function;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;

import pt.up.fe.specs.util.treenode.ATreeNode;

public class TreeNodeAstMethods<T extends ATreeNode<T>> extends AAstMethods<T> {

    private final Class<T> nodeClass;
    private final Function<T, JoinPoint> toJoinPointFunction;
    private final Function<T, String> toJoinPointNameFunction;

    public TreeNodeAstMethods(WeaverEngine engine, Class<T> nodeClass, Function<T, JoinPoint> toJoinPointFunction,
            Function<T, String> toJoinPointNameFunction) {

        super(engine);
        this.nodeClass = nodeClass;
        this.toJoinPointFunction = toJoinPointFunction;
        this.toJoinPointNameFunction = toJoinPointNameFunction;
    }

    @Override
    public Class<T> getNodeClass() {
        return nodeClass;
    }

    @Override
    protected JoinPoint toJavaJoinPointImpl(T node) {
        return toJoinPointFunction.apply(node);
    }

    @Override
    protected Object[] getChildrenImpl(T node) {
        var children = node.getChildren().toArray();
        return children;
    }

    @Override
    protected Integer getNumChildrenImpl(T node) {
        return node.getNumChildren();
    }

    @Override
    protected String getJoinPointNameImpl(T node) {
        return toJoinPointNameFunction.apply(node);
    }

}
