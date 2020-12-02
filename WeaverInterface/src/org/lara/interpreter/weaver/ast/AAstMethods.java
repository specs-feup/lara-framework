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

import java.util.ArrayList;
import java.util.List;

import org.lara.interpreter.weaver.interf.JoinPoint;
import org.lara.interpreter.weaver.interf.WeaverEngine;

/**
 * Abstract implementation of AstMethods. It takes into account conversions that might be needed to communicate more
 * seamlessly between Java and the JavaScript engine.
 * 
 * @author Joao Bispo
 *
 * @param <T>
 *            the base class of the AST node
 */
public abstract class AAstMethods<T> implements AstMethods {

    private final WeaverEngine weaverEngine;

    public AAstMethods(WeaverEngine weaverEngine) {
        this.weaverEngine = weaverEngine;
    }

    @Override
    public Object toJavaJoinPoint(Object node) {
        return toJs(toJavaJoinPointImpl(getNodeClass().cast(node)));
    }

    @Override
    public Object getJoinPointName(Object node) {
        return toJs(getJoinPointNameImpl(getNodeClass().cast(node)));
    }

    @Override
    public Object getChildren(Object node) {
        var children = getChildrenImpl(getNodeClass().cast(node));
        return toJs(children);
    }

    @Override
    public Object getNumChildren(Object node) {
        var numChildren = getNumChildrenImpl(getNodeClass().cast(node));
        return toJs(numChildren);
    }

    @Override
    public Object getScopeChildren(Object node) {
        var scopeChildren = getScopeChildrenImpl(getNodeClass().cast(node));
        return toJs(scopeChildren);
    }

    @Override
    public Object getParent(Object node) {
        var scopeChildren = getParentImpl(getNodeClass().cast(node));
        return toJs(scopeChildren);
    }

    private Object toJs(Object object) {
        return weaverEngine.getScriptEngine().toJs(object);
    }

    @Override
    public Object getDescendants(Object node) {
        var descendants = new ArrayList<Object>();
        getDescendantsPrivate(getNodeClass().cast(node), descendants);

        return toJs(descendants);
    }

    private void getDescendantsPrivate(T node, List<Object> descendants) {
        var children = getChildrenImpl(node);
        for (var child : children) {
            descendants.add(child);
            getDescendantsPrivate(getNodeClass().cast(child), descendants);
        }
    }

    public abstract Class<T> getNodeClass();

    protected abstract JoinPoint toJavaJoinPointImpl(T node);

    protected abstract String getJoinPointNameImpl(T node);

    protected abstract Object[] getChildrenImpl(T node);

    protected abstract Object[] getScopeChildrenImpl(T node);

    protected abstract Object getParentImpl(T node);

    // protected abstract Integer getNumChildrenImpl(T node);

    protected Integer getNumChildrenImpl(T node) {
        return getChildrenImpl(node).length;
    }
}
