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
import java.util.List;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;
import org.suikasoft.jOptions.treenode.DataNode;

import pt.up.fe.specs.lara.ast.utils.SourceLocation;
import pt.up.fe.specs.util.SpecsLogs;
import pt.up.fe.specs.util.exceptions.NotImplementedException;

/**
 * Represents a node of the LARA AST.
 * 
 * @author JoaoBispo
 *
 */
public abstract class EcmaNode extends DataNode<EcmaNode> {

    // DATAKEYS BEGIN

    /**
     * Global object with information available to all nodes.
     */
    public final static DataKey<EcmaContext> CONTEXT = KeyFactory.object("context", EcmaContext.class);

    /**
     * A list containing two integers representing the range of this node, or an empty list if this information is not
     * available.
     */
    public final static DataKey<List<Integer>> RANGE = KeyFactory.list("range", Integer.class);

    /**
     * The source location of this node, if available.
     */
    public final static DataKey<SourceLocation> LOC = KeyFactory.object("loc", SourceLocation.class)
            .setDefault(() -> SourceLocation.getUnknownSourceLocation());

    // DATAKEYS END

    public EcmaNode(DataStore data, Collection<? extends EcmaNode> children) {
        super(data, children);
    }

    @Override
    protected EcmaNode getThis() {
        return this;
    }

    @Override
    protected Class<EcmaNode> getBaseClass() {
        return EcmaNode.class;
    }

    @Override
    public String toContentString() {
        return getData().toInlinedString();
    }

    @Override
    public String toString() {
        return toContentString();
    }

    public String getCode() {
        SpecsLogs.info("getCode() not implemented for this node: " + this);
        throw new NotImplementedException(getClass());
    }
}
