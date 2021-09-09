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

package org.lara.interpreter.weaver.utils;

public class GenericLaraResourceProvider implements LaraResourceProvider {

    private final String originalResource;

    public GenericLaraResourceProvider(String originalResource) {
        this.originalResource = originalResource;
    }

    public GenericLaraResourceProvider(String base, String importResource) {
        String normalizedBase = base.endsWith("/") ? base : base + "/";
        originalResource = normalizedBase + LaraResourceProvider.getDefaultSeparatorChar() + importResource;
    }

    @Override
    public String getOriginalResource() {
        return originalResource;
    }

    @Override
    public String toString() {
        return getResource();
    }
}
