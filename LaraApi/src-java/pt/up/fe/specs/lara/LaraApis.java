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

package pt.up.fe.specs.lara;

import java.util.Arrays;
import java.util.List;

import pt.up.fe.specs.lang.ApacheStrings;
import pt.up.fe.specs.lang.SpecsPlatforms;
import pt.up.fe.specs.util.SpecsIo;
import pt.up.fe.specs.util.SpecsSystem;
import pt.up.fe.specs.util.providers.ResourceProvider;
import pt.up.fe.specs.util.system.ProcessOutputAsString;

public class LaraApis {

    private static final List<ResourceProvider> LARA_API = ResourceProvider.getResources(LaraApiResource.class);

    private static final List<Class<?>> LARA_IMPORTABLE_CLASSES = Arrays.asList(SpecsIo.class, SpecsPlatforms.class,
            ApacheStrings.class,
            SpecsSystem.class, ProcessOutputAsString.class, LaraApiTools.class);

    public static List<ResourceProvider> getApis() {
        return LARA_API;
    }

    public static List<Class<?>> getImportableClasses() {
        return LARA_IMPORTABLE_CLASSES;
    }
}
