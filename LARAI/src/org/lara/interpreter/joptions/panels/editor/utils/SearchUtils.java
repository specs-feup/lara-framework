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

package org.lara.interpreter.joptions.panels.editor.utils;

import java.awt.Component;
import java.awt.Container;

public class SearchUtils {
    public static <C> C findFirstComponentOfType(Component comp, Class<C> cClass) {
	if (cClass.isInstance(comp)) {
	    return cClass.cast(comp);
	}

	if (comp instanceof Container) {
	    Container container = (Container) comp;
	    for (int i = 0; i < container.getComponentCount(); i++) {
		C comp2 = findFirstComponentOfType(container.getComponent(i), cClass);
		if (comp2 != null) {
		    return comp2;
		}
	    }
	}
	return null;
    }
}
