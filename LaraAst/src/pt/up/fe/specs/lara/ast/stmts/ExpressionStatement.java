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

package pt.up.fe.specs.lara.ast.stmts;

import java.util.Collection;
import java.util.Optional;

import org.suikasoft.jOptions.Datakey.DataKey;
import org.suikasoft.jOptions.Datakey.KeyFactory;
import org.suikasoft.jOptions.Interfaces.DataStore;

import pt.up.fe.specs.lara.ast.LaraNode;
import pt.up.fe.specs.lara.ast.exprs.Expression;

public class ExpressionStatement extends Statement {

    public static final DataKey<Optional<String>> DIRECTIVE = KeyFactory.optional("directive");

    public ExpressionStatement(DataStore data, Collection<? extends LaraNode> children) {
        super(data, children);
    }

    public Expression getExpression() {
        return getChild(Expression.class, 0);
    }

    public boolean isDirective() {
        return get(DIRECTIVE).isPresent();
    }

    @Override
    public String getCode() {
        if (isDirective()) {
            return "\"" + get(DIRECTIVE).get() + "\";";
        }

        return getChild(0).getCode() + ";";
        // return getExpression().getCode() + ";";
    }

}
