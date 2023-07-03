/**
 * Copyright 2023 SPeCS.
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

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import pt.up.fe.specs.util.SpecsIo;

public enum LaraExtension {
    LARA,
    JS,
    MJS;

    private static final Set<String> VALID_EXTENSIONS = new HashSet<>(
            Arrays.stream(LaraExtension.values()).map(ext -> ext.getExtension()).collect(Collectors.toList()));

    private final String extension;

    private LaraExtension() {
        this.extension = name().toLowerCase();
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isValidExtension(String extension) {
        return VALID_EXTENSIONS.contains(extension);
    }

    public static boolean isValidExtension(File file) {
        return isValidExtension(SpecsIo.getExtension(file));
    }

}
