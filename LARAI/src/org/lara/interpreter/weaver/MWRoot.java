/**
 * Copyright 2014 SPeCS.
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

package org.lara.interpreter.weaver;

import java.util.List;

import org.lara.interpreter.weaver.interf.JoinPoint;

import pt.up.fe.specs.util.exceptions.NotImplementedException;

/**
 * A join point root encapsulation for maintainability purposes
 * 
 * @author Tiago C.
 *
 */
public class MWRoot extends JoinPoint {

    @Override
    public boolean same(JoinPoint iJoinPoint) {
        return equals(iJoinPoint);
    }

    @Override
    public String get_class() {
        return "MasterWeaverRoot";
    }

    @Override
    protected void fillWithActions(List<String> actions) {
    }

    @Override
    protected void fillWithSelects(List<String> selects) {
    }

    @Override
    protected void fillWithAttributes(List<String> attributes) {

    }

    @Override
    public Object getNode() {
        throw new NotImplementedException(this);
    }

}
