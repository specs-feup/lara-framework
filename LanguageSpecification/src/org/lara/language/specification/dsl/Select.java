/**
 * Copyright 2016 SPeCS.
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

package org.lara.language.specification.dsl;

public class Select extends BaseNode {

    private JoinPointClass clazz;
    private String alias;
    private JoinPointClass selector;

    public Select(JoinPointClass clazz) {
        this(clazz, "");
    }

    public Select(JoinPointClass clazz, String alias) {
        this.setClazz(clazz);
        this.setAlias(alias);
    }

    public JoinPointClass getClazz() {
        return clazz;
    }

    public void setClazz(JoinPointClass clazz) {
        this.clazz = clazz;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return clazz.getName() + (!alias.isEmpty() ? " as " + alias : "");
    }

    public JoinPointClass getSelector() {
        return selector;
    }

    public void setSelector(JoinPointClass selector) {
        this.selector = selector;
    }
}
