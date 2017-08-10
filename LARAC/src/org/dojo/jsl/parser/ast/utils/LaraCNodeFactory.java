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

package org.dojo.jsl.parser.ast.utils;

import org.dojo.jsl.parser.ast.ASTIdentifier;
import org.dojo.jsl.parser.ast.LARAEcmaScriptConstants;

public class LaraCNodeFactory {

    public static ASTIdentifier newIdentifier(String name) {
	ASTIdentifier id = new ASTIdentifier(LARAEcmaScriptConstants.IDENTIFIER_NAME);
	id.setName(name);
	return id;
    }
}
