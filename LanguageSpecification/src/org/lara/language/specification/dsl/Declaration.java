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

import org.lara.language.specification.dsl.types.IType;
import org.lara.language.specification.dsl.types.TypeEnum;

/**
 * A basic class that contains a type and a name
 * 
 * @author tiago
 *
 */
public class Declaration {

    private IType type;
    private String name;

    public Declaration(IType type, String name) {
	setType(type);
	setName(name);
    }

    public IType getType() {
	return type;
    }

    public void setType(IType type) {
	this.type = type;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    @Override
    public String toString() {
	if (type instanceof TypeEnum) {
	    return type.getType();
	}
	return type.getType() + " " + name;
    }
}
