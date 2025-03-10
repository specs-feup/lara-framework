/**
 * Copyright 2016 SPeCS.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

package org.lara.language.specification.dsl;

import java.util.Optional;

public class Select extends BaseNode implements Comparable<Select> {

    private JoinPointClass clazz;
    private String alias;
    private JoinPointClass selector;

    public Select(JoinPointClass clazz) {
        this(clazz, null);
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

    public Optional<String> getAlias() {
        return Optional.ofNullable(alias);
    }

    public void setAlias(String alias) {
        if (alias != null && alias.isBlank()) {
            this.alias = null;
        } else {
            this.alias = alias;
        }
    }

    @Override
    public String toString() {
        return clazz.getName() + getAlias().map(alias -> " as " + alias).orElse("");
        //return clazz.getName() + (!alias.isEmpty() ? " as " + alias : "");
    }

    public JoinPointClass getSelector() {
        return selector;
    }

    public void setSelector(JoinPointClass selector) {
        this.selector = selector;
    }

    @Override
    public int compareTo(Select o) {
        return getClazz().compareTo(o.getClazz());
    }

    /**
     * @return the alias of this select, or the class name if no alias is defined
     */
    public String getSelectName() {
        return getAlias().orElse(clazz.getName());
        /*
        if (!alias.isEmpty()) {
            return alias;
        }

        return clazz.getName();
         */
    }
}
