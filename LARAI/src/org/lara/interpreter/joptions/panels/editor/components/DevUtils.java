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

package org.lara.interpreter.joptions.panels.editor.components;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import tdrc.utils.Pair;
import tdrc.utils.PairList;

public final class DevUtils {

    final static PairList<File, String> DEV_PROJECTS = new PairList<>();
    final static Map<File, Pair<Boolean, Boolean>> PROJECT_EXPAND = new HashMap<>();

    public static void addDevProject(File f, String comment, boolean expand, boolean closeable) {
	DevUtils.DEV_PROJECTS.add(f, comment);
	DevUtils.PROJECT_EXPAND.put(f, Pair.newInstance(expand, closeable));
    }

    public static void getDevProjects(Explorer explorer) {

	for (Pair<File, String> project : DevUtils.DEV_PROJECTS) {
	    File left = project.getLeft();
	    if (left.exists()) {
		Pair<Boolean, Boolean> pair = DevUtils.PROJECT_EXPAND.get(left);
		explorer.addProject(left, project.getRight(),
			pair.getLeft(), pair.getRight());
	    }
	}

    }

}
