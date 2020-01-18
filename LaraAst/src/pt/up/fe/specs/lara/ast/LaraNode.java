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

package pt.up.fe.specs.lara.ast;

import java.util.Collection;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.treenode.DataNode;

/**
 * Represents a node of the LARA AST.
 * 
 * @author JoaoBispo
 *
 */
public abstract class LaraNode extends DataNode<LaraNode> {

    // DATAKEYS BEGIN

    /**
     * Global object with information available to all nodes.
     */
    public final static DataKey<LaraContext> CONTEXT = KeyFactory.object("context", LaraContext.class);

    // DATAKEYS END

    public LaraNode(DataStore data, Collection<? extends LaraNode> children) {
        super(data, children);
    }

    @Override
    protected LaraNode getThis() {
        return this;
    }

    @Override
    public String toContentString() {
        return getData().toInlinedString();
    }
}
