
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

package pt.up.fe.specs.lara;

import org.lara.interpreter.weaver.utils.LaraResourceProvider;

/**
 * This file has been automatically generated.
 * 
 * @author Joao Bispo
 *
 */
public enum LaraApiJsResource implements LaraResourceProvider {

    CORETEST_JS("core/coretest.js"),
    OUTPUT_JS("core/output.js"),
    CORE_JS("core.js"),
    LARACORE_JS("lara/core/LaraCore.js"),
    JAVATYPES_JS("lara/util/JavaTypes.js"),
    PRINTONCE_JS("lara/util/PrintOnce.js"),
    STRINGSET_JS("lara/util/StringSet.js");

    private final String resource;

    /**
     * @param resource
     */
    private LaraApiJsResource (String resource) {
        this.resource = resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
