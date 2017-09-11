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

package org.lara.interpreter.weaver.generator.options.utils;

import pt.up.fe.specs.util.SpecsLogs;

public interface ClassProvider {

    Class<?> getCustomClass();

    static ClassProvider newInstance(Class<?> aClass) {
	return new GenericClassProvider(aClass);
    }

    static ClassProvider newInstance(String s) {
	if (s == null) {
	    return newInstance();
	}

	try {
	    return new GenericClassProvider(Class.forName(s));
	} catch (ClassNotFoundException e) {
	    SpecsLogs.msgWarn("Error message:\n", e);
	    SpecsLogs.msgWarn("Using Object instead");
	    return new GenericClassProvider(Object.class);
	}
    }

    static ClassProvider newInstance() {
	return new GenericClassProvider(Object.class);
    }
}
