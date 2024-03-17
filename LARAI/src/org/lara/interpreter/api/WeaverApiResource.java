/**
 * Copyright 2013 SuikaSoft.
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

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * @author Joao Bispo
 *
 */
public enum WeaverApiResource implements LaraResourceProvider {

    // Weaver utils
    WEAVER_DATA_STORE("util/WeaverDataStore.js"),
    ACTION_AWARE_CACHE("util/ActionAwareCache.js"),

    // Weaver
    AST("Ast.lara"),
    JOIN_POINTS("JoinPointsBase.lara"),
    QUERY("Query.js"),
    SCRIPT("Script.js"),
    SELECTOR("Selector.lara"),
    TRAVERSAL_TYPE("TraversalType.js"),
    WEAVER("Weaver.js"),
    WEAVER_JPS("WeaverJps.lara"),
    WEAVER_LAUNCHER_BASE("WeaverLauncherBase.lara"),
    WEAVER_OPTIONS("WeaverOptions.lara");

    private final String resource;

    private static final String BASE_PACKAGE = "weaver/";

    /**
     * @param resource
     */
    private WeaverApiResource(String resource) {
        this.resource = BASE_PACKAGE + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
