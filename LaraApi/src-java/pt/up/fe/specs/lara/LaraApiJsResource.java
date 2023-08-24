
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
 * @author Joao Bispo, Luis Sousa
 *
 */
public enum LaraApiJsResource implements LaraResourceProvider {

    LARAJOINPOINT_JS("LaraJoinPoint.js"),
    OUTPUT_JS("core/output.js"),
    CORE_JS("core.js"),
    CHECK_JS("lara/Check.js"),
    CSV_JS("lara/Csv.js"),
    IO_JS("lara/Io.js"),
    JAVAINTEROP_JS("lara/JavaInterop.js"),
    STRINGS_JS("lara/Strings.js"),
    LARACORE_JS("lara/core/LaraCore.js"),
    ABSTRACTCLASSERROR_JS("lara/util/AbstractClassError.js"),
    ACCUMULATOR_JS("lara/util/Accumulator.js"),
    DATASTORE_JS("lara/util/DataStore.js"),
    IDGENERATOR_JS("lara/util/IdGenerator.js"),
    JAVATYPES_JS("lara/util/JavaTypes.js"),
    JPFILTER_JS("lara/util/JpFilter.js"),
    PRINTONCE_JS("lara/util/PrintOnce.js"),
    REPLACER_JS("lara/util/Replacer.js"),
    STRINGSET_JS("lara/util/StringSet.js"),
    TIMEUNITS_JS("lara/util/TimeUnits.js"),
    JOINPOINTS_JS("weaver/JoinPoints.js"),
    QUERY_JS("weaver/Query.js"),
    SELECTOR_JS("weaver/Selector.js"),
    TRAVERSALTYPE_JS("weaver/TraversalType.js"),
    WEAVER_JS("weaver/Weaver.js"),
    WEAVEROPTIONS_JS("weaver/WeaverOptions.js"),
    WEAVERDATASTORE_JS("weaver/util/WeaverDataStore.js");

    private final String resource;

    private static final String WEAVER_PACKAGE = "";

    /**
     * @param resource
     */
    private LaraApiJsResource (String resource) {
      this.resource = WEAVER_PACKAGE + getSeparatorChar() + resource;
    }

    /* (non-Javadoc)
     * @see org.suikasoft.SharedLibrary.Interfaces.ResourceProvider#getResource()
     */
    @Override
    public String getOriginalResource() {
        return resource;
    }

}
