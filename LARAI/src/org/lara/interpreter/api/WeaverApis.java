/**
 * Copyright 2017 SPeCS.
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

package org.lara.interpreter.api;

import java.util.Arrays;
import java.util.List;

import org.lara.interpreter.joptions.config.interpreter.LaraiKeys;
import org.lara.interpreter.joptions.config.interpreter.VerboseLevel;
import org.lara.interpreter.joptions.keys.FileList;

import larai.LaraI;
import pt.up.fe.specs.util.providers.ResourceProvider;

public class WeaverApis {

    private static final List<ResourceProvider> WEAVER_API = ResourceProvider.getResources(WeaverApiResource.class);

    private static final List<Class<?>> WEAVER_IMPORTABLE_CLASSES = Arrays.asList(VerboseLevel.class, LaraI.class,
            LaraiKeys.class, FileList.class);

    public static List<ResourceProvider> getApis() {
        return WEAVER_API;
    }

    public static List<Class<?>> getImportableClasses() {
        return WEAVER_IMPORTABLE_CLASSES;
    }

}
