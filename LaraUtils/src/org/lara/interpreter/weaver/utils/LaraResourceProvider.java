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
 * specific language governing permissions and limitations under the License.
 */

package org.lara.interpreter.weaver.utils;

import pt.up.fe.specs.util.providers.ResourceProvider;

public interface LaraResourceProvider extends ResourceProvider {

    static char getDefaultSeparatorChar() {
        return '%';
    }

    default char getSeparatorChar() {
        return getDefaultSeparatorChar();
    }

    String getOriginalResource();

    @Override
    default String getResource() {
        String originalResource = getOriginalResource();
        int separatorIndex = originalResource.indexOf(getSeparatorChar());

        if (separatorIndex == -1) {
            return originalResource;
        }

        return originalResource.substring(0, separatorIndex)
                + originalResource.substring(separatorIndex + 1);
    }

    @Override
    default String getFileLocation() {
        String originalResource = getOriginalResource();

        int separatorIndex = originalResource.indexOf(getSeparatorChar());
        if (separatorIndex == -1) {
            return originalResource;
        }

        // Remove what is before the seperator
        return originalResource.substring(separatorIndex + 1);
    }
}
